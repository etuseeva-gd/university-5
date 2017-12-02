package help;

import SecondThird.Second;
import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

public class GenerateParams {
    public static void main(String[] args) throws IOException {
        new GenerateParams().run();
    }

    void run() throws IOException {
        Second.Second m = new Second.Second();

        BufferedWriter bw = new BufferedWriter(new FileWriter("common_params.txt"));
        BufferedWriter bwL = new BufferedWriter(new FileWriter("l.txt"));
        Scanner sc = new Scanner(System.in);

        //common_params.txt
        System.out.println("Введите модуль p:");
        BigInteger p = new BigInteger(sc.nextLine());
        bw.write(p + "\n");

        System.out.println("Введите a:");
        BigInteger a = new BigInteger(sc.nextLine());
        bw.write(a + "\n");

        System.out.println("Введите образующую точку Q(x,y):");
        Pair<BigInteger, BigInteger> Q = m.getPoint(sc.nextLine());
        bw.write(getStrPoint(Q) + "\n");

        System.out.println("Введите порядок точки Q - r:");
        BigInteger r = new BigInteger(sc.nextLine());
        bw.write(r + "\n");

        //l.txt
        System.out.println("Введите l:");
        BigInteger l = new BigInteger(sc.nextLine());
        bwL.write(l + "\n");

        //common_params.txt
        Pair<BigInteger, BigInteger> R = m.multPoint(l, Q, a, p);
        bw.write(getStrPoint(R) + "\n");

        sc.close();
        bw.close();
        bwL.close();
    }

    String getStrPoint(Pair<BigInteger, BigInteger> point) {
        return "(" + point.getKey() + "," + point.getValue() + ")";
    }
}
