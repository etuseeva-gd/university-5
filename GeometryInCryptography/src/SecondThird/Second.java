package SecondThird;

import First.EllipticalCurves;
import javafx.util.Pair;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Random;
import java.util.Scanner;

public class Second {
    public static class CommonParams {
        BigInteger p, a, r;
        Pair<BigInteger, BigInteger> P, Q;
    }

    public static void main(String[] args) throws IOException {
        new Second().run();
    }

    void run() throws IOException {
        System.out.println("Выберите что вы хотите сделать?");

        //Генерация параметров
        System.out.println("0 - Сгенерировать параметры");
        System.out.println("1 - Сгенерировать вход претендента (l)");

        //Протокол
        System.out.println("2 - 1 шаг. Претендент: Сгенерировать и послать точку R верификатору");
        System.out.println("3 - 2 шаг. Верификатор: Проверить R и послать случайный бит");
        System.out.println("4 - 3 шаг. Претендент: Предьявление показателя k(или k') на основе бита");
        System.out.println("5 - 4 шаг. Верификатор: Проверка знания l претендента");

        System.out.println("6 - Выход.");

        Scanner sc = new Scanner(System.in);
        while (true) {
            String action = sc.nextLine();

            switch (action) {
                case "0": {
                    zero();
                    break;
                }
                case "1": {
                    first();
                    break;
                }
                case "2": {
                    second();
                    break;
                }
                case "3": {
                    third();
                    break;
                }
                case "4": {
                    fourth();
                    break;
                }
                case "5": {
                    fifth();
                    break;
                }
                case "6": {
                    sc.close();
                    return;
                }
                default: {
                    System.out.println("Неверная операция!");
                }
            }
        }
    }

    void zero() throws IOException {
        deleteAll();
        //p a Q r
        new EllipticalCurves().genParams("common_params.txt");
    }

    void first() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("common_params.txt"));
        BigInteger p = new BigInteger(br.readLine());
        BigInteger a = new BigInteger(br.readLine());
        Pair<BigInteger, BigInteger> Q = getPoint(br.readLine());
        BigInteger r = new BigInteger(br.readLine());
        br.close();

        Scanner sc = new Scanner(System.in);
        System.out.println("Введите l:");

        BigInteger l = new BigInteger(sc.nextLine());
        while (l.compareTo(r) > 0) {
            System.out.println("Не корректное l (l > r), введите другое.");
            l = new BigInteger(sc.nextLine());
        }

        printStr(l + "", "l.txt");

        Pair<BigInteger, BigInteger> R = multPoint(l, Q, a, p);

        BufferedWriter bw = new BufferedWriter(new FileWriter("common_params.txt"));
        bw.write(p + "\n");
        bw.write(a + "\n");
        bw.write(getStrPoint(Q) + "\n");
        bw.write(r + "\n");
        bw.write(getStrPoint(R) + "\n");
        bw.close();
    }

    void second() throws IOException {
        CommonParams cp = getCommonParams();

        //Знает сам
        BigInteger l = new BigInteger(readOneStr("l.txt"));
        //

        BigInteger k = new BigInteger(cp.r.bitLength() - 1, new Random()).mod(cp.r);
        printStr(k + "", "k.txt");
        BigInteger k1 = k.multiply(l).mod(cp.r);
        printStr(k1 + "", "k1.txt");

        Pair<BigInteger, BigInteger> R = multPoint(k, cp.P, cp.a, cp.p);
        printPoint(R, "R.txt");
    }

    void third() throws IOException {
        CommonParams cp = getCommonParams();

        //Получил от претендента
        Pair<BigInteger, BigInteger> R = getPoint(readOneStr("R.txt"));
        //

        Pair<BigInteger, BigInteger> rR = multPoint(cp.r, R, cp.a, cp.p);

        if (R == null || rR != null) {
            System.out.println("Полученное R не корректное!");
            deleteAll();
            return;
        }

        System.out.println("Введите бит (0 или 1):");
        Scanner sc = new Scanner(System.in);
        printStr(sc.nextLine(), "rand_bit.txt");
        sc.close();
    }

    void fourth() throws IOException {
        //Получил от верификатора
        int bit = Integer.parseInt(readOneStr("rand_bit.txt"));
        //

        String filePath = "k_4.txt";
        Files.deleteIfExists(new File(filePath).toPath());

        if (bit == 0) {
            //Отдает k
            Files.copy(new File("k.txt").toPath(), new File(filePath).toPath());
        } else {
            //Отдает k1
            Files.copy(new File("k1.txt").toPath(), new File(filePath).toPath());
        }
    }

    void fifth() throws IOException {
        CommonParams cp = getCommonParams();

        //Было полученно на прошлых шагах
        BigInteger K = new BigInteger(readOneStr("k_4.txt"));
        int bit = Integer.parseInt(readOneStr("rand_bit.txt"));

        Pair<BigInteger, BigInteger> chPoint = null;
        if (bit == 0) {
            chPoint = multPoint(K, cp.P, cp.a, cp.p);
        } else {
            chPoint = multPoint(K, cp.Q, cp.a, cp.p);
        }

        //Было полученно на прошлых шагах
        Pair<BigInteger, BigInteger> R = getPoint(readOneStr("R.txt"));
        if (isPointsEquals(R, chPoint)) {
            int k = Integer.parseInt(readOneStr("round.txt"));
            k++;

            System.out.println("Проверка пройдена. Пользователь знает l! С вероятностью " + (1 - 1 / Math.pow(2.0, k)));

            printStr(k + "", "round.txt");
        } else {
            System.out.println("Проверка не пройдена. Пользователь не знает l!");

            Files.deleteIfExists(new File("common_params.txt").toPath());
            Files.deleteIfExists(new File("l.txt").toPath());
        }
    }

    public static CommonParams getCommonParams() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("common_params.txt"));
            CommonParams commonParams = new CommonParams();
            commonParams.p = new BigInteger(br.readLine());
            commonParams.a = new BigInteger(br.readLine());
            commonParams.Q = getPoint(br.readLine());
            commonParams.r = new BigInteger(br.readLine());
            commonParams.P = getPoint(br.readLine());
            br.close();
            return commonParams;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Pair<BigInteger, BigInteger> getPoint(String str) {
        str = str.replace("(", "").replace(")", "");
        String[] strings = str.split(",");
        return new Pair<>(new BigInteger(strings[0].trim()), new BigInteger(strings[1].trim()));
    }

    public static Pair<BigInteger, BigInteger> multPoint(BigInteger k, Pair<BigInteger, BigInteger> point, BigInteger a, BigInteger p) {
        Pair<BigInteger, BigInteger> res = point;
        for (int i = 0; i < k.intValue() - 1; i++) {
            res = sum(res, point, a, p);
        }
        return res;
    }

    public static Pair<BigInteger, BigInteger> sum(Pair<BigInteger, BigInteger> firstPoint, Pair<BigInteger, BigInteger> secondPoint,
                                                   BigInteger a, BigInteger p) {
        try {
            if (firstPoint == null) {
                return null;
            }

            BigInteger lambda;
            BigInteger x1 = firstPoint.getKey(), y1 = firstPoint.getValue();
            BigInteger x2 = secondPoint.getKey(), y2 = secondPoint.getValue();

            if (isPointsEquals(firstPoint, secondPoint)) {
                if (y1.equals(BigInteger.ZERO)) {
                    return null;
                } else {
                    lambda = x1.pow(2);
                    lambda = lambda.multiply(BigInteger.valueOf(3));
                    lambda = lambda.add(a);
                    lambda = lambda.multiply(BigInteger.valueOf(2).multiply(y1).modInverse(p));
                }
            } else {
                BigInteger top = y2.subtract(y1);
                BigInteger bottom = x2.subtract(x1);
                lambda = top.multiply(bottom.modInverse(p));
            }

            BigInteger x3 = lambda.pow(2).subtract(x1).subtract(x2).mod(p);
            BigInteger y3 = x1.subtract(x3).multiply(lambda).subtract(y1).mod(p);
            return new Pair<>(x3, y3);
        } catch (ArithmeticException e) {
            return null;
        }
    }

    public static void printPoint(Pair<BigInteger, BigInteger> point, String file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("(" + point.getKey() + "," + point.getValue() + ")");
        bw.close();
    }

    public static void printStr(String str, String file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(str + '\n');
        bw.close();
    }

    public static String readOneStr(String file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String res = br.readLine();
        br.close();
        return res;
    }

    public static boolean isPointsEquals(Pair<BigInteger, BigInteger> firstPoint, Pair<BigInteger, BigInteger> secondPoint) {
        BigInteger x1 = firstPoint.getKey(), y1 = firstPoint.getValue();
        BigInteger x2 = secondPoint.getKey(), y2 = secondPoint.getValue();
        return x1.equals(x2) && y1.equals(y2);
    }

    public static String getStrPoint(Pair<BigInteger, BigInteger> point) {
        return "(" + point.getKey() + "," + point.getValue() + ")";
    }

    public static void deleteAll() throws IOException {
        Files.deleteIfExists(new File("common_params.txt").toPath());
        Files.deleteIfExists(new File("l.txt").toPath());
        Files.deleteIfExists(new File("k.txt").toPath());
        Files.deleteIfExists(new File("k1.txt").toPath());
        Files.deleteIfExists(new File("rand_bit.txt").toPath());
        Files.deleteIfExists(new File("k_4.txt").toPath());
        Files.deleteIfExists(new File("R.txt").toPath());

        Files.deleteIfExists(new File("round.txt").toPath());
        Files.deleteIfExists(new File("step.txt").toPath());
    }
}
