import java.util.HashMap;
import java.util.Map;

public class WordNode {
    public String word; // 单词本身（小写）
    public Map<String, Integer> outEdges; // 邻居单词 -> 边的权重

    public WordNode(String word) {
        this.word = word;
        this.outEdges = new HashMap<>();
    }

    // 添加一条出边（如果已存在则权重+1）
    public void addOutEdge(String toWord) {
        outEdges.put(toWord, outEdges.getOrDefault(toWord, 0) + 1);
    }
}