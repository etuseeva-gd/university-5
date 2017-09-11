import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Polynomials {
    public static List<Integer> Euclidean(List<Integer> a, List<Integer> b)
    {
        List<Integer> g = new ArrayList<>();
        g.add(1);
        while (a.get(0) == 0 && b.get(0) == 0)
        {
            DivideX(a);
            DivideX(b);
            g.add(0);
            MultyX(g);
        }

        List<Integer> u = new ArrayList<>(a);
        List<Integer> v = new ArrayList<>(b);

        while (!u.isEmpty())
        {
            while (u.get(0) == 0)
                DivideX(u);
            while (v.get(0) == 0)
                DivideX(v);
            int t = v.get(0);
            int k = u.get(0);

            List<Integer> temp1 = new ArrayList<>(u);
            List<Integer> temp2 = new ArrayList<>(v);

            for (int i = 0; i < u.size(); i++)
            {
                temp1.set(i, temp1.get(i) * t);
            }
            for (int i = 0; i < v.size(); i++)
            {
                temp2.set(i, temp2.get(i) * k);
            }
            int max = Math.max(temp1.size(), temp2.size());

            List<Integer> temp = new ArrayList<>(max);
            for (int i = 0; i < max; i++)
            {
                int a1 = 0, a2 = 0;
                if (temp1.size() > i)
                    a1 = temp1.get(i);
                if (temp2.size() > i)
                    a2 = temp2.get(i);
                temp.add(a1 - a2);
            }

            int e = EuclideanList(temp);

            if (u.size() >= v.size())
            {
                if (e == 0)
                    u = new ArrayList<>(0);
                else
                    for (int i = 0; i < temp.size(); i++)
                    {
                        if (i < u.size())
                            u.set(i, (int) (Math.signum(temp.get(temp.size() - 1)) * (temp.get(i) / e)));
                        else
                            u.add((int) (Math.signum(temp.get(temp.size() - 1)) * (temp.get(i) / e)));
                    }
            }
            else
            {
                for (int i = 0; i < temp.size(); i++)
                {
                    if (i < v.size())
                        v.set(i, (int) (Math.signum(temp.get(temp.size() - 1)) * (temp.get(i) / e)));
                    else
                        v.add((int) (Math.signum(temp.get(temp.size() - 1)) * (temp.get(i) / e)));
                }
            }
        }

        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < g.size() + v.size(); i++)
        {
            res.add(0);
        }

        for (int i = 0; i < g.size(); i++)
        {
            for (int j = 0; j < v.size(); j++)
            {
                res.set(i + j, res.get(i + j) + g.get(i) * v.get(j));
            }
        }

        return res;
    }

    private static void MultyX(List<Integer> g)
    {
        for (int i = g.size() - 2; i >= 0; i--)
        {
            g.set(i + 1, g.get(i));
        }
        g.set(0, 0);
    }

    private static void DivideX(List<Integer> a)
    {
        for (int i = 1; i < a.size(); i++)
        {
            a.set(i - 1, a.get(i));
        }
        a.remove(a.size() - 1);
        if (a.size() == 1 && a.get(0) == 0)
            a.set(0, 1);
    }

    private static int EuclideanList(List<Integer> list)
    {
        List<Integer> list2 = new ArrayList<>(list);
        for (int i = 0; i < list2.size(); i++)
        {
            if (list2.get(i) == 0)
            {
                list2.remove(i);
                i--;
            }
        }
        int nod = 0;
        for (Integer aList2 : list2) {
            nod = gcd(nod, aList2);
        }
        return nod;
    }

    private static int gcd(int a, int b) {
        while (b > 0) {
            a %= b;

            int t = a;
            a = b;
            b = t;
        }
        return a;
    }

    static List<Integer> read(String str) {
        String[] nums = str.split(" ");

        List<Integer> a = new ArrayList<>();
        for (String num : nums) {
            a.add(Integer.parseInt(num));
        }

        return a;
    }

    public static void main(String[] args) {
        List<String> input = readFile("input.txt");
        polynomials(input);
    }

    private static void polynomials(List<String> input) {
        List<Integer> a = read(input.get(0)), b = read(input.get(1));

        System.out.println(Euclidean(a, b));
    }

    private static List<String> readFile(String file) {
        List<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(file))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
