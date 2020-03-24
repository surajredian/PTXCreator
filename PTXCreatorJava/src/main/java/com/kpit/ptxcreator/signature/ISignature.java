package com.kpit.ptxcreator.signature;

import java.io.IOException;
import java.io.InputStream;

import com.kpit.ptxcreator.exception.PTXCreatorException;

public interface ISignature {

	/**
	 * Generates Hash for data in stream based on Provided Hash algorithm
	 * Output format will be Hex
	 * 
	 * @param is - InputStream to be read for which hashing needs to be generated
	 * 
	 * @return - Hash value of the stream in Hex Format
	 * 
	 * @throws IOException
	 */
	public String getHashForStream(InputStream is) throws IOException;
	
	/**
	 * Adds data in stream to digest based on the provided Signature algorithm
	 * 
	 * @param is - InputStream to be read that needs to be added to digest
	 * @throws IOException
	 * @throws PTXCreatorException
	 */
	public void addContentToSignatureDigest(InputStream is) throws IOException, PTXCreatorException;
	
	/**
	 * Creates the signature for the digest accumulated so far in Base64 format
	 *  
	 * @return - creates signature for the digest accumulated so far
	 * 			Returns the signature is Base64 Format
	 * @throws PTXCreatorException
	 */
	public String createSignature() throws PTXCreatorException;
}