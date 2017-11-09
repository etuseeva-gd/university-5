package SchnorrScheme;

import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        new Main().run();
    }

    void run() throws NoSuchAlgorithmException, IOException {
        System.out.println("Выберите операцию:");
        System.out.println("1 - Генерация простых чисел p, q, g");
        System.out.println("2 - Алиса: генерация открытого, закрытого ключей");
        System.out.println("2 - Алиса: генерация r");
        System.out.println("2 - Боб: генерация e");
        System.out.println("2 - Алиса: подсчет s");
        System.out.println("2 - Боб: проверка r");

        Scanner scanner = new Scanner(System.in);
        String op = scanner.nextLine();
//        switch (op) {
//            case "1": {
//                first();
//                break;
//            }
//            case "2": {
//                second();
//                break;
//            }
//            case "3": {
//                third();
//                break;
//            }
//            case "4": {
//                fourth();
//                break;
//            }
//            case "5": {
//                fifth();
//                break;
//            }
//            case "6": {
//                sixth();
//                break;
//            }
//            default: {
//                System.out.println("Некорректная операция!");
//            }
//        }
    }

    // Генерация простых чисел
    private void first() throws FileNotFoundException, NoSuchAlgorithmException {
        PrimeNumbers primeNumbers = new PrimeNumbers();
        BigInteger[] pr = primeNumbers.generatePrimes();

        String fileName = "primes.txt";
        try (PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(fileName)))) {
            for (BigInteger aPr : pr) {
                out.println(aPr);
            }
        }

        System.out.println("Простые числа сгенерированы - primes.txt");
    }

    // Алиса генерация открытого, закрытого ключей
    private void second() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("primes.txt")));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream("value_x.txt")));
             PrintWriter out2 = new PrintWriter(new BufferedOutputStream(new FileOutputStream("value_y.txt")))) {
            BigInteger p = new BigInteger(in.readLine());
            BigInteger q = new BigInteger(in.readLine());
            BigInteger g = new BigInteger(in.readLine());
            BigInteger x = getRandomNumber(q);

            out.println(x);
            BigInteger y = g.modInverse(p).modPow(x, p);
            out2.println(y);

            System.out.println("Алиса записала закрытый ключ в value_x.txt, открытый ключ в value_y.txt");
        }
    }

    private void third() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("primes.txt")));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream("value_r.txt")));
             PrintWriter out2 = new PrintWriter(new BufferedOutputStream(new FileOutputStream("value_k.txt")))) {
            BigInteger p = new BigInteger(in.readLine());
            BigInteger q = new BigInteger(in.readLine());
            BigInteger g = new BigInteger(in.readLine());
            BigInteger k = getRandomNumber(q);

            BigInteger r = g.modPow(k, p);
            out.println(r);
            out2.println(k);

            System.out.println("Алиса вычислила k и записала в value_k.txt, вычислила r и записала в value_r.txt");
        }
    }

    private void fourth() throws IOException {
        try (PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream("value_e.txt")))) {
            Random random = new Random();
            int t = Math.abs(random.nextInt()) % (1 << 16);
            BigInteger limit = new BigInteger("2").pow(t).subtract(BigInteger.ONE);
            BigInteger e = getRandomNumber(limit);
            out.println(e);
            System.out.println("Боб сгенерировал e и записал в value_e.txt");
        }
    }

    private void fifth() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("primes.txt")));
             BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream("value_e.txt")));
             BufferedReader in3 = new BufferedReader(new InputStreamReader(new FileInputStream("value_k.txt")));
             BufferedReader in4 = new BufferedReader(new InputStreamReader(new FileInputStream("value_x.txt")));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream("value_s.txt")))) {
            BigInteger p = new BigInteger(in.readLine());
            BigInteger q = new BigInteger(in.readLine());
            BigInteger g = new BigInteger(in.readLine());
            BigInteger e = new BigInteger(in2.readLine());
            BigInteger k = new BigInteger(in3.readLine());
            BigInteger x = new BigInteger(in4.readLine());

            BigInteger s = k.add(e.multiply(x)).mod(q);
            out.println(s);

            System.out.println("Алиса вычислила s и записала в value_s.txt");
        }
    }

    private void sixth() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("primes.txt")));
             BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream("value_e.txt")));
             BufferedReader in3 = new BufferedReader(new InputStreamReader(new FileInputStream("value_s.txt")));
             BufferedReader in4 = new BufferedReader(new InputStreamReader(new FileInputStream("value_y.txt")));
             BufferedReader in5 = new BufferedReader(new InputStreamReader(new FileInputStream("value_r.txt")));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream("result.txt")))) {
            BigInteger p = new BigInteger(in.readLine());
            BigInteger q = new BigInteger(in.readLine());
            BigInteger g = new BigInteger(in.readLine());
            BigInteger e = new BigInteger(in2.readLine());
            BigInteger s = new BigInteger(in3.readLine());
            BigInteger y = new BigInteger(in4.readLine());
            BigInteger r = new BigInteger(in5.readLine());

            BigInteger r1 = g.modPow(s, p).multiply(y.modPow(e, p)).mod(p);
            out.println(r);

            System.out.println("Боб вычислил r, записал в result.txt и проверил его");
            if (r.equals(r1)) {
                System.out.println("r - верное!");
            } else {
                System.out.println("r - не верное!");
            }
        }
    }

    private BigInteger getRandomNumber(BigInteger q) {
        Random random = new Random();
        int maxLength = q.bitLength();
        BigInteger result;
        do {
            result = new BigInteger(maxLength, random);
        } while (result.compareTo(q) >= 0);
        return result;
    }

}
