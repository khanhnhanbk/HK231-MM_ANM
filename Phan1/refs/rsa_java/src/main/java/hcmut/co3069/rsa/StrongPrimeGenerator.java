package hcmut.co3069.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;

public class StrongPrimeGenerator {

	public static BigInteger generate(int bitLength) {
		return randomStrongPrime(bitLength);
	}

	public static BigInteger randomStrongPrime(int N) {
		if ((N < 512) || ((N % 128) != 0)) {
			throw new ArithmeticException("Strong prime size must be at least 512 bits and a multiple of 128");
		}
		double false_positive_prob = 2E-6;

		int rabin_miller_rounds = (int) (java.lang.Math
				.ceil(-java.lang.Math.log(false_positive_prob) / java.lang.Math.log(4)));

		int x = (N - 512) / 128;
		System.out.println("x: " + x);

		BigInteger lower_bound = new BigInteger("14142135623730950489")
				.multiply(BigInteger.ONE.shiftLeft(511 + 128 * x)).divide(new BigInteger("10000000000000000000"));
		BigInteger upper_bound = BigInteger.ONE.shiftLeft(512 + 128 * x).subtract(BigInteger.ONE);
		BigInteger X = Math.randomRange(lower_bound, upper_bound);
		BigInteger[] p = { BigInteger.ZERO, BigInteger.ZERO };

		for (int i = 0; i < 2; i++) {
			p[i] = Math.randomPrime(101);
		}
		BigInteger R = Math.modInverse(p[1], p[0]).multiply(p[1]).subtract(Math.modInverse(p[0], p[1]).multiply(p[0]));

		BigInteger increment = p[0].multiply(p[1]);
		X = X.add(R.subtract(X.mod(increment)));
		while (!Math.isProbablePrime(X, rabin_miller_rounds)) {
			X = X.add(increment);

			if (X.bitLength() > N) {
				throw new RuntimeException("Couldn't find prime of size " + N);
			}
		}
		return X;
	}
}