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
        System.out.println("4 - 3 шаг. Претендент: Предьявление показателя k (или k') на основе бита");
        System.out.println("5 - 4 шаг. Верификатор: Проверка знания l претендента");

        System.out.println("6 - Выход.");

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Введите действие:");

            String strStep = readOneStr("step.txt");
            int step = strStep == null ? 0 : Integer.parseInt(strStep);
            int action = Integer.parseInt(sc.nextLine());

            if (action > 0 && action < 6) {
                if (action != step + 1) {
                    System.out.println("Порядок действия протокола не соблюден! Последнее дейстивие = " + step);
                    continue;
                } else {
                    step++;
                    write(step + "", "step.txt");
                }
            }

            switch (action) {
                case 0: {
                    zero();
                    System.out.println("Параметры сгенерировались!");
                    break;
                }
                case 1: {
                    first();
                    break;
                }
                case 2: {
                    second();
                    break;
                }
                case 3: {
                    third();
                    break;
                }
                case 4: {
                    fourth();
                    break;
                }
                case 5: {
                    fifth();
                    break;
                }
                case 6: {
                    sc.close();
                    System.exit(0);
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
        BigInteger p = null, a = null, r = null;
        Pair<BigInteger, BigInteger> Q = null;
        try(BufferedReader br = new BufferedReader(new FileReader("common_params.txt"))){
            p = new BigInteger(br.readLine());
            a = new BigInteger(br.readLine());
            Q = getPoint(br.readLine());
           r = new BigInteger(br.readLine());
        } catch (FileNotFoundException e) {
            System.out.println("Нет сгенерированных параметров!");
            deleteAll();
            System.exit(1);
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Введите l:");

        BigInteger l = new BigInteger(sc.nextLine());
        while (l.compareTo(r) > 0) {
            System.out.println("Не корректное l (l > r), введите другое.");
            l = new BigInteger(sc.nextLine());
        }

        write(l + "", "l.txt");

        Pair<BigInteger, BigInteger> R = multPoint(l, Q, a, p);

        BufferedWriter bw = new BufferedWriter(new FileWriter("common_params.txt", true));
        bw.write(getStrPoint(R) + "\n");
        bw.close();
    }

    void second() throws IOException {
        CommonParams cp = getCommonParams();

        //Знает сам
        BigInteger l = new BigInteger(readOneStr("l.txt"));
        //

        BigInteger k = new BigInteger(cp.r.bitLength() - 1, new Random()).mod(cp.r);
        write(k + "", "k.txt");
        BigInteger k1 = k.multiply(l).mod(cp.r);
        write(k1 + "", "k1.txt");

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
            System.exit(1);
        }

        write((Math.random() > 0.5 ? 1 : 0) + "", "rand_bit.txt");
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

        BigInteger k = new BigInteger(readOneStr("k_4.txt"));
        int bit = Integer.parseInt(readOneStr("rand_bit.txt"));
        System.out.println(bit);

        Pair<BigInteger, BigInteger> chPoint = null;
        if (bit == 0) {
            chPoint = multPoint(k, cp.P, cp.a, cp.p);
        } else {
            chPoint = multPoint(k, cp.Q, cp.a, cp.p);
        }

        //Было полученно на прошлых шагах
        Pair<BigInteger, BigInteger> R = getPoint(readOneStr("R.txt"));
        if (isPointsEquals(R, chPoint)) {
            String rStr = readOneStr("round.txt");
            int round = rStr == null ? 0 : Integer.parseInt(rStr);
            round++;

            System.out.println("Проверка пройдена. Пользователь знает l с вероятностью " + (1 - 1 / Math.pow(2, round)) + "!");
            write(round + "", "round.txt");

            refreshAll();
        } else {
            System.out.println("Проверка не пройдена. Пользователь не знает l!");
            deleteAll();
            System.exit(1);
        }
    }

    public static CommonParams getCommonParams() throws IOException {
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
        } catch (FileNotFoundException e) {
            System.out.println("Нет сгенерированных параметров!");
            deleteAll();
            System.exit(1);
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

    public static void write(String str, String file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(str);
        bw.close();
    }

    public static String readOneStr(String file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            br.close();
            return line;
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean isPointsEquals(Pair<BigInteger, BigInteger> firstPoint, Pair<BigInteger, BigInteger> secondPoint) {
        if (firstPoint == null && secondPoint == null) {
            return true;
        }
        if (firstPoint == null || secondPoint == null) {
            return false;
        }
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

    public static void refreshAll() throws IOException {
        CommonParams params = getCommonParams();
        try (BufferedWriter bf = new BufferedWriter(new FileWriter("common_params.txt"))) {
            bf.write(params.p + "\n");
            bf.write(params.a + "\n");
            bf.write(getStrPoint(params.Q) + "\n");
            bf.write(params.r + "\n");
        }

        Files.deleteIfExists(new File("l.txt").toPath());

        Files.deleteIfExists(new File("k.txt").toPath());
        Files.deleteIfExists(new File("k1.txt").toPath());
        Files.deleteIfExists(new File("rand_bit.txt").toPath());
        Files.deleteIfExists(new File("k_4.txt").toPath());
        Files.deleteIfExists(new File("R.txt").toPath());

        Files.deleteIfExists(new File("step.txt").toPath());
    }
}
