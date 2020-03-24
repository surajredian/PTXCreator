export declare type Module = {
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
export declare type IDependency = {
    moduleName: string;
    versionRange: string;
};
export declare type File = {
    name: string;
    path: string;
};
export declare type Entrypoint = {
    id: number;
    name: string;
};
export declare class OtxDataFactory {
    static createOtxProjectJson(projectname: string, projectversion: string, uri: string): Module;
    static addFile(otxProject: Module, filepath: string): Module;
    static addFolder(otxProject: Module, folderpath: string, selector?: string[]): Module;
    private static walkSync(dir, filelist);
}
