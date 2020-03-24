'use strict';
Object.defineProperty(exports, "__esModule", { value: true });
var fs = require("fs");
var path = require("path");
var AdmZip = require("adm-zip");
var xmlbuilder = require("xmlbuilder");
var PtxCreator = (function () {
    function PtxCreator() {
        this.OTX_DATA = 'OTX-DATA';
        this.ODX_DATA = 'ODX-DATA';
        this.BINARY = 'BINARY';
        this.OTX_EXT = '.otx';
        this.OTXT_EXT = '.otxt';
        this.ODX_EXT = '.odx';
    }
    PtxCreator.prototype.export = function (product, targetPtx, mergeSrcBin, password) {
        var zip = new AdmZip();
        var catalog = this.createCatalogFile(product, mergeSrcBin);
        var buffer = new Buffer(catalog);
        zip.addFile('index.xml', buffer);
        this.addFiles(product, zip, mergeSrcBin, password);
        zip.writeZip(targetPtx);
        console.log('PTX Container created in: ' + targetPtx);
    };
    PtxCreator.prototype.createCatalogFile = function (product, mergeSrcBin) {
        var catalog = xmlbuilder.create('CATALOG', { encoding: 'utf-8' })
            .att('xmlns', 'http://iso.org/OTX/1.0.0/Auxiliaries/Container')
            .att('xmlns:ptxXhtml', 'http://iso.org/OTX/1.0.0/Auxiliaries/ContainerXhtml')
            .att('xsi:schemaLocation', 'http://iso.org/OTX/1.0.0/Auxiliaries/Container schema.xsd')
            .att('CONTAINER-VERSION', product.version)
            .att('xmlns:xsi', 'http://www.w3.org/2001/XMLSchema-instance')
            .ele('SHORT-NAME', product.name).up()
            .ele('COMPANY-DATAS')
            .ele('COMPANY-DATA')
            .ele('ELEMENT-ID')
            .ele('SHORT-NAME', product.company).up()
            .up()
            .up()
            .up();
        var ablocks = catalog.ele('ABLOCKS');
        var ablock = ablocks.ele('ABLOCK').ele('CATEGORY', this.OTX_DATA).up();
        ablock = this.addFilesToCatalog(product, ablock, mergeSrcBin, [this.OTX_EXT, this.OTXT_EXT]);
        ablock = ablocks.ele('ABLOCK').ele('CATEGORY', this.ODX_DATA).up();
        ablock = this.addFilesToCatalog(product, ablock, mergeSrcBin, [this.ODX_EXT]);
        ablock = ablocks.ele('ABLOCK').ele('CATEGORY', this.BINARY).up();
        ablock = this.addFilesToCatalog(product, ablock, mergeSrcBin);
        catalog = ablock.up().up();
        return catalog.end({ pretty: true });
    };
    PtxCreator.prototype.addFilesToCatalog = function (product, catalog, mergeSrcBin, selector) {
        var _this = this;
        var files = catalog.ele('FILES').att('HASH-ALGORITHM', '');
        var sourcepath = path.resolve(product.projecturi, '..');
        var srcpath = path.join(product.projecturi, 'src');
        var binpath = path.join(product.projecturi, 'bin');
        if (product.files) {
            product.files.forEach(function (file) {
                if (fs.lstatSync(file.path).isDirectory()) {
                    var children = _this.walkSync(file.path, []);
                    children.forEach(function (f) {
                        var relpath = (mergeSrcBin) ? _this.relatizeFilePath(product.projecturi, srcpath, binpath, f) : path.relative(sourcepath, f);
                        files = _this.addFileToCatalog(relpath, f, files, selector);
                    });
                }
                else {
                    var relpath = (mergeSrcBin) ? _this.relatizeFilePath(product.projecturi, srcpath, binpath, file.path) : path.relative(sourcepath, file.path);
                    files = _this.addFileToCatalog(relpath, file.path, files, selector);
                }
            });
        }
        return files;
    };
    PtxCreator.prototype.addFileToCatalog = function (relpath, filepath, files, selector) {
        var ext = path.extname(filepath);
        if (selector) {
            if (selector.indexOf(ext) > -1) {
                files.ele('FILE', relpath).up();
            }
        }
        else {
            if (ext !== this.OTX_EXT && ext !== this.ODX_EXT && ext !== this.OTXT_EXT) {
                files.ele('FILE', relpath).up();
            }
        }
        return files;
    };
    PtxCreator.prototype.addFiles = function (product, zip, mergeSrcBin, password) {
        var _this = this;
        var sourcepath = product.projecturi;
        var srcpath = path.join(product.projecturi, 'src');
        var binpath = path.join(product.projecturi, 'bin');
        if (product.files) {
            product.files.forEach(function (file) {
                if (fs.lstatSync(file.path).isDirectory()) {
                    var ficheros = _this.walkSync(file.path, []);
                    ficheros.forEach(function (f) {
                        var relpath = (mergeSrcBin) ? _this.relatizeFilePath(product.projecturi, srcpath, binpath, f) : path.relative(sourcepath, f);
                        var input = fs.readFileSync(f);
                        if (password) {
                            input = _this.encryptBuffer(input, password);
                        }
                        zip.addFile(relpath, input, '', 420);
                    });
                }
                else {
                    var relpath = (mergeSrcBin) ? _this.relatizeFilePath(product.projecturi, srcpath, binpath, file.path) : path.relative(sourcepath, file.path);
                    var input = fs.readFileSync(file.path);
                    if (password) {
                        input = _this.encryptBuffer(input, password);
                    }
                    zip.addFile(relpath, input, '', 420);
                }
            });
        }
    };
    PtxCreator.prototype.relatizeFilePath = function (projectpath, srcpath, binpath, filepath) {
        var relpath = '';
        if (this.isChildOf(filepath, srcpath)) {
            relpath = path.relative(srcpath, filepath);
        }
        else if (this.isChildOf(filepath, binpath)) {
            relpath = path.relative(binpath, filepath);
        }
        else {
            relpath = path.relative(projectpath, filepath);
        }
        return relpath;
    };
    PtxCreator.prototype.isChildOf = function (child, parent) {
        if (child === parent)
            return false;
        var parentTokens = parent.split(path.sep).filter(function (i) { return i.length; });
        return parentTokens.every(function (t, i) { return child.split(path.sep)[i] === t; });
    };
    PtxCreator.prototype.encryptBuffer = function (buffer, password) {
        var crypto = require('crypto');
        var algorithm = 'aes-256-ctr';
        var cipher = crypto.createCipher(algorithm, password);
        var crypted = Buffer.concat([cipher.update(buffer), cipher.final()]);
        return crypted;
    };
    PtxCreator.prototype.walkSync = function (dir, filelist) {
        var _this = this;
        fs.readdirSync(dir).forEach(function (file) {
            var dirFile = path.join(dir, file);
            try {
                filelist = _this.walkSync(dirFile, filelist);
            }
            catch (err) {
                if (err.code === 'ENOTDIR' || err.code === 'EBUSY')
                    filelist = filelist.concat([dirFile]);
                else
                    throw err;
            }
        });
        return filelist;
    };
    return PtxCreator;
}());
exports.PtxCreator = PtxCreator;
