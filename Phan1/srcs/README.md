# RSA Cryptosystem

## Getting Started

Compile the program with the following command:

```bash
javac -d bin src/*.java
```

Run the program with the following command:

```bash
java -cp bin App [options] [arguments]
```

Export to a jar file with the following command:

```bash
jar cvfe rsa.jar App -C bin .
```
## Options
<!-- -generate : generates public and private keys
-viewpub <publickeyfile>: view public key
-viewpriv <privatekeyfile>: view private key
-encrypt <input> <output> <publickeyfile> : encrypt a file
-decrypt <input> <output> <privatekeyfile> : decrypt a file
-example n : run example n -->

- -generate : generates public and private keys
- -viewpub <publickeyfile>: view public key
- -viewpriv <privatekeyfile>: view private key
- -encrypt \<input> \<output> <publickeyfile> : encrypt a file
- -decrypt \<input> \<output> <privatekeyfile> : decrypt a file
- -example n : run example n

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies
