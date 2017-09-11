import com.sun.org.glassfish.gmbal.ManagedAttribute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        List<String> input = readFile("input.txt");
//        numbs(input);

        polynomials(input);
    }

    private static void numbs(List<String> input) {
        String[] strNumb = input.get(0).split(" ");
        int a = Integer.parseInt(strNumb[0]), b = Integer.parseInt(strNumb[1]);

        System.out.println("НОД(" + a + ", " + b + ") = " + gcd(a, b));
        System.out.println("НОД(" + a + ", " + b + ") = " + binaryGcd(a, b));
        System.out.println("--------");
        System.out.println(Arrays.toString(extendedGcd(a, b)));
    }

    private static int gcd(int a, int b) {
        while (b > 0) {
            a %= b;

            int t = a;
            a = b;
            b = t;
        }
        return a;
    }

    private static int binaryGcd(int a, int b) {
        int g = 1;

        while (a % 2 == 0 && b % 2 == 0) {
            a /= 2;
            b /= 2;
            g *= 2;
        }

        while (a != 0) {
            while (a % 2 == 0) a /= 2;

            while (b % 2 == 0) b /= 2;

            if (a >= b) {
                a -= b;
            } else {
                b -= a;
            }
        }

        return g * b;
    }

    private static int[] extendedGcd(int a, int b) {
        if (b == 0) {
            return new int[]{a, 1, 0};
        } else {
            int[] ans = extendedGcd(b, a % b);
            int d = ans[0], x = ans[1], y = ans[2];
            return new int[]{d, y, x - y * (a / b)};
        }
    }

    static class Polynomial {
        private int[] c;
        private int deg;

        Polynomial(int[] c) {
            this.c = c;
            this.deg = c.length + 1;
        }

        static int[] read(String str) {
            String[] nums = str.split(" ");

            int[] a = new int[nums.length];
            for (int i = 0; i < nums.length; i++) {
                a[i] = Integer.parseInt(nums[i]);
            }

            return a;
        }

        static int[] sum(int a[], int b[]) {
            int maxLen = Math.max(a.length, b.length), minLen = Math.min(a.length, b.length);

            int[] c = new int[maxLen];
            for (int i = 0; i < minLen; i++) {
                c[i] = a[i] + b[i];
            }

            if (a.length < b.length) {
                System.arraycopy(b, minLen, c, minLen, maxLen - minLen);
            } else {
                System.arraycopy(a, minLen, c, minLen, maxLen - minLen);
            }

            return c;
        }

        static int[] multConst(int a[], int x) {
            int c[] = new int[a.length];
            for (int i = 0; i < a.length; i++) {
                c[i] = a[i] * x;
            }
            return c;
        }

        static int[] divConst(int a[], int x) {
            int c[] = new int[a.length];
            for (int i = 0; i < a.length; i++) {
                c[i] = a[i] / x;
            }
            return c;
        }

        static int[] diff(int a[], int b[]) {
            int maxLen = Math.max(a.length, b.length), minLen = Math.min(a.length, b.length);

            int[] c = new int[maxLen];
            for (int i = 0; i < minLen; i++) {
                c[i] = a[i] - b[i];
            }

            if (a.length < b.length) {
                System.arraycopy(b, minLen, c, minLen, maxLen - minLen);
            } else {
                System.arraycopy(a, minLen, c, minLen, maxLen - minLen);
            }

            return c;
        }

        static int[] divX(int a[]) {
            int j = 0;
            while (a[j] == 0) j++;

            int[] c = new int[a.length - j];
            int k = 0;
            for (int i = j; i < a.length; i++) {
                c[k++] = a[i];
            }

            return c;
        }

        static int[] divOnceX(int a[]) {
            if (a[0] == 0) {
                int[] c = new int[a.length - 1];
                System.arraycopy(a, 1, c, 0, a.length - 1);
                return c;
            }

            return a;
        }

        static int[] multOnceX(int a[]) {
            int[] c = new int[a.length + 1];
            c[0] = 0;
            System.arraycopy(a, 0, c, 1, a.length);
            return c;
        }

        static int[] removeZeros(int a[]) {
            int j = a.length - 1;
            while (a[j] == 0) j--;
            int[] c = new int[j + 1];
            System.arraycopy(a, 0, c, 0, j + 1);
            return c;
        }

        static int[] mult(int a[], int b[]) {
            int[] c = new int[a.length + b.length];

//            for (int k = 0; k < ?; k++) {
//                for (int )
//            }

            return c;
        }

    }

    private static void polynomials(List<String> input) {
        int[] a = Polynomial.read(input.get(0)), b = Polynomial.read(input.get(1));

        System.out.println(Arrays.toString(Polynomial.removeZeros(b)));
//        System.out.println(polynomialGcd(a, b));
    }

    private static int[] polynomialGcd(int[] a, int[] b) {
        int[] g = new int[]{1};

        while (a[0] == 0 && b[0] == 0) {
            a = Polynomial.divOnceX(a);
            b = Polynomial.divOnceX(b);
            g = Polynomial.multOnceX(g);
        }

        int[] u = a, v = b;

        while (u.length != 1) {
            u = Polynomial.divOnceX(a);
            v = Polynomial.divOnceX(b);

            u = Polynomial.removeZeros(u);
            v = Polynomial.removeZeros(v);

            int t = v[0], k = u[0];
            int[] d = Polynomial.diff(Polynomial.multConst(u, t), Polynomial.multConst(v, k));

            int e = 0;
            for (int i = 0; i < d.length; i++) {
                e = gcd(e, a[i]);
            }

            int[] res = Polynomial.divConst(d, e);

            if (u.length > v.length) {
                u = res;
            } else {
                v = res;
            }
        }

        return Polynomial.mult(g, v);
    }

    private static List<String> readFile(String file) {
        List<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(file))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

}