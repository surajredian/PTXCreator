package com.kpit.ptxcreator.standalone;

import java.util.List;

import com.kpit.ptxcreator.PTXCreatorDTO;
import com.kpit.ptxcreator.PTXExtractorDTO;
import com.kpit.ptxcreator.PTXCreatorDTO.ComplianceType;
import com.kpit.ptxcreator.exception.ErrorCode;
import com.kpit.ptxcreator.exception.PTXCreatorException;

/**
 * 
 * Defines the input format of the stand alone and how it should transformed
 * into PTXCreatorDTO
 */
public class StandaloneInputDTO {
	// If the input needs to be consumed from a file instead of parameter
	private String fileName = null;
	// Common Details
	private Command command = null;
	private String ptxFile;
	private String password;
	private List<String> includeList;
	private List<String> excludeList;
	// Extract PTX Input
	private String locationToExtract;
	private String folderNameToExtractIn;
	// Create PTX Inputs
	private String company;
	private String productName;
	private String productVersion;
	private String compliance = "KPIT";
	private List<PackageBundle> packageBundleMap;

	class PackageBundle {
		String rootProjectDirectory;
		List<String> packages;
	}

	public String getFileName() {
		return fileName;
	}

	public Command getCommand() {
		return command;
	}

	public PTXCreatorDTO convertToPTXCreatorDTO() throws PTXCreatorException {
		PTXCreatorDTO ptxDto = new PTXCreatorDTO();
		ptxDto.setCompany(company);
		ptxDto.setProductName(productName);
		ptxDto.setProductVersion(productVersion);
		ptxDto.setPtxFile(ptxFile);
		ptxDto.setPassword(password);

		switch (compliance.toUpperCase()) {
		case "KPIT":
			ptxDto.setCompliance(ComplianceType.KPIT);
			break;
		case "ASAM":
			ptxDto.setCompliance(ComplianceType.ASAM);
			break;
		default:
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR, "Unsupported compliance type " + compliance);
		}

		if (null == packageBundleMap) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR, "Provide valid packageBundleMap for packging");
		}
		for (PackageBundle element : packageBundleMap) {
			ptxDto.addProjectDirectoryAndPackages(element.rootProjectDirectory, element.packages);
		}

		ptxDto.setExcludeExts(excludeList);
		ptxDto.setIncludeExts(includeList);
		return ptxDto;
	}

	public PTXExtractorDTO convertToPTXExtractorDTO() {
		PTXExtractorDTO ptxDto = new PTXExtractorDTO(ptxFile, locationToExtract, folderNameToExtractIn, password);
		ptxDto.setExcludeExts(excludeList);
		ptxDto.setIncludeExts(includeList);

		return ptxDto;
	}
}
