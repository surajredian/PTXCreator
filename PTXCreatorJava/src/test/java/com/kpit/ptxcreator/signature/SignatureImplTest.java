package com.kpit.ptxcreator.signature;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import org.junit.Test;

import com.kpit.ptxcreator.PTXCreatorDTO;
import com.kpit.ptxcreator.exception.PTXCreatorException;
import com.kpit.ptxcreator.keygen.IKeyGen;
import com.kpit.ptxcreator.keygen.KeyGeneratorFactory;

public class SignatureImplTest {

	@Test
	public void signatureImplTest() {
		try {
			PTXCreatorDTO ptxDto = new PTXCreatorDTO();
			IKeyGen keyGen = KeyGeneratorFactory.createInstance(ptxDto);
			KeyPair keyPair = keyGen.keyPairGen();
			SignatureImpl sign = new SignatureImpl(HashAlgorithm.SHA256, SignAlgorithm.SHA1withRSA,
					keyPair.getPrivate());
			File beforePTX = new File("D:/zipTest/Doc_18_106__1.otx");
			System.out.println("Hash for file Before: " + sign.getHashForStream(new FileInputStream(beforePTX)));
			String hashFile = sign.getHashForStream(new FileInputStream(beforePTX));
			String digestString = hashFile + ":" + beforePTX.getAbsolutePath();
			sign.addContentToSignatureDigest(new ByteArrayInputStream(digestString.getBytes()));
			String signature = sign.createSignature();
			System.out.println("Signature: " + signature);
			
			Base64.getDecoder().decode(signature);
			Signature signTest = Signature.getInstance(SignAlgorithm.SHA1withRSA.getAlgorithm());
			signTest.initVerify(keyPair.getPublic());
			signTest.update(digestString.getBytes());
			System.out.println("Signature: " + signTest.verify(Base64.getDecoder().decode(signature)));
			
			File afterPTX = new File("D:/zipTest/Doc_18_106__1_1.otx");
			System.out.println("Hash for file After : " + sign.getHashForStream(new FileInputStream(afterPTX)));
		} catch (PTXCreatorException | IOException | InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
			e.printStackTrace();
		}
	}
}

