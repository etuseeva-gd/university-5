package Second;

import javafx.util.Pair;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Random;
import java.util.Scanner;

public class Main {
    class CommonParams {
        BigInteger p, a, r;
        Pair<BigInteger, BigInteger> P, Q;
    }

    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    void run() throws IOException {
        System.out.println("Выберите что вы хотите сделать?");
        System.out.println("1 - Претендент: Сгенерировать и послать точку R верификатору");
        System.out.println("2 - Верификатор: Проверить R и послать случайный бит");
        System.out.println("3 - Претендент: Предьявление показателя k(или k') на основе бита");
        System.out.println("4 - Верификатор: Проверка знания l претендента");
        System.out.println();
        System.out.println("P.S.1 Общие параметры должны быть записаны в common_params.txt");
        System.out.println("1 строка - p");
        System.out.println("2 строка - a");
        System.out.println("3 строка - Q");
        System.out.println("4 строка - r");
        System.out.println("5 строка - P");
        System.out.println("P.S.2 l (P = lQ) должна быть записана в l.txt");

        Scanner sc = new Scanner(System.in);
        String action = sc.nextLine();

        switch (action) {
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
            default: {
                System.out.println("Неверная операция!");
            }
        }

        sc.close();
    }

    void first() throws IOException {
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

    void second() throws IOException {
        CommonParams cp = getCommonParams();

        //Получил от претендента
        Pair<BigInteger, BigInteger> R = getPoint(readOneStr("R.txt"));
        //

        Pair<BigInteger, BigInteger> rR = multPoint(cp.r, R, cp.a, cp.p);

        if (R == null || rR != null) {
            System.out.println("Полученное R не корректное!");
            return;
        }

        System.out.println(new Random().nextInt() + "");
        printStr(new Random().nextInt() + "", "rand_bit.txt");
    }

    void third() throws IOException {
        //Получил от верификатора
        int bit = Integer.parseInt(readOneStr("rand_bit.txt"));
        //

        if (bit == 0) {
            //Отдает k
            Files.copy(new File("k.txt").toPath(), new File("k_4.txt").toPath());
        } else {
            //Отдает k1
            Files.copy(new File("k1.txt").toPath(), new File("k_4.txt").toPath());
        }
    }

    void fourth() throws IOException {
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
            System.out.println("Проверка пройдена. Пользователь знает l!");
        } else {
            System.out.println("Проверка не пройдена. Пользователь на знает l!");
        }
    }

    CommonParams getCommonParams() {
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

    Pair<BigInteger, BigInteger> getPoint(String str) {
        str = str.replace("(", "").replace(")", "");
        String[] strings = str.split(",");
        return new Pair<>(new BigInteger(strings[0]), new BigInteger(strings[1]));
    }

    Pair<BigInteger, BigInteger> multPoint(BigInteger k, Pair<BigInteger, BigInteger> point, BigInteger a, BigInteger p) {
        Pair<BigInteger, BigInteger> res = point;
        for (int i = 0; i < k.intValue(); i++) {
            res = sum(res, point, a, p);
        }
        return res;
    }

    Pair<BigInteger, BigInteger> sum(Pair<BigInteger, BigInteger> firstPoint, Pair<BigInteger, BigInteger> secondPoint,
                                     BigInteger a, BigInteger p) {
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
    }

    void printPoint(Pair<BigInteger, BigInteger> point, String file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("(" + point.getKey() + "," + point.getValue() + ")");
        bw.close();
    }

    void printStr(String str, String file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(str);
        bw.close();
    }

    String readOneStr(String file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String res = br.readLine();
        br.close();

        return res;
    }

    boolean isPointsEquals(Pair<BigInteger, BigInteger> firstPoint, Pair<BigInteger, BigInteger> secondPoint) {
        BigInteger x1 = firstPoint.getKey(), y1 = firstPoint.getValue();
        BigInteger x2 = secondPoint.getKey(), y2 = secondPoint.getValue();
        return x1.equals(x2) && y1.equals(y2);
    }

}
