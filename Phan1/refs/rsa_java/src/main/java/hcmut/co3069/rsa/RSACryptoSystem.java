package hcmut.co3069.rsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

import javax.crypto.SecretKey;

public class RSACryptoSystem {

	private PublicKey publicKey;
	private PrivateKey privateKey;
	private BigInteger bitLength;

	public RSACryptoSystem(int keyBitLength) {
		SecureRandom random = new SecureRandom();
		bitLength = BigInteger.valueOf(keyBitLength);

		// Generate p and q, two distinct strong primes
		 BigInteger p = StrongPrimeGenerator.generate(keyBitLength / 2);
		 BigInteger q = StrongPrimeGenerator.generate(keyBitLength / 2);

		// Calculate n = p * q
		BigInteger n = p.multiply(q);

		// Calculate phi(n) = (p - 1) * (q - 1)
		BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

		// Choose e such that 1 < e < phi(n) and gcd(e, phi(n)) = 1
		BigInteger e;
		do {
			e = new BigInteger(phi.bitLength(), random);
		} while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0 || !Math.gcd(e, phi).equals(BigInteger.ONE));

		// Calculate d = e^(-1) mod phi(n)
		BigInteger d = Math.modInverse(e, phi);

		publicKey = new PublicKey(n, e);
		privateKey = new PrivateKey(n, d);
	}

	public BigInteger encrypt(BigInteger message) {
		return Math.modPow(message, publicKey.getPublicExponent(), publicKey.getModulus());
	}

	public BigInteger decrypt(BigInteger encryptedMessage) {
		return Math.modPow(encryptedMessage, privateKey.getPrivateExponent(), privateKey.getModulus());
	}

	public void generateKeyFiles() {
		System.out.println("Generating public and private keys...");

		// Write to file
		String pubKeyFile = "public.der";
		try {
			// if file doesnt exists, then create it
			File file = new File(pubKeyFile);
			if (!file.exists()) {
				file.createNewFile();
				// print file path
			}

			FileOutputStream fos = new FileOutputStream(pubKeyFile);
			fos.write(publicKey.getEncoded());
			fos.close();
		} catch (Exception e) {
			System.out.println("Error writing public key to file");
			e.printStackTrace();
		}

		String prvKeyFile = "private.der";
		try {

			File file = new File(prvKeyFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(prvKeyFile);
			fos.write(privateKey.getEncoded());
			fos.close();
		} catch (Exception e) {
			System.out.println("Error writing private key to file");
			e.printStackTrace();
		}
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("Usage:");
			System.out.println("-example <n> : runs prewritten example");
			System.out.println("-generate : generates public and private keys");
			System.out.println("-encrypt <input> <output> : encrypt a file");
			System.out.println("-decrypt <input> <output> : decrypt a file");
			return;
		}

		// CLI application
		/**
		 * Options:
		 * -example <n> : runs prewritten example
		 * -generate : generates public and private keys
		 * -encrypt <input> <output> <public_key> : encrypt a file
		 * -decrypt <input> <output> <private_key> : decrypt a file
		 * 
		 */

		RSACryptoSystem rsa = new RSACryptoSystem(2048);
		String option = args[0];

		switch (option) {
			case "-example":
				runExamples(args, rsa);
				break;
			case "-generate":
				rsa.generateKeyFiles();
				break;
			case "-encrypt":
				startEncrypt(args, rsa);
				break;
			case "-decrypt":
				startDecrypt(args, rsa);
				break;
			default:
				System.out.println("Invalid option.");
				break;
		}

	}

	private static void runExamples(String[] args, RSACryptoSystem rsa) {
		if (args.length < 2) {
			System.out.println("Please specify an example number.");
			return;
		}
		//rsa.generateKeyFiles();
		int exampleNumber = Integer.parseInt(args[1]);

		switch (exampleNumber) {
			case 1:
				example1(rsa);
				break;
			case 2:
				example2(rsa);
				break;
			case 3:
				example3(rsa);
				break;
			case 4:
				example4(rsa);
				break;

			default:
				System.out.println("Invalid number.");
				break;
		}

	}

	private static void startEncrypt(String[] args, RSACryptoSystem rsa) {
		if (args.length < 4) {
			System.out.println("Please specify input and output files.");
			return;
		}
		//rsa.generateKeyFiles();
		String inputFile = args[1];
		String outputFile = args[2];
		String pubKeyFile = args[3];

		// read file
		byte[] fileBytes = null;
		try {
			fileBytes = Files.readAllBytes(Paths.get(inputFile));
		} catch (IOException e) {
			System.out.println("Error reading file.");
			e.printStackTrace();
		}

		// read public key
		try {
			File file = new File(pubKeyFile);
			FileInputStream fis = new FileInputStream(file);
			byte[] encodedPublicKey = new byte[(int) file.length()];
			fis.read(encodedPublicKey);
			fis.close();
			rsa.getPublicKey().parsePublicKey(encodedPublicKey);
		} catch (Exception e) {
			System.out.println("Error reading private key from file");
			e.printStackTrace();
		}
		

		// encrypt
		BigInteger encrypted = rsa.encrypt(new BigInteger(fileBytes));

		// write to file
		try {
			// if file doesnt exists, then create it
			File file = new File(outputFile);
			if (!file.exists()) {
				file.createNewFile();
				// print file path
				System.out.println("Encrypted file created: " + file.getAbsolutePath());
			}

			FileOutputStream fos = new FileOutputStream(outputFile);
			fos.write(encrypted.toByteArray());
			fos.close();
		} catch (Exception e) {
			System.out.println("Error writing to file");
			e.printStackTrace();
		}
	}

	private static void startDecrypt(String[] args, RSACryptoSystem rsa) {
		if (args.length < 3) {
			System.out.println("Please specify input and output files.");
			return;
		}
		String inputFile = args[1];
		String outputFile = args[2];
		String prvKeyFile = args[3];

		// read private key
		try {
			File file = new File(prvKeyFile);
			FileInputStream fis = new FileInputStream(file);
			byte[] encodedPrivateKey = new byte[(int) file.length()];
			fis.read(encodedPrivateKey);
			fis.close();
			rsa.getPrivateKey().parsePrivateKey(encodedPrivateKey);
		} catch (Exception e) {
			System.out.println("Error reading private key from file");
			e.printStackTrace();
		}

		// read file
		byte[] fileBytes = null;
		try {
			fileBytes = Files.readAllBytes(Paths.get(inputFile));
		} catch (IOException e) {
			System.out.println("Error reading file.");
			e.printStackTrace();
		}

		// decrypt
		BigInteger decrypted = rsa.decrypt(new BigInteger(fileBytes));

		// write to file
		try {
			// if file doesnt exists, then create it
			File file = new File(outputFile);
			if (!file.exists()) {
				file.createNewFile();
				// print file path
				System.out.println("Decrypted file created: " + file.getAbsolutePath());
			}

			FileOutputStream fos = new FileOutputStream(outputFile);
			fos.write(decrypted.toByteArray());
			fos.close();
		} catch (Exception e) {
			System.out.println("Error writing to file");
			e.printStackTrace();
		}
	}

	private static void example1(RSACryptoSystem rsa) {

		/**
		 * Example 1: Encrypt and decrypt a message
		 */

		String message = "Hello, RSA!";
		BigInteger encrypted = rsa.encrypt(new BigInteger(message.getBytes()));
		BigInteger decrypted = rsa.decrypt(encrypted);
		System.out.println("Original message: " + message);
		System.out.println("Encrypted: " + encrypted);
		System.out.println("Decrypted: " + new String(decrypted.toByteArray()));

	}

	private static void example2(RSACryptoSystem rsa) {
		/**
		 * Example 2: Encrypt and decrypt a file
		 */

		String inputFileName = "input.txt";
		String encryptedFileName = "encrypted.txt";
		String decryptedFileName = "decrypted.txt";

		// Read input file
		try (
				FileInputStream fis = new FileInputStream(inputFileName);
				FileOutputStream fos = new FileOutputStream(encryptedFileName);) {
			BigInteger message = new BigInteger(fis.readAllBytes());
			BigInteger encrypted = rsa.encrypt(message);
			fos.write(encrypted.toByteArray());
		} catch (Exception e) {
			System.out.println("Error reading input file");
			e.printStackTrace();
		}

		// Read encrypted file
		try (
				FileInputStream fis = new FileInputStream(encryptedFileName);
				FileOutputStream fos = new FileOutputStream(decryptedFileName);) {
			BigInteger encrypted = new BigInteger(fis.readAllBytes());
			BigInteger decrypted = rsa.decrypt(encrypted);
			fos.write(decrypted.toByteArray());
		} catch (Exception e) {
			System.out.println("Error reading encrypted file");
			e.printStackTrace();

		}
		// Print content of input file
		try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
			String line;
			System.out.println("Content of input file:");
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			System.out.println("Error reading input file");
			e.printStackTrace();
		}

		// Print content of decrypted file
		try (BufferedReader br = new BufferedReader(new FileReader(decryptedFileName))) {
			String line;
			System.out.println("Decrypted file:");
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			System.out.println("Error reading input file");
			e.printStackTrace();
		}
	}

	private static void example3(RSACryptoSystem rsa) {
		/**
		 * Example 3: Encrypt and decrypt a file using public and private key files
		 */

		String pubKeyName = "public.der";
		String prvKeyName = "private.der";
		String inputFileName = "input.txt";
		String encryptedFileName = "encrypted.bin";
		String decryptedFileName = "decrypted.txt";

		// Read public key file
		try {
			FileInputStream fis = new FileInputStream(pubKeyName);
			byte[] pubKeyBytes = fis.readAllBytes();
			fis.close();

			rsa.getPublicKey().parsePublicKey(pubKeyBytes);
		} catch (Exception e) {
			System.out.println("Error reading public key file");
			e.printStackTrace();
		}

		// Read private key file
		try {
			FileInputStream fis = new FileInputStream(prvKeyName);
			byte[] prvKeyBytes = fis.readAllBytes();
			fis.close();

			rsa.getPrivateKey().parsePrivateKey(prvKeyBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Read input file
		try (
				FileInputStream fis = new FileInputStream(inputFileName);
				FileOutputStream fos = new FileOutputStream(encryptedFileName);) {
			BigInteger message = new BigInteger(fis.readAllBytes());
			BigInteger encrypted = rsa.encrypt(message);
			fos.write(encrypted.toByteArray());
		} catch (Exception e) {
			System.out.println("Error reading input file");
			e.printStackTrace();
		}

		// Read encrypted file
		try (
				FileInputStream fis = new FileInputStream(encryptedFileName);
				FileOutputStream fos = new FileOutputStream(decryptedFileName);) {
			BigInteger encrypted = new BigInteger(fis.readAllBytes());
			BigInteger decrypted = rsa.decrypt(encrypted);
			fos.write(decrypted.toByteArray());
		} catch (Exception e) {
			System.out.println("Error reading encrypted file");
			e.printStackTrace();
		}

		// Print content of input file
		try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
			String line;
			System.out.println("Content of input file:");
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			System.out.println("Error reading input file");
			e.printStackTrace();
		}

		// Print content of decrypted file
		try (BufferedReader br = new BufferedReader(new FileReader(decryptedFileName))) {
			String line;
			System.out.println("Decrypted file:");
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			System.out.println("Error reading input file");
			e.printStackTrace();
		}
	}

	private static void example4(RSACryptoSystem rsa) {
		/**
		 * Example 4: Encrypt and decrypt a file using AES RSA hybrid cryptosystem
		 */

		HybridCryptoSystem hybridCryptoSystem = new HybridCryptoSystem(2048);
		SecretKey aesKey = null;
		try {
			aesKey = hybridCryptoSystem.generateAESKey(128); // Generate a 128-bit AES key
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Encrypt the AES key
		byte[] encryptedAESKey = hybridCryptoSystem.encryptAESKey(aesKey);
		System.out.println(encryptedAESKey.length);

		// Encrypt the file
		Path inputFile = Path.of("1GB.bin");
		Path encryptedFile = Path.of("encrypted.bin");
		try {
			hybridCryptoSystem.encryptFile(inputFile, encryptedFile, aesKey);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Decrypt the AES key
		SecretKey decryptedAESKey = hybridCryptoSystem.decryptAESKey(encryptedAESKey);

		// Decrypt the file
		Path decryptedFile = Path.of("decrypted.bin");
		try {
			hybridCryptoSystem.decryptFile(encryptedFile, decryptedFile,
					decryptedAESKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
