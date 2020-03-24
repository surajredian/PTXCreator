package com.kpit.ptxcreator.signature;

import java.security.PrivateKey;

import com.kpit.ptxcreator.PTXCreatorDTO;
import com.kpit.ptxcreator.exception.PTXCreatorException;

public class SignatureFactory {
	
	public static ISignature createInstance(PTXCreatorDTO ptxDto, PrivateKey prvKey) throws PTXCreatorException {
		return new SignatureImpl(ptxDto.getHashAlgo(), ptxDto.getSignAlgo(), prvKey);
	}
}
