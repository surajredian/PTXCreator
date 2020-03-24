package com.kpit.ptxcreator.unzip;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.kpit.ptxcreator.exception.ErrorCode;
import com.kpit.ptxcreator.exception.PTXCreatorException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

public class UnZipImpl implements IUnZip {

	private static final String PTX_FILE_EXT = ".ptx";
	private String pass = null;
	private List<String> includeExts = null;
	private List<String> excludeExts = null;

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
	public void extractPTX(String targetPTXFile, String locationToExtract, String folderNameToExtract)
			throws PTXCreatorException {

		File compressedFile = new File(targetPTXFile);
		// File should be a ptx file
		if (!compressedFile.getName().toLowerCase().endsWith(PTX_FILE_EXT)) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR, "File extension is not .ptx");
		}

		File extractLocation = new File(locationToExtract);
		if (!extractLocation.exists()) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR,
					"Extract Location: " + locationToExtract + " doesn't exist");
		}

		if ((null == folderNameToExtract) || (0 == folderNameToExtract.trim().length())) {
			folderNameToExtract = compressedFile.getName();
			folderNameToExtract = folderNameToExtract.substring(0, folderNameToExtract.length() - 4);
		}

		File targetFolderToExtract = new File(locationToExtract, folderNameToExtract);
		if (targetFolderToExtract.exists()) {
			try {
				FileUtils.deleteDirectory(targetFolderToExtract);
			} catch (Exception ex) {
				throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, ex, "Failed deleting folder : "
						+ targetFolderToExtract + ". Kindly delete it manually and re-trigger");
			}
		}

		ZipFile zipFile;
		List<?> zipFileHeaders;
		try {
			zipFile = new ZipFile(compressedFile);
			if (zipFile.isEncrypted()) {
				if ((null == this.pass) || (0 == this.pass.trim().length())) {
					throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR,
							"No Password supplied for encrypted file");
				}
				zipFile.setPassword(this.pass);
			}
			zipFileHeaders = zipFile.getFileHeaders();
		} catch (ZipException ex) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, ex,
					"Failed in creating/accessing PTX File/it's Headers");
		}

		for (Object header : zipFileHeaders) {
			if (header instanceof FileHeader) {
				createFileForHeader(zipFile, (FileHeader) header, targetFolderToExtract);
			} else {
				throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR,
						"Unexpected value when looking through Zip File Header");
			}
		}
	}

	private void createFileForHeader(ZipFile zipFile, FileHeader fileHeader, File targetBaseFolderToExtract)
			throws PTXCreatorException {

		File fileToCreate = new File(targetBaseFolderToExtract.getAbsolutePath(), fileHeader.getFileName());
		// Include Exclude specific extensions
		String split[] = fileHeader.getFileName().split("\\.");
		String ext = split[split.length - 1];
		if (null != includeExts && includeExts.size() > 0) {
			if (!includeExts.contains(ext))
				return;
		} else if (null != excludeExts && excludeExts.size() > 0) {
			if (excludeExts.contains(ext))
				return;
		}
		fileToCreate.getParentFile().mkdirs();

		try {
			ZipInputStream src = zipFile.getInputStream(fileHeader);
			FileOutputStream dest = new FileOutputStream(fileToCreate);
			IOUtils.copy(src, dest);
			src.close();
			dest.close();
		} catch (Exception e) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, e,
					"Error when copying: " + fileHeader.getFileName() + " to " + fileToCreate.getAbsolutePath());
		}
	}
}
