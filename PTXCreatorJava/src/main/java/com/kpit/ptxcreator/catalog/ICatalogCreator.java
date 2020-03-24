package com.kpit.ptxcreator.catalog;

import java.io.ByteArrayOutputStream;

import com.kpit.ptxcreator.exception.PTXCreatorException;

public interface ICatalogCreator {
	
	/**
	 * Create a Byte Output Stream format of the Catalog 
	 *
	 * @return - Byte Output Stream of the Catalog
	 * 
	 */
	public ByteArrayOutputStream createOutputByteStream() throws PTXCreatorException;
}
