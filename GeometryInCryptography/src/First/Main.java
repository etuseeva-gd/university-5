package First;

import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private int certainty = 200, iter = 1000;
    private BigInteger TWO = new BigInteger("2"), THREE = new BigInteger("3"), FOUR = new BigInteger("4"),
            FIVE = new BigInteger("5"), EIGHT = new BigInteger("8"), D = BigInteger.ONE;

    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    void run() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите длину числа p в битах:");
        int len = Integer.parseInt(scanner.nextLine());
//        int len = 9;

        System.out.println("Введите m:");
        int m = Integer.parseInt(scanner.nextLine());
//        int m = 71;

        while (true) {
            BigInteger p;
            Trio pnr;

            while (true) {
                //1 шаг
                p = first(len);

                //2 шаг
                Pair<BigInteger, BigInteger> ab = second(p);

                //3 шаг -> p, N, r
                pnr = third(ab, p);

                //4 шаг
                boolean ok = fourth(pnr, m);

                if (ok) {
                    break;
                }
            }

            //5 шаг && 6 шаг
            //Если циклится с поиском точки -> прервать и найти новое p
            boolean ok = false;
            int k = 0;

            Trio beginPoint, point;
            while (true) {
                point = fifth(pnr); //5th
                beginPoint = point;

                if (iter == ++k) {
                    ok = true;
                    break;
                }

                if (!sixth(point, pnr.getB(), pnr.getA())) {
                    break;
                }
            }

            if (!ok) {
                //Вывод координат X, Y в файл
                writePoints(beginPoint, pnr.getB(), pnr.getA());

                //Вывод данных в файл
                print(p, point, pnr);
                break;
            }
        }
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
    Pair<BigInteger, BigInteger> second(BigInteger p) {
        Pair<BigInteger, BigInteger> w = findPrimeDecomposition(p, D);
        return getAB(w, p, 1, false);
    }

    Pair<BigInteger, BigInteger> findPrimeDecomposition(BigInteger p, BigInteger D) {
        BigInteger n = p.subtract(D);
        int legendreSymbol = getLegendreSymbol(n, p);
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

    Pair<BigInteger, BigInteger> getAB(Pair<BigInteger, BigInteger> w, BigInteger p, int choice, boolean used) {
        try {
            //Если нет решений
            if (w == null || p == null) {
                return null;
            }

            //Инициализация значений, шаг 3
            int index = 0;
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
                        getAB(w, p, 0, true);
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

            return new Pair<>(a, b);
        } catch (ArithmeticException e) {
            return null;
        }
    }

    //Шаг 3 - проверка коэффициетов
    Trio third(Pair<BigInteger, BigInteger> ab, BigInteger p) {
        if (ab == null || p == null) {
            return null;
        }

        BigInteger a = ab.getKey();
        BigInteger b = ab.getValue();

        ArrayList<BigInteger> valuesForT = new ArrayList<>();
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
    boolean fourth(Trio pnr, int m) {
        if (pnr == null) {
            return false;
        }
        if (pnr.getA().equals(pnr.getC())) {
            return false;
        }
        for (int i = 1; i <= m; i++) {
            if (pnr.getA().modPow(BigInteger.valueOf(i), pnr.getC()).equals(BigInteger.ONE)) {
                return false;
            }
        }
        return true;
    }

    //Шаг 5 - генерация произвольной точки и ее проверка
    Trio fifth(Trio pnr) {
        try {
            BigInteger p = pnr.getA(), n = pnr.getB(), r = pnr.getC();

            int len = p.bitLength();

            //Генерация координат случайной точки
            BigInteger x0;
            do {
                x0 = getRandomBigInteger(len, p);
            } while (!x0.gcd(p).equals(BigInteger.ONE));
            BigInteger y0 = getRandomBigInteger(len, p);

            BigInteger r2 = r.multiply(TWO), r4 = r.multiply(FOUR);

            BigInteger a = y0.pow(2).subtract(x0.pow(3)).multiply(x0.modInverse(p)).mod(p);
            BigInteger minusA = p.subtract(a);

            //Проверка квадратичных вычетов/невычетов
            if ((r2.equals(n) && !(getLegendreSymbol(minusA, p) == 1)) || (r4.equals(n) && (getLegendreSymbol(minusA, p) == 1))) {
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

        List<Trio> points = new ArrayList<>();
        points.add(result);
        try {
            for (BigInteger i = BigInteger.ONE; i.compareTo(n.subtract(BigInteger.ONE)) < 0;
                 i = i.add(BigInteger.ONE)) {
                result = sumPoints(result, point, p);
                if (result == null)
                    return true;
                for (Trio t : points) {
                    if (result.compareTo(t) == 0) {
                        return true;
                    }
                }
                points.add(result);
            }
        } catch (ArithmeticException e) {
            return true;
        }
        return sumPoints(result, point, p) != null;
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
                    lambda = result.getA().pow(2);
                    lambda = lambda.multiply(THREE).add(result.getC());
                    lambda = lambda.multiply(TWO.multiply(result.getB()).modInverse(p));
                }
            } else {
                BigInteger chis = point.getB().subtract(result.getB());
                BigInteger znam = point.getA().subtract(result.getA());
                lambda = chis.multiply(znam.modInverse(p));
            }
            BigInteger x3 = lambda.pow(2).subtract(result.getA()).subtract(point.getA()).mod(p);
            BigInteger y3 = result.getA().subtract(x3).multiply(lambda).subtract(result.getB()).mod(p);
            return new Trio(x3, y3, result.getC());
        } catch (ArithmeticException e) {
            return null;
        }
    }

    //Зона вывода данных
    //Вывод в файл координат X, Y
    void writePoints(Trio point, BigInteger n, BigInteger p) throws IOException {
        Trio result = point;
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("pointsX.txt"));
             BufferedWriter bufferedWriter1 = new BufferedWriter(new FileWriter("pointsY.txt"))) {
            bufferedWriter.write(result.getA() + "\n");
            bufferedWriter1.write(result.getB() + "\n");

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
            return;
        }
    }

    //Вывод данных в файл
    void print(BigInteger p, Trio point, Trio check) throws IOException {
        try (BufferedWriter bf = new BufferedWriter(new FileWriter("output.txt"))) {
            bf.write("Параметры элептической кривой:\n");
            bf.write("p = " + p.toString() + "\n");
            bf.write("А = " + point.getC() + "\n");

            Trio q = point;
            BigInteger nDivR = check.getB().divide(check.getC());

            for (int i = 0; i < nDivR.intValue() - 1; i++) {
                q = sumPoints(q, point, check.getA());
            }

            bf.write("Образующая точка - Q = (" + q.getA().toString() + ", " + q.getB().toString() + ")\n");
            bf.write("Простого порядка - r = " + check.getC() + "\n");

//            System.out.println("Проверка образующей точки");
//            Trio res = q;
//            for (int i = 2; i < check.getC().intValue() + 1; i++) {
//                res = sumPoints(res, q, check.getA());
//                if (res == null) {
//                    System.out.println(i);
//                    break;
//                }
//                System.out.println(res.getA() + " " + res.getB());
//            }
        }
    }

    class Trio implements Comparable<Trio> {
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

        @Override
        public int compareTo(Trio o) {
            if (a.compareTo(o.a) != 0)
                return a.compareTo(o.a);
            else {
                if (b.compareTo(o.b) != 0)
                    return b.compareTo(o.b);
            }
            return c.compareTo(o.c);
        }
    }
}