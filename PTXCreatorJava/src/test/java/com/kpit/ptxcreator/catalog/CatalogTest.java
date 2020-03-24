package com.kpit.ptxcreator.catalog;

import java.util.ArrayList;

import org.junit.Test;

import com.kpit.ptxcreator.PTXCreatorDTO;
import com.kpit.ptxcreator.exception.PTXCreatorException;

public class CatalogTest {

	@Test
	public void catalogTest() throws PTXCreatorException {
		try {
			PTXCreatorDTO ptxCreatorDto = new PTXCreatorDTO();
			ArrayList<String> xyz = new ArrayList<>();
			xyz.add("\\otx//");
			ptxCreatorDto.setCompany("KPIT");
			ptxCreatorDto.setProductName("OAS2.0");
			ptxCreatorDto.setProductVersion("1.23");
			ptxCreatorDto.setPtxFile("D:/zipTest/test.ptx");
			// ptxCreatorDto.setCompliance(ComplianceType.ASAM);
			ptxCreatorDto.addProjectDirectoryAndPackages("D:/zipTest/", xyz);
			//ptxCreatorDto.validate();
			CatalogCreatorImpl catalog = new CatalogCreatorImpl(ptxCreatorDto, null);
			System.out.println(catalog.createOutputByteStream().toString());
		} catch (Exception e) {

		}
	}
}
