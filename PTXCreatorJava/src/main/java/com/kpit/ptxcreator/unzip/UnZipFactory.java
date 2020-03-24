package com.kpit.ptxcreator.unzip;

import com.kpit.ptxcreator.PTXExtractorDTO;

public class UnZipFactory {

	public static IUnZip createInstance(PTXExtractorDTO ptxDto) {
		return new UnZipImpl();
	}
}
