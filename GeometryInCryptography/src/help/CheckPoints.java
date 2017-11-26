package help;

import java.io.*;
import java.util.*;

public class CheckPoints {
    static boolean isBelong(int x, int y, int p, int a) {
        return (x * x * x + a * x) % p == (y * y) % p;
    }

    public static void main(String[] args) throws IOException {
        int p = 389; //Ввести
        int a = 280; //Ввести

        BufferedReader brX = new BufferedReader(new FileReader("pointsX.txt"));
        BufferedReader brY = new BufferedReader(new FileReader("pointsY.txt"));

        Map<Integer, Set<Integer>> m = new TreeMap<Integer, Set<Integer>>();
        while (true) {
            try {
                int x = Integer.parseInt(brX.readLine());
                int y = Integer.parseInt(brY.readLine());

//                System.out.print(x + " " + y + " = ");

                if (isBelong(x, y, p, a)) {
//                    System.out.println("Ок");

                    if (!m.containsKey(x)) {
                        m.put(x, new TreeSet<>());
                    }
                    m.get(x).add(y);

                } else {
                    System.out.println("no ok!");
                    return;
                }
            } catch (NumberFormatException e) {
                break;
            }
        }
        brX.close();
        brY.close();

        Map<Integer, Set<Integer>> res = new TreeMap<Integer, Set<Integer>>(m);
        m.forEach((x, ys) -> {
            ys.forEach(y -> {
                int newY = (-1 * y) + p;
                if (!ys.contains(newY)) {
                    if (isBelong(x, newY, p, a)) {
                        res.get(x).add(newY);
                    } else {
                        System.out.println("not ok");
                        return;
                    }
                }
            });
        });

        BufferedWriter bwX = new BufferedWriter(new FileWriter("pointsX1.txt"));
        BufferedWriter bwY = new BufferedWriter(new FileWriter("pointsY1.txt"));

        m.forEach((x, ys) -> {
            ys.forEach(y -> {
                try {
                    bwX.write(x + "\n");
                    bwY.write(y + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

        bwX.close();
        bwY.close();
    }
}
