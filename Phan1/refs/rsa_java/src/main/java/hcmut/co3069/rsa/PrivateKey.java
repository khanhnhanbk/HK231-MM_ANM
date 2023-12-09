package hcmut.co3069.rsa;

import java.math.BigInteger;
import java.util.ArrayList;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class PrivateKey implements Key {

	private BigInteger modulus;
	private BigInteger privateExponent;

	public PrivateKey(BigInteger modulus, BigInteger privateExponent) {
		this.modulus = modulus;
		this.privateExponent = privateExponent;
	}

	public BigInteger getModulus() {
		return modulus;
	}

	public BigInteger getPrivateExponent() {
		return privateExponent;
	}

	@Override
	public String getAlgorithm() {
		return "RSA";
	}

	@Override
	public String getFormat() {
		return "PKCS#8";
	}

	@Override
	public byte[] getEncoded() {
		try {
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modulus, privateExponent);
			KeyFactory keyFactory = KeyFactory.getInstance(getAlgorithm());
			RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
			return privateKey.getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException("Failed to encode private key", e);
		}
	}

	public void parsePrivateKey(byte[] encoded) {
		try {
			KeyFactory kf = KeyFactory.getInstance("RSA");
			RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(encoded));
			this.modulus = privKey.getModulus();
			this.privateExponent = privKey.getPrivateExponent();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}
}