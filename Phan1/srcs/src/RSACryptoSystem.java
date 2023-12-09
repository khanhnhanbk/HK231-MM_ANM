import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RSACryptoSystem {
  /*
   * This class implements the RSA crypto system
   */
  private static PublicKey publicKey = new PublicKey();
  private static PrivateKey privateKey = new PrivateKey();

  public static BigInteger encrypt(BigInteger message) {
    return MyMath.modPow(message, publicKey.getPublicExponent(), publicKey.getModulus());
  }

  public static BigInteger decrypt(BigInteger encryptedMessage) {
    return MyMath.modPow(encryptedMessage, privateKey.getPrivateExponent(), privateKey.getModulus());
  }

  public static void generateKeys(int bitLength) {

    // Generate p and q, two distinct strong primes
    BigInteger p = StrongPrimeGenerator.generate(bitLength / 2);
    BigInteger q = StrongPrimeGenerator.generate(bitLength / 2);

    // Calculate n = p * q
    BigInteger n = p.multiply(q);

    // Calculate phi(n) = (p - 1) * (q - 1)
    BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

    // Choose e such that 1 < e < phi(n) and gcd(e, phi(n)) = 1
    BigInteger e;
    do {
      e = MyMath.randomRange(BigInteger.TWO, phi.subtract(BigInteger.ONE));
    } while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0 || !MyMath.gcd(e, phi).equals(BigInteger.ONE));

    // Calculate d = e^(-1) mod phi(n)
    BigInteger d = MyMath.modInverse(e, phi);

    publicKey = new PublicKey(n, e);
    privateKey = new PrivateKey(n, d);

  }

  public static void saveKeys(int bitLength) {
    System.out.println("Generating public and private keys...");
    generateKeys(bitLength);
    // Write public key to file
    try {
      File file = new File("public.der");
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(publicKey.getEncoded());
      fos.close();
    } catch (Exception err) {
      System.out.println("Error writing public key to file");
      err.printStackTrace();
    }
    // Write private key to file
    try {
      File file = new File("private.der");
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(privateKey.getEncoded());
      fos.close();
    } catch (Exception err) {
      System.out.println("Error writing private key to file");
      err.printStackTrace();
    }
  }

  public static void printPublicKey(String filename) {
    try {
      File file = new File(filename);
      FileInputStream fis = new FileInputStream(file);
      byte[] encodedPublicKey = new byte[(int) file.length()];
      fis.read(encodedPublicKey);
      fis.close();
      PublicKey publicKey = new PublicKey();
      publicKey.parsePublicKey(encodedPublicKey);
      System.out.println("n: " + publicKey.getModulus());
      System.out.println("e: " + publicKey.getPublicExponent());
    } catch (Exception e) {
      System.out.println("Error reading private key from file");
      e.printStackTrace();
    }
  }

  public static void printPrivateKey(String filename) {
    try {
      File file = new File(filename);
      FileInputStream fis = new FileInputStream(file);
      byte[] encodedPrivateKey = new byte[(int) file.length()];
      fis.read(encodedPrivateKey);
      fis.close();
      PrivateKey privateKey = new PrivateKey();
      privateKey.parsePrivateKey(encodedPrivateKey);
      System.out.println("n: " + privateKey.getModulus());
      System.out.println("d: " + privateKey.getPrivateExponent());
    } catch (Exception e) {
      System.out.println("Error reading private key from file");
      e.printStackTrace();
    }
  }

  public static void startEncrypt(String[] args) {
    if (args.length < 4) {
      System.out.println("Please specify input and output files.");
      return;
    }
    String inputFile = args[1];
    String outputFile = args[2];
    String pubKeyFile = args[3];

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

      publicKey.parsePublicKey(encodedPublicKey);

    } catch (Exception e) {
      System.out.println("Error reading public key from file");
      e.printStackTrace();
    }

    // encrypt
    BigInteger encrypted = encrypt(new BigInteger(fileBytes));
    System.out.println("Encrypted: " + encrypted);

    // write to file
    try {
      File file = new File(outputFile);
      if (!file.exists()) {
        file.createNewFile();
        System.out.println("Encrypted file created: " + file.getAbsolutePath());
      }

      FileOutputStream fos = new FileOutputStream(outputFile);
      fos.write(encrypted.toByteArray());
      fos.close();
      System.out.println("Encrypted file written successfully." + file.getAbsolutePath());
    } catch (Exception e) {
      System.out.println("Error writing to file");
      e.printStackTrace();
    }
  }

  public static void startDecrypt(String[] args) {
    if (args.length < 4) {
      System.out.println("java App -decrypt <input> <output> <privatekeyfile>");
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
      privateKey.parsePrivateKey(encodedPrivateKey);
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
    BigInteger decrypted = decrypt(new BigInteger(fileBytes));
    System.out.println("Decrypted: " + new String(decrypted.toByteArray()));

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
}
