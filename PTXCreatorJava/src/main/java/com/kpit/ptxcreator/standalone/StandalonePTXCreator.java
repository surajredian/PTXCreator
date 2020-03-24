package com.kpit.ptxcreator.standalone;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.security.KeyPair;
import java.util.Base64;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kpit.ptxcreator.PTXCreatorFactory;
import com.kpit.ptxcreator.exception.ErrorCode;
import com.kpit.ptxcreator.exception.PTXCreatorException;
import com.kpit.ptxcreator.standalone.StandaloneResponseDTO.KeyDetails;

public class StandalonePTXCreator {

	public static void main(String[] args) {
		StandaloneResponseDTO response = new StandaloneResponseDTO();
		Gson gson = new Gson();
		long startTime = System.currentTimeMillis();
		try {
			// Mandating at least 1 argument as part of input
			if (args.length == 0) {
				response.setSuccess(false);
				response.setErrorCode("100");
				response.setMessage("Please pass the required JSON parameter");
				//Delivering failure through standard output
				System.out.print(gson.toJson(response));
				return;
			}

			//Stand alone Request construction
			final Type collectionType = new TypeToken<StandaloneInputDTO>() {
			}.getType();
			StandaloneInputDTO input = gson.fromJson(args[0], collectionType);
			if (null != input.getFileName() && input.getFileName().trim().length() > 0) {
				Scanner scan = new Scanner(new File(input.getFileName()));
				String jsonContent = scan.useDelimiter("\\Z").next();
				scan.close();
				input = gson.fromJson(jsonContent, collectionType);
			}
			
			if(null == input.getCommand()) {
				throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR, "Unknown Command, support commands are :" 
							+ Command.listCommands().toString());
			}
			
			input.getCommand().execute(input, response);

			response.setSuccess(true);
			response.setMessage("Time Taken:" + (System.currentTimeMillis() - startTime));
		} catch (PTXCreatorException e) {
			response.setSuccess(false);
			response.setErrorCode("030");
			response.setMessage("Error in Creating PTX");
			response.setErrMessage(e.getMessage());
		} catch (FileNotFoundException e) {
			response.setSuccess(false);
			response.setErrorCode("020");
			response.setMessage("Given file not accessible");
			response.setErrMessage(e.getMessage());
		} catch (JsonSyntaxException e) {
			response.setSuccess(false);
			response.setErrorCode("010");
			response.setMessage("Input Json Incorrect format");
			response.setErrMessage(e.getMessage());
		} catch (Exception e) {
			response.setSuccess(false);
			response.setErrorCode("001");
			response.setMessage("Unhandled Exception");
			response.setErrMessage(e.getMessage());
		}

		//Delivering response through standard output
		System.out.print(gson.toJson(response));
	}
	
	public static void createPTX(StandaloneInputDTO input, StandaloneResponseDTO response) throws PTXCreatorException {
		//Invoke PTX Creation
		KeyPair keyPair = PTXCreatorFactory.createInstance().generatePTX(input.convertToPTXCreatorDTO());

		//Response construction
		if (null != keyPair) {
			KeyDetails prvKey = new KeyDetails();
			prvKey.setKeyAlgorithm(keyPair.getPrivate().getAlgorithm());
			prvKey.setKeyFormat(keyPair.getPrivate().getFormat());
			prvKey.setKey(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));

			KeyDetails pubKey = new KeyDetails();
			pubKey.setKeyAlgorithm(keyPair.getPublic().getAlgorithm());
			pubKey.setKeyFormat(keyPair.getPublic().getFormat());
			pubKey.setKey(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));

			response.setPrivateKey(prvKey);
			response.setPublicKey(pubKey);
		}
	}
	
	public static void extractPTX(StandaloneInputDTO input, StandaloneResponseDTO response) throws PTXCreatorException {
		PTXCreatorFactory.createInstance().extractPTX(input.convertToPTXExtractorDTO());
	}
}
