package com.kpit.ptxcreator.keygen;

import java.security.KeyPair;
import java.security.PrivateKey;

import com.kpit.ptxcreator.exception.PTXCreatorException;

public interface IKeyGen {
	
	/**
	 * Creates a Private key for the supplied byte[] 
	 *
	 * @param privateKey
	 *            - byte[] private key
	 *            
	 * @return - JavaSecurity PrivateKey Object
	 */
	public PrivateKey createPrivateKey(byte[] privateKey) throws PTXCreatorException;

	/**
	 * Randomly generates a Private and Public Key pair
	 *
	 * @return - generates a random Java Security Key Pair
	 */
	public KeyPair keyPairGen() throws PTXCreatorException;

}
