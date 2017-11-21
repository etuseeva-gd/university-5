import java.math.BigInteger;
import java.util.Scanner;

class Pair {
    private BigInteger a;
    private BigInteger b;

    public Pair(BigInteger a, BigInteger b) {
        this.a = a;
        this.b = b;
    }

    public BigInteger getA() {
        return a;
    }

    public void setA(BigInteger a) {
        this.a = a;
    }

    public BigInteger getB() {
        return b;
    }

    public void setB(BigInteger b) {
        this.b = b;
    }
}

public class Solution {

    private static final BigInteger TWO = new BigInteger("2");
    private static final BigInteger THREE = new BigInteger("3");

    public static void main(String[] args) {
        System.out.println(new BigInteger("2").modInverse(new BigInteger("11")));

        int p = 11;
        int b = 20;
        int a = b % 4 + 1;

        int n = 8;

//        int x = 8, y = 10;

        System.out.println(a + " " + b);

        for (int x = 0; x < p; x++) {
            System.out.print("x = " + x + " ");
            System.out.println("y^2 = " + (x * x * x + a * x + b) % p);
        }

//        Pair start = new Pair(BigInteger.valueOf(x), BigInteger.valueOf(y));
//        Pair result = start;
//
//        for (int i = 0; i < n - 1; i++) {
//            try {
//                result = sumPoints(result, start, BigInteger.valueOf(p), BigInteger.valueOf(a));
//            } catch (ArithmeticException e) {
//                System.out.println((i + 2) + " = Точка на бесконечности");
//                break;
//            }
//            if (result != null) {
//                System.out.println((i + 2) + " = " + result.getA() + " " + result.getB());
//            } else {
//                System.out.println((i + 2));
//            }
//        }
    }

    private static Pair sumPoints(Pair result, Pair point, BigInteger p, BigInteger a) throws ArithmeticException {
        if (result == null) {
            return null;
        }
        BigInteger lambda;
        if (result.getA().equals(point.getA()) && result.getB().equals(point.getB())) {
            if (result.getB().equals(BigInteger.ZERO)) {
                return null;
            } else {
                lambda = result.getA().pow(TWO.intValue());
                lambda = lambda.multiply(THREE);
                lambda = lambda.add(a);
                lambda = lambda.multiply(TWO.multiply(result.getB()).modInverse(p));
            }
        } else {
            BigInteger chis = point.getB().subtract(result.getB());
            BigInteger znam = point.getA().subtract(result.getA());
            lambda = chis.multiply(znam.modInverse(p));
        }
        BigInteger x3 = lambda.pow(TWO.intValue()).subtract(result.getA()).subtract(point.getA()).mod(p);
        BigInteger y3 = result.getA().subtract(x3).multiply(lambda).subtract(result.getB()).mod(p);
        return new Pair(x3, y3);
    }
}
