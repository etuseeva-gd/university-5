package OngSchnorrShamir;

import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    private String commonParams = "common_params.txt",
            openKeyY = "open_key_y.txt",
            closeKeyX = "close_key_x.txt",
            signature = "signature.txt",
            message = "message.txt",
            hiddenMessage = "hidden_message.txt",
            result = "result.txt",
            resultMessage = "result_message.txt";

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        new Main().run();
    }

    void run() throws IOException, NoSuchAlgorithmException {
        System.out.println("Выберите действие:");
        System.out.println("1. Генерация общих параметров (n)");
        System.out.println("2. Вычисление закрытого ключа x, открытого ключа y");
        System.out.println("3. Алиса: Подпись сообщения, сокрытие подсознательного сообщения");
        System.out.println("4. Боб/Уолтер: Проверка подлинности сообщения");
        System.out.println("5. Боб: Получение подсознательного сообщения");

        System.out.println("P.S. Безобидное сообщение должно быть записано в message.txt");
        System.out.println("Скрытое сообщение должно быть записано в hidden_message.txt");

        Scanner scanner = new Scanner(System.in);
        String op = scanner.nextLine();
        switch (op) {
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
            default: {
                System.out.println("Некорректная операция!");
            }
        }
    }

    void first() throws FileNotFoundException, NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);

        System.out.println("Введите длину p:");
        int pLen = Integer.parseInt(sc.nextLine());
        System.out.println("Введите длину q:");
        int qLen = Integer.parseInt(sc.nextLine());

        PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(commonParams)));

        BigInteger p = genPrimeNum(pLen), q = genPrimeNum(qLen);
        out.println(p.multiply(q));

        out.close();
        System.out.println("Общие параметры (n): common_params.txt");
    }

    void second() throws IOException, NoSuchAlgorithmException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(commonParams)));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(closeKeyX)));
             PrintWriter out2 = new PrintWriter(new BufferedOutputStream(new FileOutputStream(openKeyY)))) {
            BigInteger n = new BigInteger(in.readLine());

            BigInteger x;
            do {
                x = genPrimeNum(n.bitLength());
            } while (!x.gcd(n).equals(BigInteger.ONE));

            BigInteger x_1 = x.modInverse(n);
            BigInteger y = new BigInteger("-1").multiply(x_1.multiply(x_1)).mod(n);

            out.println(x);
            out2.println(y);

            System.out.println("Открытый ключ - open_key_y.txt, закрытый ключ - close_key_x.txt");
        }
    }

    void third() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(commonParams)));
             BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(closeKeyX)));
             BufferedReader in3 = new BufferedReader(new InputStreamReader(new FileInputStream(message)));
             BufferedReader in4 = new BufferedReader(new InputStreamReader(new FileInputStream(hiddenMessage)));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(signature)))) {

            BigInteger n = new BigInteger(in.readLine());
            BigInteger x = new BigInteger(in2.readLine());

            BigInteger m = strToBI(in3.readLine());
            BigInteger hiddenMes = strToBI(in4.readLine());

            //условие

            BigInteger s1 = m.divide(hiddenMes).add(hiddenMes).divide(new BigInteger("2")).mod(n);
            BigInteger s2 = x.divide(new BigInteger("2")).multiply(m.divide(hiddenMes).subtract(hiddenMes)).mod(n);

            out.println(s1);
            out.println(s2);

            System.out.println("Алиса, подписала сообщение, скрыла подсознательное. Подпись - signature.txt");
        }
    }

    void fourth() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(commonParams)));
             BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(openKeyY)));
             BufferedReader in3 = new BufferedReader(new InputStreamReader(new FileInputStream(message)));
             BufferedReader in4 = new BufferedReader(new InputStreamReader(new FileInputStream(signature)));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(result)))) {

            BigInteger n = new BigInteger(in.readLine());
            BigInteger y = new BigInteger(in2.readLine());

            BigInteger m = strToBI(in3.readLine());

            BigInteger s1 = new BigInteger(in4.readLine());
            BigInteger s2 = new BigInteger(in4.readLine());

            BigInteger m1 = s1.multiply(s1).add(y.multiply(s2.multiply(s2))).mod(n);

            out.println(m1);

            if (Objects.equals(m1, m)) {
                System.out.println("Подпись верна. Отправитель подтвержен");
            } else {
                System.out.println("Подпись не верна. Отправитель не подтвержден");
            }

            System.out.println("Результат проверки в result.txt");
        }
    }

    void fifth() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(commonParams)));
             BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(closeKeyX)));
             BufferedReader in3 = new BufferedReader(new InputStreamReader(new FileInputStream(signature)));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(result)))) {

            BigInteger n = new BigInteger(in.readLine());
            BigInteger x = new BigInteger(in2.readLine());

            BigInteger s1 = new BigInteger(in3.readLine());
            BigInteger s2 = new BigInteger(in3.readLine());

            BigInteger m = s1.subtract(s2.multiply(x.modInverse(n))).mod(n);

            byte[] byteM = m.toByteArray();
            String hiddenMessage = new String(byteM);

            out.println(hiddenMessage);
            System.out.println("Скрытое сообщение после вычисления его из подписи в result_message.txt");
        }
    }

    private BigInteger genPrimeNum(int length) throws NoSuchAlgorithmException {
        return this.getRandBigInteger(length).nextProbablePrime();
    }

    private BigInteger getRandBigInteger(int length) throws NoSuchAlgorithmException {
        return new BigInteger(length, SecureRandom.getInstance("SHA1PRNG"));
    }

    private BigInteger strToBI(String str) {
        byte[] byteMess = str.getBytes();
        return new BigInteger(byteMess);
    }
}
