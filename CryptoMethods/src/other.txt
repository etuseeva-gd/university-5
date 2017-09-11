using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NumberTeoreticMethod1._2
{
    class Program
    {
        public static List<int> Euclidean(List<int> a, List<int> b)
        {

            List<int> g = new List<int>();
            g.Add(1);
            while (a[0] == 0 && b[0] == 0)
            {
                DivideX(a);
                DivideX(b);
                g.Add(0);
                MultyX(g);
            }
            List<int> u = new List<int>(a);
            List<int> v = new List<int>(b);
            while (u.Any())
            {
                while (u[0] == 0)
                    DivideX(u);
                while (v[0] == 0)
                    DivideX(v);
                int t = v[0];
                int k = u[0];
                List<int> temp1 = new List<int>(u);
                List<int> temp2 = new List<int>(v);
                for (int i = 0; i < u.Count; i++)
                {
                    temp1[i] *= t;
                }
                for (int i = 0; i < v.Count; i++)
                {
                    temp2[i] *= k;
                }
                int max = Math.Max(temp1.Count, temp2.Count);
                List<int> temp = new List<int>(max);
                for (int i = 0; i < max; i++)
                {
                    int a1 = 0, a2 = 0;
                    if (temp1.Count > i)
                        a1 = temp1[i];
                    if (temp2.Count > i)
                        a2 = temp2[i];
                    temp.Add(a1 - a2);
                }
                int e = EuclideanList(temp);

                if (u.Count() >= v.Count())
                {
                    if (e == 0)
                        u = new List<int>(0);
                    else
                        for (int i = 0; i < temp.Count; i++)
                        {
                            if (i < u.Count)
                                u[i] = Math.Sign(temp[temp.Count - 1]) * (temp[i] / e);
                            else
                                u.Add(Math.Sign(temp[temp.Count - 1]) * (temp[i] / e));
                        }
                }
                else
                {
                    for (int i = 0; i < temp.Count; i++)
                    {
                        if (i < v.Count)
                            v[i] = Math.Sign(temp[temp.Count - 1]) * (temp[i] / e);
                        else
                            v.Add(Math.Sign(temp[temp.Count - 1]) * (temp[i] / e));
                    }
                }
            }

            List<int> res = new List<int>();
            for (int i = 0; i < g.Count + v.Count; i++)
            {
                res.Add(0);
            }

            for (int i = 0; i < g.Count; i++)
            {
                for (int j = 0; j < v.Count; j++)
                {
                    res[i + j] += g[i] * v[j];
                }
            }

            return res;
        }

        private static void MultyX(List<int> g)
        {
            for (int i = g.Count - 2; i >= 0; i--)
            {
                g[i + 1] = g[i];
            }
            g[0] = 0;
        }

        private static void DivideX(List<int> a)
        {
            for (int i = 1; i < a.Count; i++)
            {
                a[i - 1] = a[i];
            }
            a.RemoveAt(a.Count - 1);
            if (a.Count == 1 && a[0] == 0)
                a[0] = 1;
        }

        private static int EuclideanList(List<int> list)
        {
            List<int> list2 = new List<int>(list);
            for (int i = 0; i < list2.Count; i++)
            {
                if (list2[i] == 0)
                {
                    list2.RemoveAt(i);
                    i--;
                }
            }
            int nod = 0;
            for (int i = 0; i < list2.Count; i++)
            {
                nod = Euclidean(nod, list2[i]);
            }
            return nod;
        }

        public static int Euclidean(int a, int b)
        {
            List<int> r = new List<int>();
            r.Add(a);
            r.Add(b);
            for (int i = 1; ; i++)
            {
                int r2 = r[i - 1] % r[i];
                r.Add(r2);
                if (r[i + 1] == 0)
                    return r[i];
            }
        }



        static void Main(string[] args)
        {
            List<int> a = new List<int>();
            List<int> b = new List<int>();
            String path = "";

            try
            {
                path = Directory.GetCurrentDirectory() + @"\input.txt";
                List<string> text = new List<string>(File.ReadLines(path));
                string[] A = text[0].Split();
                foreach (var item in A)
                {
                    a.Add(int.Parse(item));
                }
                string[] B = text[1].Split();
                foreach (var item in B)
                {
                    b.Add(int.Parse(item));
                }
                a.DelZeroEnd();
                b.DelZeroEnd();

            }
            catch (Exception e)
            {
                Console.WriteLine("Ошибка считывания input.txt");
                Console.ReadLine();
                return;
            }

            try
            {
                if (a.Count() < b.Count() && b.Count() > 0)
                    throw new Exception();
            }
            catch (Exception e)
            {
                Console.WriteLine("Проверьте удовлетворение условий для параметоров a(x) и b(x)");
                Console.ReadLine();
                return;
            }

            PrintAnswer(a, b);
        }

        private static void PrintAnswer(List<int> a, List<int> b)
        {
            string path;
            string s = "";
            List<int> res = Euclidean(a, b);
            for (int i = 0; i < res.Count; i++)
            {
                if (res[i] != 0)
                {
                    if (res[i] > 0 && i > 0)
                        s += "+";
                    else
                        if (res[i] < 0)
                        s += "-";
                    if (i == 0)
                        s += Math.Abs(res[i]);
                    else
                        if (i == 1)
                        s += Math.Abs(res[i]) + "x";
                    else
                        s += Math.Abs(res[i]) + "x^" + i;
                }
            }
            if (s[s.Length - 1].CompareTo('+') == 0)
                s = s.Remove(s.Length - 1, 1);
            path = Directory.GetCurrentDirectory() + @"\output.txt";
            StreamWriter outputFile = new StreamWriter(path);
            outputFile.WriteLine(s);
            outputFile.Flush();
            outputFile.Close();
        }
    }
}
