import { Module } from './otx-data';
export declare class PtxCreator {
    private readonly OTX_DATA;
    private readonly ODX_DATA;
    private readonly BINARY;
    private readonly OTX_EXT;
    private readonly OTXT_EXT;
    private readonly ODX_EXT;
    constructor();
    export(product: Module, targetPtx: string, mergeSrcBin: boolean, password?: string): void;
    private createCatalogFile(product, mergeSrcBin);
    private addFilesToCatalog(product, catalog, mergeSrcBin, selector?);
    private addFileToCatalog(relpath, filepath, files, selector?);
    private addFiles(product, zip, mergeSrcBin, password?);
    private relatizeFilePath(projectpath, srcpath, binpath, filepath);
    private isChildOf(child, parent);
    private encryptBuffer(buffer, password);
    private walkSync(dir, filelist);
}
