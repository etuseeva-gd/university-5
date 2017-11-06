package SchnorrScheme;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class PrimeNumbers {
    void generatePrimes(String nameFile) throws NoSuchAlgorithmException, FileNotFoundException {
        try (PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(nameFile)))) {
            BigInteger q = generateQ(160);
            BigInteger p = generateP(q, 1024);

            out.println(p);
            out.println(q);

            BigInteger g = generateG(p, q);

            out.println(g);
        }
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