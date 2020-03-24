package com.kpit.ptxcreator.unzip;

import java.util.List;

import com.kpit.ptxcreator.exception.PTXCreatorException;

public interface IUnZip {

	/**
	 * Extracts a give PTX at target location.
	 * If folder already exists for targetProjectName, it will be purged and re-created.
	 * If targetProjectName is not provided it will take the targetProjectName as targetPTXFile name
	 * 
	 * @param targetPTXFile - file which the project will be extracted
	 * @param locationToExtract - target folder where project should be created 
	 * @param folderNameToExtract - name of the sub folder under locationToExtract in which project will be extracted
	 * 				(folder will be created, if already present will be purged and created)
	 * 
	 * @return - none
	 * 
	 */
	public void extractPTX(String targetPTXFile, String locationToExtract, String folderNameToExtract)
			throws PTXCreatorException;

	/**
	 * Sets the password for file encryption during zip process 
	 * 
	 * @param pass - Password for the encryption
	 * @return - none
	 * 
	 */
	public void setPassword(String pass);
	
	/**
	 * Includes the file extensions passed to this function
	 * Include has a higher priority, 
	 * so if both include and exclude lists are set 
	 * only include list will be considered
	 * 
	 * @param exts - list of file extensions to include
	 */
	public void includeExtensionList(List<String> exts);
	
	/**
	 * Excludes the file extensions passed to this function
	 * Include has a higher priority, 
	 * so if both include and exclude lists are set 
	 * only include list will be considered
	 * 
	 * @param exts - list of file extensions to exclude
	 */
	public void excludeExtensionList(List<String> exts);
}
