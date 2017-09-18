using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Task02
{
    class Program
    {
        static int BinEuclide(int a, int b)
        {
            int g = 1;
            while (a % 2 == 0 && b % 2 == 0)
            {
                a = a / 2;
                b = b / 2;
                g = 2 * g;
            }
            int u = a, v = b;
            while (u != 0)
            {
                while (u % 2 == 0)
                    u = u / 2;
                while (v % 2 == 0)
                    v = v / 2;
                if (u >= v)
                    u = u - v;
                else
                    v = v - u;
            }
            return g * v;
        }

        static Tuple<int, int> ExtendedEuclide(int a, int b)
        {
            int r0 = a, r1 = b, x0 = 1, x1 = 0, y0 = 0, y1 = 1;
            int x, y, d;
            while (true)
            {
                int q = r0 / r1;
                int r = r0 % r1;

                if (r == 0)
                    break;
                else
                {
                    r0 = r1;
                    r1 = r;
                    x = x0 - q * x1;
                    x0 = x1;
                    x1 = x;
                    y = y0 - q * y1;
                    y0 = y1;
                    y1 = y;
                }
            }
            d = r1;
            x = x1;
            y = y1;
            return new Tuple<int, int>(x, y);
        }

        static List<int> ComparisonSolution(int a, int b, int m)
        {
            List<int> solution = new List<int>();

            int d = BinEuclide(a, m);
            if (b % d != 0)
            {
                return solution;
            }
            else
            {
                int a1 = a / d;
                int b1 = b / d;
                int m1 = m / d;
                Tuple<int, int> tuple = ExtendedEuclide(a1, m1);
                int x0 = (b1 * tuple.Item1) % m1;
                while (x0 < 0)
                    x0 = x0 + m1;
                solution.Add(x0 % m1);

                for (int i = 1; i <= d - 1; i++)
                {
                    int x1 = x0 + i * m1;
                    solution.Add(x1);
                }
            }

            return solution;
        }

        static int ReverseElement(int a, int m)
        {
            StringBuilder builder = new StringBuilder();

            int d = BinEuclide(a, m);
            if (d != 1)
            {
                return -1;
            }
            else
            {
                List<int> solution = ComparisonSolution(a, 1, m);
                return solution[0];
            }
        }

        static Tuple<int, int> SystemComparisonSolution(List<Tuple<int, int>> system)
        { 
            int d = 1;
            for (int i = 0; i < system.Count; i++)
            {
                d *= system[i].Item2;
                for (int j = 0; j < system.Count; j++)
                {
                    if (i != j && BinEuclide(system[i].Item2, system[j].Item2) != 1)
                    {
                        return new Tuple<int, int>(-1, -1);
                    }
                }
            }

            int x0 = 0;

            for (int i = 0; i < system.Count; i++)
            {
                int s1 = system[i].Item1;
                int p1 = system[i].Item2;
                int d1 = d / p1;
                int dHatch = ReverseElement(d1, p1);
                x0 += d1 * dHatch * s1;
            }

            return new Tuple<int, int>(x0 % d, d);
        }

        static void Main(string[] args)
        {
            int mode;
            Console.WriteLine("Введите 1, чтобы решить сравнение 1-ой степени");
            Console.WriteLine("Введите 2, чтобы вычислить обратный элемент по модулю");
            Console.WriteLine("Введите 3, чтобы решить систему сравнений 1-ой степени");
            mode = int.Parse(Console.ReadLine());
            switch (mode)
            {
                case 1:
                    {
                        StreamReader reader = new StreamReader(@"C:\Users\zloif\source\repos\Task1\Task02\input1.txt");
                        int a = int.Parse(reader.ReadLine());
                        int b = int.Parse(reader.ReadLine());
                        int m = int.Parse(reader.ReadLine());
                        List<int> answer = ComparisonSolution(a, b, m);
                        reader.Close();
                        StreamWriter writer = new StreamWriter(@"C:\Users\zloif\source\repos\Task1\Task02\output1.txt");
                        if (answer.Count == 0)
                        {
                            writer.WriteLine("Решений нет");
                            Console.WriteLine("Решений нет");
                        }
                        else
                        {
                            for (int i = 0; i < answer.Count; i++)
                            {
                                writer.WriteLine("x{0} = {1}, т.е. {2} * {1} = {3} (mod {4})", i, answer[i], a, b, m);
                                Console.WriteLine("x{0} = {1}, т.е. {2} * {1} = {3} (mod {4})", i, answer[i], a, b, m);
                            }
                        }
                        writer.Close();
                        break;
                    }
                case 2:
                    {
                        StreamReader reader = new StreamReader(@"C:\Users\zloif\source\repos\Task1\Task02\input1.txt");
                        int a = int.Parse(reader.ReadLine());
                        int m = int.Parse(reader.ReadLine());
                        int answer = ReverseElement(a, m);
                        reader.Close();
                        StreamWriter writer = new StreamWriter(@"C:\Users\zloif\source\repos\Task1\Task02\output1.txt");
                        if (answer == -1)
                        {
                            writer.WriteLine("Обратного элемента к элементу {0} по модулю {1} не существует", a, m);
                            Console.WriteLine("Обратного элемента к элементу {0} по модулю {1} не существует", a, m);
                        }
                        else
                        {
                            writer.WriteLine("x0 = {0}, т.е. {1} * {0} = 1 (mod {2})", answer, a, m);
                            writer.Close();
                            Console.WriteLine("x0 = {0}, т.е. {1} * {0} = 1 (mod {2})", answer, a, m);
                        }
                        break;
                    }
                case 3:
                    {
                        StreamReader reader = new StreamReader(@"C:\Users\zloif\source\repos\Task1\Task02\input1.txt");
                        List<Tuple<int, int>> input = new List<Tuple<int, int>>();
                        while (!reader.EndOfStream)
                        {
                            string[] elements = reader.ReadLine().Split(' ');
                            input.Add(new Tuple<int, int>(int.Parse(elements[0]), int.Parse(elements[1])));
                        }

                        StreamWriter writer = new StreamWriter(@"C:\Users\zloif\source\repos\Task1\Task02\output1.txt");
                        Tuple<int, int> answer = SystemComparisonSolution(input);
                        if (answer.Item1 == -1 && answer.Item2 == -1)
                        {

                            writer.WriteLine("Решение не существует");
                            Console.WriteLine("Решение не существует");
                        }
                        else
                        {
                            writer.WriteLine("x0 = {0} (mod {1})", answer.Item1, answer.Item2);
                            Console.WriteLine("x0 = {0} (mod {1})", answer.Item1, answer.Item2);
                        }
                        break;
                    }
                default:
                    break;
            }
            Console.ReadLine();
        }
    }
}
