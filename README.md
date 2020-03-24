# PTX Container Creator

Node.js Module for exporting OTX projects as PTX Container.

### Installing

Get a copy of this project and install it in your project

```
npm install --save path_to_my_local_modules/ptx-creator
```

This will update your package.json as it were a public module.

In case that you need to update the local module, just do

```
rm -rf node_modules/ptx-creator && npm install
```

### How to use it

In order to export a OTX project as a PTX container, you should follow a 3-step operation.

Import the module:

```
let ptxCreatorModule = require('ptx-creator');
```

Second, describe your OTX project. For that, you'll use a JSON object, this is a template:

```
{
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
}
```

In the following snippet you can see how to fill in the JSON object. The name, version and uri of the
projects are mandatory:

```
let otxproject = OtxDataFactory.createOtxProjectJson('demo project', '1.0.0', 'D:\\OTX_PROJECT\\demopro');
OtxDataFactory.addFolder(otxproject, 'D:\\OTX_PROJECT\\demopro\\package2', ['.png', '.txt']);
OtxDataFactory.addFolder(otxproject, 'D:\\OTX_PROJECT\\demopro\\src');
OtxDataFactory.addFolder(otxproject, 'D:\\OTX_PROJECT\\demopro\\bin');
OtxDataFactory.addFile(otxproject, 'D:\\OTX_PROJECT\\demopro\\package1\\file1.otx');
```

You can also do it directly:

```
let otxproject = OtxDataFactory.createEmptyOtxProjectJson();

otxproject.name = 'Test Otx Project';
otxproject.company = 'KPIT';
otxproject.files.push({
    name: 'file1.otx',
    path: 'D:\\otxproject-sample\\package1\\file1.otx'
});
otxproject.files.push({
    name: 'file2.otx',
    path: 'D:\\otxproject-sample\\package1\\file2.otx'
});
otxproject.files.push({
    name: 'binary-file1.txt',
    path: 'D:\\otxproject-sample\\package1\\binary-file1.txt'
});
otxproject.files.push({
    name: 'main.otx',
    path: 'D:\\otxproject-sample\\main.otx'
});

otxproject.files.push({
    name: 'main.otx',
    path: 'D:\\otxproject-sample\\package2'
});
```

Once you have the OTX Project JSON object ready, you can create a PtxCreator object.

```
const projectUri = 'D:\\otxproject-sample';
let ptxExporter = new PtxCreator();
```

Finally, you can export it in a directory.

```
const targetPtx = 'D:\\otxproject-sample\\test.zip';
const mergeSrcBin = true;
ptxExporter.export(otxproject, targetPtx, mergeSrcBin); 
```

Optionally you can encrypt the .otx files with a password.

```
const targetPtx = 'D:\\otxproject-sample\\test.zip';
const mergeSrcBin = true;
const password = 'kpit2018.';
ptxExporter.export(otxproject, targetPtx, mergeSrcBin, password); 
```

## License

This project is licensed under the KPIT License - see the [LICENSE.md](LICENSE.md) file for details
