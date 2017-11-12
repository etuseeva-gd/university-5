package DSA;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PrimeNumbers {
    BigInteger[] generatePrimes(int qLen, int pLen) throws NoSuchAlgorithmException {
//        BigInteger q = generateQ(qLen);
        BigInteger q = new BigInteger("11");
        BigInteger p = generateP(q, pLen);
        BigInteger g = generateG(p, q, pLen);
        return new BigInteger[]{p, q, g};
    }

    private BigInteger generateQ(int length) throws NoSuchAlgorithmException {
        return genPrimeNum(length);
    }

    private BigInteger generateP(BigInteger q, int length) throws NoSuchAlgorithmException {
        int k = 1;
        BigInteger p = BigInteger.ONE;
        while (!p.isProbablePrime(100)) {
            p = q.multiply(new BigInteger("2").pow(k)).add(BigInteger.ONE);
            k++;
        }
        return p;
    }

    private BigInteger generateG(BigInteger p, BigInteger q, int pLen) throws NoSuchAlgorithmException {
        while (true) {
            BigInteger a = getRandBigInteger(pLen).mod(p).add(BigInteger.ONE);
            BigInteger g = a.modPow(p.subtract(BigInteger.ONE).divide(q), p);
//            if (g.equals(BigInteger.ZERO)) {
//                g = g.add(BigInteger.ONE);
//            }
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