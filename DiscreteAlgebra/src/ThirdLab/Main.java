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
                first();
                break;
            }
            case "2": {
                second();
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

    private void first() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt")));
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
        if (checkCommutative(table)) {
            System.out.println("Операция коммутативна");
        } else {
            System.out.println("Операция некоммутативна");
        }
        if (checkAssociative(table, elems)) {
            System.out.println("Операция ассоциативна");
        } else {
            System.out.println("Операция неассоциативна");
        }
    }

    private void second() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt")));
        String[] elem = in.readLine().split(" ");
        Map<String, String> equality = new HashMap<>();
        int n = Integer.parseInt(in.readLine());
        //все соотношения должны быть разные
        for (int i = 0; i < n; i++) {
            String[] s = in.readLine().split("=");
            if (s[0].length() > s[1].length())
                equality.put(s[0], s[1]);
            else {
                equality.put(s[1], s[0]);
            }
        }

        List<String> words = getWords(elem, equality);
        System.out.println("Элементы:");
        for (String word : words) {
            System.out.print(word + " ");
        }
        System.out.println();


        String[][] table = new String[words.size()][words.size()];
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                String tmp = words.get(i).concat(words.get(j));
                table[i][j] = updateFunction(tmp, equality);
            }
        }
        System.out.println("Таблица Кэли:");
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                System.out.print(table[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();


    }

    private void third(BufferedReader in) throws IOException {
        int count = Integer.parseInt(in.readLine());
        int n = Integer.parseInt(in.readLine());
        ArrayList<ArrayList<ArrayList<Integer>>> semiGroupElements = new ArrayList<>();
        for (int k = 0; k < count; k++) {
            ArrayList<ArrayList<Integer>> toAdd = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                ArrayList<Integer> toAdd1 = new ArrayList<>();
                String s = in.readLine();
                String[] split = s.split(" ");
                for (String aSplit : split) {
                    toAdd1.add(Integer.parseInt(aSplit));
                }
                toAdd.add(toAdd1);
            }
            semiGroupElements.add(toAdd);
        }
        buildSemiGroup(semiGroupElements);
    }

    private List<String> getWords(String[] elem, Map<String, String> equality) {
        List<String> result = new ArrayList<>();

        for (String s : elem) {
            String tmp = s;
            if (equality.containsKey(tmp)) {
                tmp = equality.get(tmp);
            }
            if (!result.contains(tmp)) {
                result.add(tmp);
            }
        }
        int len = 2;
        boolean flag = true;
        while (flag) {
            flag = false;
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).length() == len - 1) {
                    for (String s : elem) {
                        String tmp = result.get(i).concat(s);
                        boolean update = true;
                        tmp = updateFunction(tmp, equality);
                        if (!result.contains(tmp)) {
                            result.add(tmp);
                            flag = true;
                        }
                    }
                }
            }
            len++;
        }
        return result;
    }

    private String updateFunction(String tmp, Map<String, String> equality) {
        boolean flag = true;
        while (flag) {
            flag = false;
            for (String s : equality.keySet()) {
                if (tmp.contains(s)) {
                    flag = true;
                    tmp = tmp.replaceAll(s, equality.get(s));
                }
            }
        }
        return tmp;
    }

    private boolean checkCommutative(String[][] table) {
        boolean result = true;
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                if (!table[i][j].equals(table[j][i])) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    private boolean checkAssociative(String[][] table, Map<String, Integer> elems) {
        boolean result = true;
        for (String[] aTable : table) {
            for (int j = 0; j < table.length; j++) {
                for (int k = 0; k < table.length; k++) {
                    if (!table[elems.get(aTable[j])][k].equals(aTable[elems.get(table[j][k])])) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private static void buildSemiGroup(ArrayList<ArrayList<ArrayList<Integer>>> semiGroupElements) {
        Map<Integer, ArrayList<ArrayList<Integer>>> elements = new HashMap<>();
        for (int i = 0; i < semiGroupElements.size(); i++) {
            elements.put(i, semiGroupElements.get(i));
        }
        ArrayList<ArrayList<Integer>> table;
        int count;
        do {
            count = elements.size();
            table = createNewCaley(elements);
        } while (count != elements.size());
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
        ArrayList<Integer> keys = new ArrayList<>(elements.keySet());
        for (Integer key : keys) {
            System.out.println((char) ('a' + key) + " ");
            ArrayList<ArrayList<Integer>> temp = elements.get(key);
            for (ArrayList<Integer> aTemp : temp) {
                for (Integer anATemp : aTemp) {
                    System.out.print(anATemp + " ");
                }
                System.out.println();
            }
        }
    }

    private static ArrayList<ArrayList<Integer>> createNewCaley(Map<Integer, ArrayList<ArrayList<Integer>>> elements) {
        int size = elements.size();
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(new ArrayList<Integer>());
            for (int j = 0; j < size; j++) {
                result.get(i).add(0);
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                ArrayList<ArrayList<Integer>> resultOfMultiply = multiplyMatrix(elements.get(i), elements.get(j));
                boolean flag = true;
                if (elements.containsValue(resultOfMultiply)) {
                    flag = false;
                    for (Map.Entry<Integer, ArrayList<ArrayList<Integer>>> entry : elements.entrySet()) {
                        if (entry.getValue().equals(resultOfMultiply)) {
                            if (j >= result.get(i).size()) {
                                result.get(i).add(entry.getKey());
                            } else {
                                result.get(i).set(j, entry.getKey());
                            }
                            break;
                        }
                    }
                }
                if (flag) {
                    elements.put(elements.size(), resultOfMultiply);
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

    private static ArrayList<ArrayList<Integer>> multiplyMatrix(ArrayList<ArrayList<Integer>> first, ArrayList<ArrayList<Integer>> second) {
        int[][] forResult = new int[first.size()][second.size()];
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for (int i = 0; i < first.size(); i++) {
            for (int j = 0; j < second.get(0).size(); j++) {
                for (int k = 0; k < first.get(0).size(); k++) {
                    if (first.get(i).get(k) == 1 && second.get(k).get(j) == 1) {
                        forResult[i][j] = 1;
                    }
                }
            }
        }
        for (int i = 0; i < forResult.length; i++) {
            result.add(new ArrayList<Integer>());
            for (int j = 0; j < forResult[i].length; j++) {
                result.get(i).add(forResult[i][j]);
            }
        }
        return result;
    }
}

