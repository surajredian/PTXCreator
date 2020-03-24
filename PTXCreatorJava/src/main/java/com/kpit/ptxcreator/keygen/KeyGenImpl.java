package com.kpit.ptxcreator.keygen;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import com.kpit.ptxcreator.exception.ErrorCode;
import com.kpit.ptxcreator.exception.PTXCreatorException;

class KeyGenImpl implements IKeyGen {

	// TODO: Expose these constant to become optional user input
	private static final int KEY_SIZE = 512;
	private static final String KEY_GEN_ALGORITHM = "RSA";

	public PrivateKey createPrivateKey(byte[] privateKey) throws PTXCreatorException {
		try {
			KeyFactory kf = KeyFactory.getInstance(KEY_GEN_ALGORITHM);
			return kf.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
		} catch (NoSuchAlgorithmException e) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, e, "Invalid Key Pair Generator Algorithm");
		} catch (InvalidKeySpecException e) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR, e, "Invalid Private Key Passed");
		}
	}

	public KeyPair keyPairGen() throws PTXCreatorException {
		try {
			KeyPairGenerator pairGen = KeyPairGenerator.getInstance(KEY_GEN_ALGORITHM);
			// TODO: Add SecureRandom here for stronger encryption
			pairGen.initialize(KEY_SIZE);
			KeyPair pair = pairGen.generateKeyPair();
			return pair;
		} catch (NoSuchAlgorithmException e) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, e, "Invalid Key Pair Generator Algorithm");
		}
	}
}
