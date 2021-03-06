package ThirdLab;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    private void run() throws IOException {
        System.out.println("Что вы хотите сделать?");
        System.out.println("1: Проверить свойства алгебраических операций");
        System.out.println("2: Построить полугруппы по порождающему множеству и определяющему соотношению");
        System.out.println("3: Построить полугруппы бинарных отношений по заданному порождающему множетсву");

        Scanner scanner = new Scanner(System.in);
        String op = scanner.nextLine();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt")));
        switch (op) {
            case "1": {
                first(in);
                break;
            }
            case "2": {
                second(in);
                break;
            }
            case "3": {
                third(in);
                break;
            }
            default: {
                System.out.println("Не коррекная операция!");
                break;
            }
        }
        in.close();
    }

    private void first(BufferedReader in) throws IOException {
        String[] elem = in.readLine().split(" ");
        Map<String, Integer> elems = new HashMap<>();
        int idx = 0;
        for (String s : elem) {
            elems.put(s, idx);
            idx++;
        }
        String[][] table = new String[idx][idx];
        for (int i = 0; i < idx; i++) {
            String s = in.readLine();
            String[] tmpStr = s.split(" ");
            for (int j = 0; j < tmpStr.length; j++) {
                table[i][j] = tmpStr[j];
            }
        }
        if (isCommutative(table)) {
            System.out.println("Операция коммутативна");
        } else {
            System.out.println("Операция некоммутативна");
        }
        if (isAssociative(table, elems)) {
            System.out.println("Операция ассоциативна");
        } else {
            System.out.println("Операция неассоциативна");
        }
    }

    private boolean isCommutative(String[][] m) {
        int n = m.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!m[i][j].equals(m[j][i])) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAssociative(String[][] m, Map<String, Integer> elems) {
        int n = m.length;
        for (String[] aM : m) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    if (!m[elems.get(aM[j])][k].equals(aM[elems.get(m[j][k])])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void second(BufferedReader in) throws IOException {
        //Элементы
        String[] elements = in.readLine().split(" ");

        //Определяющие соотношения
        Map<String, String> equality = new HashMap<>();
        int n = Integer.parseInt(in.readLine());

        for (int i = 0; i < n; i++) {
            String[] s = in.readLine().split("=");
            if (s[0].length() > s[1].length())
                equality.put(s[0], s[1]);
            else {
                equality.put(s[1], s[0]);
            }
        }

        List<String> tElements = getCayleyElements(elements, equality);

        System.out.print("T = {");
        for (int i = 0; i < tElements.size(); i++) {
            if (i == tElements.size() - 1) {
                System.out.print(tElements.get(i));
            } else {
                System.out.print(tElements.get(i) + ", ");
            }
        }
        System.out.print("}");
        System.out.println();

        String[][] tableCayley = new String[tElements.size()][tElements.size()];
        int nT = tableCayley.length;
        for (int i = 0; i < nT; i++) {
            for (int j = 0; j < nT; j++) {
                String tmp = tElements.get(i).concat(tElements.get(j));
                tableCayley[i][j] = updateCayleyElement(tmp, equality);
            }
        }
        System.out.println("Таблица Кэли:");
        for (String[] aTable : tableCayley) {
            for (int j = 0; j < nT; j++) {
                System.out.print(aTable[j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    private List<String> getCayleyElements(String[] elements, Map<String, String> equality) {
        List<String> result = new ArrayList<>();
        for (String s : elements) {
            String tmp = s;
            if (equality.containsKey(tmp)) {
                tmp = equality.get(tmp);
            }
            if (!result.contains(tmp)) {
                result.add(tmp);
            }
        }
        int len = 2;
        while (true) {
            boolean ok = false;
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).length() == len - 1) {
                    for (String s : elements) {
                        String tmp = result.get(i).concat(s);
                        tmp = updateCayleyElement(tmp, equality);
                        if (!result.contains(tmp)) {
                            result.add(tmp);
                            ok = true;
                        }
                    }
                }
            }
            if (!ok) {
                break;
            }
            len++;
        }
        return result;
    }

    private String updateCayleyElement(String str, Map<String, String> equality) {
        while (true) {
            boolean ok = false;
            for (String s : equality.keySet()) {
                if (str.contains(s)) {
                    str = str.replaceAll(s, equality.get(s));
                    ok = true;
                }
            }
            if (!ok) {
                break;
            }
        }
        return str;
    }

    private void third(BufferedReader in) throws IOException {
        //Количество элементов
        String[] line = in.readLine().split(" ");
        int count = Integer.parseInt(line[0]);
        //Размерность матрицы отношения
        int n = Integer.parseInt(line[1]);
        in.readLine();

        ArrayList<ArrayList<ArrayList<Integer>>> elements = new ArrayList<>();
        for (int k = 0; k < count; k++) {
            ArrayList<ArrayList<Integer>> mm = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                ArrayList<Integer> m = new ArrayList<>();
                String[] s = in.readLine().split(" ");
                for (String aS : s) {
                    m.add(Integer.parseInt(aS));
                }
                mm.add(m);
            }
            elements.add(mm);
            in.readLine();
        }

        buildSemiGroup(elements);
    }

    private static void buildSemiGroup(ArrayList<ArrayList<ArrayList<Integer>>> elem) {
        Map<Integer, ArrayList<ArrayList<Integer>>> elements = new HashMap<>();
        for (int i = 0; i < elem.size(); i++) {
            elements.put(i, elem.get(i));
        }
        ArrayList<ArrayList<Integer>> table;
        int count;
        do {
            count = elements.size();
            table = createCayley(elements);
        } while (count != elements.size());

        System.out.println("Элементы:");
        ArrayList<Integer> keys = new ArrayList<>(elements.keySet());
        for (Integer key : keys) {
            System.out.println((char) ('a' + key) + " ");
            for (ArrayList<Integer> aElem : elements.get(key)) {
                for (Integer a : aElem) {
                    System.out.print(a + " ");
                }
                System.out.println();
            }
        }

        System.out.println();
        System.out.println("Таблица Кэли:");
        System.out.print("  ");
        for (int i = 0; i < table.size(); i++) {
            System.out.print((char) ('a' + i) + " ");
        }
        System.out.println();

        for (int i = 0; i < table.size(); i++) {
            System.out.print((char) ('a' + i) + " ");
            for (int j = 0; j < table.get(i).size(); j++) {
                System.out.print((char) ('a' + table.get(i).get(j)) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static ArrayList<ArrayList<Integer>> createCayley(Map<Integer, ArrayList<ArrayList<Integer>>> elements) {
        int n = elements.size();
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(new ArrayList<Integer>());
            for (int j = 0; j < n; j++) {
                result.get(i).add(0);
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                ArrayList<ArrayList<Integer>> multiplyMatrix = multiplyMatrix(elements.get(i), elements.get(j));
                if (elements.containsValue(multiplyMatrix)) {
                    for (Map.Entry<Integer, ArrayList<ArrayList<Integer>>> entry : elements.entrySet()) {
                        if (entry.getValue().equals(multiplyMatrix)) {
                            if (j >= result.get(i).size()) {
                                result.get(i).add(entry.getKey());
                            } else {
                                result.get(i).set(j, entry.getKey());
                            }
                            break;
                        }
                    }
                } else {
                    elements.put(elements.size(), multiplyMatrix);
                    if (j >= result.get(i).size()) {
                        result.get(i).add(elements.size() - 1);
                    } else {
                        result.get(i).set(j, elements.size() - 1);
                    }
                }
            }
        }
        return result;
    }

    private static ArrayList<ArrayList<Integer>> multiplyMatrix(ArrayList<ArrayList<Integer>> a, ArrayList<ArrayList<Integer>> b) {
        int[][] ab = new int[a.size()][b.size()];
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.get(0).size(); j++) {
                for (int k = 0; k < a.get(0).size(); k++) {
                    if (a.get(i).get(k) == 1 && b.get(k).get(j) == 1) {
                        ab[i][j] = 1;
                    }
                }
            }
        }

        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for (int i = 0; i < ab.length; i++) {
            result.add(new ArrayList<>());
            for (int j = 0; j < ab[i].length; j++) {
                result.get(i).add(ab[i][j]);
            }
        }
        return result;
    }
}

