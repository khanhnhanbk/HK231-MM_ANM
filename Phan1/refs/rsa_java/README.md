<h1 align="center"> Java RSA Implementation</h1>
   <h3 style="color:blue;"> Made for the CO3069 course</h3>

## Thực thi chương trình

### Thực thi sử dụng `java` để chạy `.class` files

- Các file `.class` được tạo ra sau khi biên dịch sẽ được lưu trong thư mục `out/`. Để thực thi chương trình, ta cần thực hiện 2 lệnh sau:

```bash
javac -d ./out ./src/main/java/hcmut/co3069/rsa/*.java
```

```bash
java -cp ./out/ hcmut.co3069.rsa.RSACryptoSystem
```

### Tạo `jar` file để thực thi

Sau khi thực thi 2 lệnh trên, ta sẽ có các file `.class` trong thư mục `out/`. Để tạo file `.jar` để thực thi, ta cần tạo file `Manifest` chứa thông tin về `Main-Class` của chương trình.

```bash
echo Main-Class: hcmut.co3069.rsa.RSACryptoSystem > myManifest
```

```bash
jar cfm ./output.jar myManifest -C ./out/ .
```

```bash
java -jar  output.jar
```
## Project Setup
  - IDE: IntelliJ IDEA (recommended)
  - Java 11 (temporary)
  - Build Automation: Gradle
  - Testing: JUnit 5

## Tổ chức code
  - `src/main/java` - Chứa source code của project.
  - `src/test/java` - Chứa test code của project.
  - `hcmut.co3069.rsa.RSACryptoSystem` - Chứa hàm `main` để thực thi chương trình.
  - `hcmut.co3069.rsa.Math` - Chứa các hàm tính toán liên quan đến số học.
  - `hcmut.co3069.rsa.PrivateKey` - Chứa các hàm để tạo và lưu private key.
  - `hcmut.co3069.rsa.PublicKey` - Chứa các hàm để tạo và lưu public key.
  - `hcmut.co3069.rsa.StrongPrimeGenerator` - Chứa các hàm để tạo số nguyên tố mạnh.

# References
  - [Java Guide](https://www.baeldung.com/java-tutorial)
  - [Java Quick Start](https://www.baeldung.com/get-started-with-java-series)
  - [Java Packages][java_packages] 
  - [IntelliJ](https://www.jetbrains.com/help/idea/getting-started-with-gradle.html)

[java_packages]: https://www.baeldung.com/java-packages
