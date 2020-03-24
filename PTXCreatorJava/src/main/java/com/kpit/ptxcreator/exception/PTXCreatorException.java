package com.kpit.ptxcreator.exception;

/**
 * 
 * Custom Exception for PTX Creator
 * It can be used to transform an existing exception to a PTX Creator Exception
 * and still track the source exception along with an exception code
 * 
 */
public class PTXCreatorException extends Exception {

	private static final long serialVersionUID = 1244979763787894532L;
	
	protected ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
	protected Throwable sourceException = null;
	protected String additionalMessage = "";
	private final int MAX_STACK_LENGTH = 15;
	

	public PTXCreatorException(ErrorCode errorCode) {
		this(errorCode, null, null);
	}

	public PTXCreatorException(ErrorCode errorCode, Throwable originalException) {
		this(errorCode, originalException, null);

	}

	public PTXCreatorException(ErrorCode errorCode, String additionalMessage) {
		this(errorCode, null, additionalMessage);

	}

	public PTXCreatorException(ErrorCode errorCode, Throwable originalException, String additionalMessage) {
		if (null != errorCode) {
			this.errorCode = errorCode;
		}
		if (null != originalException) {
			this.sourceException = originalException;
		}
		if (null != additionalMessage && !additionalMessage.isEmpty()) {
			this.additionalMessage = additionalMessage;
		}
	}
	
	@Override
	public String getMessage() {
		String message = errorCode.getDescription();
		if(!additionalMessage.isEmpty()) {
			message += ":" + additionalMessage;
		}
		if(null != sourceException) {
			message += "\n[\nSourceExceptionMessage: " + sourceException.getMessage();
			message += "\n";
			int length = sourceException.getStackTrace().length;
			length = (length > this.MAX_STACK_LENGTH) ? this.MAX_STACK_LENGTH : length;
			for(int i = 0; i < length; i++) {
				message += "\t" + sourceException.getStackTrace()[i] + "\n";
			}
			message += "]";
		}
		return message;
	}
	
	public String getErrorCode() {
		return this.errorCode.getCode();
	}
}
