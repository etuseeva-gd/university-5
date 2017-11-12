package SixthTask;

import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;


public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        new Main().run();
    }

    void run() throws FileNotFoundException {
        Scanner scanSystem = new Scanner(System.in);
        Scanner scanFile = new Scanner(new FileInputStream("input.txt"));
        PrintWriter out = new PrintWriter(new FileOutputStream("output.txt"));

        System.out.println("Выберити действие:");
        System.out.println("1 - Решение уравнения Пелла");
        System.out.println("2 - Решение систем линейных уравнений. Метод Гаусса");
        System.out.println("3 - Представление числа в виде суммы двух квадратов");

        int operationNumber = scanSystem.nextInt();
        switch (operationNumber) {
            case 1: {
                //Вводится N
                long N = scanFile.nextLong();

                String[] answer = first(N);

                out.println("x = " + answer[0]);
                out.println("y = " + answer[1]);
                out.close();
                break;
            }

            case 2: {
                //Простое число p
                long p = Long.parseLong(scanFile.nextLine());
                //Матрица
                List<String> equation = new ArrayList<>();
                while (scanFile.hasNext()) {
                    equation.add(scanFile.nextLine());
                }

                StringBuilder[] answer = second(p, equation);
                for (StringBuilder s : answer) {
                    out.println(s.toString());
                }

                out.close();
                break;
            }
            case 3: {
                //Вводится a - натуральное число
                int a = Integer.parseInt(scanFile.nextLine());

                List<Triple> result = third(a);
                if (result.size() == 0) {
                    out.println("Нет решения!");
                } else {
                    for (Triple triple : result) {
                        out.println(triple.d + " = " + triple.x + "^2" + " + " + triple.y + "^2");
                    }
                }

                out.close();
                break;
            }
        }
    }

    private List<Triple> third(int a) {
        int aa = (a * a + 1);
        boolean[] tmp = new boolean[(int) aa + 1];
        List<Integer> primes = fillSieve(aa, tmp);

        List<Integer> numbers = func(aa, primes);

        Task5Runner task = new Task5Runner();
        List<Triple> result = new ArrayList<>();
        for (Integer d : numbers) {
            double sqrtD = Math.sqrt(d);
            StringBuilder ans = task.firstPart(a, (long) d)[1];
            List<Pair<Integer, Integer>> drobi = parseStringBuilder(ans);
            for (int k = 0; k < drobi.size() - 1; k++) {
                if (sqrtD >= (double) (drobi.get(k).getValue()) &&
                        sqrtD <= (double) (drobi.get(k + 1).getValue())) {
                    int x = (a * drobi.get(k).getValue() - d * drobi.get(k).getKey());
                    int y = drobi.get(k).getValue();
                    result.add(new Triple(d, x, y));
                }
            }
        }
        return result;
    }

    class Triple {
        int d;
        int x;
        int y;

        public Triple(int d, int x, int y) {
            this.d = d;
            this.x = x;
            this.y = y;
        }
    }

    private List<Pair<Integer, Integer>> parseStringBuilder(StringBuilder ans) {
        List<Pair<Integer, Integer>> result = new ArrayList<>();
        String parse = ans.toString();
        parse = parse.replaceAll("\\(", "");
        parse = parse.replaceAll("\\)", "");
        String[] mass = parse.split(",");
        for (int i = 0; i < mass.length; i += 2) {
            int x = Integer.parseInt(mass[i]);
            int y = Integer.parseInt(mass[i + 1]);
            result.add(new Pair<>(x, y));
        }
        return result;
    }

    private List<Integer> func(long a, List<Integer> primes) {
        List<Integer> result = new ArrayList<>();

        for (int i =2; i*i < a; i++){
            if (a % i == 0)
            {
                result.add(i);
                if (!result.contains(a/i))
                    result.add((int)a/i);
            }

        }
        result.remove(Integer.valueOf((int) a));
        Collections.sort(result);
        return result;
    }

    public List<Integer> fillSieve(int n, boolean[] primes) {
        List<Integer> list = new ArrayList<>();
        Arrays.fill(primes, true);
        primes[0] = primes[1] = false;
        for (int i = 2; i < primes.length; ++i) {
            if (primes[i]) {
                list.add(i);
                for (int j = 2; i * j < primes.length; ++j) {
                    primes[i * j] = false;
                }
            }
        }
        return list;
    }

    public String[] first(long a) throws FileNotFoundException {
        Task5Runner task5Runner = new Task5Runner();
        StringBuilder[] answerFromTask5SecondPart = task5Runner.secondPart(0, a);
        StringBuilder drobiInStringBuilder = answerFromTask5SecondPart[1];
        String[] drobiInStringArray = drobiInStringBuilder.toString().split("\\)");
        String answerString = "";
        if (drobiInStringArray.length % 2 != 0) {
            answerString = drobiInStringArray[drobiInStringArray.length - 1];
        } else
            answerString = drobiInStringArray[drobiInStringArray.length - 2];
        answerString = answerString.replaceAll(",\\(", "");
        String[] resolve = answerString.split(",");
        return resolve;
    }

    public StringBuilder[] second(long p, List<String> equation) {
        Map<String, Integer> parametrs = new HashMap<>();
        Map<Integer, String> reverseParameters = new HashMap<>();
        List<List<Pair<Integer, String>>> coeffs = new ArrayList<>();
        List<Integer> b = new ArrayList<>();
        for (String s : equation) {
            s = s.replaceAll(" ", "");
            coeffs.add(new ArrayList<>());
            String[] parsEquation = s.split("=");
            b.add(Integer.parseInt(parsEquation[1]));
            String leftPartOfEquation = parsEquation[0];
            String param = "";
            String coeff = "";
            boolean flag = false;
            for (int i = 0; i < leftPartOfEquation.length(); i++) {
                if (Character.isDigit(leftPartOfEquation.charAt(i))) {
                    if (!flag) {
                        coeff += leftPartOfEquation.charAt(i);
                    } else
                        param += leftPartOfEquation.charAt(i);
                } else if (Character.isAlphabetic(leftPartOfEquation.charAt(i))) {
                    flag = true;
                    param += leftPartOfEquation.charAt(i);
                } else {
                    addParametrs(parametrs, reverseParameters, coeffs, param, coeff);
                    flag = false;
                    coeff = "";
                    param = "";
                    if (leftPartOfEquation.charAt(i) == '-')
                        coeff += "-";
                }

            }

            addParametrs(parametrs, reverseParameters, coeffs, param, coeff);

        }
        int[][] matrix = new int[equation.size()][parametrs.size()];
        for (int i = 0; i < coeffs.size(); i++) {
            for (Pair pair : coeffs.get(i)) {
                matrix[i][parametrs.get((String) pair.getValue())] = (int) pair.getKey();
            }
        }

        return solveEquation(matrix, b, (int) p, reverseParameters);

    }

    private StringBuilder[] solveEquation(int[][] table, List<Integer> b, int p, Map<Integer, String> params) {
        int n = table.length;
        int m = table[0].length;
        int size = Math.min(n, m);
        for (int i = 0; i < size; i++) {
            if (table[i][i] == 0)
                shuffleTable(table, b, i, n, m);
            if (table[i][i] == 0) {
                return new StringBuilder[]{new StringBuilder("Ошибка")};
            }
            int el = (int) firstOrderComparison(table[i][i], 1, p)[0];
            for (int j = 0; j < m; j++) {
                table[i][j] = (table[i][j] * el) % p;
            }
            b.set(i, (b.get(i) * el) % p);

            for (int k = i + 1; k < n; k++) {
                int coff = table[k][i];
                for (int j = 0; j < m; j++) {
                    table[k][j] = (table[k][j] - coff * table[i][j] + 1000 * p) % p;
                }
                b.set(k, (b.get(k) - coff * b.get(i) + 1000 * p) % p);
            }
        }


        for (int i = size - 1; i >= 0; i--) {
            for (int k = i - 1; k >= 0; k--) {
                int coff = table[k][i];
                for (int j = 0; j < m; j++) {
                    table[k][j] = (table[k][j] - coff * table[i][j] + 1000 * p) % p;
                }
                b.set(k, (b.get(k) - coff * b.get(i) + 1000 * p) % p);
            }
        }

        StringBuilder[] answer = new StringBuilder[size];
        for (int i = 0; i < size; i++) {
            StringBuilder str = new StringBuilder();
            str.append(params.get(i)).append("=");
            str.append(new Integer(b.get(i).toString()));
            for (int j = i + 1; j < m; j++) {
                if (table[i][j] == 0)
                    continue;
                if (table[i][j] > 0) {
                    str.append("-");
                } else {
                    str.append("+");
                }
                str.append(Integer.toString(Math.abs(table[i][j])));
                str.append(params.get(j));
            }
            answer[i] = str;
        }

//
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                System.out.print(table[i][j] + " ");
//            }
//            System.out.print(b.get(i) + " ");
//            System.out.println();
//        }
        return answer;
    }

    void shuffleTable(int[][] table, List<Integer> b, int id, int n, int m) {
        for (int i = id + 1; i < n; i++) {
            if (table[i][id] != 0) {
                for (int j = 0; j < m; j++) {
                    int cnt = table[i][j];
                    table[i][j] = table[id][j];
                    table[id][j] = cnt;
                }
                int cnt = b.get(i);
                b.set(i, b.get(id));
                b.set(id, cnt);
                break;
            }
        }
    }

    void addParametrs(Map<String, Integer> parametrs, Map<Integer, String> reverseParameters, List<List<Pair<Integer, String>>> coeffs, String param, String coeff) {
        if (param.length() != 0) {
            if (!parametrs.containsKey(param)) {
                int id = parametrs.size();
                parametrs.put(param, id);
                reverseParameters.put(id, param);
            }
            if (coeff.equals("-"))
                coeff += "1";
            if (Objects.equals(coeff, ""))
                coeff = "1";
            coeffs.get(coeffs.size() - 1).add(new Pair<>(Integer.parseInt(coeff), param));
        }

    }

    public long[] firstOrderComparison(long a, long b, long m) {
        long[] result = null;
        long d = getGCD_binary(a, m);
        if (b % d == 0) {
            a /= d;
            b /= d;
            m /= d;
            long[] newGcd = getGCD_extended(a, m);
            long x0 = b * newGcd[1] % m;
            while (x0 < 0) {
                x0 += m;
            }
            x0 %= m;
            result = new long[(int) d];
            for (int i = 0; i < result.length; i++) {
                result[i] = x0;
                x0 += m;
            }
        }
        return result;
    }

    public static long getGCD_binary(long a, long b) {
        if (b > a) {
            long c = a;
            a = b;
            b = c;
        }
        long result = 0;
        if (a > 0 && b > 0) {
            result = 1;
            while (a % 2 == 0 && b % 2 == 0) {
                a /= 2;
                b /= 2;
                result *= 2;
            }
            long u = a;
            long v = b;
            while (u != 0) {
                while (u % 2 == 0) {
                    u /= 2;
                }
                while (v % 2 == 0) {
                    v /= 2;
                }
                if (u >= v) {
                    u = u - v;
                } else {
                    v = v - u;
                }
            }
            result = result * v;
        }
        return result;
    }

    public static long[] getGCD_extended(long a, long b) {
        boolean rotate = false;
        if (b > a) {
            long c = a;
            a = b;
            b = c;
            rotate = true;
        }
        long result[] = {0, 0, 0};

        if (a > 0 && b > 0) {
            long r[] = {a, b};
            long x[] = {1, 0};
            long y[] = {0, 1};
            int i = 1;
            while (true) {
                long q = r[(i + 1) % 2] / r[i];
                r[(i + 1) % 2] = r[(i + 1) % 2] % r[i];
                if (r[(i + 1) % 2] == 0) {
                    result[0] = r[i];
                    result[1] = x[i];
                    result[2] = y[i];
                    break;
                } else {
                    x[(i + 1) % 2] = x[(i + 1) % 2] - q * x[i];
                    y[(i + 1) % 2] = y[(i + 1) % 2] - q * y[i];
                    i = (i + 1) % 2;
                }
            }
        }

        if (rotate) {
            result[1] += result[2];
            result[2] = result[1] - result[2];
            result[1] -= result[2];
        }
        return result;
    }
}
