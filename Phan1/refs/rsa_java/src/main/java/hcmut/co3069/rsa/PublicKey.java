package hcmut.co3069.rsa;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class PublicKey implements Key {
	private BigInteger modulus;
	private BigInteger publicExponent;

	public PublicKey(BigInteger modulus, BigInteger publicExponent) {
		this.modulus = modulus;
		this.publicExponent = publicExponent;
	}

	public BigInteger getModulus() {
		return modulus;
	}

	public BigInteger getPublicExponent() {
		return publicExponent;
	}

	@Override
	public String getAlgorithm() {
		return "RSA";
	}

	@Override
	public String getFormat() {
		return "X.509";
	}

	@Override
	public byte[] getEncoded() {
		try {
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, publicExponent);
			KeyFactory keyFactory = KeyFactory.getInstance(getAlgorithm());
			RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
			return publicKey.getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException("Failed to encode public key", e);
		}

	}

	public void parsePublicKey(byte[] encoded) {
		try {
			KeyFactory kf = KeyFactory.getInstance("RSA");
			RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
			this.modulus = pubKey.getModulus();
			this.publicExponent = pubKey.getPublicExponent();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}
}
