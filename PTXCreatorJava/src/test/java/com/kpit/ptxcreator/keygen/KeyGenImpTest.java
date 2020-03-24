package com.kpit.ptxcreator.keygen;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.junit.Test;

import com.kpit.ptxcreator.exception.PTXCreatorException;

public class KeyGenImpTest {
	
	@Test
	public void keyGenTest() {
		try {
			KeyGenImpl keyGen = new KeyGenImpl();
			KeyPair keyPair = keyGen.keyPairGen();
			PrivateKey prv = keyPair.getPrivate();
			PublicKey pub = keyPair.getPublic();
			
			String pubKey = Base64.getEncoder().encodeToString(pub.getEncoded());
			String prvKey = Base64.getEncoder().encodeToString(prv.getEncoded());
			System.out.println(pubKey);
			System.out.println(prvKey);
			
			KeyFactory kf = KeyFactory.getInstance("RSA");
			kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(prvKey)));
			kf.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(pubKey)));
			System.out.println("Success");
		} catch (PTXCreatorException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
