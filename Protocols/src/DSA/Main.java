package DSA;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

public class Main {
    private String primes = "primes.txt",
            openKeyY = "open_key_y.txt",
            closeKeyX = "close_key_x.txt",
            message = "message.txt",
            message_with_signature = "message_with_signature.txt",
            result_checking = "result_checking.txt";

    public static void main(String[] args) throws Exception {
        new Main().run();
    }

    void run() throws Exception {
        System.out.println("Выберите действие:");
        System.out.println("1. Генерация общих параметров (p, q, g)");
        System.out.println("2. Алиса: Генерация открытого (y), закрытого (x) ключей");
        System.out.println("3. Алиса: Подпись сообщения");
        System.out.println("4. Боб: Проверка подписи");
        System.out.println("P.S. Сообщение должно быть записано в message.txt");

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
            default: {
                System.out.println("Некорректная операция!");
            }
        }
    }

    void first() throws NoSuchAlgorithmException, FileNotFoundException {
        PrimeNumbers primeNumbers = new PrimeNumbers();

        System.out.println("Введите длину p:");
        Scanner sc = new Scanner(System.in);
        int pLen = Integer.parseInt(sc.nextLine());
        System.out.println("Введите длину q:");
        int qLen = Integer.parseInt(sc.nextLine());

        System.out.println(pLen + " " + qLen);

        BigInteger[] pr = primeNumbers.generatePrimes(pLen, qLen);

//        BigInteger[] pr = primeNumbers.generatePrimes(1024, 256);

        try (PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(primes)))) {
            for (BigInteger aPr : pr) {
                out.println(aPr);
            }
        }

        System.out.println("Простые числа сгенерированы: primes.txt");
    }

    void second() throws IOException, NoSuchAlgorithmException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(primes)));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(closeKeyX)));
             PrintWriter out2 = new PrintWriter(new BufferedOutputStream(new FileOutputStream(openKeyY)))) {
            BigInteger p = new BigInteger(in.readLine());
            BigInteger q = new BigInteger(in.readLine());
            BigInteger g = new BigInteger(in.readLine());

            BigInteger x = getRandomNumber(q);
            BigInteger y = g.modPow(x, p);

            out.println(x);
            out2.println(y);

            System.out.println("Алиса: открытый ключ - open_key_y.txt, закрытый ключ - close_key_x.txt");
        }
    }

    void third() throws Exception {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(primes)));
             BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(closeKeyX)));
             BufferedReader in3 = new BufferedReader(new InputStreamReader(new FileInputStream(message)));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(message_with_signature)))) {

            BigInteger p = new BigInteger(in.readLine());
            BigInteger q = new BigInteger(in.readLine());
            BigInteger g = new BigInteger(in.readLine());

            BigInteger x = new BigInteger(in2.readLine());

            String m = in3.readLine();

            BigInteger k, r, s;
            do {
                do {
                    k = getRandomNumber(q);
                    r = g.modPow(k, p).mod(q);
                } while (r.equals(BigInteger.ZERO));
                BigInteger tmp = h(m).add(x.multiply(r));
                s = k.modInverse(q).multiply(tmp).mod(q);
            } while (s.equals(BigInteger.ZERO));

            out.println(r);
            out.println(s);

            System.out.println("Алиса, подпись сообщения. Подпись - message_with_signature.txt");
        }
    }

    void fourth() throws Exception {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(primes)));
             BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(openKeyY)));
             BufferedReader in3 = new BufferedReader(new InputStreamReader(new FileInputStream(message_with_signature)));
             BufferedReader in4 = new BufferedReader(new InputStreamReader(new FileInputStream(message)));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(result_checking)))) {

            BigInteger p = new BigInteger(in.readLine());
            BigInteger q = new BigInteger(in.readLine());
            BigInteger g = new BigInteger(in.readLine());

            BigInteger y = new BigInteger(in2.readLine());

            BigInteger r = new BigInteger(in3.readLine());
            BigInteger s = new BigInteger(in3.readLine());

            String m = in4.readLine();

            BigInteger w = s.modInverse(q);
            BigInteger u1 = h(m).multiply(w).mod(q);

            BigInteger u2 = r.multiply(w).mod(q);
            BigInteger v = g.modPow(u1, p).multiply(y.modPow(u2, p)).mod(p).mod(q); //???

            out.println(v);

            System.out.println("Боб, проверяет подпись. Полученный им результат - result_checking.txt");

            if (v.equals(r)) {
                System.out.println("Подпись верна! Автор подтвержден");
            } else {
                System.out.println("Подпись не верна!");
            }
        }
    }

    BigInteger h(String val) throws Exception {
        String s = sha1(val);
        byte[] byteS = s.getBytes();
        return new BigInteger(byteS);
    }

    private BigInteger getRandomNumber(BigInteger limit) throws NoSuchAlgorithmException {
        return new BigInteger(limit.bitLength(), SecureRandom.getInstance("SHA1PRNG")).mod(limit).add(BigInteger.ONE);
    }

    public String sha1(String input) throws Exception {
        MessageDigest msdDigest = MessageDigest.getInstance("SHA-1");
        msdDigest.update(input.getBytes("UTF-8"), 0, input.length());
        return DatatypeConverter.printHexBinary(msdDigest.digest());
    }
}

//Пункт
//6.2 - скрытый канал, на основе подписи (подпись выбрать самой)