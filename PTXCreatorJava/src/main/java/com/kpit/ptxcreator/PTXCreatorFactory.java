package com.kpit.ptxcreator;

public class PTXCreatorFactory {

	public static IPTXCreator createInstance() {
		return new PTXCreatorImpl();
	}
}
