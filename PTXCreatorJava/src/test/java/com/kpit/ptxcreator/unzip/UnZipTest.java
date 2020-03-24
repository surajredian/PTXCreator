package com.kpit.ptxcreator.unzip;

import org.junit.Test;

public class UnZipTest {
	@Test
	public void unZipTest() {
		try {
			IUnZip unZip = new UnZipImpl();
			String targetPTXFile = "D:/zipTest/StandaloneCreated.ptx";
			String locationToExtract = "D:/zipTest/";
			String targetProjectName = null;
			unZip.setPassword("Testing");
			unZip.extractPTX(targetPTXFile, locationToExtract, targetProjectName);
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
	}
}
