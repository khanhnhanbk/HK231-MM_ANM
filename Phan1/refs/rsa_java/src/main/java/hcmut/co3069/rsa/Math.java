package hcmut.co3069.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;
public class Math {

    private static SecureRandom rng = new SecureRandom();
    public static final BigInteger SMALL_PRIME_PRODUCT = BigInteger
            .valueOf(2L * 3 * 5 * 7 * 11 * 13 * 17 * 19 * 23 * 29 * 31 * 37 * 41);

    /*
     * FUNCTION TrialDivision(a,b: Longlnt): BOOLEAN returns the value TRUE if
     * and only if a is not divisible by a prime smaller or equal to b. This
     * procedure
     * requires a list of small primes, e.g., the primes smaller than 216 = 65536.
     */


    public Math(SecureRandom random) {
        rng = random;
    }



	/**
	* Thuật toán như sau:
	* - Nếu số mũ là lẻ thì result = result * base % modulus
	* - Nếu số mũ là chẵn thì base = base * base % modulus
	* - Lấy số mũ chia 2
	* Khi số mũ bằng 0 thì dừng
	* Thuật toán có dạng chứng minh như sau:
	* base ^ exponent % modulus = (((base ^ 2) ^ (exponent // 2) % modulus)* (base
	* ^ (exponent % 2) % modulus)) mod modulus
	*/
    static BigInteger modPow(BigInteger base, BigInteger exponent, BigInteger modulus) {
        if (exponent.bitLength() < 32) {
            return modPow(base, exponent.intValue(), modulus);
        }
        BigInteger result = BigInteger.ONE;

        while (exponent.compareTo(BigInteger.ZERO) > 0) {

            // ta kiểm tra xem số mũ có chẵn hay lẻ
            if (exponent.testBit(0)) {
                result = result.multiply(base).mod(modulus);
            }
            // Sau đó base mũ 2 rồi phần trăm cho modulus để tìm base mới
            base = base.multiply(base).mod(modulus);
            // Lấy số mũ chia 2
            exponent = exponent.shiftRight(1);
        }
        return result;
    }

    static BigInteger modPow(BigInteger base, int exponent, BigInteger modulus) {
        /**
         * Thuật toán như sau:
         * - Nếu số mũ là lẻ thì result = result * base % modulus
         * - Nếu số mũ là chẵn thì base = base * base % modulus
         * - Lấy số mũ chia 2
         * Khi số mũ bằng 0 thì dừng
         * Thuật toán có dạng chứng minh như sau:
         * base ^ exponent % modulus = (((base ^ 2) ^ (exponent // 2) % modulus)* (base
         * ^ (exponent % 2) % modulus)) mod modulus
         */
        BigInteger result = BigInteger.ONE;

        while (exponent > 0) {

            // ta kiểm tra xem số mũ có chẵn hay lẻ
            if (exponent % 2 == 1) {
                result = result.multiply(base).mod(modulus);
            }
            // Sau đó base mũ 2 rồi phần trăm cho modulus để tìm base mới
            base = base.multiply(base).mod(modulus);
            // Lấy số mũ chia 2
            exponent = exponent >> 1;
        }
        return result;
    }

    public static BigInteger randomRange(BigInteger min, BigInteger max) {
        BigInteger range = max.subtract(min);
        if (range.compareTo(BigInteger.ZERO) < 0) {
			throw new ArithmeticException("Invalid range: [" + min + "," + max + "]");
		}
		if (range.compareTo(BigInteger.ZERO) == 0) {
			return min;
		}
		int bitLength = range.bitLength();
		BigInteger a;
		do {
			a = new BigInteger(bitLength, rng);
		} while (a.compareTo(range) >= 0);
		return a.add(min);
    }


    // Generate a random number of the specified bit length in the range 2^(bits-1)
    // and 2^bits-1
    // since small primes are not considered to be secure.
    public static BigInteger randomBigInteger(int bitLength) {
        if (bitLength < 2) {
            throw new ArithmeticException("Prime size must be at least 2 bits");
        }
		BigInteger min = BigInteger.ONE.shiftLeft(bitLength - 1);
		BigInteger max = BigInteger.ONE.shiftLeft(bitLength).subtract(BigInteger.ONE);
		return randomRange(min, max);
    }

    public static BigInteger randomPrime(int bitLength) {
        while (true) {
            BigInteger a = Math.randomBigInteger(bitLength);
            if (isProbablePrime(a, 10)) {
                return a;
            }
        }
    }



    public static BigInteger gordonStrongPrime(int bitLen) {

        SecureRandom random = new SecureRandom();
        BigInteger r;
		BigInteger s = randomPrime(bitLen);
		BigInteger t = randomPrime(bitLen);

        // Step 2
        int i = 69;
        while (true) {
            r = t.multiply(BigInteger.valueOf(2)).multiply(BigInteger.valueOf(i)).add(BigInteger.ONE);
            if (r.isProbablePrime(10)) {
                break;
            }
            i++;
        }

        // // Step 3
        BigInteger p0 = BigInteger.valueOf(2).multiply(s.modPow(r.subtract(BigInteger.valueOf(2)), r)).multiply(s)
                .subtract(BigInteger.ONE);

        // // Step 4
        BigInteger p;
        int j = 69;
        while (true) {
            p = p0.add(BigInteger.valueOf(2).multiply(BigInteger.valueOf(j)).multiply(r).multiply(s));
            if (p.isProbablePrime(10)) {
                break;
            }
            j++;
        }

        return p;
        // return BigInteger.ZERO;
    }

    public static BigInteger randomSecondBigInteger(BigInteger prime1, int bitLength) {
        /**
         * Range là khoảng cách từ min đến max của 1 số n bits
         * secondRandomBigInt lớn hơn n * ln2 nếu prime1 < range / 2; ngược lại, prime2
         * phải bé hơn n * ln2
         */
        BigInteger range = BigInteger.ONE.shiftLeft(bitLength - 1);
        BigInteger secondRandomBigInt;
        SecureRandom secureRandom = new SecureRandom();

        if (prime1.compareTo(range.divide(BigInteger.TWO)) < 0) {
            do {
                secondRandomBigInt = new BigInteger(bitLength, secureRandom);
            } while (secondRandomBigInt.compareTo(range) <= 0);
        } else {
            do {
                secondRandomBigInt = new BigInteger(bitLength, secureRandom);
            } while (secondRandomBigInt.compareTo(range) >= 0);
        }

        return secondRandomBigInt.add(range);
    }

    public static BigInteger gcd(BigInteger a, BigInteger b) {
        while (!b.equals(BigInteger.ZERO)) {
            BigInteger temp = a.mod(b);
            a = b;
            b = temp;
        }
        ;
        return a;
    }

    static public BigInteger[] gcdExtended(BigInteger a, BigInteger b) {
        BigInteger x = BigInteger.ZERO, y = BigInteger.ONE;
        BigInteger lastx = BigInteger.ONE,
                lasty = BigInteger.ZERO, temp;
        while (!b.equals(BigInteger.ZERO)) {
            BigInteger q = a.divide(b);
            BigInteger r = a.mod(b);

            a = b;
            b = r;

            temp = x;
            x = lastx.subtract(q.multiply(x));
            lastx = temp;

            temp = y;
            y = lasty.subtract(q.multiply(y));
            lasty = temp;
        }

        // a * lastx + b * lasty = 1
        return new BigInteger[] { a, lastx, lasty };
    }

    public static BigInteger modInverse(BigInteger e, BigInteger phi) {
        BigInteger x = gcdExtended(e, phi)[1];
        if (x.compareTo(BigInteger.ZERO) < 0) {
            x = x.add(phi);
        }
        return x;
    }

    public static BigInteger probableStrongPrime() {
        // TODO
        return BigInteger.ONE;
    }

    public static boolean isProbablePrime(BigInteger n, int k) {
        if (!n.testBit(0)) {
            return false;
        }
        if (n.compareTo(BigInteger.valueOf(3)) <= 0) {
            return true;
        }
        if (!fermatTestBase(n, BigInteger.valueOf(2))) {
            return false;
        }
        if (gcd(n, SMALL_PRIME_PRODUCT).compareTo(BigInteger.ONE) != 0) {
            return false;
        }
        return millerRabinTest(n, BigInteger.valueOf(k));
    }

    public static boolean fermatTestBase(BigInteger n, BigInteger a) {
        return modPow(a,n.subtract(BigInteger.ONE), n).equals(BigInteger.ONE);
    }

    // millerRabinTest takes a BigInteger n and an integer k as input and returns
    // true if n is prime and false if n is composite.
    // @param n: the number to be tested
    // @param k: the number of times the test is repeated
    // @return true if n is prime and false if n is composite
    public static boolean millerRabinTest(BigInteger n, BigInteger k) {
        // miller-rabin algorithm
        if (n.compareTo(BigInteger.ONE) <= 0) {
            return false;
        }

        if (n.compareTo(BigInteger.valueOf(3)) <= 0) {
            return true;
        }
        int r = 0;
        BigInteger d = n.subtract(BigInteger.ONE);
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            r++;
            d = d.divide(BigInteger.TWO);
        }
        for (BigInteger i = BigInteger.ZERO; i.compareTo(k) < 0; i = i.add(BigInteger.ONE)) {
            BigInteger a;
            do {
                a = new BigInteger(n.bitLength(), new SecureRandom());
            } while (a.compareTo(BigInteger.TWO) <0 || a.compareTo(n) >= 0);
            // BigInteger x = a.modPow(d, n);
            BigInteger x = modPow(a, d, n);
            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) {
                continue;
            }
            boolean isProbablePrime = false;
            for (int j = 0; j < r - 1; j++) {
                // x = x.modPow(BigInteger.TWO, n);
                x = modPow(x, BigInteger.TWO, n);
                if (x.equals(BigInteger.ONE)) {
                    return false;
                }
                if (x.equals(n.subtract(BigInteger.ONE))) {
                    isProbablePrime = true;
                    break;
                }
            }
            if (!isProbablePrime) {
                return false;
            }
        }
        return true;
    }

	public static BigInteger maurerAlgorithm(int bitLength) {
		if (bitLength < 2) {
			throw new IllegalArgumentException("Bit length must be at least 2");
		}
	
		if (bitLength == 2) {
			return BigInteger.valueOf(3);
		}
	
		// Generate a random prime with bit length `bitLength / 2 + 1`
		BigInteger q = new BigInteger(bitLength / 2 + 1, 100, new SecureRandom());
	
		// Calculate I = 2^(bitLength - 2)
		BigInteger I = BigInteger.valueOf(2).pow(bitLength - 2);
	
		while (true) {
			// Generate a random integer R, with 1 < R < I
			BigInteger R = randomBigInteger(BigInteger.valueOf(2), I.subtract(BigInteger.ONE));
			//BigInteger R = randomRange(BigInteger.valueOf(2), I.subtract(BigInteger.ONE));
	
			// Calculate n = 2Rq + 1
			BigInteger n = R.multiply(q).multiply(BigInteger.valueOf(2)).add(BigInteger.ONE);
	
			// Check if n is prime
			//if (isPrime(n, 100)) {
			//	return n;
			//}
			if (isProbablePrime(n, 100)) {
				return n;
			}
		}

	}
	public static BigInteger randomBigInteger(BigInteger min, BigInteger max) {
		SecureRandom random = new SecureRandom();
		BigInteger range = max.subtract(min).add(BigInteger.ONE);
		int bitLength = range.bitLength();
		BigInteger randomBigInt;
	
		do {
			randomBigInt = new BigInteger(bitLength, random);
		} while (randomBigInt.compareTo(range) >= 0);
	
		return randomBigInt.add(min);
	}
    public static BigInteger randomStrongPrime(int N) {
        if ((N < 512) || ((N % 128) != 0)){
            throw new ArithmeticException("Strong prime size must be at least 512 bits and a multiple of 128");
        }
        double false_positive_prob = 2E-6;

        int rabin_miller_rounds = (int) (java.lang.Math.ceil(-java.lang.Math.log(false_positive_prob)/java.lang.Math.log(4)));

        int x = (N - 512) / 128;
        System.out.println("x: " + x);

        BigInteger lower_bound = new BigInteger("14142135623730950489").multiply(BigInteger.ONE.shiftLeft(511 + 128 * x )).divide(new BigInteger("10000000000000000000"));
        BigInteger upper_bound = BigInteger.ONE.shiftLeft(512 + 128 * x).subtract(BigInteger.ONE);
        BigInteger X = randomRange(lower_bound, upper_bound);
        BigInteger[] p = {BigInteger.ZERO, BigInteger.ZERO};

        for (int i = 0; i < 2; i++) {
            p[i] = randomPrime(101);
        }
        BigInteger R = modInverse(p[1], p[0]).multiply(p[1]).subtract(modInverse(p[0], p[1]).multiply(p[0]));

        BigInteger increment = p[0].multiply(p[1]);
        X = X.add(R.subtract(X.mod(increment)));
        while (!isProbablePrime(X, rabin_miller_rounds)) {
            X = X.add(increment);

            if (X.bitLength() > N) {
                throw new RuntimeException("Couldn't find prime of size " + N);
            }
        }
        return X;
    }
}

//    public static BigInteger encrypt(PublicKey publicKey, String msg) {
//        // ref:
//        // https://crypto.stackexchange.com/questions/53219/how-to-encrypt-plain-message-with-rsa

//        // BigInteger modPow(BigInteger base,BigInteger exponent,BigInteger modulus);
//        byte[] msgBytes = msg.getBytes();
//        BigInteger e = publicKey.e;
//        BigInteger n = publicKey.n;
//        BigInteger decryptInput = new BigInteger(msgBytes);

//        return modPow(decryptInput, e, n);
//    }

//    public static String decrypt(PrivateKey privateKey, BigInteger encryptNumber) {
//        // ref:
//        // https://crypto.stackexchange.com/questions/53219/how-to-encrypt-plain-message-with-rsa

//        // BigInteger modPow(BigInteger base,BigInteger exponent,BigInteger modulus);
//        BigInteger d = privateKey.d;
//        BigInteger n = privateKey.n;

//        byte[] decryptBytes = modPow(encryptNumber, d, n).toByteArray();
//        String decryptText = new String(decryptBytes);

//        return decryptText;
//    }

//}