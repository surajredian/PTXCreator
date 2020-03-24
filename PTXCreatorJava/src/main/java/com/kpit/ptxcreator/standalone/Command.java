package com.kpit.ptxcreator.standalone;

import java.util.ArrayList;
import java.util.List;

import com.kpit.ptxcreator.exception.PTXCreatorException;

public enum Command {
	
	createptx(StandalonePTXCreator::createPTX),
	extractptx(StandalonePTXCreator::extractPTX);
	
	private interface CommandExecute {
		void execute(StandaloneInputDTO input, StandaloneResponseDTO response) throws PTXCreatorException;
	}
	
	private CommandExecute cmdExecute;
	
	Command(CommandExecute cmdExecute) {
		this.cmdExecute = cmdExecute;
	}
	
	public static List<String> listCommands() {
		List<String> values = new ArrayList<>();
		for(Command value: Command.values()){
			values.add(value.name());
		}
		return values;
	}
	
	public void execute(StandaloneInputDTO input, StandaloneResponseDTO response) throws PTXCreatorException {
		cmdExecute.execute(input, response);
	}
}
