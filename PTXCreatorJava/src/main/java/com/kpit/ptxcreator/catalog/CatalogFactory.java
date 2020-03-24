package com.kpit.ptxcreator.catalog;

import java.security.PrivateKey;

import com.kpit.ptxcreator.PTXCreatorDTO;
import com.kpit.ptxcreator.exception.PTXCreatorException;

public class CatalogFactory {

	public static ICatalogCreator createInstance(PTXCreatorDTO ptxDto, PrivateKey prvKey) throws PTXCreatorException {
		return new CatalogCreatorImpl(ptxDto, prvKey);
	}
}
