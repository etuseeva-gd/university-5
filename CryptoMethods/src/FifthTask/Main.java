package FifthTask;

import FirstTask.GCD;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        new Main().run();
    }

    void run() throws FileNotFoundException {
        //Разложение рац числа в конечную обыкновенную непрерывную дробь
        //и вычисление ее подходящих дробей
        //a, b
        first();

        //Разложение квадратичной иррациональноси в периодическую обыкновенную
        //непрерывную дробь и вычисление ее подходящих дробей
        //a, b
        //second();

        //Решение диафантового уравнения
        //a, b, c
        //third();
    }

    public void first() throws FileNotFoundException {
        Scanner scan = new Scanner(new FileInputStream("input.txt"));
        PrintWriter out = new PrintWriter(new FileOutputStream("output.txt"));
        long a = scan.nextLong();
        long b = scan.nextLong();

        List<Long> qList = euclidFuncWithQList(a, b);
        StringBuilder answer = new StringBuilder("Непрерывная дробь:\n");
        answer.append("[").append(qList.get(0)).append("; ");
        for (int i = 1; i < qList.size(); i++) {
            answer.append(qList.get(i)).append(", ");
        }
        answer.delete(answer.length() - 2, answer.length());
        answer.append("]");
        out.println(answer);

        suitableFractions(qList, out);
        out.close();
    }

    public void second() throws FileNotFoundException {
        Scanner scan = new Scanner(new FileInputStream("input.txt"));
        PrintWriter out = new PrintWriter(new FileOutputStream("output.txt"));

        long a = scan.nextLong();
        long b = scan.nextLong();

        List<Pair> pairList = new ArrayList<>();
        long sqrtB = (long) Math.sqrt(b);
        Pair current = new Pair(1, sqrtB * (-1));
        pairList.add(current);
        List<Long> result = new ArrayList<>();
        result.add(a + sqrtB);
        int idx = 0;
        while (true) {
            long tmpB = b - current.b * current.b;
            long gcd = GCD.binaryGCD(Math.abs(current.a), Math.abs(tmpB));
            current.a /= gcd;
            tmpB /= gcd;
            long tmpCel = (current.b * (-1) + sqrtB) * current.a;
            long tmp = current.b * (-1) - tmpCel + tmpCel % tmpB;
            result.add(tmpCel / tmpB);
            current = new Pair(tmpB, tmp);

            if (pairList.contains(current)) {
                idx = pairList.indexOf(current);
                break;
            }
            pairList.add(current);
        }

        StringBuilder answer = new StringBuilder("Непрерывная дробь:\n");
        answer.append("[").append(result.get(0)).append(";");
        for (int i = 1; i < result.size(); i++) {
            if (i == idx + 1)
                answer.append("{");
            answer.append(result.get(i) + ",");
        }
        answer.deleteCharAt(answer.length() - 1);
        answer.append("}]");

        out.println(answer);

        suitableFractions(result, out);
        out.close();
    }

    public void third() throws FileNotFoundException {
        Scanner scan = new Scanner(new FileInputStream("input.txt"));
        PrintWriter out = new PrintWriter(new FileOutputStream("output.txt"));
        long a = scan.nextLong();
        long b = scan.nextLong();
        long c = scan.nextLong();

        List<Long> AB = thirdHelper(a, b);

        long d = GCD.binaryGCD(Math.abs(a), Math.abs(b));
        if (c % d != 0) {
            out.println("Решений нет!");
            out.close();
            return;
        }

        StringBuilder x = new StringBuilder("");
        x.append("x = ");
        long antihypeX = (long) Math.pow(-1, AB.get(2)) * c * AB.get(1) / d;
        long antihypeY = (long) Math.pow(-1, AB.get(2) + 1) * c * AB.get(0) / d;
        if (a * antihypeX + b * antihypeY == c * (-1)) {
            antihypeX *= -1;
            antihypeY *= -1;
        }
        x.append(antihypeX);
        if (b < 0)
            x.append(" + ").append(Math.abs(b)).append("t;");
        else
            x.append(" - ").append(Math.abs(b)).append("t;");

        StringBuilder y = new StringBuilder("");
        y.append("y = ");
        y.append(antihypeY);
        if (a < 0) {
            y.append(" - ").append(Math.abs(a)).append("t;");
        } else {
            y.append(" + ").append(Math.abs(a)).append("t;");
        }

        out.println(x);
        out.println(y);

        out.close();
    }

    public List<Long> euclidFuncWithQList(long a, long b) {
        List<Long> q = new ArrayList<>();
        while (a % b != 0) {
            q.add(a / b);
            long tmp = a;
            a = b;
            b = tmp % b;
        }
        q.add(a / b);
        return q;
    }

    void suitableFractions(List<Long> qList, PrintWriter out) {
        StringBuilder answer = new StringBuilder("Подходящие дроби:\n");

        long P = 1, Q = 0, P0 = qList.get(0), Q0 = 1;
        answer.append("(" + P + "," + Q + ")," + "(" + P0 + "," + Q0 + "),");

        for (int i = 1; i < qList.size(); i++) {
            long tmpP = P0, tmpQ = Q0;
            P0 = qList.get(i) * tmpP + P;
            Q0 = qList.get(i) * tmpQ + Q;
            P = tmpP;
            Q = tmpQ;

            answer.append("(" + P0 + "," + Q0 + "),");
        }

        answer.deleteCharAt(answer.length() - 1);
        out.println(answer);
    }

    public List<Long> thirdHelper(long a, long b) throws FileNotFoundException {
        List<Long> qList = euclidFuncWithQList(a, b);
        long P = 1, Q = 0, P0 = qList.get(0), Q0 = 1;

        for (int i = 1; i < qList.size(); i++) {
            long tmpP = P0;
            long tmpQ = Q0;
            P0 = qList.get(i) * tmpP + P;
            Q0 = qList.get(i) * tmpQ + Q;
            P = tmpP;
            Q = tmpQ;
        }

        return new ArrayList<Long>(Arrays.asList(P, Q, (long) qList.size()));
    }

    class Pair {
        long a;
        long b;

        Pair(long a, long b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            if (a != pair.a) return false;
            return b == pair.b;
        }

        @Override
        public int hashCode() {
            int result = (int) (a ^ (a >>> 32));
            result = 31 * result + (int) (b ^ (b >>> 32));
            return result;
        }
    }
}
