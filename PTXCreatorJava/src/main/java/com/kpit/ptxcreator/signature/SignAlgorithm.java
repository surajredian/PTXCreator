package com.kpit.ptxcreator.signature;

/**
 * Maintains a list of supported Signature Algorithms
 *
 */
public enum SignAlgorithm {
	// TODO: Support more algorithms
	SHA1withRSA("SHA256withRSA");

	private String algorithm;

	SignAlgorithm(String algo) {
		algorithm = algo;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	// TODO: place holder to directly arrive at algorithm based on string
	// Needed when mechanism is exposed to stand alone
	public static SignAlgorithm getValidAlgorithm(String algo) {
		return null;
	}
}
