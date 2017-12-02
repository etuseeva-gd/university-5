package SecondThird;

import First.EllipticalCurves;
import javafx.util.Pair;

import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Scanner;

import static SecondThird.Second.*;

public class Third {
    class Coin {
        BigInteger m, s;
        Pair<BigInteger, BigInteger> R;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
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
        System.out.println("5 - 4 шаг. Книет: Проверка подписи, результат");

        //Погашение монеты
        System.out.println("6 - 1-3 шаги. Погашение монеты");

        System.out.println("7 - Выход");

        Scanner sc = new Scanner(System.in);
        while (true) {
            String action = sc.nextLine();

            switch (action) {
                case "0": {
                    zero();
                    System.out.println("Общие параметры сгенерировались.");
                    break;
                }
                case "1": {
                    first();
                    System.out.println("Точка P = lQ вычислена. l сохранено.");
                    break;
                }
                case "2": {
                    second();
                    System.out.println("Вычислено R'.");
                    break;
                }
                case "3": {
                    third();
                    System.out.println("Вычисленно m'.");
                    break;
                }
                case "4": {
                    fourth();
                    System.out.println("Вычисленно s'.");
                    break;
                }
                case "5": {
                    fifth();
                    System.out.println("Результат (m, R, s).");
                    break;
                }
                case "6": {
                    sixth();
                    System.out.println("");
                    break;
                }
                case "7": {
                    break;
                }
                default: {
                    System.out.println("Ошибка в веденой операции");
                }
            }

            if (Objects.equals(action, "7")) {
                break;
            }

            System.out.println("Введите следующее действие:");
        }

        sc.close();
    }

    //Gen params
    void zero() throws IOException {
        //p
        //A
        //Q
        //r
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

    //Gen coin
    void second() throws NoSuchAlgorithmException, IOException {
        CommonParams params = getCommonParams();

        BigInteger k1;
        do {
            k1 = getRandomNumber(params.r);
            Pair<BigInteger, BigInteger> R1 = multPoint(k1, params.Q, params.a, params.p);

            if (!f(R1).equals(BigInteger.ZERO)) {
                printStr(k1 + "", "k1.txt");
                printStr(getStrPoint(R1), "R1.txt");
                break;
            }
        } while (true);
    }

    void third() throws IOException, NoSuchAlgorithmException {
        CommonParams params = getCommonParams();
        Pair<BigInteger, BigInteger> R1 = getPoint(readOneStr("R1.txt"));

        if (!isBelongsCurve(R1, params.a, params.p)) {
            System.out.println("Не корректное R'!");
            //error
            //stop
            return;
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
        printStr(R + "", "R.txt");

        //Todo: check mod p and r
        BigInteger betta = f(R).multiply(f(R1).modInverse(params.p)).mod(params.r);
        printStr(betta + "", "betta.txt");

        BigInteger m = new BigInteger("123"); //Todo: message!!!
        BigInteger m1 = alpha.multiply(betta.modInverse(params.p)).multiply(m).mod(params.r);
        printStr(m1 + "", "m1.txt");
    }

    void fourth() throws IOException {
        CommonParams params = getCommonParams();

        //Todo: rewrite
        BigInteger m1 = new BigInteger(readOneStr("m1.txt"));

        if (m1.equals(BigInteger.ZERO)) {
            System.out.println("Не корректное m'!");
            //error
            //stop
            return;
        }

        BigInteger l = new BigInteger(readOneStr("l.txt"));
        Pair<BigInteger, BigInteger> R1 = getPoint(readOneStr("R1.txt"));
        BigInteger k1 = new BigInteger(readOneStr("k1.txt"));

        BigInteger s1 = l.multiply(f(R1)).add(k1.multiply(m1)).mod(params.r);

        printStr(s1 + "", "s1.txt");
    }

    void fifth() throws IOException {
        CommonParams params = getCommonParams();

        BigInteger s1 = new BigInteger(readOneStr("s1.txt"));
        Pair<BigInteger, BigInteger> R1 = getPoint(readOneStr("R1.txt"));
        BigInteger m1 = new BigInteger(readOneStr("m1.txt"));

        Pair<BigInteger, BigInteger> left = multPoint(s1, params.Q, params.a, params.p);

        Pair<BigInteger, BigInteger> right1 = multPoint(f(R1), params.P, params.a, params.p);
        Pair<BigInteger, BigInteger> right2 = multPoint(m1, R1, params.a, params.p);
        Pair<BigInteger, BigInteger> right = sum(right1, right2, params.a, params.p);

        if (!isPointsEquals(left, right)) {
            System.out.println("Подпись недействительна!");
            //error
            //stop
            return;
        }

        BigInteger betta = new BigInteger(readOneStr("betta.txt"));
        BigInteger s = s1.multiply(betta).mod(params.r);

        BigInteger m = new BigInteger("123"); //Todo: rewrite
        Pair<BigInteger, BigInteger> R = getPoint(readOneStr("R.txt"));

        //Результат
        System.out.println(m);
        System.out.println(R);
        System.out.println(s);

        //Todo: result to file
    }

    //Store
    void sixth() {
        CommonParams params = getCommonParams();
        Coin coin = getCoin();

        if (coin.m.equals(BigInteger.ZERO)) {
            System.out.println("Подпись недействительна! m = 0");
            //error
            //stop
            return;
        }

        if (f(coin.R).equals(BigInteger.ZERO)) {
            System.out.println("Подпись недействительна! f(R) = 0");
            //error
            //stop
            return;
        }

        Pair<BigInteger, BigInteger> left = multPoint(coin.s, params.Q, params.a, params.p);
        Pair<BigInteger, BigInteger> right1 = multPoint(f(coin.R), params.P, params.a, params.p);
        Pair<BigInteger, BigInteger> right2 = multPoint(coin.m, coin.R, params.a, params.p);
        Pair<BigInteger, BigInteger> right = sum(right1, right2, params.a, params.p);

        if (!isPointsEquals(left, right)) {
            System.out.println("Подпись недействительна! Соотношение не выполняется!");
            //error
            //stop
            return;
        }
    }

    Coin getCoin() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("coin.txt"));
            Coin coin = new Coin();
            coin.m = new BigInteger(br.readLine());
            coin.R = getPoint(br.readLine());
            coin.s = new BigInteger(br.readLine());
            br.close();
            return coin;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean isBelongsCurve(Pair<BigInteger, BigInteger> point, BigInteger a, BigInteger p) {
        BigInteger x = point.getKey(), y = point.getValue();
        return y.pow(2).mod(p).equals(x.pow(3).add(a.multiply(x)).mod(p));
    }

    BigInteger f(Pair<BigInteger, BigInteger> point) {
        return point.getValue();
    }

    private BigInteger getRandomNumber(BigInteger limit) throws NoSuchAlgorithmException {
        BigInteger res = new BigInteger(limit.bitLength(), SecureRandom.getInstance("SHA1PRNG")).mod(limit);
        if (res.equals(BigInteger.ZERO)) {
            res.add(BigInteger.ONE);
        }
        return res;
    }
}
