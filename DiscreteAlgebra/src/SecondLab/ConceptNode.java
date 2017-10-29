package SecondLab;

import java.util.List;

public class ConceptNode {
    private List<Integer> g;
    private List<String> m;

    public ConceptNode(List<Integer> g, List<String> m) {
        this.g = g;
        this.m = m;
    }

    public List<Integer> getG() {
        return g;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConceptNode node = (ConceptNode) o;
        if (g != null ? !g.equals(node.g) : node.g != null) return false;
        return m != null ? m.equals(node.m) : node.m == null;
    }

    @Override
    public int hashCode() {
        int result = g != null ? g.hashCode() : 0;
        result = 31 * result + (m != null ? m.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("{");
        for (Integer integer : g) {
            str.append(String.valueOf(integer + 1)).append(",");
        }
        if (g.size() != 0) {
            str.deleteCharAt(str.length() - 1);
        }
        str.append("}").append(",").append("{");
        for (String s : m) {
            str.append(s).append(",");
        }
        if (m.size() != 0) {
            str.deleteCharAt(str.length() - 1);
        }
        str.append("}");
        return str.toString();
    }

    public boolean inclusion(ConceptNode minElement) {
        boolean result = true;
        for (Integer el : g) {
            if (!minElement.getG().contains(el))
            {
                result = false;
                break;
            }
        }
        return result;
    }
}
