package com.kpit.ptxcreator.keygen;

import com.kpit.ptxcreator.PTXCreatorDTO;

public class KeyGeneratorFactory {
	
	public static IKeyGen createInstance(PTXCreatorDTO ptxDto) {
		return new KeyGenImpl();
	}
}
