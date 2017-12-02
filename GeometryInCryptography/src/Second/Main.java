package Second;

import javafx.util.Pair;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public class CommonParams {
        BigInteger p, a, r;
        Pair<BigInteger, BigInteger> P, Q;
    }

    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    void run() throws IOException {
        System.out.println("Выберите что вы хотите сделать?");
        System.out.println("0 - Сгенерировать параметры");
        System.out.println("1 - Претендент: Сгенерировать и послать точку R верификатору");
        System.out.println("2 - Верификатор: Проверить R и послать случайный бит");
        System.out.println("3 - Претендент: Предьявление показателя k(или k') на основе бита");
        System.out.println("4 - Верификатор: Проверка знания l претендента");

        Scanner sc = new Scanner(System.in);
        String action = sc.nextLine();

        switch (action) {
            case "0": {
                genParams();
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
            default: {
                System.out.println("Неверная операция!");
            }
        }

        sc.close();
    }

    void genParams() throws IOException {
        deleteAll();

        BufferedWriter bw = new BufferedWriter(new FileWriter("common_params.txt"));
        BufferedWriter bwL = new BufferedWriter(new FileWriter("l.txt"));
        Scanner sc = new Scanner(System.in);

        //common_params.txt
        System.out.println("Введите модуль p:");
        BigInteger p = new BigInteger(sc.nextLine());
        bw.write(p + "\n");

        System.out.println("Введите a:");
        BigInteger a = new BigInteger(sc.nextLine());
        bw.write(a + "\n");

        System.out.println("Введите образующую точку Q(x,y):");
        Pair<BigInteger, BigInteger> Q = getPoint(sc.nextLine());
        bw.write(getStrPoint(Q) + "\n");

        System.out.println("Введите порядок точки Q - r:");
        BigInteger r = new BigInteger(sc.nextLine());
        bw.write(r + "\n");

        //l.txt
        System.out.println("Введите l:");
        BigInteger l = new BigInteger(sc.nextLine());
        bwL.write(l + "\n");

        //common_params.txt
        Pair<BigInteger, BigInteger> R = multPoint(l, Q, a, p);
        bw.write(getStrPoint(R) + "\n");

        sc.close();
        bw.close();
        bwL.close();

        BufferedWriter bwR = new BufferedWriter(new FileWriter("round.txt"));
        bwR.write(0 + "");
        bwR.close();
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
            deleteAll();
            return;
        }

        System.out.println("Введите бит (0 или 1):");
        Scanner sc = new Scanner(System.in);
        printStr(sc.nextLine(), "rand_bit.txt");
        sc.close();
    }

    void third() throws IOException {
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

    public CommonParams getCommonParams() {
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

    public Pair<BigInteger, BigInteger> getPoint(String str) {
        str = str.replace("(", "").replace(")", "");
        String[] strings = str.split(",");
        return new Pair<>(new BigInteger(strings[0].trim()), new BigInteger(strings[1].trim()));
    }

    public Pair<BigInteger, BigInteger> multPoint(BigInteger k, Pair<BigInteger, BigInteger> point, BigInteger a, BigInteger p) {
        Pair<BigInteger, BigInteger> res = point;
        for (int i = 0; i < k.intValue() - 1; i++) {
            res = sum(res, point, a, p);
        }
        return res;
    }

    public Pair<BigInteger, BigInteger> sum(Pair<BigInteger, BigInteger> firstPoint, Pair<BigInteger, BigInteger> secondPoint,
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

    public void printPoint(Pair<BigInteger, BigInteger> point, String file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("(" + point.getKey() + "," + point.getValue() + ")");
        bw.close();
    }

    public void printStr(String str, String file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(str + '\n');
        bw.close();
    }

    public String readOneStr(String file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String res = br.readLine();
        br.close();
        return res;
    }

    public boolean isPointsEquals(Pair<BigInteger, BigInteger> firstPoint, Pair<BigInteger, BigInteger> secondPoint) {
        BigInteger x1 = firstPoint.getKey(), y1 = firstPoint.getValue();
        BigInteger x2 = secondPoint.getKey(), y2 = secondPoint.getValue();
        return x1.equals(x2) && y1.equals(y2);
    }

    public String getStrPoint(Pair<BigInteger, BigInteger> point) {
        return "(" + point.getKey() + "," + point.getValue() + ")";
    }

    public void deleteAll() throws IOException {
        Files.deleteIfExists(new File("common_params.txt").toPath());
        Files.deleteIfExists(new File("l.txt").toPath());
        Files.deleteIfExists(new File("round.txt").toPath());
        Files.deleteIfExists(new File("k.txt").toPath());
        Files.deleteIfExists(new File("k1.txt").toPath());
        Files.deleteIfExists(new File("rand_bit.txt").toPath());
        Files.deleteIfExists(new File("k_4.txt").toPath());
        Files.deleteIfExists(new File("R.txt").toPath());
    }
}
