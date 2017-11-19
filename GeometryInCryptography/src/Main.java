import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class Trio {
    private BigInteger a;
    private BigInteger b;
    private BigInteger c;

    public Trio(BigInteger a, BigInteger b, BigInteger c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public BigInteger getA() {
        return a;
    }

    public BigInteger getB() {
        return b;
    }

    public BigInteger getC() {
        return c;
    }
}

public class Main {
    public static final int certainty = 200;
    public static final BigInteger TWO = new BigInteger("2");
    public static final BigInteger THREE = new BigInteger("3");
    public static final BigInteger FOUR = new BigInteger("4");
    public static final BigInteger FIVE = new BigInteger("5");
    public static final BigInteger SIX = new BigInteger("6");
    public static final BigInteger EIGHT = new BigInteger("8");

    private static final BigInteger D = BigInteger.ONE;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите длину числа p в битах:");
        int len = scanner.nextInt();

        BigInteger p;
        Trio w, check, coeffs;
        boolean flag;
        Trio point;

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("output.txt"));
        do {
            //1 шаг
            p = getPrimeNumber(len); //1st

            //2 шаг
            w = findPrimeDecomposition(p, D); //2nd - a
            coeffs = getCoeffs(w, p, 1, false); //2nd - b

            //3 шаг
            check = checkCoeffs(coeffs, p); //3rd

            //4 шаг
            flag = checkTrio(check); //4th
        } while (!flag);

        bufferedWriter.write("P: " + p.toString() + "\n");
        bufferedWriter.write("c: " + w.getA() + " d: " + w.getB() + "\n");
        Trio startPoint;

        do {
            point = generateAndCheckB(check); //5th
            startPoint = point;
        } while (checkPoint(point, check.getB(), check.getA()));

        writePoints(startPoint, check.getB(), check.getA());
        bufferedWriter.write("x0: " + point.getA() + " y0: " + point.getB() + " А: " + point.getC() + "\n");
        Trio q = point;
        BigInteger n = check.getB().divide(check.getC());
        bufferedWriter.write("N: " + n.intValue() + "\n");
        for (int i = 0; i < n.intValue() - 1; i++) {
            q = sumPoints(q, point, check.getA());
        }
        bufferedWriter.write("Q = (" + q.getA().toString() + ", " + q.getB().toString() + ")");
        bufferedWriter.close();
    }

    //Первый шаг, сегенрировать простое число p
    private static BigInteger getPrimeNumber(int len) {
        BigInteger p = new BigInteger(len, certainty, new SecureRandom());
        while (!p.isProbablePrime(certainty) || !p.mod(FOUR).equals(BigInteger.ONE)) {
            p = new BigInteger(len, certainty, new SecureRandom());
        }
        return p;
    }

    //Шаг второй, алгоритм 7.8.1 - Разложение простого числа в Z|-D^1/2|
    private static Trio findPrimeDecomposition(BigInteger p, BigInteger D) {
        BigInteger n = p.subtract(D);
        int legendreSymbol = getLegendreSymbol(n, p); //Зачем?
        if (legendreSymbol != 1) {
            return null;
        } else {
            return REASONAlgorithm(p, n);
        }
    }

    //Todo: может быть переписать
    public static int getLegendreSymbol(BigInteger a, BigInteger b) {
        if (!b.gcd(a).equals(BigInteger.ONE))
            return 0;
        int r = 1;
        if (a.signum() == -1) {
            a = a.negate();
            if ((b.mod(FOUR).equals(THREE))) {
                r = -r;
            }
        }
        do {
            while ((a.mod(TWO).equals(BigInteger.ZERO))) {
                if ((b.mod(EIGHT)).equals(THREE) || (b.mod(EIGHT).equals(FIVE))) {
                    r = -r;
                }
                a = a.divide(TWO);
            }
            if ((a.mod(FOUR).equals(b.mod(FOUR)) && (b.mod(FOUR).equals(THREE)))) {
                r = -r;
            }
            BigInteger c = a;
            a = b.mod(c);
            b = c;
        } while (!a.equals(BigInteger.ZERO));
        if (b.equals(BigInteger.ONE)) {
            return r;
        } else {
            return 0;
        }
    }

    //Todo: to pair
    private static Trio REASONAlgorithm(BigInteger p, BigInteger n) {
        BigInteger q = p.subtract(BigInteger.ONE); // p - 1
        int s = 0; // 0

        do {
            q = q.divide(TWO);
            s++;
        } while (!q.mod(TWO).equals(BigInteger.ONE));

        if (s == 1) {
            BigInteger r = n.modPow(p.add(BigInteger.ONE).divide(FOUR), p);
            return new Trio(r, p.subtract(r), null); //r, p - r
        } else {
            //Выбрали произвольный квадратичный невычет
            BigInteger z = BigInteger.ONE;
            do {
                z = z.add(BigInteger.ONE);
            }
            while (getLegendreSymbol(z, p) != -1);

            BigInteger c = z.modPow(q, p),
                    r = n.modPow(q.add(BigInteger.ONE).divide(TWO), p),
                    t = n.modPow(q, p);

            int m = s;

            while (!t.mod(p).equals(BigInteger.ONE)) {
                int index = 1;
                for (int i = 1; i < m; i++) {
                    BigInteger exp = TWO.pow(i);
                    if (t.modPow(exp, p).equals(BigInteger.ONE)) {
                        index = i;
                        break;
                    }
                }
                BigInteger exp = TWO.pow(m - index - 1);
                BigInteger b = c.modPow(exp, p);

                r = r.multiply(b).mod(p);
                t = t.multiply(b).multiply(b).mod(p);
                c = b.multiply(b).mod(p);

                m = index;
            }

            return new Trio(r, p.subtract(r), null); //r, p - r
        }
    }

    private static Trio getCoeffs(Trio w, BigInteger p, int choice, boolean used) {
        try {
            //Если нет решений
            if (w == null || p == null) {
                return null;
            }

            //Инициализация значений, шаг 3
            int index = 0;
            //Связан с выбором между r, p-r ???
            BigInteger u = choice == 1 ? w.getA() : w.getB();

            ArrayList<BigInteger> valuesForU = new ArrayList<BigInteger>();
            valuesForU.add(u);

            ArrayList<BigInteger> valuesForM = new ArrayList<BigInteger>();
            valuesForM.add(p);

            do {
                BigInteger temp = valuesForU.get(index).pow(2);
                temp = temp.add(BigInteger.ONE);

                boolean flag1 = temp.mod(valuesForM.get(index)).equals(BigInteger.ZERO);
                temp = temp.divide(valuesForM.get(index));
                boolean flag = temp.equals(BigInteger.ZERO);
                if (flag || !flag1) {
                    if (!used) {
                        getCoeffs(w, p, 0, true);
                    } else {
                        return null;
                    }
                }
                BigInteger min1 = valuesForU.get(index).mod(temp);
                BigInteger min2 = temp.subtract(valuesForU.get(index)).mod(temp);
                valuesForU.add(min1.min(min2));
                valuesForM.add(temp);
                index++;
            } while (!valuesForM.get(index).equals(BigInteger.ONE));

            BigInteger a = valuesForU.get(index - 1);
            BigInteger b = BigInteger.ONE;
            index--;
            while (index != 0) {
                BigInteger chisOne = valuesForU.get(index - 1).multiply(a);
                BigInteger chisOneNegate = chisOne.negate();
                chisOne = chisOne.add(BigInteger.ONE.multiply(b));
                chisOneNegate = chisOneNegate.add(BigInteger.ONE.multiply(b));
                BigInteger znam = a.pow(TWO.intValue());
                znam = znam.add(BigInteger.ONE.multiply(b.pow(TWO.intValue())));
                BigInteger chisTwo = a.negate();
                BigInteger chisTwoNegate = chisTwo;
                chisTwo = chisTwo.add(valuesForU.get(index - 1).multiply(b));
                chisTwoNegate = chisTwoNegate.subtract(valuesForU.get(index - 1).multiply(b));
                if (chisOne.mod(znam).equals(BigInteger.ZERO)) {
                    a = chisOne.divide(znam);
                } else {
                    a = chisOneNegate.divide(znam);
                }
                if (chisTwo.mod(znam).equals(BigInteger.ZERO)) {
                    b = chisTwo.divide(znam);
                } else {
                    b = chisTwoNegate.divide(znam);
                }
                index--;
            }
            return new Trio(a, b, null);
        } catch (ArithmeticException e) {
            return null;
        }
    }

    //Шаг 3 - проверка коэффициетов
    private static Trio checkCoeffs(Trio coeffs, BigInteger p) {
        if (coeffs == null || p == null) {
            return null;
        }
        BigInteger a = coeffs.getA();
        BigInteger b = coeffs.getB();
        ArrayList<BigInteger> valuesForT = new ArrayList<BigInteger>();
        valuesForT.add(b.multiply(TWO).negate());
        valuesForT.add(a.multiply(TWO));
        valuesForT.add(b.multiply(TWO));
        valuesForT.add(a.multiply(TWO).negate());
        for (BigInteger bigInteger : valuesForT) {
            bigInteger = bigInteger.add(BigInteger.ONE).add(p);
            if (bigInteger.mod(TWO).equals(BigInteger.ZERO) && bigInteger.divide(TWO).isProbablePrime(certainty)) {
                return new Trio(p, bigInteger, bigInteger.divide(TWO));
            } else if (bigInteger.mod(FOUR).equals(BigInteger.ZERO) && bigInteger.divide(FOUR).isProbablePrime(certainty)) {
                return new Trio(p, bigInteger, bigInteger.divide(FOUR));
            }
        }
        return null;
    }

    //Шаг 4 - проверка полученных данных
    private static boolean checkTrio(Trio check) {
        if (check == null) {
            return false;
        }
        if (check.getA().equals(check.getC())) {
            return false;
        }
        for (int i = 1; i <= 5; i++) {
            if (check.getA().modPow(BigInteger.valueOf(i), check.getC()).equals(BigInteger.ONE)) {
                return false;
            }
        }
        return true;
    }

    //Шаг 5 - генерация произвольной точки и ее проверка
    private static Trio generateAndCheckB(Trio check) {
        Random random = new Random();
        BigInteger p = check.getA();
/*        BigInteger x0 = new BigInteger(random.nextInt(p.bitLength() + 1) + p.bitLength() / 2, certainty, new SecureRandom()).mod(p);
        BigInteger y0 = new BigInteger(random.nextInt(p.bitLength() + 1) + p.bitLength() / 2, certainty, new SecureRandom()).mod(p);*/
        BigInteger x0 = BigInteger.ONE;
        BigInteger y0 = TWO;
        BigInteger n = check.getB();
        BigInteger r = check.getC();
        BigInteger r2 = r.multiply(TWO).mod(p);
        BigInteger r4 = r.multiply(FOUR).mod(p);
        BigInteger a = y0.pow(TWO.intValue()).subtract(x0.pow(THREE.intValue())).mod(p);
        a = a.multiply(x0.modInverse(p));
        BigInteger minusA = p.subtract(a);
        if ((r2.equals(n) && !(getLegendreSymbol(minusA, n) == 1)) || (r4.equals(n) && (getLegendreSymbol(minusA, n) == 1))) {
            return new Trio(x0, y0, a);
        } else {
            return null;
        }
    }

    private static boolean checkPoint(Trio point, BigInteger n, BigInteger p) {
        if (point == null) {
            return true;
        }
        Trio result = point;
        BigInteger i;
        try {
            for (i = BigInteger.ZERO; n.subtract(BigInteger.ONE).compareTo(i) >= 0; i = i.add(BigInteger.ONE)) {
                result = sumPoints(result, point, p);
                if (result == null) {
                    return true;
                }
            }
        } catch (ArithmeticException e) {
            //do nothing
        }
        return false;
    }

    private static void writePoints(Trio point, BigInteger n, BigInteger p) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("pointX.txt"));
        BufferedWriter bufferedWriter1 = new BufferedWriter(new FileWriter("pointY.txt"));
        Trio result = point;
        BigInteger i;
        try {
            for (i = BigInteger.ZERO; n.subtract(BigInteger.ONE).compareTo(i) >= 0; i = i.add(BigInteger.ONE)) {
                result = sumPoints(result, point, p);
                if (result == null) {
                    break;
                } else {
                    bufferedWriter.write(result.getA() + "\n");
                    bufferedWriter1.write(result.getB() + "\n");
                }
            }
        } catch (ArithmeticException e) {
            //если мы нашли обратный элемент, то просто закончить искать точки
        } finally {
            bufferedWriter.close();
            bufferedWriter1.close();
        }
    }

    private static Trio sumPoints(Trio result, Trio point, BigInteger p) {
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
                lambda = lambda.multiply(TWO.multiply(result.getB()).modInverse(p));
            }
        } else {
            BigInteger chis = point.getB().subtract(result.getB());
            BigInteger znam = point.getA().subtract(result.getA());
            lambda = chis.multiply(znam.modInverse(p));
        }
        BigInteger x3 = lambda.pow(TWO.intValue()).subtract(result.getA()).subtract(point.getA()).mod(p);
        BigInteger y3 = result.getA().subtract(x3).multiply(lambda).subtract(result.getB()).mod(p);
        return new Trio(x3, y3, null);
    }
}