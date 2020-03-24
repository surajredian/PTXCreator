package com.kpit.ptxcreator.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kpit.ptxcreator.exception.ErrorCode;
import com.kpit.ptxcreator.exception.PTXCreatorException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

class ZipImpl implements IZip {

	private static final String PTX_FILE_EXT = ".ptx";
	private String pass = null;
	private List<String> includeExts = null;
	private List<String> excludeExts = null;
	private boolean streamFeature = true;
	private ZipOutputStream zipOutputStream = null;

	@Override
	public void setPassword(String pass) {
		this.pass = pass;
	}

	@Override
	public void includeExtensionList(List<String> exts) {
		this.includeExts = exts;
	}

	@Override
	public void excludeExtensionList(List<String> exts) {
		this.excludeExts = exts;
	}

	@Override
	public void createPTX(String targetPTXFileLocation, Map<String, List<File>> inputFiles) throws PTXCreatorException {
		try {
			zipFiles(targetPTXFileLocation, inputFiles, true);
		} finally {
			finalizeZipFileStream();
		}
	}

	@Override
	public void addToPTX(String targetPTXFileLocation, Map<String, List<File>> inputFiles) throws PTXCreatorException {
		try {
			zipFiles(targetPTXFileLocation, inputFiles, false);
		} finally {
			finalizeZipFileStream();
		}
	}

	private void zipFiles(String targetPTXFileLocation, Map<String, List<File>> inputFiles,
			boolean deleteIfAlreadyPresent) throws PTXCreatorException {

		File compressedFile = new File(targetPTXFileLocation);
		// File should be a ptx file
		if (!compressedFile.getName().toLowerCase().endsWith(PTX_FILE_EXT)) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR, "File extension is not .ptx");
		}

		// Delete if ptx already exists or adds file to existing ptx based on
		// Flag
		if (compressedFile.exists() && deleteIfAlreadyPresent) {
			compressedFile.delete();
		}

		ZipFile zipFile;

		if (streamFeature) {
			zipFile = null;
			initializeZipFileStream(targetPTXFileLocation);
		} else {
			zipOutputStream = null;
			try {
				zipFile = new ZipFile(compressedFile);

			} catch (ZipException e) {
				throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, e, "Failed in creating/accessing PTX File");
			}
		}

		ZipParameters zipParams = new ZipParameters();
		zipParams.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		zipParams.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

		if (null != pass && pass.trim().length() > 0) {
			setZipParamsToEncryptFiles(zipParams);
		}

		for (Entry<String, List<File>> pair : inputFiles.entrySet()) {
			for (File inputFile : pair.getValue()) {

				if (!inputFile.exists()) {
					throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR,
							inputFile.getAbsolutePath() + " Doesn't Exist. PTX partially created");
				}
				addDirFileToZipRecursive(zipFile, zipParams, pair.getKey(), inputFile);
			}
		}
	}

	private void setZipParamsToEncryptFiles(ZipParameters zipParams) {
		zipParams.setEncryptFiles(true);
		zipParams.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
		zipParams.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
		zipParams.setPassword(pass);
	}

	private void addDirFileToZipRecursive(ZipFile zipFile, ZipParameters zipParams, String packagePath,
			File fileToBundle) throws PTXCreatorException {
		if (fileToBundle.isDirectory()) {
			File files[] = fileToBundle.listFiles();
			for (int i = 0; i < files.length; i++) {
				addDirFileToZipRecursive(zipFile, zipParams,
						((null == packagePath) ? "" : packagePath + File.separator) + fileToBundle.getName(), files[i]);
			}
		} else {
			addFileToZip(zipFile, zipParams, packagePath, fileToBundle);
		}
	}

	private void addFileToZip(ZipFile zipFile, ZipParameters zipParams, String packagePath, File file)
			throws PTXCreatorException {
		// Include Exclude specific extensions
		String split[] = file.getName().split("\\.");
		String ext = split[split.length - 1];
		if (null != includeExts && includeExts.size() > 0) {
			if (!includeExts.contains(ext))
				return;
		} else if (null != excludeExts && excludeExts.size() > 0) {
			if (excludeExts.contains(ext))
				return;
		}

		zipParams.setRootFolderInZip(packagePath);
		try {
			if (null == zipFile) {
				zipOutputStream.putNextEntry(file, zipParams);
				zipOutputStream.write(Files.readAllBytes(file.toPath()));
				zipOutputStream.closeEntry();
			} else {
				zipFile.addFile(file, zipParams);
			}
		} catch (Exception ex) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, ex, "Error when adding file: " + file.getPath());
		}
	}

	@Override
	public void addStreamToPTX(String targetPTXFileLocation, InputStream inStream, String file, String targetPackage)
			throws PTXCreatorException {
		File compressedFile = new File(targetPTXFileLocation);
		// File should be a ptx file
		if (!compressedFile.getName().toLowerCase().endsWith(PTX_FILE_EXT)) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR, "File extension is not .ptx");
		}

		ZipFile zipFile;
		try {
			zipFile = new ZipFile(compressedFile);
		} catch (ZipException e) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, e, "Failed in creating/accessing PTX File");
		}

		ZipParameters zipParams = new ZipParameters();

		zipParams.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		zipParams.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		zipParams.setRootFolderInZip(targetPackage);
		zipParams.setFileNameInZip(file);
		zipParams.setSourceExternalStream(true);

		if (null != pass && pass.trim().length() > 0) {
			setZipParamsToEncryptFiles(zipParams);
		}

		try {
			zipFile.addStream(inStream, zipParams);
		} catch (ZipException e) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, e,
					"Failed in adding Input Stream" + file + " to PTX File");
		}
	}

	public void initializeZipFileStream(String targetPTXFileLocation) throws PTXCreatorException {
		File compressedFile = new File(targetPTXFileLocation);
		try {
			OutputStream stream = new FileOutputStream(compressedFile);
			stream = new BufferedOutputStream(stream);
			zipOutputStream = new ZipOutputStream(stream);
		} catch (Exception ex) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, ex, "Zip file creation Failed!");
		}
	}

	public void finalizeZipFileStream() throws PTXCreatorException {
		try {
			if (null != zipOutputStream) {
				zipOutputStream.finish();
				zipOutputStream.close();
			}
		} catch (Exception ex) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, ex, "Error when closing Stream for Zip File!");
		}
	}
}
