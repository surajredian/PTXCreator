package com.kpit.ptxcreator;

import java.util.List;

import com.kpit.ptxcreator.exception.ErrorCode;
import com.kpit.ptxcreator.exception.PTXCreatorException;

public class PTXExtractorDTO {

	private String targetPTXFile;
	private String locationToExtract;
	private String targetProjectName;
	private String password;
	private List<String> includeExts = null;
	private List<String> excludeExts = null;

	/**
	 * Creates the DTO using which Extraction of PTX will be processed
	 * 
	 * @param targetPTXFile
	 *            - the PTX file that will act as the source for Extraction
	 * @param locationToExtract
	 *            - Destination folder where extraction will be done. This
	 *            should be an existing folder in the system
	 * @param targetProjectName
	 *            - This will be folder name created under 'locationToExtract'
	 *            This folder if already present will be purged and re-created.
	 *            Pass it as null if you would like to create a folder with same
	 *            name as PTX File.
	 * @param password
	 *            - password to read the PTX in case it is an encrypted file.
	 *            pass it as null if no password needed. Trailing spaces will be
	 *            trimmed.
	 * 
	 */
	public PTXExtractorDTO(String targetPTXFile, String locationToExtract, String targetProjectName, String password) {
		this.targetPTXFile = targetPTXFile;
		this.locationToExtract = locationToExtract;
		this.targetProjectName = targetProjectName;
		this.password = password;
	}

	public String getTargetPTXFile() {
		return targetPTXFile;
	}

	public String getLocationToExtract() {
		return locationToExtract;
	}

	public String getTargetProjectName() {
		return targetProjectName;
	}

	public String getPassword() {
		return password;
	}

	public List<String> getIncludeExts() {
		return includeExts;
	}

	/**
	 * Sets the List of file extensions to be included while packaging 
	 * Include list takes priority over exclude list 
	 * If both are set then exclude list will be ignored
	 * 
	 * @param includeExts
	 *            - list of files to be included
	 */
	public void setIncludeExts(List<String> includeExts) {
		this.includeExts = includeExts;
	}

	public List<String> getExcludeExts() {
		return excludeExts;
	}

	/**
	 * Sets the List of file extensions to be excluded while packaging 
	 * Include list takes priority over exclude list 
	 * If both are set then exclude list will be ignored
	 * 
	 * @param includeExts
	 *            - list of files to be included
	 */
	public void setExcludeExts(List<String> excludeExts) {
		this.excludeExts = excludeExts;
	}
	
	public void validate() throws PTXCreatorException {
		if (null == this.targetPTXFile || 0 == this.targetPTXFile.trim().length()) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR, "Please provide a valid PTX file to Extract");
		}
		if (null == this.locationToExtract || 0 == this.locationToExtract.trim().length()) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR,
					"Please provide a valid location where to extract the PTX file");
		}
	}
}
