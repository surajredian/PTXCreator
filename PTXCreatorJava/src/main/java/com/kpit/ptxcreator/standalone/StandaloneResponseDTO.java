package com.kpit.ptxcreator.standalone;

/**
 * 
 * Defining how the response object for stand alone will be
 * Since the object will undergo a simple GSON transform
 * there are no getters
 *
 */
@SuppressWarnings("unused")
public class StandaloneResponseDTO {

	public static class KeyDetails {
		private String key;
		private String keyFormat;
		private String keyAlgorithm;

		public void setKey(String key) {
			this.key = key;
		}

		public void setKeyFormat(String keyFormat) {
			this.keyFormat = keyFormat;
		}

		public void setKeyAlgorithm(String keyAlgorithm) {
			this.keyAlgorithm = keyAlgorithm;
		}
	}

	private boolean success;
	private String message;
	private String errorCode;
	private String errMessage;
	private KeyDetails publicKey;
	private KeyDetails privateKey;

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

	public void setPublicKey(KeyDetails publicKey) {
		this.publicKey = publicKey;
	}

	public void setPrivateKey(KeyDetails privateKey) {
		this.privateKey = privateKey;
	}

}
