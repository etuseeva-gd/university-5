package FourthLab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        new Main().run();
    }

    void run() {
        System.out.println("Выберите, что вы хотите сделать:");
        System.out.println("1 - Проверить свойства кольца;");
        System.out.println("2 - Построить подкольцо, идеал, фактор-кольцо по идеалу (по порождающему множеству);");
        System.out.println("3 - Построить гомоморфизм между двумя кольцами;");
        System.out.println("4 - Построить алгебраическое расширение;");

        Scanner scanner = new Scanner(System.in);
        String action = scanner.nextLine();
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            switch (action) {
                case "1": {
                    int[][][] cayleyTables = readCayleyTables(reader);
                    isRing(cayleyTables[0], cayleyTables[1]);
                    break;
                }
                case "2": {
                    int[][][] cayleyTables = readCayleyTables(reader);
                    int[][] sum = cayleyTables[0], mult = cayleyTables[1];
                    if (!isRing(sum, mult)) {
                        return;
                    } else {
                        int[] genSet = readGenSet(reader);

                        List<Integer> subRing = getSubRing(genSet, sum, mult);
                        System.out.println("Подкольцо:");
                        for (Integer element : subRing) {
                            System.out.print(element + " ");
                        }
                        System.out.println();

                        List<Integer> ideal = getIdeal(genSet, sum, mult);
                        System.out.println("Идеал:");
                        for (Integer element : ideal) {
                            System.out.print(element + " ");
                        }
                        System.out.println();

                        HashMap<Integer, List<Integer>> factorRing = getFactorRing(ideal, mult);
                        System.out.println("Фактор-кольцо по идеалу:");
                        factorRing.forEach((key, value) -> {
                            System.out.print(key + ": ");
                            for (Integer integer : value) {
                                System.out.print(integer + " ");
                            }
                        });
                    }
                    break;
                }
                case "3": {
                    String[] split = reader.readLine().split(" ");
                    int firstP = Integer.parseInt(split[0]),
                            secondP = Integer.parseInt(split[1]);

                    int[] ring = new int[secondP];
                    for (int i = 0; i < secondP; i++) {
                        ring[i] = i;
                    }

                    HashMap<Integer, Integer> mapping = new HashMap<>();
                    for (int r : ring) {
                        mapping.put(r, r % firstP);
                    }

                    System.out.println("Отображение:");
                    mapping.forEach((key, value) -> {
                        System.out.println(key + " -> " + value);
                    });
                    break;
                }
                case "4": {
                    int p = Integer.parseInt(reader.readLine());
                    String[] split = reader.readLine().split(" ");
                    int[] m = new int[split.length];
                    for (int i = 0; i < split.length; i++) {
                        m[i] = Integer.parseInt(split[i]);
                    }
                    System.out.println("Модуль поля = " + p);
                    System.out.println("Многочлен m(x) = " + printPolynomial(m));

                    List<int[]> expansion = getElementsExtention(p, m);
                    System.out.print("Элементы алгебраического расширения: ");
                    int rowLength = 0;
                    for (int i = 0; i < expansion.size(); i++) {
                        String s = printPolynomial(expansion.get(i));
                        String prefix = ", ";
                        if (i == expansion.size() - 1) {
                            prefix = "\n";
                        }
                        System.out.print(s + prefix);
                        rowLength = Math.max(rowLength, s.length());
                    }

                    System.out.println("Таблицы Кэли:");
                    printCayleyTable(m, p, expansion, rowLength, 0);
                    printCayleyTable(m, p, expansion, rowLength, 1);
                    break;
                }
                default: {
                    System.out.println("Введите корректное действие!");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int[][][] readCayleyTables(BufferedReader reader) throws IOException {
        int n = Integer.parseInt(reader.readLine());
        int[][] sum = new int[n][n];
        for (int i = 0; i < n; i++) {
            String[] split = reader.readLine().split(" ");
            for (int j = 0; j < n; j++) {
                sum[i][j] = Integer.parseInt(split[j]);
            }
        }

        int[][] mult = new int[n][n];
        for (int i = 0; i < n; i++) {
            String[] split = reader.readLine().split(" ");
            for (int j = 0; j < n; j++) {
                mult[i][j] = Integer.parseInt(split[j]);
            }
        }
        return new int[][][]{sum, mult};
    }

    int[] readGenSet(BufferedReader reader) throws IOException {
        String[] split = reader.readLine().split(" ");
        int[] genSet = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            genSet[i] = Integer.parseInt(split[i]);
        }
        return genSet;
    }

    boolean isRing(int[][] sum, int[][] mult) {
        if (!isСommutative(sum) || !isAssociative(sum) ||
                !hasOppositeElementAdd(sum) || !hasNeutralElementAdd(sum) ||
                !isAssociative(mult) || !isDistributive(sum, mult)) {
            System.out.println("Данное множество не является кольцом!");
            return false;
        } else {
            System.out.println("Данное множество является кольцом!");
            return true;
        }
    }

    boolean isСommutative(int[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m.length; j++) {
                if (m[i][j] != m[j][i]) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean isAssociative(int[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m.length; j++) {
                for (int k = 0; k < m.length; k++) {
                    if (m[m[i][j]][k] != m[i][m[j][k]]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    boolean isDistributive(int[][] sum, int[][] mult) {
        for (int i = 0; i < sum.length; i++) {
            for (int j = 0; j < sum.length; j++) {
                for (int k = 0; k < sum.length; k++) {
                    if ((mult[i][sum[j][k]] != sum[mult[i][j]][mult[i][k]]) ||
                            (mult[sum[j][k]][i] != sum[mult[j][i]][mult[k][i]])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    boolean hasNeutralElementAdd(int[][] m) {
        for (int i = 0; i < m.length; i++) {
            boolean neutral = true;
            for (int j = 0; j < m.length; j++) {
                if (m[i][j] != j) {
                    neutral = false;
                    break;
                }
            }
            if (neutral) {
                return true;
            }
        }
        return false;
    }

    boolean hasOppositeElementAdd(int[][] m) {
        for (int i = 0; i < m.length; i++) {
            boolean opposite = false;
            for (int j = 0; j < m.length; j++) {
                if (m[i][j] == 0) {
                    opposite = true;
                    break;
                }
            }
            if (!opposite) {
                return false;
            }
        }
        return true;
    }

    List<Integer> getSubRing(int[] genSet, int[][] sum, int[][] mult) {
        List<Integer> subRing = new ArrayList<>();
        for (int i = 0; i < genSet.length; i++) {
            subRing.add(genSet[i]);
        }
        int n;
        do {
            n = subRing.size();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int element = sum[i][j];
                    if (!subRing.contains(element)) {
                        subRing.add(element);
                    }
                    element = mult[i][j];
                    if (!subRing.contains(element)) {
                        subRing.add(element);
                    }
                }
            }
        } while (n != subRing.size());
        return subRing;
    }

    List<Integer> getIdeal(int[] genSet, int[][] sum, int[][] mult) {
        List<Integer> ideal = new ArrayList<>();
        for (int i = 0; i < genSet.length; i++) {
            ideal.add(genSet[i]);
        }
        int[] ring = new int[sum.length];
        for (int i = 0; i < sum.length; i++) {
            ring[i] = i;
        }
        int n;
        do {
            n = ideal.size();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int element = sum[i][getJ(ring.length, j)];
                    if (!ideal.contains(element)) {
                        ideal.add(element);
                    }
                    element = mult[i][ring[j]];
                    if (!ideal.contains(element)) {
                        ideal.add(element);
                    }
                }
            }
        } while (n != ideal.size());
        return ideal;
    }

    HashMap<Integer, List<Integer>> getFactorRing(List<Integer> ideal, int[][] sum) {
        HashMap<Integer, List<Integer>> factorRing = new HashMap<>();
        int[] ring = new int[sum.length];
        for (int i = 0; i < sum.length; i++) {
            ring[i] = i;
        }
        boolean[] used = new boolean[sum.length];
        for (int i = 0; i < sum.length; i++) {
            if (!used[i]) {
                used[i] = true;
                for (int j = 0; j < sum.length; j++) {
                    if (ideal.contains(sum[i][getJ(ring.length, j)])) {
                        if (!factorRing.containsKey(i)) {
                            factorRing.put(i, new ArrayList<>());
                        }
                        factorRing.get(i).add(j);
                        used[j] = true;
                    }
                }
            }
        }
        return factorRing;
    }

    int getJ(int n, int j) {
        for (int i = 0; i < n; i++) {
            if ((i + j) % n == 0) {
                return i;
            }
        }
        return -1;
    }

    String printPolynomial(int[] m) {
        StringBuilder builder = new StringBuilder();
        boolean flag = true;
        for (int i = 0; i < m.length; i++) {
            if (m[i] != 0) {
                if (i > 0 && !flag) {
                    builder.append("+");
                }
                flag = false;
                if (i == 0) {
                    builder.append(m[i]);
                } else {
                    if (i == 1) {
                        if (m[i] > 1) {
                            builder.append(m[i]);
                            builder.append("*");
                        }

                        builder.append("x");
                    } else {
                        if (m[i] > 1) {
                            builder.append(m[i]);
                            builder.append("*x^");
                            builder.append(i);
                        } else {
                            builder.append("x^");
                            builder.append(i);
                        }
                    }
                }
            }
        }
        if (flag) {
            builder.append(0);
        }
        return builder.toString();
    }

    List<int[]> getElementsExtention(int p, int[] m) {
        int amount = (int) Math.pow(p, m.length - 1);
        List<int[]> expansion = new ArrayList<>();
        int[] polynomial = new int[m.length];
        for (int i = 0; i < amount; i++) {
            expansion.add(polynomial);
            polynomial = getPolynomial(polynomial, p);
        }
        return expansion;
    }

    int[] getPolynomial(int[] m, int p) {
        int[] polynomial = m.clone();
        for (int i = 0; i < polynomial.length; i++) {
            if (polynomial[i] == p - 1) {
                polynomial[i] = 0;
            } else {
                polynomial[i]++;
                break;
            }
        }
        return polynomial;
    }

    int[] add(int[] x, int[] y, int p) {
        List<Integer> z = new ArrayList<>();
        if (x.length > y.length) {
            for (int aX : x) {
                z.add(aX);
            }
        } else {
            for (int aY : y) {
                z.add(aY);
            }
        }
        for (int i = 0; i < z.size(); i++) {
            z.set(i, (x[i] + y[i]) % p);
        }
        while (z.size() > 1 && z.get(z.size() - 1) == 0) {
            z.remove(z.size() - 1);
        }
        int[] result = new int[z.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = z.get(i);
        }
        return result;
    }

    int[] mult(int[] x, int[] y, int p) {
        ArrayList<Integer> z = new ArrayList<>();
        for (int i = 0; i < x.length + y.length; i++) {
            z.add(0);
        }
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < y.length; j++) {
                z.set(i + j, (z.get(i + j) + ((x[i] * y[j]) % p)) % p);
            }
        }
        while (z.size() > 1 && z.get(z.size() - 1) == 0) {
            z.remove(z.size() - 1);
        }

        int[] result = new int[z.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = z.get(i);
        }
        return result;
    }

    int[] mod(int[] x, int[] y, int p) {
        ArrayList<Integer> z = new ArrayList<>();
        for (int aX : x) {
            z.add(aX);
        }
        if (x.length < y.length || extendedEuclid(y[y.length - 1], p) != 1) {
            int[] result = new int[z.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = z.get(i);
            }
            return result;
        }
        int degX = x.length - 1;
        int degY = y.length - 1;
        int inverse = modInverse(y[y.length - 1], p);
        while (z.size() >= y.length) {
            int degZ = degX - degY;
            int element = (inverse * z.get(degX)) % p;
            for (int i = 0; i < degY + 1; i++) {
                z.set(i + degZ, (z.get(i + degZ) - y[i] * element) % p);
                if (z.get(i + degZ) < 0) {
                    z.set(i + degZ, z.get(i + degZ) + p);
                }
            }
            while (z.size() > 1 && z.get(z.size() - 1) == 0) {
                z.remove(z.size() - 1);
            }
            degX = z.size() - 1;
            if (z.get(z.size() - 1) == 0) {
                break;
            }
        }
        while (z.size() > 1 && z.get(z.size() - 1) == 0) {
            z.remove(z.size() - 1);
        }
        int[] result = new int[z.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = z.get(i);
        }
        return result;
    }

    int modInverse(int x, int p) {
        return (extendedEuclid(x, p) % p + p) % p;
    }

    int extendedEuclid(int a, int b) {
        int x, y;
        int r0 = a, r1 = b, x0 = 1, x1 = 0, y0 = 0, y1 = 1;
        while (true) {
            int q = r0 / r1;
            int r = r0 % r1;
            if (r == 0)
                break;
            else {
                r0 = r1;
                r1 = r;
                x = x0 - q * x1;
                x0 = x1;
                x1 = x;
                y = y0 - q * y1;
                y0 = y1;
                y1 = y;
            }
        }
        x = x1;
        y = y1;
        return x;
    }

    void printCayleyTable(int[] m, int p, List<int[]> ex, int rowLength, int type) {
        for (int i = 0; i < ex.size(); i++) {
            if (i == 0) {
                if (type == 0) {
                    System.out.print("+");
                } else {
                    System.out.print("*");
                }
                for (int j = 0; j < rowLength; j++) {
                    System.out.print(' ');
                }
                for (int[] e : ex) {
                    String str = printPolynomial(e);
                    System.out.print(str);
                    for (int k = 0; k < rowLength - str.length() + 1; k++) {
                        System.out.print(' ');
                    }
                }
                System.out.println();
            }
            for (int j = 0; j < ex.size(); j++) {
                if (j == 0) {
                    String str = printPolynomial(ex.get(i));
                    System.out.print(str);
                    for (int k = 0; k < rowLength - str.length() + 1; k++) {
                        System.out.print(' ');
                    }
                }
                int[] polynomial = type == 0 ? mod(add(ex.get(i), ex.get(j), p), m, p) :
                        mod(mult(ex.get(i), ex.get(j), p), m, p);
                String str = printPolynomial(polynomial);
                System.out.print(str);
                for (int k = 0; k < rowLength - str.length() + 1; k++) {
                    System.out.print(' ');
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}