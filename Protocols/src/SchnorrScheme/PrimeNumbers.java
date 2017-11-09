package SchnorrScheme;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PrimeNumbers {
    BigInteger[] generatePrimes() throws NoSuchAlgorithmException {
        BigInteger q = generateQ(160);
        BigInteger p = generateP(q, 1024);
        BigInteger g = generateG(p, q);

        return new BigInteger[]{p, q, g};
    }

    private BigInteger generateQ(int length) throws NoSuchAlgorithmException {
        return genPrimeNum(length);
    }

    private BigInteger generateP(BigInteger q, int length) throws NoSuchAlgorithmException {
        if (length % 64 != 0) {
            throw new InvalidParameterException("Длина должна делиться на 64");
        }
        BigInteger p = genPrimeNum(512);
        BigInteger remainder = p.mod(q);
        p = p.subtract(remainder).add(BigInteger.ONE);
        while (!p.isProbablePrime(100)) {
            p = p.add(q);
        }
        if (p.bitCount() > length) {
            return generateP(q, length);
        }
        return p;
    }

    private BigInteger generateG(BigInteger p, BigInteger q) throws NoSuchAlgorithmException {
        while (true) {
            BigInteger a = getRandBigInteger(1024).mod(p);
            BigInteger g = a.modPow(p.subtract(BigInteger.ONE).divide(q), p);
            if (!g.equals(BigInteger.ONE)) {
                return g;
            }
        }
    }

    private BigInteger genPrimeNum(int length) throws NoSuchAlgorithmException {
        return this.getRandBigInteger(length).nextProbablePrime();
    }

    private BigInteger getRandBigInteger(int length) throws NoSuchAlgorithmException {
        return new BigInteger(length, SecureRandom.getInstance("SHA1PRNG"));
    }
}