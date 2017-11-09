package SixthTask;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Task5Runner {
    Scanner scanFile;
    Scanner scanSystem;
    PrintWriter out;

    public static void main(String[] args) throws FileNotFoundException {
        new Task5Runner().run();
    }

    void run() throws FileNotFoundException {
        scanSystem = new Scanner(System.in);
        scanFile = new Scanner(new FileInputStream("src/main/java/novikov/task5/task_5_input.txt"));
        out = new PrintWriter(new FileOutputStream("src/main/java/novikov/task5/task_5_output.txt"));
        System.out.println("Enter operation number:");
        int operationNumer = scanSystem.nextInt();
        switch (operationNumer) {
            case 1: {
                long a = scanFile.nextLong();
                long b = scanFile.nextLong();
                StringBuilder[] answer = firstPart(a, b);
                out.println(answer[0]);
                out.println(answer[1]);
                out.close();
                break;
            }

            case 2: {
                long a = scanFile.nextLong();
                long b = scanFile.nextLong();
                StringBuilder[] answer = secondPart(a, b);
                out.println(answer[0]);
                out.println(answer[1]);
                out.close();
                break;

            }

            case 3: {
                long a = scanFile.nextLong();
                long b = scanFile.nextLong();
                long c = scanFile.nextLong();
                StringBuilder[] answer = thirdPart(a, b, c);
                out.println(answer[0]);
                out.println(answer[1]);
                out.close();
                break;
            }

        }
    }

    public StringBuilder[] firstPart(long a, long b) {
        List<Long> qList = evklidFuncWithQList(a, b);
        StringBuilder result = new StringBuilder("");
        result.append("[" + qList.get(0) + "; ");
        for (int i = 1; i < qList.size(); i++)
            result.append(qList.get(i) + ", ");
        result.delete(result.length() - 2, result.length());
        result.append("]");
        StringBuilder drobi = goodDrobi(qList, out);
        return new StringBuilder[]{result, drobi};
    }

    StringBuilder goodDrobi(List<Long> qList, PrintWriter out) {
        StringBuilder answer = new StringBuilder("");
        long P = 1;
        long Q = 0;
        long P0 = qList.get(0);
        long Q0 = 1;
        answer.append("(" + P + "," + Q + ")," + "(" + P0 + "," + Q0 + "),");

        for (int i = 1; i < qList.size(); i++) {
            long tmpP = P0;
            long tmpQ = Q0;
            P0 = qList.get(i) * tmpP + P;
            Q0 = qList.get(i) * tmpQ + Q;
            P = tmpP;
            Q = tmpQ;
            answer.append("(" + P0 + "," + Q0 + "),");
        }

        answer.deleteCharAt(answer.length() - 1);
        return answer;
    }

    public StringBuilder[] secondPart(long a, long b) {
        List<Long> aList = new ArrayList<>();
        List<Pair> pairList = new ArrayList<>();
        long sqrtB = (long) Math.sqrt(b);
        Pair current = new Pair(1, sqrtB * (-1));
        pairList.add(current);
        List<Long> result = new ArrayList<>();
        result.add(a + sqrtB);
        int idx = 0;
        while (true) {
            long tmpB = b - current.b * current.b;
            long GCD = Main.getGCD_binary(current.a, tmpB);
            current.a /= GCD;
            tmpB /= GCD;
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


        StringBuilder answer = new StringBuilder();
        answer.append("[" + result.get(0) + ";");
        for (int i = 1; i < result.size(); i++) {
            if (i == idx + 1)
                answer.append("{");
            answer.append(result.get(i) + ",");
        }
        answer.deleteCharAt(answer.length() - 1);
        answer.append("}]");
        StringBuilder drobi = goodDrobi(result, out);
        return new StringBuilder[]{answer, drobi};

    }

    public StringBuilder[] thirdPart(long a, long b, long c) throws FileNotFoundException {
        List<Long> AB = thirdPartHelper(a, b);

        long d = Main.getGCD_binary(a, b);
        if (c % d != 0) {
            out.println("Решений нет!");
            out.close();
            return new StringBuilder[0];
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
            x.append(" + " + Math.abs(b) + "t;");
        else
            x.append(" - " + Math.abs(b) + "t;");

        StringBuilder y = new StringBuilder("");
        y.append("y = ");
        y.append(antihypeY);
        if (a < 0)
            y.append(" - " + Math.abs(a) + "t;");
        else
            y.append(" + " + Math.abs(a) + "t;");

        StringBuilder[] result = new StringBuilder[]{x, y};
        return result;
    }

    public List<Long> thirdPartHelper(long a, long b) throws FileNotFoundException {
        List<Long> qList = evklidFuncWithQList(a, b);
        long P = 1;
        long Q = 0;
        long P0 = qList.get(0);
        long Q0 = 1;

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

    public List<Long> evklidFuncWithQList(long a, long b) {
        List<Long> q = new ArrayList<>();
        while (a % b != 0) {
            q.add(a / b);
            long tmp = a;
            a = b;
            b = tmp % b;
        }
        q.add(a);
        return q;
    }

    public class Pair {
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
