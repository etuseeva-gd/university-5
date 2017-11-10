package SchnorrScheme;

import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

public class Main {
    private String primes = "primes.txt",
            openKeyY = "open_key_y.txt",
            closeKeyX = "close_key_x.txt",
            numR = "number_r.txt",
            numK = "number_k.txt",
            numE = "number_e.txt",
            numS = "number_s.txt",
            result = "result.txt";

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        new Main().run();
    }

    void run() throws NoSuchAlgorithmException, IOException {
        System.out.println("Выберите операцию:");
        System.out.println("1 - Генерация общих параметров (p, q, g)");
        System.out.println("2 - Алиса: генерация открытого (y), закрытого (x) ключей");
        System.out.println("3 - Алиса: генерация k и вычисление r");
        System.out.println("4 - Боб: генерация e");
        System.out.println("5 - Алиса: вычисление s");
        System.out.println("6 - Боб: вычисление и проверка r");

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
            case "6": {
                sixth();
                break;
            }
            default: {
                System.out.println("Некорректная операция!");
            }
        }
    }

    private void first() throws FileNotFoundException, NoSuchAlgorithmException {
        PrimeNumbers primeNumbers = new PrimeNumbers();

        System.out.println("Введите длину p:");
        Scanner sc = new Scanner(System.in);
        int pLen = Integer.parseInt(sc.nextLine());
        System.out.println("Введите длину q:");
        int qLen = Integer.parseInt(sc.nextLine());

        System.out.println(pLen + " " + qLen);

        BigInteger[] pr = primeNumbers.generatePrimes(pLen, qLen);
        try (PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(primes)))) {
            for (BigInteger aPr : pr) {
                out.println(aPr);
            }
        }

        System.out.println("Простые числа сгенерированы: primes.txt");
        sc.close();
    }

    private void second() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(primes)));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(closeKeyX)));
             PrintWriter out2 = new PrintWriter(new BufferedOutputStream(new FileOutputStream(openKeyY)))) {
            BigInteger p = new BigInteger(in.readLine());
            BigInteger q = new BigInteger(in.readLine());
            BigInteger g = new BigInteger(in.readLine());
            BigInteger x = getRandomNumber(q);

            out.println(x);
            BigInteger y = g.modInverse(p).modPow(x, p);
            out2.println(y);

            System.out.println("Алиса: открытый ключ - open_key_y.txt, закрытый ключ - close_key_x.txt");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void third() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(primes)));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(numR)));
             PrintWriter out2 = new PrintWriter(new BufferedOutputStream(new FileOutputStream(numK)))) {
            BigInteger p = new BigInteger(in.readLine());
            BigInteger q = new BigInteger(in.readLine());
            BigInteger g = new BigInteger(in.readLine());
            BigInteger k = getRandomNumber(q).add(BigInteger.ONE);

            BigInteger r = g.modPow(k, p);
            out.println(r);
            out2.println(k);

            System.out.println("Алиса: k - number_k.txt, r - number_r.txt");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void fourth() throws IOException {
        try (PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(numE)))) {
            System.out.println("Введите параметр устойчивости (t):");
            Scanner sc = new Scanner(System.in);
            int t = Integer.parseInt(sc.nextLine());

            BigInteger limit = new BigInteger("2").pow(t).subtract(BigInteger.ONE);
            BigInteger e = getRandomNumber(limit);
            out.println(e);

            System.out.println("Боб: e - number_e.txt");
            sc.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void fifth() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(primes)));
             BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(numE)));
             BufferedReader in3 = new BufferedReader(new InputStreamReader(new FileInputStream(numK)));
             BufferedReader in4 = new BufferedReader(new InputStreamReader(new FileInputStream(closeKeyX)));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(numS)))) {
            BigInteger p = new BigInteger(in.readLine());
            BigInteger q = new BigInteger(in.readLine());
            BigInteger g = new BigInteger(in.readLine());
            BigInteger e = new BigInteger(in2.readLine());
            BigInteger k = new BigInteger(in3.readLine());
            BigInteger x = new BigInteger(in4.readLine());

            BigInteger s = k.add(e.multiply(x)).mod(q);
            out.println(s);

            System.out.println("Алиса: s - number_s.txt");
        }
    }

    private void sixth() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(primes)));
             BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(numE)));
             BufferedReader in3 = new BufferedReader(new InputStreamReader(new FileInputStream(numS)));
             BufferedReader in4 = new BufferedReader(new InputStreamReader(new FileInputStream(openKeyY)));
             BufferedReader in5 = new BufferedReader(new InputStreamReader(new FileInputStream(numR)));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(result)))) {
            BigInteger p = new BigInteger(in.readLine());
            BigInteger q = new BigInteger(in.readLine());
            BigInteger g = new BigInteger(in.readLine());
            BigInteger e = new BigInteger(in2.readLine());
            BigInteger s = new BigInteger(in3.readLine());
            BigInteger y = new BigInteger(in4.readLine());
            BigInteger r = new BigInteger(in5.readLine());

            BigInteger r1 = g.modPow(s, p).multiply(y.modPow(e, p)).mod(p);
            out.println(r);

            System.out.println("Боб: вычислил r - result.txt, проверил его корректность");
            System.out.println("Результат:");
            if (r.equals(r1)) {
                System.out.println("r - верное!");
            } else {
                System.out.println("r - не верное!");
            }
        }
    }

    private BigInteger getRandomNumber(BigInteger limit) throws NoSuchAlgorithmException {
        return new BigInteger(limit.bitLength(), SecureRandom.getInstance("SHA1PRNG")).mod(limit).add(BigInteger.ONE);
    }
}

//3.5 Схема подписи DSA
