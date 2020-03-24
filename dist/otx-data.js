'use strict';
Object.defineProperty(exports, "__esModule", { value: true });
var fs = require("fs");
var path = require("path");
var OtxDataFactory = (function () {
    function OtxDataFactory() {
    }
    OtxDataFactory.createOtxProjectJson = function (projectname, projectversion, uri) {
        var otxproject = {
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
    };
    OtxDataFactory.addFile = function (otxProject, filepath) {
        var filename = path.basename(filepath);
        var f = {
            name: filename,
            path: filepath
        };
        otxProject.files.push(f);
        return otxProject;
    };
    OtxDataFactory.addFolder = function (otxProject, folderpath, selector) {
        if (fs.lstatSync(folderpath).isDirectory()) {
            var children = this.walkSync(folderpath, []);
            children.forEach(function (child) {
                var ext = path.extname(child);
                var filename = path.basename(child);
                var f = {
                    name: filename,
                    path: child
                };
                if (selector) {
                    if (selector.indexOf(ext) > -1) {
                        otxProject.files.push(f);
                    }
                }
                else {
                    otxProject.files.push(f);
                }
            });
        }
        return otxProject;
    };
    OtxDataFactory.walkSync = function (dir, filelist) {
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
    return OtxDataFactory;
}());
exports.OtxDataFactory = OtxDataFactory;
