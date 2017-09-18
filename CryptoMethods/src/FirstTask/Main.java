package FirstTask;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        nums();
        //polynomials();
    }

    private static void nums() {
        List<String> input = readFromFile();
        String[] nums = input.get(0).split(" ");
        long a = Long.parseLong(nums[0]), b = Long.parseLong(nums[1]);

        String out = "НОД(" + a + ", " + b + ") = " + GCD.gcd(a, b) + "\n";
        out += "НОД(" + a + ", " + b + ") = " + GCD.binaryGCD(a, b) + " (бинарный алгоритм) \n";

        long[] extendedGCD = GCD.extendedGCD(a, b);
        out += "НОД(" + a + ", " + b + ") = " + extendedGCD[0] + ", x = " + extendedGCD[1] + ", y = " + extendedGCD[2] + " (расширенный алгоритм) \n";

        outToFile(out);
    }

    private static void polynomials() {
        List<String> input = readFromFile();
        long[] a = parsePolynomials(input.get(0)), b = parsePolynomials(input.get(1));
        long[] polGCD = GCD.polynomialGCD(a, b);
        outToFile("НОД(a, b) = " + polToStr(polGCD));
    }

    private static long[] parsePolynomials(String s) {
        String[] nums = s.split(" ");
        long[] res = new long[nums.length];
        for (int i = 0; i < nums.length; i++) {
            res[i] = Long.parseLong(nums[i]);
        }
        return res;
    }

    private static String polToStr(long[] p) {
        String s = "";
        for (int i = 0; i < p.length; i++) {
            if (p[i] != 0) {
                if (i == 0) {
                    s += p[i];
                } else if (i == 1) {
                    String pn = p[i] == 1 ? "" : p[i] + "";
                    s += pn + "x";
                } else {
                    String pn = p[i] == 1 ? "" : p[i] + "";
                    s += pn + "x^" + i;
                }

                if (i != p.length - 1) {
                    s += " + ";
                }
            }
        }
        return s;
    }

    public static List<String> readFromFile() {
        List<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get("input.txt"))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void outToFile(String out) {
        try {
            PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
            writer.println(out);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}