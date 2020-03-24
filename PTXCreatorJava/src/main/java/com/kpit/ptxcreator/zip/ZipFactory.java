package com.kpit.ptxcreator.zip;

import com.kpit.ptxcreator.PTXCreatorDTO;

public class ZipFactory {
	
	public static IZip createInstance(PTXCreatorDTO ptxDto) {
		return new ZipImpl();
	}
}
