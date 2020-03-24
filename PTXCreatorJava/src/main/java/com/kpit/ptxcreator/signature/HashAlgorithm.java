package com.kpit.ptxcreator.signature;

/**
 * Maintains a list of supported File Hashing Algorithms
 *
 */
public enum HashAlgorithm {
	// TODO: Support more algorithms
	SHA256("SHA-256"), 
	MD5("MD5");

	private String algorithm;

	HashAlgorithm(String algo) {
		algorithm = algo;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	// TODO: place holder to directly arrive at algorithm based on string
	// Needed when mechanism is exposed to stand alone
	public static HashAlgorithm getValidAlgorithm(String algo) {
		return null;
	}
}
