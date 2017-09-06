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
        numbs(input);
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

    private static void polygons(List<String> input) {
        int[] a, b;
    }

    private static int polygonGcd(int[] a, int[] b) {
        return 0;
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