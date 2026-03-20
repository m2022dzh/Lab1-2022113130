import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
// R5 modification
// C4 branch modification
// B1 branch modification

// B1 branch modification (second)

// C4 branch

public class WordGraph {
    private Map<String, WordNode> nodes; // 根据单词快速找到节点

    public WordGraph() {
        nodes = new HashMap<>();
    }

    // 获取或创建节点
    public WordNode getOrCreateNode(String word) {
        if (!nodes.containsKey(word)) {
            nodes.put(word, new WordNode(word));
        }
        return nodes.get(word);
    }

    // 添加一条边：从 fromWord 到 toWord
    public void addEdge(String fromWord, String toWord) {
        WordNode fromNode = getOrCreateNode(fromWord);
        WordNode toNode = getOrCreateNode(toWord); // 确保目标节点也存在
        fromNode.addOutEdge(toWord);
    }

    // 打印图（用于测试）
    public void printGraph() {
        for (WordNode node : nodes.values()) {
            System.out.print(node.word + " -> ");
            for (Map.Entry<String, Integer> entry : node.outEdges.entrySet()) {
                System.out.print(entry.getKey() + "(" + entry.getValue() + ") ");
            }
            System.out.println();
        }
    }

    // 检查单词是否存在
    public boolean containsWord(String word) {
        return nodes.containsKey(word);
    }

    // 查询桥接词
    public List<String> getBridgeWords(String word1, String word2) {
        List<String> bridges = new ArrayList<>();
        if (!nodes.containsKey(word1) || !nodes.containsKey(word2)) {
            return null;
        }
        WordNode node1 = nodes.get(word1);
        for (String neighbor : node1.outEdges.keySet()) {
            WordNode neighborNode = nodes.get(neighbor);
            if (neighborNode.outEdges.containsKey(word2)) {
                bridges.add(neighbor);
            }
        }
        return bridges;
    }

    // 4.根据桥接词生成新文本
    public String generateNewText(String inputText) {
        // 1. 清洗输入文本，得到单词数组（与文件读取逻辑一致）
        String cleaned = inputText.toLowerCase().replaceAll("[^a-z]", " ");
        String[] words = cleaned.split("\\s+");
        if (words.length == 0)
            return "";

        // 2. 构建结果列表
        List<String> result = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < words.length; i++) {
            result.add(words[i]); // 先添加当前单词
            if (i < words.length - 1) {
                String w1 = words[i];
                String w2 = words[i + 1];
                // 查询桥接词
                List<String> bridges = getBridgeWords(w1, w2);
                if (bridges != null && !bridges.isEmpty()) {
                    // 随机选择一个桥接词
                    String bridge = bridges.get(rand.nextInt(bridges.size()));
                    result.add(bridge);
                }
                // 如果没有桥接词，不加任何东西
            }
        }

        // 3. 连接结果
        return String.join(" ", result);
    }

    // 5.计算两个单词之间的最短路径
    public String getShortestPath(String from, String to) {
        // 检查单词是否存在
        if (!nodes.containsKey(from) || !nodes.containsKey(to)) {
            return "No " + from + " or " + to + " in the graph!";
        }
        if (from.equals(to)) {
            return from + " (distance 0)";
        }

        // Dijkstra 算法
        Map<String, Double> dist = new HashMap<>(); // 最短距离
        Map<String, String> prev = new HashMap<>(); // 前驱节点
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));

        // 初始化距离为无穷大
        for (String word : nodes.keySet()) {
            dist.put(word, Double.POSITIVE_INFINITY);
        }
        dist.put(from, 0.0);
        pq.add(from);

        while (!pq.isEmpty()) {
            String u = pq.poll();
            if (u.equals(to))
                break; // 找到目标，提前结束
            if (dist.get(u) == Double.POSITIVE_INFINITY)
                continue; // 不可达

            WordNode nodeU = nodes.get(u);
            for (Map.Entry<String, Integer> entry : nodeU.outEdges.entrySet()) {
                String v = entry.getKey();
                double w = entry.getValue(); // 边的权重
                double newDist = dist.get(u) + w;
                if (newDist < dist.get(v)) {
                    dist.put(v, newDist);
                    prev.put(v, u);
                    // 更新优先队列中的优先级（先移除再添加）
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }

        // 如果无法到达 to
        if (!prev.containsKey(to) && !from.equals(to)) {
            return "No path from " + from + " to " + to + "!";
        }

        // 重建路径
        List<String> path = new ArrayList<>();
        String cur = to;
        while (cur != null) {
            path.add(0, cur); // 逆序插入
            cur = prev.get(cur);
        }

        // 检查路径是否真的从 from 开始（避免由于图结构异常）
        if (!path.get(0).equals(from)) {
            return "No path from " + from + " to " + to + "!";
        }

        // 格式化输出
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0)
                sb.append(" -> ");
            sb.append(path.get(i));
        }
        sb.append(" (distance ").append(dist.get(to)).append(")");
        return sb.toString();
    }

    // 6.PageRank
    // 计算指定单词的 PageRank 值（d=0.85）
    public double calcPageRank(String word) {
        if (!nodes.containsKey(word)) {
            return 0.0; // 或者返回 -1 表示不存在，但实验要求不明确，我们返回 0
        }

        int N = nodes.size();
        double d = 0.85;
        double epsilon = 1e-6; // 收敛阈值
        int maxIter = 100; // 最大迭代次数

        // 初始化所有节点 PR = 1/N
        Map<String, Double> pr = new HashMap<>();
        for (String w : nodes.keySet()) {
            pr.put(w, 1.0 / N);
        }

        // 预计算每个节点的出度
        Map<String, Integer> outDegree = new HashMap<>();
        for (WordNode node : nodes.values()) {
            outDegree.put(node.word, node.outEdges.size());
        }

        // 迭代计算
        for (int iter = 0; iter < maxIter; iter++) {
            Map<String, Double> newPR = new HashMap<>();
            double sinkPR = 0.0; // 所有出度为0的节点的 PR 总和

            // 先计算所有节点的 newPR（不包括 sink 的分配）
            for (String u : nodes.keySet()) {
                double sum = 0.0;
                // 找出所有指向 u 的节点 v
                for (WordNode v : nodes.values()) {
                    if (v.outEdges.containsKey(u)) {
                        int Lv = outDegree.get(v.word);
                        sum += pr.get(v.word) / Lv;
                    }
                }
                newPR.put(u, (1 - d) / N + d * sum);
            }

            // 收集出度为0的节点的 PR（它们不会贡献给任何节点，但需要均分给所有节点）
            for (String v : nodes.keySet()) {
                if (outDegree.get(v) == 0) {
                    sinkPR += pr.get(v);
                }
            }

            // 将 sinkPR 平均分配给所有节点
            if (sinkPR > 0) {
                double share = sinkPR / N;
                for (String u : nodes.keySet()) {
                    newPR.put(u, newPR.get(u) + share);
                }
            }

            // 检查收敛
            double diff = 0.0;
            for (String u : nodes.keySet()) {
                diff += Math.abs(newPR.get(u) - pr.get(u));
            }
            pr = newPR;
            if (diff < epsilon)
                break;
        }

        return pr.get(word);
    }

    // 7.获取所有节点的单词列表
    public List<String> getAllWords() {
        return new ArrayList<>(nodes.keySet());
    }

    // 随机获取一个节点（用于起点）
    public String getRandomWord() {
        List<String> words = getAllWords();
        if (words.isEmpty())
            return null;
        Random rand = new Random();
        return words.get(rand.nextInt(words.size()));
    }

    // 判断某个节点是否有出边
    public boolean hasOutEdges(String word) {
        WordNode node = nodes.get(word);
        return node != null && !node.outEdges.isEmpty();
    }

    // 随机游走，返回经过的节点序列（字符串形式）
    public String randomWalk() {
        if (nodes.isEmpty())
            return "图中没有节点";

        Random rand = new Random();
        List<String> path = new ArrayList<>();
        List<String> edgesUsed = new ArrayList<>(); // 记录走过的边 "from->to"

        // 随机选择起点
        String current = getRandomWord();
        path.add(current);

        // 最多游走1000步防止死循环（可调整）
        for (int step = 0; step < 1000; step++) {
            if (!hasOutEdges(current)) {
                break; // 没有出边，停止
            }

            // 获取当前节点的所有出边目标
            WordNode node = nodes.get(current);
            List<String> neighbors = new ArrayList<>(node.outEdges.keySet());
            // 随机选择一个邻居
            String next = neighbors.get(rand.nextInt(neighbors.size()));

            // 记录这条边
            String edge = current + "->" + next;
            if (edgesUsed.contains(edge)) {
                // 遇到重复边，停止
                path.add(next);
                break;
            }

            edgesUsed.add(edge);
            path.add(next);
            current = next;
        }

        // 将路径转换为字符串
        return String.join(" ", path);
    }

}