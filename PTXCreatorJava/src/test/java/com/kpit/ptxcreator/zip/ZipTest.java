package com.kpit.ptxcreator.zip;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ZipTest {

	@Test
	public void zipTest() {
		try {
			ZipImpl zip = new ZipImpl();
			Long x = System.currentTimeMillis();
			String targetPTXFileLocation = "D:/zipTest/test.ptx";
			Map<String, List<File>> inputFiles = new HashMap<>();

			List<File> filesToCompress = new ArrayList<>();
			filesToCompress.add(new File("D:/zipTest/otx"));

			List<String> includeExts = new ArrayList<>();
			includeExts.add("otx");

			List<String> excludeExts = new ArrayList<>();
			excludeExts.add("map");

			zip.includeExtensionList(includeExts);
			zip.excludeExtensionList(excludeExts);

			inputFiles.put("xyz/abcd", filesToCompress);
			zip.setPassword("Testing");
			zip.createPTX(targetPTXFileLocation, inputFiles);
			zip.setPassword(null);
			ByteArrayInputStream byteIn = new ByteArrayInputStream("<Test>Testing</Test>".getBytes());
			zip.addStreamToPTX(targetPTXFileLocation, byteIn, "ExampleCatalog.xml", null);
			x = System.currentTimeMillis() - x ;
			System.out.println("Total time : " + x);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
