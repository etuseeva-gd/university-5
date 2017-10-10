package ThirdTask;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static FirstTask.Main.outToFile;
import static FirstTask.Main.readFromFile;

public class Main {

    static int getLegendreSymbol(int a, int p) {
        if (a == 0 || a == 1) {
            return a;
        }
        if (a % 2 == 0) {
            return (int) (getLegendreSymbol(a / 2, p) * Math.pow(-1, (p * p - 1) / 8));
        } else {
            return (int) (getLegendreSymbol(p % a, a) * Math.pow(-1, (a - 1) * (p - 1) / 4));
        }
    }

    static boolean isPrime(int num) {
        if (num < 2) return false;
        if (num == 2) return true;
        if (num % 2 == 0) return false;
        for (int i = 3; i * i <= num; i += 2)
            if (num % i == 0) return false;
        return true;
    }

    static List<Integer> factorize(int n) {
        List<Integer> f = new ArrayList<>();

        int p = 2;
        while (true) {
            while (n % p == 0 && n > 0) {
                f.add(p);
                n = n / p;
            }
            p++;
            if (p > n / p) {
                break;
            }
        }

        if (n > 1) {
            f.add(n);
        }

        return f;
    }

    static int legendre(int a, int p) {
        if (a >= p || a < 0) {
            return legendre(a % p, p);
        } else if (a == 1 || a == 0) {
            return a;
        } else if (a == 2) {
            if (p % 8 == 1 || p % 8 == 7) {
                return 1;
            } else {
                return -1;
            }
        } else if (a == p - 1) {
            if (p % 4 == 1) {
                return 1;
            } else {
                return -1;
            }
        } else if (!isPrime(a)) {
            List<Integer> factors = factorize(a);
            int pr = 1;
            for (int factor : factors) {
                pr *= legendre(factor, p);
            }
            return pr;
        } else if (((p - 1) / 2) % 2 == 0 || ((a - 1) / 2) % 2 == 0) {
            return legendre(p, a);
        } else {
            return -1 * legendre(p, a);
        }
    }

    public static void main(String[] args) {
        List<String> lines = readFromFile();
        String[] nums = lines.get(0).split(" ");

        int a = Integer.parseInt(nums[0]),
                p = Integer.parseInt(nums[1]);

//        String out = MessageFormat.format("L({0}, {1}) = {2}", a, p, getLegendreSymbol(a, p)) + "\n" +
//                MessageFormat.format("L({0}, {1}) = {2}", a, p, legendre(a, p));
//        outToFile(out);

        outToFile(MessageFormat.format("L({0}, {1}) = {2}", a, p, getLegendreSymbol(a, p)));
    }
}
