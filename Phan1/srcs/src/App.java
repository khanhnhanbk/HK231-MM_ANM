import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.math.BigInteger;

public class App {

  public static void main(String[] args) {

    if (args.length == 0) {
      System.out.println("Usage:");
      System.out.println("-generate : generates public and private keys");
      System.out.println("-viewpub <publickeyfile>: view public key");
      System.out.println("-viewpriv <privatekeyfile>: view private key");
      System.out.println("-encrypt <input> <output> <publickeyfile> : encrypt a file");
      System.out.println("-decrypt <input> <output> <privatekeyfile> : decrypt a file");
      System.out.println("-example n : run example n");
      return;
    }

    String option = args[0].toLowerCase();

    switch (option) {
      // case "-example":
      // runExamples(args, rsa);
      // break;
      case "-generate":
      case "-gen":
        RSACryptoSystem.saveKeys(2048);
        break;
      case "-viewpub":
        RSACryptoSystem.printPublicKey(args[1]);
        break;
      case "-viewpriv":
        RSACryptoSystem.printPrivateKey(args[1]);
        break;
      case "-encrypt":
        RSACryptoSystem.startEncrypt(args);
        break;
      case "-decrypt":
        RSACryptoSystem.startDecrypt(args);
        break;
      case "-example":
        int example = Integer.parseInt(args[1]);
        switch (example) {
          case 1:
            example1();
            break;
          case 2:
            example2();
            break;
          case 3:
            example3();
            break;
          default:
            System.out.println("Invalid example number.");
            break;
        }
        break;
      default:
        System.out.println("Invalid option.");
        break;
    }

  }

  private static void example1() {

    /**
     * Example 1: Encrypt and decrypt a message
     */

    String message = "Hello, RSA!";
    RSACryptoSystem.generateKeys(1024);
    BigInteger encrypted = RSACryptoSystem.encrypt(new BigInteger(message.getBytes()));
    BigInteger decrypted = RSACryptoSystem.decrypt(encrypted);
    System.out.println("Original message: " + message);
    System.out.println("Encrypted: " + encrypted);
    System.out.println("Decrypted: " + new String(decrypted.toByteArray()));

  }

  private static void example2() {
    /**
     * Example 2: Encrypt and decrypt a file
     */

    String inputFileName = "input.txt";
    String encryptedFileName = "encrypted.txt";
    String decryptedFileName = "decrypted.txt";
    RSACryptoSystem.generateKeys(1024);
    // Read input file
    try (
        FileInputStream fis = new FileInputStream(inputFileName);
        FileOutputStream fos = new FileOutputStream(encryptedFileName);) {
      BigInteger message = new BigInteger(fis.readAllBytes());
      BigInteger encrypted = RSACryptoSystem.encrypt(message);
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
      BigInteger decrypted = RSACryptoSystem.decrypt(encrypted);
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

  private static void example3() {
    /**
     * Example 3: Encrypt and decrypt a file using public and private key files
     */

    String pubKeyName = "public.der";
    String prvKeyName = "private.der";
    String inputFileName = "input.txt";
    String encryptedFileName = "encrypted.bin";
    String decryptedFileName = "decrypted.txt";

    // generate keys
    RSACryptoSystem.saveKeys(1024);

    // encrypt
    RSACryptoSystem.startEncrypt(new String[] { "", inputFileName, encryptedFileName, pubKeyName });

    // decrypt
    RSACryptoSystem.startDecrypt(new String[] { "", encryptedFileName, decryptedFileName, prvKeyName });

  }

}
