package com.wordgraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * 单词有向图数据结构。管理单词节点和它们之间的边。
 * 支持多种图算法如桥接词查询、最短路径、PageRank等。
 *
 * @author lab-team
 * @version 1.0
 */
public class WordGraph {
    /** 根据单词快速找到节点 */
    private Map<String, WordNode> nodes;

    /** 随机数生成器（全局实例，避免重复创建） */
    private Random random;

    /**
     * 创建一个空的有向图。
     */
    public WordGraph() {
        nodes = new HashMap<>();
        random = new Random();
    }

    /**
     * 获取或创建指定单词的节点。
     *
     * @param word 单词
     * @return 对应的节点
     */
    public WordNode getOrCreateNode(String word) {
        if (!nodes.containsKey(word)) {
            nodes.put(word, new WordNode(word));
        }
        return nodes.get(word);
    }

    /**
     * 添加一条边：从 fromWord 到 toWord。
     *
     * @param fromWord 源单词
     * @param toWord   目标单词
     */
    public void addEdge(String fromWord, String toWord) {
        WordNode fromNode = getOrCreateNode(fromWord);
        fromNode.addOutEdge(toWord);
    }

    /**
     * 打印图中所有节点和其出边（用于测试和调试）。
     */
    public void printGraph() {
        for (WordNode node : nodes.values()) {
            System.out.print(node.word + " -> ");
            for (Map.Entry<String, Integer> entry : node.outEdges.entrySet()) {
                System.out.print(entry.getKey() + "(" + entry.getValue() + ") ");
            }
            System.out.println();
        }
    }

    /**
     * 检查图中是否存在指定单词。
     *
     * @param word 单词
     * @return 存在返回true，否则返回false
     */
    public boolean containsWord(String word) {
        return nodes.containsKey(word);
    }

    /**
     * 查询从word1到word2的桥接词。
     * 桥接词定义为：word1 -> bridge -> word2 的路径存在。
     *
     * @param word1 第一个单词
     * @param word2 第二个单词
     * @return 桥接词列表，如果任一单词不存在则返回null
     */
    public List<String> getBridgeWords(String word1, String word2) {
        List<String> bridges = new ArrayList<>();
        if (!nodes.containsKey(word1) || !nodes.containsKey(word2)) {
            return null;
        }
        WordNode node1 = nodes.get(word1);
        for (String neighbor : node1.outEdges.keySet()) {
            WordNode neighborNode = nodes.get(neighbor);
            if (neighborNode != null && neighborNode.outEdges.containsKey(word2)) {
                bridges.add(neighbor);
            }
        }
        return bridges;
    }

    /**
     * 根据桥接词生成新文本。
     * 在输入文本的相邻单词之间插入随机选择的桥接词。
     *
     * @param inputText 输入文本
     * @return 生成的新文本
     */
    public String generateNewText(String inputText) {
        String cleaned = inputText.toLowerCase().replaceAll("[^a-z]", " ");
        String[] words = cleaned.split("\\s+");
        if (words.length == 0) {
            return "";
        }

        List<String> result = new ArrayList<>();

        for (int i = 0; i < words.length; i++) {
            result.add(words[i]); // 先添加当前单词
            if (i < words.length - 1) {
                String w1 = words[i];
                String w2 = words[i + 1];
                // 查询桥接词
                List<String> bridges = getBridgeWords(w1, w2);
                if (bridges != null && !bridges.isEmpty()) {
                    String bridge = bridges.get(random.nextInt(bridges.size()));
                    result.add(bridge);
                }
            }
        }

        return String.join(" ", result);
    }

    /**
     * 计算两个单词之间的最短路径（使用Dijkstra算法）。
     * 边的权重为该边出现的次数。
     *
     * @param from 起始单词
     * @param to   目标单词
     * @return 最短路径字符串，包含路径和距离
     */
    public String getShortestPath(String from, String to) {
        if (!nodes.containsKey(from) || !nodes.containsKey(to)) {
            return "No " + from + " or " + to + " in the graph!";
        }
        if (from.equals(to)) {
            return from + " (distance 0)";
        }

        // Dijkstra 算法
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();

        // 初始化距离为无穷大
        for (String word : nodes.keySet()) {
            dist.put(word, Double.POSITIVE_INFINITY);
        }
        dist.put(from, 0.0);

        // 使用优先队列优化
        PriorityQueue<String> pq = new PriorityQueue<>(
                Comparator.comparingDouble(dist::get));
        pq.add(from);

        while (!pq.isEmpty()) {
            String u = pq.poll();
            if (u.equals(to)) {
                break;
            }
            if (dist.get(u) == Double.POSITIVE_INFINITY) {
                continue;
            }

            WordNode nodeU = nodes.get(u);
            for (Map.Entry<String, Integer> entry : nodeU.outEdges.entrySet()) {
                String v = entry.getKey();
                double w = entry.getValue();
                double newDist = dist.get(u) + w;
                if (newDist < dist.get(v)) {
                    dist.put(v, newDist);
                    prev.put(v, u);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }

        // 检查是否能到达目标
        if (!prev.containsKey(to) && !from.equals(to)) {
            return "No path from " + from + " to " + to + "!";
        }

        // 重建路径
        List<String> path = new ArrayList<>();
        String cur = to;
        while (cur != null) {
            path.add(0, cur);
            cur = prev.get(cur);
        }

        if (!path.get(0).equals(from)) {
            return "No path from " + from + " to " + to + "!";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) {
                sb.append(" -> ");
            }
            sb.append(path.get(i));
        }
        sb.append(" (distance ").append(dist.get(to)).append(")");
        return sb.toString();
    }

    /**
     * 计算指定单词的PageRank值（使用阻尼因子d=0.85）。
     *
     * @param word 单词
     * @return PageRank值（0到1之间），如果单词不存在返回0
     */
    public double calcPageRank(String word) {
        if (!nodes.containsKey(word)) {
            return 0.0;
        }

        int n = nodes.size();
        double d = 0.85;
        double epsilon = 1e-6;
        int maxIterations = 100;

        Map<String, Double> pageRank = new HashMap<>();
        for (String w : nodes.keySet()) {
            pageRank.put(w, 1.0 / n);
        }

        Map<String, Integer> outDegree = new HashMap<>();
        for (WordNode node : nodes.values()) {
            outDegree.put(node.word, node.outEdges.size());
        }

        for (int iter = 0; iter < maxIterations; iter++) {
            Map<String, Double> newPageRank = new HashMap<>();
            double sinkPageRank = 0.0;

            for (String u : nodes.keySet()) {
                double sum = 0.0;
                for (WordNode v : nodes.values()) {
                    if (v.outEdges.containsKey(u)) {
                        int outDeg = outDegree.get(v.word);
                        if (outDeg > 0) {
                            sum += pageRank.get(v.word) / outDeg;
                        }
                    }
                }
                newPageRank.put(u, (1 - d) / n + d * sum);
            }

            for (String v : nodes.keySet()) {
                if (outDegree.get(v) == 0) {
                    sinkPageRank += pageRank.get(v);
                }
            }

            if (sinkPageRank > 0) {
                double share = sinkPageRank / n;
                for (String u : nodes.keySet()) {
                    newPageRank.put(u, newPageRank.get(u) + share);
                }
            }

            double diff = 0.0;
            for (String u : nodes.keySet()) {
                diff += Math.abs(newPageRank.get(u) - pageRank.get(u));
            }
            pageRank = newPageRank;
            if (diff < epsilon) {
                break;
            }
        }

        return pageRank.get(word);
    }

    /**
     * 获取图中所有单词的列表。
     *
     * @return 单词列表
     */
    public List<String> getAllWords() {
        return new ArrayList<>(nodes.keySet());
    }

    /**
     * 随机获取一个节点作为起点。
     *
     * @return 随机单词，如果图为空返回null
     */
    public String getRandomWord() {
        List<String> words = getAllWords();
        if (words.isEmpty()) {
            return null;
        }
        return words.get(random.nextInt(words.size()));
    }

    /**
     * 判断指定单词是否有出边。
     *
     * @param word 单词
     * @return 有出边返回true
     */
    public boolean hasOutEdges(String word) {
        WordNode node = nodes.get(word);
        return node != null && !node.outEdges.isEmpty();
    }

    /**
     * 执行随机游走。从随机节点开始，随机选择下一跳，
     * 直到遇到重复的边或无出边为止。
     *
     * @return 游走路径（空格分隔的单词序列）
     */
    public String randomWalk() {
        if (nodes.isEmpty()) {
            return "图中没有节点";
        }

        List<String> path = new ArrayList<>();
        List<String> edgesUsed = new ArrayList<>();

        String current = getRandomWord();
        if (current == null) {
            return "图中没有节点";
        }

        path.add(current);

        for (int step = 0; step < 1000; step++) {
            if (!hasOutEdges(current)) {
                break;
            }

            WordNode node = nodes.get(current);
            List<String> neighbors = new ArrayList<>(node.outEdges.keySet());
            String next = neighbors.get(random.nextInt(neighbors.size()));

            String edge = current + "->" + next;
            if (edgesUsed.contains(edge)) {
                path.add(next);
                break;
            }

            edgesUsed.add(edge);
            path.add(next);
            current = next;
        }

        return String.join(" ", path);
    }
}
