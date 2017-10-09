package FirstLab;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static int[][] readMatrix() {
        List<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get("input.txt"))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int n = Integer.parseInt(lines.get(0));
        int[][] m = new int[n][n];

        for (int i = 1; i < lines.size(); i++) {
            String[] nums = lines.get(i).split(" ");

            for (int j = 0; j < nums.length; j++)
                m[i - 1][j] = Integer.parseInt(nums[j]);
        }

        return m;
    }

    public static void printClosure(String name, int[][] m) {
        int n = m.length;
        System.out.println(name + ":");
        for (int[] aM : m) {
            for (int j = 0; j < n; j++) {
                System.out.print(aM[j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int[][] m = readMatrix();

        System.out.println("Данное замыкание: ");

        if (isReflexive(m))
            System.out.println("- рефлексивное");
        else if (isAntiReflexive(m))
            System.out.println("- антирефлексивное");
        else
            System.out.println("- не рефлексивное");

        if (isSymmetric(m))
            System.out.println("- симметричное");
        else if (isAntiSymmetric(m))
            System.out.println("- антисимметричное");
        else
            System.out.println("- не симметричное");

        if (isTransitive(m))
            System.out.println("- транзитивное");

        if (isOneComplete(m))
            System.out.println("- 1-полное");

        if (isTwoComplete(m))
            System.out.println("- 2-полное");

        if (isSingleValued(m))
            System.out.println("- однозначное");

        if (isRevereSingleValued(m))
            System.out.println("- обратно однозначное");

        int[][] ref = reflexiveСlosure(m);
        printClosure("Рефлексивное замыкание", ref);

        int[][] sym = symmetricСlosure(m);
        printClosure("Симметричное замыкание", sym);

        int[][] trans = transitiveСlosure(m);
        printClosure("Транзитивное замыкание", trans);
    }

    public static boolean isReflexive(int[][] m) {
        int n = m.length;
        for (int i = 0; i < n; i++)
            if (m[i][i] != 1)
                return false;
        return true;
    }

    static boolean isAntiReflexive(int[][] m) {
        int n = m.length;
        for (int i = 0; i < n; i++)
            if (m[i][i] != 0)
                return false;
        return true;
    }

    public static boolean isSymmetric(int[][] m) {
        int n = m.length;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (m[i][j] != m[j][i])
                    return false;
        return true;
    }

    public static boolean isAntiSymmetric(int[][] m) {
        int n = m.length;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (m[i][j] == m[j][i])
                    return false;
        return true;
    }

    public static boolean isTransitive(int[][] m) {
        int n = m.length;
        for (int[] aM : m)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    if (aM[j] == 1 && m[j][k] == 1 && aM[k] == 0)
                        return false;
        return true;
    }

    static boolean isOneComplete(int[][] m) {
        int n = m.length;
        for (int[] aM : m) {
            int k = 0;
            for (int j = 0; j < n; j++)
                k += aM[j];
            if (k == 0)
                return false;
        }
        return true;
    }

    static boolean isTwoComplete(int[][] m) {
        int n = m.length;
        for (int i = 0; i < n; i++) {
            int k = 0;
            for (int[] aM : m)
                k += aM[i];
            if (k == 0)
                return false;
        }
        return true;
    }

    static boolean isSingleValued(int[][] m) {
        int n = m.length;
        for (int[] aM : m) {
            int k = 0;
            for (int j = 0; j < n; j++)
                k += aM[j];
            if (k != 1)
                return false;
        }
        return true;
    }

    static boolean isRevereSingleValued(int[][] m) {
        int n = m.length;
        for (int i = 0; i < n; i++) {
            int k = 0;
            for (int[] aM : m)
                k += aM[i];
            if (k != 1)
                return false;
        }
        return true;
    }

    public static int[][] reflexiveСlosure(int[][] m) {
        int n = m.length;
        int[][] e = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (i == j)
                    e[i][j] = 1;
                else
                    e[i][j] = 0;

        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                result[i][j] = m[i][j] | e[i][j];
        return result;
    }

    public static int[][] symmetricСlosure(int[][] m) {
        int n = m.length;
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                result[i][j] = m[i][j] | m[j][i];
        return result;
    }

    static int[][] mult(int[][] a, int[][] b) {
        int n = a.length;
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    result[i][j] |= a[i][k] & b[k][j];
        return result;
    }

    static int[][] add(int[][] a, int[][] b) {
        int n = a.length;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                a[i][j] |= b[i][j];
        return a;
    }

    public static int[][] transitiveСlosure(int[][] m) {
        int[][] result = m.clone();
        for (int[] aM : m)
            result = add(result, mult(result, result));
        return result;
    }
}
