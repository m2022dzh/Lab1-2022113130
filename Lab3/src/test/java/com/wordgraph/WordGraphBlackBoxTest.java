package com.wordgraph;

import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 黑盒测试：getBridgeWords方法
 * 使用等价类划分法设计测试用例
 *
 * 选择被测函数：getBridgeWords
 *
 * 等价类划分表：
 * | 约束条件说明              | 有效等价类及其编号 | 无效等价类及其编号 |
 * |-------------------------|-------------------|-------------------|
 * | word1 在图中是否存在     | 存在 (1)          | 不存在 (4)        |
 * | word2 在图中是否存在     | 存在 (2)          | 不存在 (5)        |
 * | 是否存在桥接词           | 有桥接词 (3)      | 无桥接词 (6)      |
 * | word1 与 word2 的关系    | word1 ≠ word2 (7) | word1 = word2 (8) |
 *
 * 测试用例表：
 * | 用例编号 | 输入                              | 期望输出          | 所覆盖的等价类编号 |
 * |---------|-----------------------------------|------------------|-------------------|
 * | TC1     | word1="hello", word2="world"      | ["beautiful"]    | (1)(2)(3)(7)      |
 * | TC2     | word1="hello", word2="test"       | []               | (1)(2)(6)(7)      |
 * | TC3     | word1="notexist", word2="world"   | null             | (2)(4)            |
 * | TC4     | word1="hello", word2="notexist"   | null             | (1)(5)            |
 * | TC5     | word1="x", word2="y"              | null             | (4)(5)            |
 * | TC6     | word1="hello", word2="hello"      | [] 或 ["hello"]  | (1)(2)(8)         |
 */
public class WordGraphBlackBoxTest {
    private WordGraph graph;

    @Before
    public void setUp() {
        graph = new WordGraph();
    }

    /**
     * TC1: word1、word2 存在，有 1 个桥接词
     * 覆盖等价类：(1)(2)(3)(7)
     * 图：hello -> beautiful -> world
     */
    @Test
    public void testOneBridgeWord() {
        graph.addEdge("hello", "beautiful");
        graph.addEdge("beautiful", "world");

        List<String> bridges = graph.getBridgeWords("hello", "world");

        assertNotNull("应返回非null列表", bridges);
        assertEquals("应有1个桥接词", 1, bridges.size());
        assertTrue("桥接词应为beautiful", bridges.contains("beautiful"));
    }

    /**
     * TC2: word1、word2 存在，无桥接词
     * 覆盖等价类：(1)(2)(6)(7)
     * 图：hello -> world, test -> result (两个不连通的分量)
     */
    @Test
    public void testNoBridgeWord() {
        graph.addEdge("hello", "world");
        graph.addEdge("test", "result");

        List<String> bridges = graph.getBridgeWords("hello", "test");

        assertNotNull("应返回非null列表", bridges);
        assertTrue("应返回空列表", bridges.isEmpty());
    }

    /**
     * TC3: word1 不存在，word2 存在
     * 覆盖等价类：(2)(4)
     */
    @Test
    public void testWord1NotExist() {
        graph.addEdge("hello", "world");

        List<String> bridges = graph.getBridgeWords("notexist", "world");

        assertNull("word1不存在应返回null", bridges);
    }

    /**
     * TC4: word1 存在，word2 不存在
     * 覆盖等价类：(1)(5)
     */
    @Test
    public void testWord2NotExist() {
        graph.addEdge("hello", "world");

        List<String> bridges = graph.getBridgeWords("hello", "notexist");

        assertNull("word2不存在应返回null", bridges);
    }

    /**
     * TC5: word1、word2 均不存在
     * 覆盖等价类：(4)(5)
     */
    @Test
    public void testBothWordsNotExist() {
        graph.addEdge("hello", "world");

        List<String> bridges = graph.getBridgeWords("x", "y");

        assertNull("两个单词都不存在应返回null", bridges);
    }

    /**
     * TC6: word1 == word2 (同一个单词)
     * 覆盖等价类：(1)(2)(8)
     * 图：hello -> hello (自环)
     */
    @Test
    public void testSameWord() {
        graph.addEdge("hello", "hello");

        List<String> bridges = graph.getBridgeWords("hello", "hello");

        // 自环情况：hello -> hello -> hello，hello 可以作为自己的桥接词
        assertNotNull("应返回非null列表", bridges);
        // 可能返回空列表或包含 "hello"，取决于具体实现
        assertTrue("应返回空列表或包含hello", bridges.isEmpty() || bridges.contains("hello"));
    }
}
