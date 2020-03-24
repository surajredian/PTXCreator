/*---------------------------------------------------------------------------------------------
 *  Copyright (c) KPIT. All rights reserved.
 *  Licensed under the TODO License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
'use strict';

import * as fs from 'fs';
import * as path from 'path';

import { Module } from './otx-data';
import * as AdmZip from 'adm-zip';
import * as xmlbuilder from 'xmlbuilder';


export class PtxCreator {

	//private readonly OTX_MAIN = 'OTX-MAIN';
	private readonly OTX_DATA = 'OTX-DATA';
	private readonly ODX_DATA = 'ODX-DATA';
	private readonly BINARY = 'BINARY';

	private readonly OTX_EXT = '.otx';
	private readonly OTXT_EXT = '.otxt';
	private readonly ODX_EXT = '.odx';
	
	constructor() {
	}

	/**
	 * Creates a PTX Container in the provided path
	 * If a password is provided, it will be encrypted
	 * @param targetPtx 
	 * @param password 
	 */
	public export(product: Module, targetPtx: string, mergeSrcBin: boolean, password?: string): void {
		let zip = new AdmZip();

		// PTX Catalog (index.xml)
		let catalog = this.createCatalogFile(product, mergeSrcBin);
		let buffer = new Buffer(catalog); //JSON.stringify(catalog, null, 4)
		zip.addFile('index.xml', buffer);

		// Files
		this.addFiles(product, zip, mergeSrcBin, password);

		zip.writeZip(targetPtx);
		console.log('PTX Container created in: ' + targetPtx);
	}

	private createCatalogFile(product: Module, mergeSrcBin: boolean): string {
		let catalog = xmlbuilder.create('CATALOG', { encoding: 'utf-8' })
			.att('xmlns', 'http://iso.org/OTX/1.0.0/Auxiliaries/Container')
			.att('xmlns:ptxXhtml', 'http://iso.org/OTX/1.0.0/Auxiliaries/ContainerXhtml')
			.att('xsi:schemaLocation', 'http://iso.org/OTX/1.0.0/Auxiliaries/Container schema.xsd')
			.att('CONTAINER-VERSION', product.version)
			.att('xmlns:xsi', 'http://www.w3.org/2001/XMLSchema-instance')
			// SHORT-NAME
			.ele('SHORT-NAME', product.name).up()
			// COMPANY-DATAS
			.ele('COMPANY-DATAS')
				.ele('COMPANY-DATA')
					.ele('ELEMENT-ID')
						.ele('SHORT-NAME', product.company).up()
					.up()
				.up()
			.up();
			// ADMIN-DATAS


			// ABLOCKS
			let ablocks = catalog.ele('ABLOCKS');

				// OTX Files
			let ablock = ablocks.ele('ABLOCK').ele('CATEGORY', this.OTX_DATA).up();
			ablock = this.addFilesToCatalog(product, ablock, mergeSrcBin, [this.OTX_EXT, this.OTXT_EXT]);

				// ODX Files
			ablock = ablocks.ele('ABLOCK').ele('CATEGORY', this.ODX_DATA).up();
			ablock = this.addFilesToCatalog(product, ablock, mergeSrcBin, [this.ODX_EXT]);
				
				// Binary files
			ablock = ablocks.ele('ABLOCK').ele('CATEGORY', this.BINARY).up();
			ablock = this.addFilesToCatalog(product, ablock, mergeSrcBin);

			catalog = ablock.up().up();


		return catalog.end({ pretty: true });
	}

	/**
	 * Add the files included in the JSON object to the Catalog.
	 * If a directory is provided, it will include all its children files.
	 * If a selector is provided, it will include the file only if it matches with the selector.
	 * If a selector is not provided, it will included all files except .otx and .odx files
	 * @param catalog 
	 * @param selector 
	 * @param mergeSrcBin if true, it will include in th ptx container everything under /src and /bin 
	 */
	private addFilesToCatalog(product: Module, catalog: xmlbuilder.XMLElementOrXMLNode, mergeSrcBin: boolean, selector?: string[]): xmlbuilder.XMLElementOrXMLNode {
		let files = catalog.ele('FILES').att('HASH-ALGORITHM', '');

		const sourcepath = path.resolve(product.projecturi, '..');
		const srcpath = path.join(product.projecturi, 'src');
		const binpath = path.join(product.projecturi, 'bin');

		if (product.files) {
			product.files.forEach(file => {
				if (fs.lstatSync(file.path).isDirectory()) {
					let children = this.walkSync(file.path, []);
					children.forEach(f => {
						let relpath = (mergeSrcBin) ? this.relatizeFilePath(product.projecturi, srcpath, binpath, f) : path.relative(sourcepath, f);
						files = this.addFileToCatalog(relpath, f, files, selector);
					});
				} else {
					let relpath = (mergeSrcBin) ? this.relatizeFilePath(product.projecturi, srcpath, binpath, file.path) : path.relative(sourcepath, file.path);
					files = this.addFileToCatalog(relpath, file.path, files, selector);
				}

			});
		}
		return files;
	}

	/**
	 * Add a file to the Catalog.
	 * If a selector is provided, it will include the file only if it matches with the selector.
	 * If a selector is not provided, it will included all files except .otx and .odx files
	 * @param sourcepath 
	 * @param filepath 
	 * @param files 
	 * @param selector 
	 */
	private addFileToCatalog(relpath: string, filepath: string, files: xmlbuilder.XMLElementOrXMLNode, selector?: string[]): xmlbuilder.XMLElementOrXMLNode {
		let ext = path.extname(filepath);
		if (selector) {
			if (selector.indexOf(ext) > -1) {
				files.ele('FILE', relpath).up();
			}
		} else {
			if (ext !== this.OTX_EXT && ext !== this.ODX_EXT && ext !== this.OTXT_EXT ) {
				files.ele('FILE', relpath).up();
			}
		}
		return files;
	}

	/**
	 * Add the files included in the JSON file into the zip file.
	 * If a directory is provided, it will include all children files.
	 * @param zip 
	 */
	private addFiles(product: Module, zip: AdmZip, mergeSrcBin: boolean, password?: string): void {
		const sourcepath = product.projecturi; //path.resolve(product.projecturi, '..');
		const srcpath = path.join(product.projecturi, 'src');
		const binpath = path.join(product.projecturi, 'bin');

		if (product.files) {
			product.files.forEach(file => {
				if (fs.lstatSync(file.path).isDirectory()) {
					let ficheros = this.walkSync(file.path, []);
					ficheros.forEach(f => {
						let relpath = (mergeSrcBin) ? this.relatizeFilePath(product.projecturi, srcpath, binpath, f) : path.relative(sourcepath, f);
						let input = fs.readFileSync(f);
						if (password) {
							input = this.encryptBuffer(input, password);
						}
						zip.addFile(relpath, input, '', 0o644);
					});
				} else {
					let relpath = (mergeSrcBin) ? this.relatizeFilePath(product.projecturi, srcpath, binpath, file.path) : path.relative(sourcepath, file.path);
					let input = fs.readFileSync(file.path);
					if (password) {
						input = this.encryptBuffer(input, password);
					}
					zip.addFile(relpath, input, '', 0o644);
				}
			});
		}
	}

	private relatizeFilePath(projectpath: string, srcpath: string, binpath: string, filepath: string): string {
		let relpath = '';
		if (this.isChildOf(filepath, srcpath)) {
			relpath = path.relative(srcpath, filepath);
		} else if (this.isChildOf(filepath, binpath)) {
			relpath = path.relative(binpath, filepath);
		} else {
			relpath = path.relative(projectpath, filepath);
		}
		return relpath;
	}

	/**
	 * Check if a path is a subdirectory of another path
	 * @param child 
	 * @param parent 
	 */
	private isChildOf(child: string, parent: string): boolean {
		if (child === parent) return false;
		const parentTokens = parent.split(path.sep).filter(i => i.length);
		return parentTokens.every((t, i) => child.split(path.sep)[i] === t);
	}

	/**
	 * Encrypt the data with the AES-256 algorithm.
	 * @param buffer 
	 * @param password 
	 */
	private encryptBuffer(buffer: Buffer, password: string): Buffer {
		let crypto = require('crypto');
		const algorithm = 'aes-256-ctr';
		let cipher = crypto.createCipher(algorithm, password); // should use createCipheriv?
		let crypted = Buffer.concat([cipher.update(buffer), cipher.final()]);
		return crypted;
	}

	/**
	 * Provides all the files included in a directory recursively
	 * @param dir 
	 * @param filelist 
	 */
	private walkSync(dir: string, filelist: string[]) {
		fs.readdirSync(dir).forEach(file => {
			const dirFile = path.join(dir, file);
			try {
				filelist = this.walkSync(dirFile, filelist);
			}
			catch (err) {
				if (err.code === 'ENOTDIR' || err.code === 'EBUSY') filelist = [...filelist, dirFile];
				else throw err;
			}
		});
		return filelist;
	}

}
