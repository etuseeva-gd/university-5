package SecondLab;

import java.util.*;

import static FirstLab.Main.*;

public class Equivalence {
    private int n;
    private int[][] m;

    public Equivalence(int[][] m) {
        this.n = m.length;
        this.m = m;
    }

    private void print(String s) {
        System.out.println(s);
    }

    void getEquivalences() {
        if (!(isReflection() && isSymmetry() && isTransitivity())) {
            print("Введенное отношение не является отношением эквивалентности!");
        }

        int[][] eqv = getEquivalenceClosing();
        getEquivalenceClasses(eqv);
    }

    private int[][] getEquivalenceClosing() {
        int[][] closure = reflexiveСlosure(m);
        closure = symmetricСlosure(closure);
        closure = transitiveСlosure(closure);
        print("Эквивалентное замыкание:");
        printMass(closure);
        return closure;
    }

    private void getEquivalenceClasses(int[][] m) {
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        for (int i = 0; i < n; i++) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < n; j++) {
                s.append(m[i][j] - '0');
            }
            if (!map.containsKey(s.toString())) {
                map.put(s.toString(), new ArrayList<Integer>());
            }
            map.get(s.toString()).add(i + 1);
        }
        int id = 1;
        print("Классы эквивалентности:");
        for (String s : map.keySet()) {
            System.out.print(id + ": ");
            for (int val : map.get(s)) {
                System.out.print(val + " ");
            }
            System.out.println();
            id++;
        }
        id = 1;
        print("Представители:");
        for (String s : map.keySet()) {
            System.out.print(id + ": ");
            for (int val : map.get(s)) {
                System.out.print(val + " ");
                break;
            }
            System.out.println();
            id++;
        }
    }

    private int[][] transitivityClosing(int[][] m) {
        int[][] result = cloneMass(m);
        int[][] tmp = cloneMass(m);
        for (int[] a : m) {
            tmp = mult(tmp, tmp);
            add(result, tmp);
        }
        return result;
    }

    private int[][] symmetryClosing(int[][] m) {
        int[][] result = cloneMass(m);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = m[i][j] | m[j][i];
            }
        }
        return result;
    }

    private int[][] reflectionClosing(int[][] m) {
        return add(m, getSingleMass(n));
    }

    private boolean isTransitivity() {
        for (int[] aM : m)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    if (aM[j] == 1 && m[j][k] == 1 && aM[k] == 0)
                        return false;
        return true;
    }

    private boolean isSymmetry() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (i != j && m[i][j] == 1 && m[i][j] != m[j][i])
                    return false;
        return true;
    }

    private boolean isAntiSymmetric() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (i != j && m[i][j] == 1 && m[i][j] == m[j][i])
                    return false;
        return true;
    }

    private boolean isReflection() {
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum += m[i][i];
        }
        if (sum == n) {
            return true;
        } else if (sum == 0) {
            return false;
        } else {
            return false;
        }
    }

    private int[][] add(int[][] a, int[][] b) {
        int[][] result = new int[a.length][a.length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result.length; j++) {
                result[i][j] = a[i][j] | b[i][j];
            }
        }
        return result;
    }

    private int[][] mult(int[][] a, int[][] b) {
        int[][] result = new int[a.length][a.length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result.length; j++) {
                for (int k = 0; k < result.length; k++) {
                    result[i][j] |= a[i][k] & b[k][j];
                }
            }
        }
        return result;
    }

    private int[][] getSingleMass(int n) {
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            result[i][i] = 1;
        }
        return result;
    }

    private int[][] cloneMass(int[][] a) {
        int[][] tmp = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tmp[i][j] = a[i][j];
            }
        }
        return tmp;
    }

    private void printMass(int[][] result) {
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result.length; j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }
    }

    void workWithOrder() {
//        if (!(isReflection() && isAntiSymmetric() && isTransitivity())) {
//            print("Введенное отношение не является порядком!");
//        } else {
            List<Integer> list = getMinMaxElements(m, true);
            if (list.size() > 0) {
                print("Максимальные элементы:");
                for (Integer integer : list) {
                    System.out.print((integer + 1) + " ");
                }
                System.out.println();
                if (list.size() == 1) {
                    print("Наибольший элемент:");
                    print(list.get(0) + 1 + "");
                } else {
                    print("Наибольшего элемента нет.");
                }
            } else {
                print("Максимального и наибольшего элементов нет.");
            }

            list = getMinMaxElements(m, false);
            if (list.size() > 0) {
                print("Минимальные элементы:");
                for (Integer integer : list) {
                    System.out.print((integer + 1) + " ");
                }
                System.out.println();
                if (list.size() == 1) {
                    print("Наименьший элемент:");
                    print(list.get(0) + 1 + "");
                } else {
                    print("Наименьшего элемента нет.");
                }
            } else {
                print("Минимального и наименьшего элементов нет.");
            }

            createHaasDiagram();
//        }
    }

    private List<Integer> getMinMaxElements(int[][] m, boolean isMax) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            int k = 0;
            if (m[i][i] == -1)
                continue;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    if (isMax) {
                        k += m[i][j];
                    } else {
                        k += m[j][i];
                    }
                }
            }
            if (k == 0) {
                list.add(i);
            }
        }
        return list;
    }

    private void createHaasDiagram() {
        int[][] tmp = cloneMass(m);
        List<Integer> list = getMinMaxElements(tmp, false);
        List<List<Integer>> levels = new ArrayList<List<Integer>>();
        while (list.size() > 0) {
            levels.add(list);
            for (Integer integer : list) {
                for (int i = 0; i < n; i++) {
                    tmp[integer][i] = 0;
                }
                tmp[integer][integer] = -1;
            }
            list = getMinMaxElements(tmp, false);
        }

        System.out.println("Диаграмма Хаccе:");

        int id = 0;
        for (int i = 0; i < levels.size(); i++) {
            System.out.print("Уровень " + id + ": ");
            for (Integer el : levels.get(i)) {
                System.out.print((el + 1) + " ");
                if (i < levels.size() - 1) {
                    List<Integer> edge = new ArrayList<>();
                    for (int k = 0; k < n; k++) {
                        if (m[el][k] == 1 && levels.get(i + 1).contains(k)) {
                            edge.add(k + 1);
                        }
                    }
                    if (edge.size() > 0) {
                        System.out.print("соединяется с ");
                        for (int j = 0; j < edge.size(); j++) {
                            String end = j == edge.size() - 1 ? "; " : ", ";
                            System.out.print(edge.get(j) + end);
                        }
                    }
                }
            }
            System.out.println();
            id++;
        }
    }

    void getConcept() {
        System.out.println("Решетка концептов:");
        List<ConceptNode> elements = getAllConceptsElements();
        Map<Integer, List<ConceptNode>> levelsOfConcept = getLevelsConcept(elements);
        Map<ConceptNode, List<ConceptNode>> connection = getConnectionForElement(levelsOfConcept);

        for (Integer i : levelsOfConcept.keySet()) {
            System.out.print("Уровень " + i + ": ");
            for (ConceptNode element : levelsOfConcept.get(i)) {
                System.out.print(element + " ");
                if (connection.containsKey(element)) {
                    System.out.print("соединяется с (");
                    for (int j = 0; j < connection.get(element).size(); j++) {
                        if (j != connection.get(element).size() - 1) {
                            System.out.print(connection.get(element).get(j).toString() + "; ");
                        } else {
                            System.out.print(connection.get(element).get(j).toString());
                        }
                    }
                    System.out.print("); ");
                }
            }
            System.out.println();
        }
    }

    private Map<ConceptNode, List<ConceptNode>> getConnectionForElement(Map<Integer, List<ConceptNode>> levels) {
        Map<ConceptNode, List<ConceptNode>> connection = new HashMap<>();
        Map<ConceptNode, Integer> nonConnectElements = new HashMap<>();
        for (Integer i : levels.keySet()) {
            for (ConceptNode node : levels.get(i)) {
                for (ConceptNode pretender : nonConnectElements.keySet()) {
                    if (nonConnectElements.get(pretender) != 2 && pretender.inclusion(node)) {
                        if (!connection.containsKey(node)) {
                            connection.put(node, new ArrayList<ConceptNode>());
                        }
                        connection.get(node).add(pretender);
                        nonConnectElements.put(pretender, 1);
                    }
                }
            }
            for (ConceptNode node : nonConnectElements.keySet()) {
                if (nonConnectElements.get(node) == 1)
                    nonConnectElements.put(node, 2);
            }
            for (ConceptNode element : levels.get(i)) {
                nonConnectElements.put(element, 0);
            }
        }
        return connection;
    }

    private Map<Integer, List<ConceptNode>> getLevelsConcept(List<ConceptNode> nodes) {
        Map<Integer, List<ConceptNode>> result = new HashMap<>();
        int maxLevel = 0;
        while (nodes.size() > 0) {
            ConceptNode minElement = nodes.get(0);
            for (ConceptNode node : nodes) {
                if (node.getG().size() < minElement.getG().size()) {
                    minElement = node;
                }
            }
            boolean isAddElement = false;
            for (Integer i : result.keySet()) {
                boolean isCorrectLevel = true;
                for (ConceptNode element : result.get(i)) {
                    if (element.inclusion(minElement)) {
                        isCorrectLevel = false;
                        break;
                    }
                }
                if (isCorrectLevel) {
                    result.get(i).add(minElement);
                    isAddElement = true;
                    break;
                }
            }
            if (!isAddElement) {
                if (!result.containsKey(maxLevel)) {
                    result.put(maxLevel, new ArrayList<ConceptNode>());
                }
                result.get(maxLevel).add(minElement);
                maxLevel++;
            }

            nodes.remove(minElement);
        }

        return result;
    }

    private List<ConceptNode> getAllConceptsElements() {
        List<ConceptNode> result = new ArrayList<>();

        List<List<Integer>> intersections = new ArrayList<>();
        intersections.add(new ArrayList<Integer>());
        List<Integer> fullIntersection = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            fullIntersection.add(i);
        }
        intersections.add(fullIntersection);

        for (int j = 0; j < n; j++) {
            List<Integer> tmpList = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (m[i][j] == 1)
                    tmpList.add(i);
            }
            boolean isNewIntersection = true;
            for (int k = 0; k < intersections.size(); k++) {
                if (tmpList.equals(intersections.get(k))) {
                    isNewIntersection = false;
                    break;
                }
            }
            if (isNewIntersection)
                intersections.add(tmpList);
        }

        boolean isFindNewIntersection = true;
        while (isFindNewIntersection) {
            isFindNewIntersection = false;
            for (int i = 0; i < intersections.size(); i++) {
                for (int j = i + 1; j < intersections.size(); j++) {
                    List<Integer> probableIntersection = getIntersection(intersections.get(i), intersections.get(j));
                    boolean isNewIntersection = true;
                    for (List<Integer> intersection : intersections) {
                        if (probableIntersection.equals(intersection)) {
                            isNewIntersection = false;
                            break;
                        }
                    }
                    if (isNewIntersection) {
                        isFindNewIntersection = true;
                        intersections.add(probableIntersection);
                    }
                }
            }
        }

        for (List<Integer> intersection : intersections) {
            result.add(new ConceptNode(intersection, getMInterpretation(intersection)));
        }
        return result;
    }

    private List<Integer> getIntersection(List<Integer> a, List<Integer> b) {
        List<Integer> result = new ArrayList<>();
        for (Integer el : a) {
            if (b.contains(el))
                result.add(el);
        }
        return result;
    }

    private List<String> getMInterpretation(List<Integer> g) {
        List<String> m = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            int cnt = 0;
            for (Integer i : g) {
                cnt += this.m[i][j];
            }
            if (cnt == g.size()) {
                m.add(String.valueOf((char) ('a' + j)));
            }
        }
        return m;
    }
}
