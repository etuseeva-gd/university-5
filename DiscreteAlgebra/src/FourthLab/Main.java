package FourthLab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        new Main().run();
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

    void run() {
        System.out.println("Выберите, что вы хотите сделать:");
        System.out.println("1 - Проверить свойства кольца");
        System.out.println("2 - Построить подкольцо");
        System.out.println("3 - Построить идеал");
        System.out.println("4 - Построить фактор-кольцо по идеалу");
        System.out.println("5 - Построить гомоморфизм между двумя кольцами");
        System.out.println("3 - Построить алгебраическое расширение");

        Scanner scanner = new Scanner(System.in);
        String action = scanner.nextLine();
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
             BufferedReader reader1 = new BufferedReader(new FileReader("inputExt.txt"));
             BufferedReader reader2 = new BufferedReader(new FileReader("inputHomo.txt"))) {
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
                        System.out.println("Не кольцо!");
                        return;
                    } else {
                        int[] genSet = readGenSet(reader);
                        ArrayList<Integer> result = getSubRing(genSet, sum, mult);
                        System.out.println("Подкольцо:");
                        for (Integer aResult : result) {
                            System.out.print(aResult + " ");
                        }
                    }
                    break;
                }
                case "3": {
                    n = Integer.parseInt(reader.readLine());
                    System.out.println("Модуль: " + n);
                    elementsSum = new int[n][n];
                    for (int i = 0; i < n; i++) {
                        String[] split = reader.readLine().split(" ");
                        for (int j = 0; j < n; j++) {
                            elementsSum[i][j] = Integer.parseInt(split[j]);
                        }
                    }
                    elementsMul = new int[n][n];
                    for (int i = 0; i < n; i++) {
                        String[] split = reader.readLine().split(" ");
                        for (int j = 0; j < n; j++) {
                            elementsMul[i][j] = Integer.parseInt(split[j]);
                        }
                    }
                    if (!isRing(elementsSum, elementsMul)) {
                        return;
                    } else {
                        String[] split = reader.readLine().split(" ");
                        Integer[] ideal = new Integer[split.length];
                        for (int i = 0; i < split.length; i++) {
                            ideal[i] = Integer.parseInt(split[i]);
                        }
                        ArrayList<Integer> result = buildIdeal(ideal, elementsSum, elementsMul);
                        System.out.println("Идеал:");
                        for (Integer aResult : result) {
                            System.out.print(aResult + " ");
                        }
                    }
                    break;
                }
                case "4": {
                    n = Integer.parseInt(reader.readLine());
                    System.out.println("Модуль: " + n);
                    elementsSum = new int[n][n];
                    for (int i = 0; i < n; i++) {
                        String[] split = reader.readLine().split(" ");
                        for (int j = 0; j < n; j++) {
                            elementsSum[i][j] = Integer.parseInt(split[j]);
                        }
                    }
                    elementsMul = new int[n][n];
                    for (int i = 0; i < n; i++) {
                        String[] split = reader.readLine().split(" ");
                        for (int j = 0; j < n; j++) {
                            elementsMul[i][j] = Integer.parseInt(split[j]);
                        }
                    }
                    if (!isRing(elementsSum, elementsMul)) {
                        return;
                    } else {
                        String[] split = reader.readLine().split(" ");
                        Integer[] ideal = new Integer[split.length];
                        for (int i = 0; i < split.length; i++) {
                            ideal[i] = Integer.parseInt(split[i]);
                        }
                        HashMap<Integer, ArrayList<Integer>> result = buildFactorRing(buildIdeal(ideal, elementsSum, elementsMul), elementsSum);
                        System.out.println("Фактор-кольцо по идеалу:");
                        for (Map.Entry<Integer, ArrayList<Integer>> entry : result.entrySet()) {
                            System.out.print(entry.getKey() + ": ");
                            for (Integer integer : entry.getValue()) {
                                System.out.print(integer + " ");
                            }
                        }
                    }
                    break;
                }
                case "5":
                    int p = Integer.parseInt(reader2.readLine());
                    String[] split = reader2.readLine().split(" ");
                    HashMap<Integer, Integer> result = new HashMap<>();
                    for (String aSplit1 : split) {
                        int temp = Integer.parseInt(aSplit1);
                        result.put(temp, temp % p);
                    }
                    System.out.println("Отображение:");
                    for (Map.Entry<Integer, Integer> entry : result.entrySet()) {
                        System.out.println(entry.getKey() + " -> " + entry.getValue());
                    }
                    break;
                case "6": {
                    p = Integer.parseInt(reader1.readLine());
                    split = reader1.readLine().split(" ");
                    ArrayList<Integer> e = new ArrayList<>();
                    for (String aSplit : split) {
                        e.add(Integer.parseInt(aSplit));
                    }
                    int[] m = new int[e.size()];
                    for (int i = 0; i < e.size(); i++) {
                        m[i] = e.get(i);
                    }
                    System.out.println("Модуль: " + p);
                    System.out.println("m(x): " + printPolynom(m));
                    System.out.print("Элементы алгебраического расширения: ");
                    ArrayList<int[]> extention = getElementsExtention(p, m);
                    int maxLength = 0;
                    for (int i = 0; i < extention.size(); i++) {
                        String s = printPolynom(extention.get(i));
                        if (s.length() > maxLength) {
                            maxLength = s.length();
                        }
                        System.out.print(s);
                        if (i < extention.size() - 1) {
                            System.out.print(", ");
                        }
                        if (i == extention.size() - 1) {
                            System.out.println();
                        }
                    }
                    System.out.println("Таблицы Кэли");
                    for (int i = 0; i < extention.size(); i++) {
                        if (i == 0) {
                            System.out.print("+");
                            for (int j = 0; j < maxLength; j++) {
                                System.out.print(' ');
                            }
                            for (int[] anExtention : extention) {
                                String s = printPolynom(anExtention);
                                System.out.print(s);
                                for (int j = 0; j < maxLength - s.length() + 1; j++) {
                                    System.out.print(' ');
                                }
                            }
                            System.out.println();
                        }
                        for (int j = 0; j < extention.size(); j++) {
                            if (j == 0) {
                                String s = printPolynom(extention.get(i));
                                System.out.print(s);
                                for (int k = 0; k < maxLength - s.length() + 1; k++) {
                                    System.out.print(' ');
                                }
                            }
                            int[] add = div(toAdd(extention.get(i), extention.get(j), p), m, p);
                            String str = printPolynom(add);
                            System.out.print(str);
                            for (int k = 0; k < maxLength - str.length() + 1; k++) {
                                System.out.print(' ');
                            }
                        }
                        System.out.println();
                    }
                    System.out.println();
                    for (int i = 0; i < extention.size(); i++) {
                        if (i == 0) {
                            System.out.print("*");
                            for (int j = 0; j < maxLength; j++) {
                                System.out.print(' ');
                            }
                            for (int[] anExtention : extention) {
                                String s = printPolynom(anExtention);
                                System.out.print(s);
                                for (int l = 0; l < maxLength - s.length() + 1; l++) {
                                    System.out.print(' ');
                                }
                            }
                            System.out.println();
                        }

                        for (int j = 0; j < extention.size(); j++) {
                            if (j == 0) {
                                String s = printPolynom(extention.get(i));
                                System.out.print(s);
                                for (int l = 0; l < maxLength - s.length() + 1; l++) {
                                    System.out.print(' ');
                                }
                            }
                            int[] add = div(mult(extention.get(i), extention.get(j), p), m, p);
                            String str = printPolynom(add);
                            System.out.print(str);
                            for (int k = 0; k < maxLength - str.length() + 1; k++) {
                                System.out.print(' ');
                            }
                        }
                        System.out.println();
                    }
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

    private static int[] mult(int[] x, int[] y, int p) {
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

    private static int[] div(int[] x, int[] y, int p) {
        ArrayList<Integer> z = new ArrayList<>();
        for (int aX : x) {
            z.add(aX);
        }
        if (x.length < y.length || extendedEuklid(y[y.length - 1], p) != 1) {
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

    private static int modInverse(int x, int p) {
        return (extendedEuklid(x, p) % p + p) % p;
    }

    private static int extendedEuklid(int a, int b) {
        int d, x, y;
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
        d = r1;
        x = x1;
        y = y1;
        return x;
    }

    private static int[] toAdd(int[] x, int[] y, int p) {
        ArrayList<Integer> z = new ArrayList<>();
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

    private static ArrayList<int[]> getElementsExtention(int p, int[] m) {
        int deg = m.length - 1;
        int amount = (int) Math.pow(p, deg);
        ArrayList<int[]> extention = new ArrayList<>();
        int[] polynom = new int[m.length];
        for (int i = 0; i < amount; i++) {
            extention.add(polynom);
            polynom = getPolynom(polynom, p);
        }
        return extention;
    }

    private static int[] getPolynom(int[] m, int p) {
        int[] polynom = new int[m.length];
        System.arraycopy(m, 0, polynom, 0, m.length);
        for (int i = 0; i < polynom.length; i++) {
            if (polynom[i] == p - 1) {
                polynom[i] = 0;
            } else {
                polynom[i]++;
                break;
            }
        }
        return polynom;
    }

    private static String printPolynom(int[] m) {
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

    private static HashMap<Integer, ArrayList<Integer>> buildFactorRing(ArrayList<Integer> ideal, int[][] elementsSum) {
        HashMap<Integer, ArrayList<Integer>> result = new HashMap<>();
        ArrayList<Integer> ring = new ArrayList<>();
        for (int i = 0; i < elementsSum.length; i++) {
            ring.add(i);
        }
        boolean[] used = new boolean[elementsSum.length];
        for (int i = 0; i < elementsSum.length; i++) {
            if (!used[i]) {
                used[i] = true;
                for (int j = 0; j < elementsSum.length; j++) {
                    if (ideal.contains(elementsSum[i][findContr(ring, j)])) {
                        if (!result.containsKey(i)) {
                            result.put(i, new ArrayList<Integer>());
                        }
                        result.get(i).add(j);
                        used[j] = true;
                    }
                }
            }
        }
        return result;
    }

    private static ArrayList<Integer> buildIdeal(Integer[] ideal, int[][] elementsSum, int[][] elementsMul) {
        ArrayList<Integer> result = new ArrayList<>(Arrays.asList(ideal));
        ArrayList<Integer> ring = new ArrayList<>();
        for (int i = 0; i < elementsSum.length; i++) {
            ring.add(i);
        }
        int size;
        do {
            size = result.size();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int newSum = elementsSum[i][findContr(ring, j)];
                    int newMul = elementsMul[i][ring.get(j)];
                    if (!result.contains(newSum)) {
                        result.add(newSum);
                    }
                    if (!result.contains(newMul)) {
                        result.add(newMul);
                    }
                }
            }
        } while (size != result.size());
        return result;
    }

    private static int findContr(ArrayList<Integer> ring, int j) {
        for (int i = 0; i < ring.size(); i++) {
            if ((j + i) % ring.size() == 0) {
                return i;
            }
        }
        return -1;
    }

    private static List<Integer> getSubRing(int[] genSet, int[][] sum, int[][] mult) {
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

    //Проверка свойств кольца
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
}