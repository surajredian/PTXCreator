package com.kpit.ptxcreator;

import java.util.ArrayList;

import org.junit.Test;

import com.kpit.ptxcreator.exception.PTXCreatorException;

public class PTXCreatorDummyUnitTests {

	@Test
	public void DTOTests() {
		PTXCreatorDTO ptxCreatorDto = new PTXCreatorDTO();
		ArrayList<String> xyz = new ArrayList<>();
		xyz.add("\\otx/dcts");
		try {
			ptxCreatorDto.addProjectDirectoryAndPackages("D:/zipTest/", xyz);
		} catch (PTXCreatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
