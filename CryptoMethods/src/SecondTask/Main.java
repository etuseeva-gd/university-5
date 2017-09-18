package SecondTask;

import FirstTask.GCD;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static FirstTask.Main.outToFile;
import static FirstTask.Main.readFromFile;

public class Main {
    public static List<Long> ComparisonSolution(long a, long b, long m) {
        List<Long> res = new ArrayList<>();

        long d = GCD.binaryGCD(a, m);
        if (b % d != 0) {
            return res;
        }

        long a1 = a / d, b1 = b / d, m1 = m / d;
        long[] t = GCD.extendedGCD(a1, m1);
        long x0 = (b1 * t[1]) % m1;
        while (x0 < 0) {
            x0 += m1;
        }
        res.add(x0 % m1);
        for (int i = 1; i <= d - 1; i++) {
            res.add(x0 + i * m1);
        }
        return res;
    }

    public static long ReverseElement(long a, long m) {
        long d = GCD.binaryGCD(a, m);
        if (d != 1) {
            return -1;
        }
        return ComparisonSolution(a, 1, m).get(0);
    }

    public static long[] SystemComparisonSolution(List<Long[]> s) {
        int d = 1;
        for (int i = 0; i < s.size(); i++) {
            d *= s.get(i)[1];
            for (int j = 0; j < s.size(); j++) {
                if (i != j && GCD.binaryGCD(s.get(i)[1], s.get(j)[1]) != 1) {
                    return new long[]{-1, -1};
                }
            }
        }
        int x0 = 0;
        for (int i = 0; i < s.size(); i++) {
            long s1 = s.get(i)[0], p1 = s.get(i)[1], d1 = d / p1;
            x0 += d1 * ReverseElement(d1, p1) * s1;
        }
        return new long[]{x0 % d, d};
    }

    public static void taskOne() {
        List<String> lines = readFromFile();
        String[] nums = lines.get(0).split(" ");

        long a = Long.parseLong(nums[0]),
                b = Long.parseLong(nums[1]),
                m = Long.parseLong(nums[2]);

        List<Long> ans = ComparisonSolution(a, b, m);

        String answer = "";
        if (ans.size() == 0) {
            answer = "Решений нет";
        } else {
            for (int i = 0; i < ans.size(); i++) {
                answer += MessageFormat.format("x{0} = {1}, {2} * {1} = {3} (mod {4}) \n", i, ans.get(i), a, b, m);
            }
        }
        outToFile(answer);
    }

    public static void taskTwo() {
        List<String> lines = readFromFile();
        String[] nums = lines.get(0).split(" ");

        long a = Long.parseLong(nums[0]),
                m = Long.parseLong(nums[1]);

        long ans = ReverseElement(a, m);

        String answer = "";
        if (ans == -1)
            answer = MessageFormat.format("Не существует обратного элемента для {0} по модулю {1}", a, m);
        else
            answer = MessageFormat.format("x0 = {0}, {1} * {0} = 1 (mod {2})", ans, a, m);

        outToFile(answer);
    }

    public static void taskThree() {
        List<String> lines = readFromFile();
        List<Long[]> input = new ArrayList<>();
        lines.forEach(line -> {
            String[] item = line.split(" ");
            input.add(new Long[]{Long.parseLong(item[0]), Long.parseLong(item[1])});
        });

        long[] ans = SystemComparisonSolution(input);
        String answer = "";
        if (ans[0] == -1 && ans[1] == -1)
            answer = "Решения не существует";
        else
            answer = MessageFormat.format("x0 = {0} (mod {1})", ans[0], ans[1]);
        outToFile(answer);
    }

    public static void main(String[] args) {
        //taskOne();
        //taskTwo();
        //taskThree();
    }
}
