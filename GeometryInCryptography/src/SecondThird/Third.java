package SecondThird;

import First.EllipticalCurves;
import javafx.util.Pair;

import java.io.*;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Scanner;

import static SecondThird.Second.*;

public class Third {
    public static void main(String[] args) throws IOException {
        new Third().run();
    }

    void run() throws IOException {
        System.out.println("Введите, то, что вы хотите сделать:");
        System.out.println("0 - Сгенерировать общие параметры для клиента и банка");
        System.out.println("1 - Сгенерировать вход банка (l)");

        //Результат электронная монета
        System.out.println("2 - 1 шаг. Банк: генерация R'");
        System.out.println("3 - 2 шаг. Клиент: Проверка R', вычисление m'");
        System.out.println("4 - 3 шаг. Банк: Проверка m', вычисление подписи s'");
        System.out.println("5 - 4 шаг. Книет: Проверка подписи, результат");

        //Погашение монеты
        System.out.println("6 - 1 шаг. Проверка m");
        System.out.println("7 - 2 шаг. Проверка функции");
        System.out.println("8 - 3 шаг. Провека равенства");

        System.out.println("9 - Выход");

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
                }
                case "9": {
                    break;
                }
                default: {
                    System.out.println("Ошибка в веденой операции");
                }
            }

            if (Objects.equals(action, "9")) {
                break;
            }

            System.out.println("Введите следующее действие:");
        }

        sc.close();
    }

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

    void second() {
        CommonParams params = getCommonParams();


    }

}
