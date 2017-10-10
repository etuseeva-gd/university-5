package FourthTask;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.List;

import static FirstTask.Main.outToFile;
import static FirstTask.Main.readFromFile;

public class Main {

    public static void main(String[] args) {
        //first();
        second();
    }

    static void first() {
        List<String> lines = readFromFile();
        String s = lines.get(0);
        String ss[] = s.split(" ");
        int a = Integer.parseInt(ss[0]), n = Integer.parseInt(ss[1]);
        outToFile(MessageFormat.format("Y({0}, {1}) = {2}", a, n, Y(a, n)));
    }

    static void second() {
        List<String> lines = readFromFile();
        String ss[] = lines.get(0).split(" ");
        long a = Long.parseLong(ss[0]), p = Long.parseLong(ss[1]);
        if (ComparisonSolution(a, p)[0] == -1)
            outToFile("Нет решений");
        else {
            long[] r = ComparisonSolution(a, p);
            outToFile(r[0] + " " + r[1]);
        }
    }

    public static int Y(int a, int n) {
        int res = 1;
        int s = 0;

        while (true) {
            if (a == 0)
                return 0;

            if (a == 1)
                return res;

            int k = 0;
            int a1 = a;
            while (a1 % 2 == 0) {
                a1 /= 2;
                k++;
            }

            if (k % 2 == 0) {
                s = 1;
            } else {
                if (n % 8 == 1 || n % 8 == 7)
                    s = 1;
                else if (n % 8 == 3 || n % 8 == 5)
                    s = -1;
            }

            if (a1 == 1)
                return res * s;

            if (n % 4 == 3 && a1 % 4 == 3)
                s = -s;

            a = n % a1;
            n = a1;
            res = res * s;
        }
    }

    public static long[] ComparisonSolution(long a, long p) {
        if (Y((int) a, (int) p) == -1)
            return new long[]{-1};
        int n = 0;
        if (Y((int) a, (int) p) == 1) {
            for (int i = 2; i < p; i++) {
                if (Y(i, (int) p) == -1) {
                    n = i;
                    break;
                }
            }
        }

        long h = p - 1;
        long k = 0;
        while (h % 2 == 0) {
            h /= 2;
            k++;
        }

        long a1 = Long.parseLong(String.valueOf(BigInteger.valueOf(a).modPow(BigInteger.valueOf((h + 1) / 2), BigInteger.valueOf(p))));

        long a2 = Long.parseLong(String.valueOf(BigInteger.valueOf(a).modInverse(BigInteger.valueOf(p))));
        long n1 = Long.parseLong(String.valueOf(BigInteger.valueOf(n).modPow(BigInteger.valueOf(h), BigInteger.valueOf(p))));
        long n2 = 1;
        long j = 0;

        for (int i = 0; i < k - 1; i++) {
            long b = (a1 * n2) % p;
            long c = (a2 * b * b) % p;
            long d = Long.parseLong(String.valueOf(BigInteger.valueOf(c).modPow(BigInteger.valueOf((int) Math.pow(2, k - 2 - i)), BigInteger.valueOf(p))));
            int ji = 0;
            if (d == 1)
                ji = 0;
            else if (d == p - 1)
                ji = 1;
            n2 = n2 * Long.parseLong(String.valueOf(BigInteger.valueOf(n1).pow(((int) Math.pow(2, i) * ji)))) % p;
        }
        return new long[]{(a1 * n2) % p, ((-1) * (a1 * n2) % p) + p};
    }
}
