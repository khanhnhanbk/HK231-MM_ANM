package hcmut.co3069.rsa;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

public class HybridCryptoSystem {
	private RSACryptoSystem rsaCryptoSystem;

	public HybridCryptoSystem(int keyBitLength) {
		rsaCryptoSystem = new RSACryptoSystem(keyBitLength);
	}

	public byte[] encryptAESKey(SecretKey aesKey) {
		return rsaCryptoSystem.encrypt(new BigInteger(1, aesKey.getEncoded())).toByteArray();
	}

	public SecretKey decryptAESKey(byte[] encryptedAESKey) {
		// BigInteger decryptedKeyBigInt = rsaCryptoSystem.decrypt(new
		// BigInteger(encryptedAESKey));
		// byte[] decryptedKeyBytes = decryptedKeyBigInt.toByteArray();
		// return new javax.crypto.spec.SecretKeySpec(decryptedKeyBytes, "AES");
		BigInteger decryptedKeyBigInt = rsaCryptoSystem.decrypt(new BigInteger(encryptedAESKey));
		byte[] decryptedKeyBytes = decryptedKeyBigInt.toByteArray();
		//System.out.println(decryptedKeyBytes.length);
		if (decryptedKeyBytes.length > 16) {
			decryptedKeyBytes = Arrays.copyOfRange(decryptedKeyBytes, decryptedKeyBytes.length - 16,
					decryptedKeyBytes.length);
		}
		return new javax.crypto.spec.SecretKeySpec(decryptedKeyBytes, "AES");
	}

	public void encryptFile(Path inputFile, Path outputFile, SecretKey aesKey)
			throws IOException, GeneralSecurityException {
		byte[] fileContent = Files.readAllBytes(inputFile);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]); // Assuming a 16-byte IV
		cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivParameterSpec);
		byte[] encryptedContent = cipher.doFinal(fileContent);
		Files.write(outputFile, encryptedContent);
	}

	public void decryptFile(Path inputFile, Path outputFile, SecretKey aesKey)
			throws IOException, GeneralSecurityException {
		byte[] encryptedContent = Files.readAllBytes(inputFile);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]); // Assuming a 16-byte IV
		cipher.init(Cipher.DECRYPT_MODE, aesKey, ivParameterSpec);
		byte[] decryptedContent = cipher.doFinal(encryptedContent);
		Files.write(outputFile, decryptedContent);
	}

	public SecretKey generateAESKey(int keySize) throws GeneralSecurityException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(keySize, new SecureRandom());
		return keyGenerator.generateKey();
	}
}
