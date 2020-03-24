package com.kpit.ptxcreator.exception;

/**
 * 
 * Maintaining a list of error codes the PTX Creator will use
 *
 */
public enum ErrorCode {

	UNKNOWN_ERROR("1000", "Unknown Error"),
	VALIDATION_ERROR("1001", "Error in Input"),
	INTERNAL_ERROR("1010", "Error in internal Processing");
	
	private String code;
	private String description;
	
	ErrorCode(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
