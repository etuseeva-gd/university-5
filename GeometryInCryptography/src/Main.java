import javafx.util.Pair;

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
    private int certainty = 200;
    private BigInteger TWO = new BigInteger("2"), THREE = new BigInteger("3"), FOUR = new BigInteger("4"),
            FIVE = new BigInteger("5"), EIGHT = new BigInteger("8"), D = BigInteger.ONE;

    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    void run() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите длину числа p в битах:");
        int len = scanner.nextInt();

//        while (true) {
            BigInteger p;
            Trio check, coeffs;
            while (true) {
                //1 шаг
                p = first(len);

                //2 шаг
                coeffs = second(p);

                //3 шаг -> p, N, r
                check = third(coeffs, p);

                //4 шаг
                boolean ok = fourth(check);

                if (ok) {
                    break;
                }
            }

            //5 шаг && 6 шаг
            Pair<Trio, Trio> res = fifthAndSixth(check);

            //Вывод координат X, Y в файл
            writePoints(res.getKey(), check.getB(), check.getA());

            //Вывод данных в файл
            print(p, res.getValue(), check);
//        }
    }

    //Первый шаг, сегенрировать простое число p
    BigInteger first(int len) {
        BigInteger p = new BigInteger(len, certainty, new SecureRandom());
        while (!p.isProbablePrime(certainty) || !p.mod(FOUR).equals(BigInteger.ONE)) {
            p = new BigInteger(len, certainty, new SecureRandom());
        }
        return p;
    }

    //Шаг второй, алгоритм 7.8.1 - Разложение простого числа в Z|-D^1/2|
    Trio second(BigInteger p) {
        Pair<BigInteger, BigInteger> w = findPrimeDecomposition(p, D);
        return getCoeffs(w, p, 1, false);
    }

    Pair<BigInteger, BigInteger> findPrimeDecomposition(BigInteger p, BigInteger D) {
        BigInteger n = p.subtract(D);
        int legendreSymbol = getLegendreSymbol(n, p); //Зачем?
        if (legendreSymbol != 1) {
            return null;
        } else {
            return REASONAlgorithm(p, n);
        }
    }

    int getLegendreSymbol(BigInteger a, BigInteger b) {
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

    Pair<BigInteger, BigInteger> REASONAlgorithm(BigInteger p, BigInteger n) {
        BigInteger q = p.subtract(BigInteger.ONE); // p - 1
        int s = 0; // 0

        do {
            q = q.divide(TWO);
            s++;
        } while (!q.mod(TWO).equals(BigInteger.ONE));

        if (s == 1) {
            BigInteger r = n.modPow(p.add(BigInteger.ONE).divide(FOUR), p);
            return new Pair<>(r, p.subtract(r)); //r, p - r
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
            return new Pair<>(r, p.subtract(r)); //r, p - r
        }
    }

    Trio getCoeffs(Pair<BigInteger, BigInteger> w, BigInteger p, int choice, boolean used) {
        try {
            //Если нет решений
            if (w == null || p == null) {
                return null;
            }

            //Инициализация значений, шаг 3
            int index = 0;
            //Связан с выбором между r, p-r ???
            BigInteger u = choice == 1 ? w.getKey() : w.getValue();

            ArrayList<BigInteger> valuesForU = new ArrayList<BigInteger>();
            valuesForU.add(u);

            ArrayList<BigInteger> valuesForM = new ArrayList<BigInteger>();
            valuesForM.add(p);

            do {
                BigInteger mPlusOne = valuesForU.get(index).pow(2);
                mPlusOne = mPlusOne.add(D);

                boolean ok1 = mPlusOne.mod(valuesForM.get(index)).equals(BigInteger.ZERO);
                mPlusOne = mPlusOne.divide(valuesForM.get(index));
                boolean ok2 = mPlusOne.equals(BigInteger.ZERO);

                if (!ok1 || ok2) {
                    if (!used) {
                        getCoeffs(w, p, 0, true);
                    } else {
                        return null;
                    }
                }

                BigInteger min1 = valuesForU.get(index).mod(mPlusOne);
                BigInteger min2 = mPlusOne.subtract(valuesForU.get(index)).mod(mPlusOne);

                valuesForU.add(min1.min(min2));
                valuesForM.add(mPlusOne);

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
    //Окончание 2 шага

    //Шаг 3 - проверка коэффициетов
    Trio third(Trio coeffs, BigInteger p) {
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
    boolean fourth(Trio check) {
        if (check == null) {
            return false;
        }
        if (check.getA().equals(check.getC())) {
            return false;
        }
        int m = 5; //степень безопасности выбирается в соответствии с таблицой
        for (int i = 1; i <= m; i++) {
            if (check.getA().modPow(BigInteger.valueOf(i), check.getC()).equals(BigInteger.ONE)) {
                return false;
            }
        }
        return true;
    }

    //Шаг 5 - генерация произвольной точки и ее проверка
    Pair<Trio, Trio> fifthAndSixth(Trio check) {
        Trio startPoint, point;
        do {
            point = generateAndCheckB(check); //5th
            startPoint = point;
        } while (sixth(point, check.getB(), check.getA()));

        return new Pair<>(startPoint, point);
    }

    Trio generateAndCheckB(Trio check) {
        try {
            BigInteger p = check.getA(), n = check.getB(), r = check.getC();

            int len = p.bitLength();
            BigInteger x0;
            do {
                x0 = getRandomBigInteger(len, p);
            } while (!x0.gcd(p).equals(BigInteger.ONE));
            BigInteger y0 = getRandomBigInteger(len, p);

            BigInteger r2 = r.multiply(TWO).mod(p), r4 = r.multiply(FOUR).mod(p);

            BigInteger a = y0.pow(2).subtract(x0.pow(3)).multiply(x0.modInverse(p)).mod(p);
            BigInteger minusA = p.subtract(a);

            //Проверка квадратичных вычетов/невычетов
            if ((r2.equals(n) && !(getLegendreSymbol(minusA, n) == 1)) || (r4.equals(n) && (getLegendreSymbol(minusA, n) == 1))) {
                return new Trio(x0, y0, a);
            } else {
                return null;
            }
        } catch (ArithmeticException e) {
            return null;
        }
    }

    BigInteger getRandomBigInteger(int len, BigInteger p) {
        return new BigInteger(new Random().nextInt(len + 1) + len / 2, certainty, new SecureRandom()).mod(p);
    }

    //Шаг 6 - проверка выбранной точки (сложение ее с собой N раз)
    boolean sixth(Trio point, BigInteger n, BigInteger p) {
        if (point == null) {
            return true;
        }
        Trio result = point;
        for (BigInteger i = BigInteger.ZERO; n.subtract(BigInteger.ONE).compareTo(i) >= 0; i = i.add(BigInteger.ONE)) {
            result = sumPoints(result, point, p);
            if (result == null) {
                return true;
            }
        }
        return false;
    }

    //Сумма точек
    Trio sumPoints(Trio result, Trio point, BigInteger p) {
        try {
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
        } catch (ArithmeticException e) {
            return null;
        }
    }

    //Зона вывода данных
    //Вывод в файл координат X, Y относительно порождающей точки
    void writePoints(Trio point, BigInteger n, BigInteger p) throws IOException {
        Trio result = point;
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("pointX.txt"));
             BufferedWriter bufferedWriter1 = new BufferedWriter(new FileWriter("pointY.txt"))) {
            for (BigInteger i = BigInteger.ZERO; n.subtract(BigInteger.ONE).compareTo(i) >= 0; i = i.add(BigInteger.ONE)) {
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
        }
    }

    void print(BigInteger p, Trio point, Trio check) throws IOException {
        //Вывод точки в файл
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("output.txt"));
        bufferedWriter.write("p = " + p.toString() + "\n");

//        bufferedWriter.write("(x0, y0) = (" + point.getA() + ", " + point.getB() + ")");
        bufferedWriter.write("А = " + point.getC() + "\n");

        Trio q = point;
        BigInteger nDivR = check.getB().divide(check.getC());

        for (int i = 0; i < nDivR.intValue() - 1; i++) {
            q = sumPoints(q, point, check.getA());
        }

        bufferedWriter.write("Q = (" + q.getA().toString() + ", " + q.getB().toString() + ")\n");
        bufferedWriter.write("r = " + check.getC());

        bufferedWriter.close();
    }
}