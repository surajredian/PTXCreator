/*---------------------------------------------------------------------------------------------
 *  Copyright (c) KPIT. All rights reserved.
 *  Licensed under the TODO License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
'use strict';

import * as fs from 'fs';
import * as path from 'path';

export type Module = {
    id: string;
    version: string;
    name: string;
    company: string;
    description: string;
    projecturi: string;
    metainformation: {
        state: {
            value: string;
            options: string[];
        };
        type: {
            value: string;
            options: string[];
        };
        keywords: string;
        summary: string;
    };
    dependencies: IDependency[];
    files: File[];
    entrypoints: Entrypoint[];
    license: {
        url: string;
        text: string;
    };
    copyright: {
        url: string;
        text: string;
    };

};

export type IDependency = {
    moduleName: string;
    versionRange: string;
};

export type File = {
    name: string;
    path: string;
};

export type Entrypoint = {
    id: number;
    name: string;
};

export class OtxDataFactory {

    public static createOtxProjectJson(projectname: string, projectversion: string, uri: string): Module {
        let otxproject: Module = {
            id: '',
            version: projectversion,
            name: projectname,
            company: 'KPIT',
            description: '',
            projecturi: uri,
            metainformation: {
                state: {
                    value: '',
                    options: []
                },
                type: {
                    value: '',
                    options: []
                },
                keywords: '',
                summary: '',
            },
            dependencies: [],
            files: [],
            entrypoints: [],
            license: {
                url: '',
                text: ''
            },
            copyright: {
                url: '',
                text: ''
            }
        };
        return otxproject;
    }

    /**
     * Adds a single file to the otxProject json
     * @param otxProject 
     * @param filepath 
     */
    public static addFile(otxProject: Module, filepath: string): Module {
        let filename = path.basename(filepath);
        let f: File = {
            name: filename,
            path: filepath
        };
        otxProject.files.push(f);
        return otxProject;
    }

    /**
     * Adds a folder to the otxProject json file.
     * Optionally, you can add a selector to select only certain files
     * Example: let selector = ['.png', '.txt']
     * @param otxProject 
     * @param folderpath 
     * @param selector 
     */
    public static addFolder(otxProject: Module, folderpath: string, selector?: string[]): Module {
        if (fs.lstatSync(folderpath).isDirectory()) {
            let children = this.walkSync(folderpath, []);
            children.forEach(child => {
                let ext = path.extname(child);
                let filename = path.basename(child);
                let f: File = {
                    name: filename,
                    path: child
                };

                if (selector) {
                    if (selector.indexOf(ext) > -1) {
                        // if there is a file selector, only the files
                        // that match the selector will be included.
                        otxProject.files.push(f);
                    }
                } else {
                    // if there is no selector, all files are included
                    otxProject.files.push(f);
                }
            });
        }
        return otxProject;
    }

    /**
	 * Provides all the files included in a directory recursively
	 * @param dir 
	 * @param filelist 
	 */
	private static walkSync(dir: string, filelist: string[]) {
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