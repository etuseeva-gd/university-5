package OngSchnorrShamir;

import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

public class Main {
    private String commonParams = "common_params.txt",
            openKeyY = "open_key_y.txt",
            closeKeyX = "close_key_x.txt",
            message = "message.txt",
            signature = "signature.txt",
            hiddenMessage = "hidden_message.txt",
            result = "result.txt";

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        new Main().run();
    }

    void run() throws IOException, NoSuchAlgorithmException {
        System.out.println("Выберите действие:");
        System.out.println("1. Генерация открытого (n, k) и закрытых ключей (p, q)");
        System.out.println("2. Алиса: Вычисление закрытого ключа x, открытого ключа y");
        System.out.println("3. Алиса: Подпись сообщения, сокрытие подсознательного сообщения");
        System.out.println("4. Боб/Уолтер: Проверка подлинности сообщения");
        System.out.println("5. Боб: Получение подсознательного сообщения");

        System.out.println("P.S. Безобидное сообщение должно быть записано в message.txt");
        System.out.println("Подсознательное сообщение должно быть записано в hidden_message.txt");

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
        System.out.println("Введите длину p:");
        Scanner sc = new Scanner(System.in);
        int pLen = Integer.parseInt(sc.nextLine());

        PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(commonParams)));
        out.println(genPrimeNum(pLen));
        out.close();

        System.out.println("Общие параметры (p): common_params.txt");
    }

    void second() throws IOException, NoSuchAlgorithmException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(commonParams)));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(closeKeyX)));
             PrintWriter out2 = new PrintWriter(new BufferedOutputStream(new FileOutputStream(openKeyY)))) {
            BigInteger p = new BigInteger(in.readLine());

            BigInteger x;
            do {
                x = genPrimeNum(p.bitLength());
            } while (!x.gcd(p).equals(BigInteger.ONE));

            BigInteger y = new BigInteger("-1").multiply(x.multiply(x)).mod(p);

            out.println(x);
            out2.println(y);

            System.out.println("Алиса: открытый ключ - open_key_y.txt, закрытый ключ - close_key_x.txt");
        }
    }

    void third() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(commonParams)));
             BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(closeKeyX)));
             BufferedReader in3 = new BufferedReader(new InputStreamReader(new FileInputStream(message)));
             BufferedReader in4 = new BufferedReader(new InputStreamReader(new FileInputStream(hiddenMessage)));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(signature)))) {

            BigInteger p = new BigInteger(in.readLine());

            System.out.println("Алиса, подписала сообщение, скрыла подсознательное. Подпись - signature.txt");
        }
    }

    void fourth() {

    }

    void fifth() {

    }

    private BigInteger genPrimeNum(int length) throws NoSuchAlgorithmException {
        return this.getRandBigInteger(length).nextProbablePrime();
    }

    private BigInteger getRandBigInteger(int length) throws NoSuchAlgorithmException {
        return new BigInteger(length, SecureRandom.getInstance("SHA1PRNG"));
    }
}
