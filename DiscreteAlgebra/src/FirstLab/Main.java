package FirstLab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
            int n = Integer.parseInt(bufferedReader.readLine());
            int[][] matrix = new int[n][n];
            int i = 0;
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] items = line.split(" ");
                for (int j = 0; j < items.length; j++) {
                    matrix[++i][j] = Integer.parseInt(items[j]);
                }
            }

            if (isRef(matrix)) {
                System.out.println("Отношение является рефлексивным.");
            } else {
                if (isAntiRef(matrix)) {
                    System.out.println("Отношение является антирефлексивным.");
                } else {
                    System.out.println("Отношение не является рефлексивным.");
                }
            }

            if (isSim(matrix)) {
                System.out.println("Отношение является симметричным.");
            } else {
                if (isAntiSim(matrix)) {
                    System.out.println("Отношение является антисимметричным.");
                } else {
                    System.out.println("Отношение не является симметричным.");
                }
            }

            if (isTran(matrix)) {
                System.out.println("Отношение является транзитивным.");
            } else {
                System.out.println("Отношение является не транзитивным.");
            }

            if (FirstFull(matrix)) {
                System.out.println("Отношение является 1-полным.");
            } else {
                System.out.println("Отношение не является 1-полным.");
            }

            if (SecondFull(matrix)) {
                System.out.println("Отношение является 2-полным.");
            } else {
                System.out.println("Отношение не является 2-полным.");
            }

            System.out.println("Рефлексивное замыкание:");
            RefClosing(matrix);
            System.out.println("Симметричное замыкание:");
            SimClosing(matrix);
            System.out.println("Транзитивное замыкание:");
            TranCLosing(matrix);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean isRef(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            if (a[i][i] != 1) {
                return false;
            }
        }
        return true;
    }

    static boolean isAntiRef(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            if (a[i][i] != 0) {
                return false;
            }
        }
        return true;
    }

    static boolean isSim(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if (a[i][j] != a[j][i]) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean isAntiSim(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if (a[i][j] == a[j][i]) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean isTran(int[][] a) {
        for (int[] elem : a) {
            for (int j = 0; j < a.length; j++) {
                for (int k = 0; k < a.length; k++) {
                    if (elem[j] == 1 && a[j][k] == 1 && elem[k] == 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    static boolean FirstFull(int[][] a) {
        int count;
        for (int[] elem : a) {
            count = 0;
            for (int j = 0; j < a.length; j++) {
                count += elem[j];
            }
            if (count == 0) {
                return false;
            }
        }
        return true;
    }

    static boolean SecondFull(int[][] a) {
        int count;
        for (int i = 0; i < a.length; i++) {
            count = 0;
            for (int[] elem : a) {
                count += elem[i];
            }
            if (count == 0) {
                return false;
            }
        }
        return true;
    }

    static void RefClosing(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if (i == j) {
                    System.out.print("1 ");
                } else {
                    System.out.print(a[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    static void SimClosing(int[][] a) {
        int[][] temp = a.clone();
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if (a[i][j] == 1) {
                    temp[j][i] = 1;
                }
            }
        }
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                System.out.print(temp[i][j] + " ");
            }
            System.out.println();
        }
    }

    static void add(int[][] a, int[][] b, int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (a[i][j] > 0 || b[i][j] > 0) {
                    a[i][j] = 1;
                }
            }
        }
    }

    static int[][] mul(int[][] a, int[][] b, int n) {
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
                if (result[i][j] < 0) {
                    result[i][j] = 1;
                }
            }
        }
        return result;
    }

    static void TranCLosing(int[][] a) {
        int[][] result = a.clone();
        int[][] matrix1 = a.clone();
        for (int[] elem : a) {
            matrix1 = mul(matrix1, matrix1, a.length);
            add(result, matrix1, a.length);
        }
        for (int[] aResult : result) {
            for (int j = 0; j < result.length; j++) {
                System.out.print(aResult[j] + " ");
            }
            System.out.println();
        }
    }
}
