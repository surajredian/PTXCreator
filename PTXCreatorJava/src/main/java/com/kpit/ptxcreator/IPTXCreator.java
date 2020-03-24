package com.kpit.ptxcreator;

import java.security.KeyPair;

import com.kpit.ptxcreator.exception.PTXCreatorException;

public interface IPTXCreator {

	/**
	 * Generates the PTX File based on information provided in the DTO
	 *
	 * @param ptxCreatorDTO
	 *            - DTO based on which PTX is generated
	 * @return - Java Security Key Pair, This will be null if Private key was
	 *         provided in ptxCreatorDTO. Else it will contain both a generated
	 *         Public Key and Private Key
	 * @exception - throws PTXCreatorException
	 * @see <a href="https://conf-muc.kpit.com/display/OTXSuite/PTX+Creator+Java+Library">
	 * 		Refer here for more details</a>
	 */
	public KeyPair generatePTX(PTXCreatorDTO ptxCreatorDTO) throws PTXCreatorException;
	
	/**
	 * Extracts a PTX based on information provided
	 * 
	 * @param ptxExtractDTO - DTO based on which PTX is extracted
	 * @throws PTXCreatorException
	 * @see <a href="https://conf-muc.kpit.com/display/OTXSuite/PTX+Creator+Java+Library">
	 * 		Refer here for more details</a>
	 */
	public void extractPTX(PTXExtractorDTO ptxExtractDTO) throws PTXCreatorException;
}
