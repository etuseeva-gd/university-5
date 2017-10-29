package SecondLab;

import java.util.*;

public class Equivalence {
    private int size;
    private int[][] relation;

    public Equivalence(int[][] relation) {
        this.size = relation.length;
        this.relation = relation;
    }

    private void print(String s) {
        System.out.println(s);
    }

    //first action
    void getAllEquiv() {
        getEquivalenceClosing();

        if (!(isReflection() && isSymmetry() && isTransitivity())) {
            print("Введенное отношение не эквивалентно!");
        } else {
            getEquivalenceClasses();
        }
    }

    //Вычислить эквивалентное отношение
    private void getEquivalenceClosing() {
        int[][] result = reflectionClosing(relation);
        result = symmetryClosing(result);
        result = transitivityClosing(result);

        print("Эквивалентное замыкание:");
        printMass(result);
    }

    //Вычислить классы эквивалентности и представителей
    private void getEquivalenceClasses() {
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        for (int i = 0; i < size; i++) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < size; j++) {
                s.append(relation[i][j] - '0');
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

    int[][] transitivityClosing(int[][] relation) {
        int[][] result = cloneMass(relation);
        int[][] tmp = cloneMass(relation);
        for (int[] a : relation) {
            tmp = mult(tmp, tmp);
            add(result, tmp);
        }
        return result;
    }

    int[][] symmetryClosing(int[][] relation) {
        int[][] result = cloneMass(relation);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result[i][j] = relation[i][j] | relation[j][i];
            }
        }
        return result;
    }

    int[][] reflectionClosing(int[][] relation) {
        int[][] result = add(relation, getSingleMass(size));
        return result;
    }

    boolean isTransitivity() {
        for (int[] aM : relation)
            for (int j = 0; j < size; j++)
                for (int k = 0; k < size; k++)
                    if (aM[j] == 1 && relation[j][k] == 1 && aM[k] == 0)
                        return false;
        return true;
    }

    boolean isSymmetry() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (i != j && relation[i][j] == 1 && relation[i][j] != relation[j][i])
                    return false;
        return true;
    }

    boolean isAntiSymmetric() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (i != j && relation[i][j] == 1 && relation[i][j] == relation[j][i])
                    return false;
        return true;
    }

    boolean isReflection() {
        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum += relation[i][i];
        }
        if (sum == size) {
            return true;
        } else if (sum == 0) {
            return false;
        } else {
            return false;
        }
    }

    int[][] add(int[][] a, int[][] b) {
        int[][] result = new int[a.length][a.length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result.length; j++) {
                result[i][j] = a[i][j] | b[i][j];
            }
        }
        return result;
    }

    int[][] mult(int[][] a, int[][] b) {
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

    int[][] getSingleMass(int n) {
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            result[i][i] = 1;
        }
        return result;
    }

    int[][] cloneMass(int[][] a) {
        int[][] tmp = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
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

    //----------------

    //second action
    //Поиск максимальных, минимальных, наименьших, наибольших элементов
    void getOrder() {
        if (!(isReflection() && isAntiSymmetric() && isTransitivity())) {
            print("Введенное отношение не является порядоком!");
        } else {
            List<Integer> list = getMinMaxElements(relation, true);
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

            list = getMinMaxElements(relation, false);
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
        }
    }

    //Вычислить мин/макс элементы
    private List<Integer> getMinMaxElements(int[][] relation, boolean isMax) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            int k = 0;
            if (relation[i][i] == -1)
                continue;
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    if (isMax) {
                        k += relation[i][j];
                    } else {
                        k += relation[j][i];
                    }
                }
            }
            if (k == 0) {
                list.add(i);
            }
        }
        return list;
    }

    //Диаграмма Хаcсе
    void createHaas() {
        int[][] tmp = cloneMass(relation);

        List<Integer> list = getMinMaxElements(tmp, false);
        List<List<Integer>> levels = new ArrayList<List<Integer>>();
        while (list.size() > 0) {
            levels.add(new ArrayList<Integer>());
            for (Integer integer : list) {
                levels.get(levels.size() - 1).add(integer);
            }
            for (Integer integer : list) {
                for (int i = 0; i < size; i++) {
                    tmp[integer][i] = 0;
                }
                tmp[integer][integer] = -1;
            }
            list = getMinMaxElements(tmp, false);
        }
        int id = 1;
        System.out.println("Диаграмма Хаccе:");
        for (List<Integer> level : levels) {
            System.out.print("Уровень " + id + ": ");
            for (Integer integer : level) {
                System.out.print((integer + 1) + " ");
            }
            System.out.println();
            id++;
        }
        for (int i = 0; i < levels.size() - 1; i++) {
            for (int j = 0; j < levels.get(i).size(); j++) {
                int from = levels.get(i).get(j);
                for (int k = 0; k < size; k++) {
                    if (relation[from][k] == 1 && levels.get(i + 1).contains(k)) {
                        System.out.println("Элемент " + (from + 1) + " соединяется с элементом " + (k + 1));
                    }
                }
            }
        }
    }

    //-----------

    void getConcept() {
        System.out.println();
        int[][] tmp = cloneMass(relation);
        System.out.println("Концепты");
        getAllConcept();
    }

    private void getAllConcept() {
        List<ConceptsElement> elements = getAllConceptsElements();
        Map<Integer, List<ConceptsElement>> levelsOfConcept = getLevelsConcept(elements);
        Map<ConceptsElement, List<ConceptsElement>> connection = getConnectionForElement(levelsOfConcept);

        for (Integer i : levelsOfConcept.keySet()) {
            System.out.print(i + ": ");
            for (ConceptsElement element : levelsOfConcept.get(i)) {
                System.out.print(element + " ");
                if (connection.containsKey(element)) {
                    System.out.print("connect with (");
                    for (int j = 0; j < connection.get(element).size(); j++) {
                        if (j != connection.get(element).size() - 1) {
                            System.out.print(connection.get(element).get(j).toString() + "; ");
                        } else {
                            System.out.print(connection.get(element).get(j).toString());
                        }
                    }
                    System.out.print(");   ");
                }
            }
            System.out.println();
        }
    }

    private Map<ConceptsElement, List<ConceptsElement>> getConnectionForElement(Map<Integer, List<ConceptsElement>> levelsOfConcept) {
        Map<ConceptsElement, List<ConceptsElement>> connection = new HashMap<>();
        Map<ConceptsElement, Integer> nonConnectElements = new HashMap<>();
        for (Integer i : levelsOfConcept.keySet()) {
            for (ConceptsElement element : levelsOfConcept.get(i)) {
                for (ConceptsElement pretender : nonConnectElements.keySet()) {
                    if (nonConnectElements.get(pretender) != 2 && pretender.inclusion(element)) {
                        if (!connection.containsKey(element)) {
                            connection.put(element, new ArrayList<ConceptsElement>());
                        }
                        connection.get(element).add(pretender);
                        nonConnectElements.put(pretender, 1);
                    }
                }
            }
            for (ConceptsElement element : nonConnectElements.keySet()) {
                if (nonConnectElements.get(element) == 1)
                    nonConnectElements.put(element, 2);
            }
            for (ConceptsElement element : levelsOfConcept.get(i)) {
                nonConnectElements.put(element, 0);
            }
        }
        return connection;
    }

    private Map<Integer, List<ConceptsElement>> getLevelsConcept(List<ConceptsElement> elements) {
        Map<Integer, List<ConceptsElement>> result = new HashMap<>();
        int maxLevel = 0;
        while (elements.size() > 0) {

            ConceptsElement minElement = elements.get(0);
            for (ConceptsElement element : elements) {
                if (element.getG().size() < minElement.getG().size()) {
                    minElement = element;
                }
            }
            boolean isAddElement = false;
            for (Integer i : result.keySet()) {
                boolean isCorrectLevel = true;
                for (ConceptsElement element : result.get(i)) {
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
                    result.put(maxLevel, new ArrayList<ConceptsElement>());
                }
                result.get(maxLevel).add(minElement);
                maxLevel++;
            }

            elements.remove(minElement);
        }

        return result;
    }

    private List<ConceptsElement> getAllConceptsElements() {
        List<ConceptsElement> result = new ArrayList<>();
        List<List<Integer>> allIntersections = new ArrayList<>();
        allIntersections.add(new ArrayList<Integer>());
        List<Integer> fullIntersection = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            fullIntersection.add(i);
        }
        allIntersections.add(fullIntersection);
        for (int j = 0; j < size; j++) {
            List<Integer> tmpList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                if (relation[i][j] == 1)
                    tmpList.add(i);
            }
            boolean isNewIntersection = true;
            for (int k = 0; k < allIntersections.size(); k++) {
                if (tmpList.equals(allIntersections.get(k))) {
                    isNewIntersection = false;
                    break;
                }
            }
            if (isNewIntersection)
                allIntersections.add(tmpList);
        }
        boolean isFindNewIntersection = true;
        while (isFindNewIntersection) {
            isFindNewIntersection = false;

            for (int i = 0; i < allIntersections.size(); i++) {
                for (int j = i + 1; j < allIntersections.size(); j++) {
                    List<Integer> probableIntersection = getIntersection(allIntersections.get(i), allIntersections.get(j));
                    boolean isNewIntersection = true;
                    for (int k = 0; k < allIntersections.size(); k++) {
                        if (probableIntersection.equals(allIntersections.get(k))) {
                            isNewIntersection = false;
                            break;
                        }
                    }
                    if (isNewIntersection) {
                        isFindNewIntersection = true;
                        allIntersections.add(probableIntersection);
                    }
                }
            }
        }

        for (List<Integer> intersection : allIntersections) {
            result.add(new ConceptsElement(intersection, getMInterpretation(intersection)));
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
        for (int j = 0; j < size; j++) {
            int cnt = 0;
            for (Integer i : g) {
                cnt += relation[i][j];
            }
            if (cnt == g.size()) {
                m.add(String.valueOf((char) ('a' + j)));
            }
        }
        return m;
    }
}
