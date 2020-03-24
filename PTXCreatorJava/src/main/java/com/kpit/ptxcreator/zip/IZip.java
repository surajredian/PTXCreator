package com.kpit.ptxcreator.zip;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.kpit.ptxcreator.exception.PTXCreatorException;

public interface IZip {

	/**
	 * Creates a compressed PTX file based on the input files. Will delete PTX
	 * file if it already exists in that location
	 * 
	 * @param targetPTXFileLocation
	 *            - location where PTX file will be created
	 * @param inputFiles
	 *            - Map <"Folder inside the PTX", "List of files/folders to go
	 *            into that location">. If key is null, will directly put it at root
	 * @return - none, file will be created at targetPTXFileLocation
	 * 
	 */
	public void createPTX(String targetPTXFileLocation, Map<String, List<File>> inputFiles) throws PTXCreatorException;
	
	/**
	 * Adds input files to existing PTX file. 
	 * 
	 * @param targetPTXFileLocation
	 *            - location where PTX file will be created
	 * @param inputFiles
	 *            - Map <"Folder inside the PTX", "List of files/folders to go
	 *            into that location">. If key is null, will directly put it at root
	 * @return - none, file will be created at targetPTXFileLocation
	 * 
	 */
	public void addToPTX(String targetPTXFileLocation, Map<String, List<File>> inputFiles) throws PTXCreatorException;
	
	/**
	 * Adds in memory input stream to existing PTX file. 
	 * 
	 * @param targetPTXFileLocation
	 *            - location where PTX file will be created
	 * @param inStream
	 *            - in memory stream that needs to be written
	 * @param file
	 * 			  - File name into which the inStream needs to be written into
	 * @return - none, file will be created at targetPTXFileLocation
	 * 
	 */
	public void addStreamToPTX(String targetPTXFileLocation, InputStream inStream, String file, String targetPackage) throws PTXCreatorException;
	
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
