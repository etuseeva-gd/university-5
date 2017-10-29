package SecondLab;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    void run() throws IOException {
        Equivalence rel = new Equivalence(readMatrix());
        Scanner scanner = new Scanner(System.in);

        System.out.println("Что хотите сделать?:");
        System.out.println("1 - Проверить эквивалентность");
        System.out.println("2 - Определить порядок, диаграмму Хаасе");
        System.out.println("3 - Построить решетку концептов");

        String action = scanner.nextLine();

        switch (action) {
            case "1": {
                rel.getEquivalences();
                break;
            }
            case "2": {
                rel.workWithOrder();
                break;
            }
            case "3": {
                rel.getConcept();
                break;
            }
            default: {
                System.out.println("Такого действия нет! Ошибка");
            }
        }
    }

    public static int[][] readMatrix() {
        List<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get("input.txt"))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int n = Integer.parseInt(lines.get(0));
        int[][] m = new int[n][n];

        for (int i = 1; i < lines.size(); i++) {
            String[] nums = lines.get(i).split(" ");

            for (int j = 0; j < nums.length; j++)
                m[i - 1][j] = Integer.parseInt(nums[j]);
        }

        return m;
    }
}
