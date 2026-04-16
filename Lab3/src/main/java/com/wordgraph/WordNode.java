package com.wordgraph;

import java.util.HashMap;
import java.util.Map;

/**
 * 代表图中的一个单词节点。每个节点包含一个单词和其出边信息。
 *
 * @author lab-team
 * @version 1.0
 */
public class WordNode {
    /** 单词本身（小写） */
    public String word;

    /** 邻居单词 -> 边的权重 (即到该邻居的边数) */
    public Map<String, Integer> outEdges;

    /**
     * 创建一个新的单词节点。
     *
     * @param word 单词内容
     */
    public WordNode(String word) {
        this.word = word;
        this.outEdges = new HashMap<>();
    }

    /**
     * 添加一条出边。如果该边已存在，则权重加1。
     *
     * @param toWord 目标单词
     */
    public void addOutEdge(String toWord) {
        outEdges.put(toWord, outEdges.getOrDefault(toWord, 0) + 1);
    }
}
