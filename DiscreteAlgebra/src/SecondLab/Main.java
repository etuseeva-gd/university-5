package SecondLab;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static FirstLab.Main.*;

public class Main extends Application {
    static void first() {
        int[][] m = readMatrix();

        //Эквивалентное замыкание
        int[][] closure = reflexiveСlosure(m);
        closure = symmetricСlosure(closure);
        closure = transitiveСlosure(closure);
        printClosure("Эквивалентное замыкание", closure);

        //Система представителей
        Set<String> s = new HashSet<>();
        System.out.println("Система представителей:");
        for (int i = 0; i < m.length; i++) {
            String str = Arrays.toString(m[i]);
            if (!s.contains(str)) {
                s.add(str);
                System.out.print((i + 1) + " ");
            }
        }
        System.out.println();
    }

    public static List<Object> readData() {
        List<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get("input.txt"))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int n = Integer.parseInt(lines.get(0));
        int[][] m = new int[n][n];
        int[] set = new int[n];

        for (int i = 1; i < lines.size(); i++) {
            if (i == lines.size() - 1) {
                String[] nums = lines.get(i).split(" ");
                for (int j = 0; j < nums.length; j++) {
                    set[j] = Integer.parseInt(nums[j]);
                }
            } else {
                String[] nums = lines.get(i).split(" ");
                for (int j = 0; j < nums.length; j++) {
                    m[i - 1][j] = Integer.parseInt(nums[j]);
                }
            }
        }

        List<Object> res = new ArrayList<>();
        res.add(m);
        res.add(set);
        return res;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hasse Diagram");
        Group root = new Group();
        Canvas canvas = new Canvas(600, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        second(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    void second(GraphicsContext gc) {
        List<Object> data = readData();
        int[][] m = (int[][]) data.get(0);
        int[] set = (int[]) data.get(1);

        if (!(isReflexive(m) && isAntiSymmetric(m) && isTransitive(m))) {
            System.out.println("Данное отношение не является отношением порядка");
            return;
        }

        for (int i = 0; i < m.length; i++) {
            m[i][i] = 0;
        }

        Set<Integer> s = new HashSet<>(), ss = new HashSet<>();
        List<List<Integer>> lvl = new ArrayList<>();
        for (int k = 0; k < m.length; k++) {
            if (!ss.contains(k)) {
                List<Integer> lv = new ArrayList<>();
                for (int j = 0; j < m.length; j++) {
                    if (!ss.contains(j)) {
                        int sum = 0;
                        for (int i = 0; i < m.length; i++) {
                            if (!ss.contains(i)) {
                                sum += m[i][j];
                            }
                        }
                        if (sum == 0) {
                            lv.add(j);
                            s.add(j);
                        }
                    }
                }
                lvl.add(lv);
                ss = new HashSet<>(s);
            }
        }

        List<Pair<Integer, Integer>> edges = new ArrayList<>();
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m.length; j++) {
                for (int k = 0; k < m.length; k++) {
                    if (m[i][j] == 1 && m[j][k] == 1 && m[i][k] == 1) {
                        edges.add(new Pair<>(i, k));
                    }
                }
            }
        }

        edges.forEach(edge -> {
            m[edge.getKey()][edge.getValue()] = 0;
        });

        int[] invLvl = new int[m.length];
        for (List<Integer> aLvl : lvl) {
            for (int j = 0; j < aLvl.size(); j++) {
                invLvl[aLvl.get(j)] = j;
            }
        }

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);

        int size = 600;
        List<Pair<Integer, Integer>> points = new ArrayList<>(Collections.nCopies(m.length, new Pair<>(0, 0)));
        int h = size / (lvl.size() + 1);
        int posY = size - h;
        for (int i = 0; i < lvl.size(); i++) {
            int w = size / (lvl.get(i).size() + 1);
            int posX = w;
            for (int j = 0; j < lvl.get(i).size(); j++) {
                gc.strokeOval(posX - 2, posY - 2, 5, 5);
                points.set(lvl.get(i).get(j), new Pair<>(posX, posY));
                posX += w;
            }
            posY -= h;
        }

        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m.length; j++) {
                if (m[i][j] == 1) {
                    int x0 = points.get(i).getKey();
                    int y0 = points.get(i).getValue();
                    int x1 = points.get(j).getKey();
                    int y1 = points.get(j).getValue();

                    gc.strokeLine(x0, y0, x1, y1);
                }
            }
        }

        for (int i = 0; i < lvl.size(); i++) {
            for (int j = 0; j < lvl.get(i).size(); j++) {
                int tmp = lvl.get(i).get(j);
                gc.fillText(set[tmp] + "", points.get(tmp).getKey() - 15, points.get(tmp).getValue() + 5);
            }
        }

        int len = lvl.get(0).size();
        if (len == 1) {
            System.out.println("Наименьший и единственный минимальный элемент:");
            System.out.println(set[lvl.get(0).get(0)]);
        } else if (len > 0) {
            System.out.println("Наименьший элемент не существует");
            System.out.println("Минимальные элементы:");
            for (int i = 0; i < len; i++) {
                System.out.print(set[lvl.get(0).get(i)] + " ");
            }
            System.out.println();
        }

        List<Integer> st = new ArrayList<>();
        for (int i = 0; i < m.length; i++) {
            int sum = 0;
            for (int j = 0; j < m.length; j++) {
                sum += m[i][j];
            }
            if (sum == 0) {
                st.add(i);
            }
        }

        len = st.size();
        if (len == 1) {
            System.out.println("Наибольший и единственный максимальный элемент:");
            System.out.println(set[st.get(0)]);
        } else if (len > 1) {
            System.out.println("Наибольшего элемента не существует");
            System.out.println("Максимальные элементы:");
            for (int i = 0; i < len; i++) {
                System.out.print(set[st.get(i)] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        //Fot fist task
        //first();

        //For second task
        launch(args);
    }
}
