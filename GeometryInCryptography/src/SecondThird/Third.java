package SecondThird;

import First.EllipticalCurves;
import javafx.util.Pair;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

import static SecondThird.Second.*;

public class Third {
    class Coin {
        List<BigInteger> m, s;
        Pair<BigInteger, BigInteger> R;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
//        new Third().bigMessageToSmall(BigInteger.valueOf(122));
        new Third().run();
    }

    void run() throws IOException, NoSuchAlgorithmException {
        System.out.println("Введите, то, что вы хотите сделать:");
        System.out.println("0 - Сгенерировать общие параметры для клиента и банка");
        System.out.println("1 - Сгенерировать вход банка (l)");

        //Результат электронная монета
        System.out.println("2 - 1 шаг. Банк: генерация R'");
        System.out.println("3 - 2 шаг. Клиент: Проверка R', вычисление m'");
        System.out.println("4 - 3 шаг. Банк: Проверка m', вычисление подписи s'");
        System.out.println("5 - 4 шаг. Клиет: Проверка подписи, результат");

        //Погашение монеты
        System.out.println("6 - 1-3 шаги. Погашение монеты");

        System.out.println("7 - Выход");

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Введите действие:");

            String strStep = readOneStr("step.txt");
            int step = strStep == null ? 0 : Integer.parseInt(strStep);
            int action = Integer.parseInt(sc.nextLine());

            if (action > 0 && action < 6) {
                if (action != step + 1) {
                    System.out.println("Порядок генерации не соблюден! Последнее дейстивие = " + step);
                    continue;
                } else {
                    step++;
                    write(step + "", "step.txt");
                }
            }

            switch (action) {
                case 0: {
                    zero();
                    System.out.println("Общие параметры сгенерировались.");
                    break;
                }
                case 1: {
                    first();
                    System.out.println("Точка P = lQ вычислена.");
                    break;
                }
                case 2: {
                    second();
                    System.out.println("Вычислено R'.");
                    break;
                }
                case 3: {
                    third();
                    System.out.println("Вычисленно m'.");
                    break;
                }
                case 4: {
                    fourth();
                    System.out.println("Вычисленно s'.");
                    break;
                }
                case 5: {
                    fifth();
                    System.out.println("Результат (m, R, s).");
                    break;
                }
                case 6: {
                    sixth();
                    System.out.println("");
                    break;
                }
                case 7: {
                    sc.close();
                    System.exit(0);
                }
                default: {
                    System.out.println("Неверная операция!");
                }
            }
        }
    }

    //Gen params
    void zero() throws IOException {
        deleteAll();
        //p a Q r
        new EllipticalCurves().genParams("common_params.txt");
    }

    void first() throws IOException {
        BigInteger p = null, a = null, r = null;
        Pair<BigInteger, BigInteger> Q = null;
        try (BufferedReader br = new BufferedReader(new FileReader("common_params.txt"))) {
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

        Pair<BigInteger, BigInteger> P = multPoint(l, Q, a, p);

        BufferedWriter bw = new BufferedWriter(new FileWriter("common_params.txt", true));
        bw.write(getStrPoint(P) + "\n");
        bw.close();
    }

    //Gen coin
    void second() throws NoSuchAlgorithmException, IOException {
        CommonParams params = getCommonParams();

        BigInteger k1;
        do {
            k1 = getRandomNumber(params.r);
            Pair<BigInteger, BigInteger> R1 = multPoint(k1, params.Q, params.a, params.p);

            if (!f(R1).equals(BigInteger.ZERO)) {
                write(k1 + "", "k1.txt");
                write(getStrPoint(R1), "R1.txt");
                break;
            }
        } while (true);
    }

    void third() throws IOException, NoSuchAlgorithmException {
        CommonParams params = getCommonParams();
        Pair<BigInteger, BigInteger> R1 = getPoint(readOneStr("R1.txt"));

        if (!isBelongsCurve(R1, params.a, params.p)) {
            System.out.println("Не корректное R'!");
            deleteAll();
            System.exit(1);
        }

        BigInteger alpha;
        Pair<BigInteger, BigInteger> R;
        do {
            alpha = getRandomNumber(params.r);
            R = multPoint(alpha, R1, params.a, params.p);
            if (!f(R).equals(BigInteger.ZERO)) {
                break;
            }
        } while (true);
        write(getStrPoint(R), "R.txt");

        BigInteger FR1Inv = f(R1).modInverse(params.r);
        BigInteger betta = f(R).multiply(FR1Inv).mod(params.r);
        write(betta + "", "betta.txt");

        List<BigInteger> m = bigMessageToSmall(params.r);

        StringBuilder m1 = new StringBuilder();
        for (BigInteger mt : m) {
            BigInteger bettaInv = betta.modInverse(params.r);
            m1.append(alpha.multiply(bettaInv).multiply(mt).mod(params.r)).append('\n');
        }
        write(m1 + "", "m1.txt");
    }

    void fourth() throws IOException {
        CommonParams params = getCommonParams();

        BigInteger l = new BigInteger(readOneStr("l.txt"));
        Pair<BigInteger, BigInteger> R1 = getPoint(readOneStr("R1.txt"));
        BigInteger k1 = new BigInteger(readOneStr("k1.txt"));

        List<BigInteger> m1 = readNumbers("m1.txt");

        StringBuilder s1 = new StringBuilder();
        for (int i = 0; i < m1.size(); i++) {
            BigInteger m1t = m1.get(i);
            if (m1t.equals(BigInteger.ZERO)) {
                System.out.println("Не корректное m'!");
                deleteAll();
                System.exit(1);
            }
            s1.append(l.multiply(f(R1)).add(k1.multiply(m1t)).mod(params.r)).append('\n');
        }
        write(s1 + "", "s1.txt");
    }

    void fifth() throws IOException {
        CommonParams params = getCommonParams();

        Pair<BigInteger, BigInteger> R1 = getPoint(readOneStr("R1.txt"));
        BigInteger betta = new BigInteger(readOneStr("betta.txt"));

        List<BigInteger> m1 = readNumbers("m1.txt");
        List<BigInteger> s1 = readNumbers("s1.txt");

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < s1.size(); i++) {
            BigInteger m1t = m1.get(i), s1t = s1.get(i);

            Pair<BigInteger, BigInteger> right1 = multPoint(f(R1), params.P, params.a, params.p);
            Pair<BigInteger, BigInteger> right2 = multPoint(m1t, R1, params.a, params.p);
            Pair<BigInteger, BigInteger> right = sumPoints(right1, right2, params.a, params.p);

            Pair<BigInteger, BigInteger> left = multPoint(s1t, params.Q, params.a, params.p);

            if (!isPointsEquals(left, right)) {
                System.out.println("Ошибка при генерации подписи!");
                deleteAll();
                System.exit(1);
            }

            s.append(s1t.multiply(betta).mod(params.r)).append('\n');
        }

        //Todo: maybe remove
        deleteUnnecessaryFiles();

        write(s + "", "s.txt");
    }

    //Store
    void sixth() throws IOException {
        CommonParams params = getCommonParams();
        Coin coin = getCoin();

        for (int i = 0; i < coin.m.size(); i++) {
            if (coin.m.get(i).equals(BigInteger.ZERO)) {
                System.out.println("Подпись недействительна! m = 0");
                System.exit(1);
            }
        }

        if (f(coin.R).equals(BigInteger.ZERO)) {
            System.out.println("Подпись недействительна! f(R) = 0");
            System.exit(1);
        }

        for (int i = 0; i < coin.m.size(); i++) {
            BigInteger s = coin.s.get(i), m = coin.m.get(i);

            Pair<BigInteger, BigInteger> left = multPoint(s, params.Q, params.a, params.p);
            Pair<BigInteger, BigInteger> right1 = multPoint(f(coin.R), params.P, params.a, params.p);
            Pair<BigInteger, BigInteger> right2 = multPoint(m, coin.R, params.a, params.p);

            Pair<BigInteger, BigInteger> right = sumPoints(right1, right2, params.a, params.p);

            if (!isPointsEquals(left, right)) {
                System.out.println("Подпись недействительна! Соотношение не выполняется!");
                System.exit(1);
            }
        }

        System.out.println("Подпись корректна!");
    }

    Coin getCoin() throws IOException {
        CommonParams params = getCommonParams();
        Coin coin = new Coin();
        coin.m = bigMessageToSmall(params.r);
        coin.s = readNumbers("s.txt");
        coin.R = getPoint(readOneStr("R.txt"));
        return coin;
    }

    boolean isBelongsCurve(Pair<BigInteger, BigInteger> point, BigInteger a, BigInteger p) {
        BigInteger x = point.getKey(), y = point.getValue();
        return y.pow(2).mod(p).equals(x.pow(3).add(a.multiply(x)).mod(p));
    }

    BigInteger f(Pair<BigInteger, BigInteger> point) {
        return point.getKey();
    }

    private BigInteger getRandomNumber(BigInteger limit) throws NoSuchAlgorithmException {
        BigInteger res = new BigInteger(limit.bitLength(), SecureRandom.getInstance("SHA1PRNG")).mod(limit);
        if (res.equals(BigInteger.ZERO)) {
            res.add(BigInteger.ONE);
        }
        return res;
    }

    void deleteAll() throws IOException {
        Files.deleteIfExists(new File("common_params.txt").toPath());
        Files.deleteIfExists(new File("l.txt").toPath());

        Files.deleteIfExists(new File("k1.txt").toPath());
        Files.deleteIfExists(new File("R1.txt").toPath());

        Files.deleteIfExists(new File("R.txt").toPath());
        Files.deleteIfExists(new File("betta.txt").toPath());
        Files.deleteIfExists(new File("m1.txt").toPath());

        Files.deleteIfExists(new File("s1.txt").toPath());

        Files.deleteIfExists(new File("coin.txt").toPath());

        Files.deleteIfExists(new File("s.txt").toPath());

        Files.deleteIfExists(new File("step.txt").toPath());
    }

    void deleteUnnecessaryFiles() throws IOException {
        Files.deleteIfExists(new File("l.txt").toPath());
        Files.deleteIfExists(new File("k1.txt").toPath());
        Files.deleteIfExists(new File("R1.txt").toPath());
        Files.deleteIfExists(new File("betta.txt").toPath());
        Files.deleteIfExists(new File("m1.txt").toPath());
        Files.deleteIfExists(new File("s1.txt").toPath());
        Files.deleteIfExists(new File("step.txt").toPath());
    }

    public static Pair<BigInteger, BigInteger> sumPoints(Pair<BigInteger, BigInteger> firstPoint, Pair<BigInteger, BigInteger> secondPoint,
                                                         BigInteger a, BigInteger p) {
        try {
            if (firstPoint == null && secondPoint != null) {
                return secondPoint;
            } else if (secondPoint == null && firstPoint != null) {
                return firstPoint;
            } else if (secondPoint == null && firstPoint == null) {
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

    List<BigInteger> bigMessageToSmall(BigInteger r) {
        StringBuilder message = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("message.txt"))) {
            String line = br.readLine();
            while (line != null) {
                message.append(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] byteArr = String.valueOf(message).getBytes();
        BigInteger m = new BigInteger(byteArr);
        String mStr = String.valueOf(m);

        List<BigInteger> mess = new ArrayList<>();

        int i = mStr.length() - 1;
        while (i >= 0) {
            int j = 0;
            while (i - j >= 0 && new BigInteger(mStr.substring(i - j)).compareTo(r) < 0) {
                j++;
            }
            mess.add(new BigInteger(mStr.substring(i - j + 1)));
            mStr = mStr.substring(0, i - j + 1);
            i = mStr.length() - 1;
        }

        return mess;
    }

    List<BigInteger> readNumbers(String file) {
        List<BigInteger> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while (line != null) {
                lines.add(new BigInteger(line));
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
