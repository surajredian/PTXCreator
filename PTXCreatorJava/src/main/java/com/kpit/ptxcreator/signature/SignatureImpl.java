package com.kpit.ptxcreator.signature;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import com.kpit.ptxcreator.exception.ErrorCode;
import com.kpit.ptxcreator.exception.PTXCreatorException;

class SignatureImpl implements ISignature {

	static final int BUFFER_SIZE = 2048;

	// Constructor takes PTX Creator DTO
	final MessageDigest hashDigest;
	final Signature signatureDigest;

	SignatureImpl(HashAlgorithm hashAlgo, SignAlgorithm signAlgo, PrivateKey privateKey) throws PTXCreatorException {
		try {
			hashDigest = MessageDigest.getInstance(hashAlgo.getAlgorithm());
			signatureDigest = Signature.getInstance(signAlgo.getAlgorithm());
			// TODO: Check if SecureRandom can be used here
			signatureDigest.initSign(privateKey);
		} catch (NoSuchAlgorithmException e) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, e, "Hash/Sign Algorithm not found");
		} catch (InvalidKeyException e) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, e, "Invalid Key for Signature");
		}
	}

	public String getHashForStream(InputStream is) throws IOException {
		// Data Chunk used to read
		byte[] byteArray = new byte[BUFFER_SIZE];
		int bytesCount = 0;
		hashDigest.reset();
		
		// Update message digest with file content in chunks
		while ((bytesCount = is.read(byteArray)) != -1) {
			hashDigest.update(byteArray, 0, bytesCount);
		}

		// Convert Digest byte[] to Hex
		byte[] bytes = hashDigest.digest();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	public void addContentToSignatureDigest(InputStream is) throws IOException, PTXCreatorException {
		// Data Chunk used to read
		byte[] byteArray = new byte[BUFFER_SIZE];
		int bytesCount = 0;
		try {
			while ((bytesCount = is.read(byteArray)) != -1) {
				signatureDigest.update(byteArray, 0, bytesCount);
			}
		} catch (SignatureException e) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, e, "Signature not properly initialised ");
		}
	}

	public String createSignature() throws PTXCreatorException {
		try {
			return Base64.getEncoder().encodeToString(signatureDigest.sign());
		} catch (SignatureException e) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, e, "Signature not properly initialised ");
		}
	}
}
