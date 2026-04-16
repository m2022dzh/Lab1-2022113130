package com.wordgraph;

import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * 黑盒测试：使用等价类划分方法测试 getBridgeWords 方法。
 *
 * @author lab-team
 */
public class WordGraphBlackBoxTest {
    private WordGraph graph;

    @Before
    public void setUp() {
        graph = new WordGraph();
    }

    // ============ 等价类划分 ============
    // 有效等价类：
    // C1: 两个单词都存在，有1个桥接词
    // C2: 两个单词都存在，有多个桥接词
    // C3: 两个单词都存在，无桥接词
    // C4: word1存在，word2不存在
    // C5: word1不存在，word2存在
    // 无效等价类：
    // E1: 两个单词都不存在
    // E2: word1为null或word2为null
    // E3: 同一个单词（word1 == word2)

    /**
     * 测试用例1：两个单词都存在，有1个桥接词
     * 测试路径：word1 -> bridge -> word2
     */
    @Test
    public void testGetBridgeWords_OneWordBridge() {
        // 构造图：hello -> world -> test
        // hello -> there -> test
        graph.addEdge("hello", "world");
        graph.addEdge("world", "test");
        graph.addEdge("hello", "there");
        graph.addEdge("there", "test");

        List<String> bridges = graph.getBridgeWords("hello", "test");

        assertFalse("应该找到桥接词", bridges.isEmpty());
        assertEquals("应该有2个桥接词", 2, bridges.size());
        assertTrue("world应该是桥接词", bridges.contains("world"));
        assertTrue("there应该是桥接词", bridges.contains("there"));
    }

    /**
     * 测试用例2：两个单词都存在，无桥接词
     */
    @Test
    public void testGetBridgeWords_NoBridges() {
        // 构造图：hello -> test, world -> hello (无从hello到test的桥接词)
        graph.addEdge("hello", "test");
        graph.addEdge("world", "hello");

        List<String> bridges = graph.getBridgeWords("hello", "world");

        assertTrue("应该返回空列表", bridges.isEmpty());
    }

    /**
     * 测试用例3：第一个单词存在，第二个不存在
     */
    @Test
    public void testGetBridgeWords_Word2NotFound() {
        graph.addEdge("hello", "world");
        graph.addEdge("world", "test");

        List<String> bridges = graph.getBridgeWords("hello", "notexist");

        assertNull("word2不存在时应返回null", bridges);
    }

    /**
     * 测试用例4：第一个单词不存在，第二个存在
     */
    @Test
    public void testGetBridgeWords_Word1NotFound() {
        graph.addEdge("hello", "world");
        graph.addEdge("world", "test");

        List<String> bridges = graph.getBridgeWords("notexist", "test");

        assertNull("word1不存在时应返回null", bridges);
    }

    /**
     * 测试用例5：两个单词都不存在
     */
    @Test
    public void testGetBridgeWords_BothNotFound() {
        graph.addEdge("hello", "world");

        List<String> bridges = graph.getBridgeWords("notexist1", "notexist2");

        assertNull("两个单词都不存在时应返回null", bridges);
    }

    /**
     * 测试用例6：同一个单词（word1 == word2)
     */
    @Test
    public void testGetBridgeWords_SameWord() {
        graph.addEdge("hello", "world");
        graph.addEdge("hello", "hello");

        // 同一个单词：查找从hello到hello的桥接词实际上是自环
        List<String> bridges = graph.getBridgeWords("hello", "hello");

        // 根据实现，hello -> hello -> hello 意味着hello自己不是its own bridge
        assertTrue("应返回空列表或包含hello", bridges != null);
    }

    /**
     * 测试用例7：word1有出边但都不指向word2的中间节点
     */
    @Test
    public void testGetBridgeWords_NoPathViaNeighbors() {
        // 构造图：a -> b, a -> c, b -> d, c -> e (从a到d无桥接词)
        graph.addEdge("a", "b");
        graph.addEdge("a", "c");
        graph.addEdge("b", "d");
        graph.addEdge("c", "e");

        List<String> bridges = graph.getBridgeWords("a", "d");

        assertTrue("应返回空列表", bridges.isEmpty());
    }

    /**
     * 测试用例8：多个出边指向同一个中间节点（重复边）
     */
    @Test
    public void testGetBridgeWords_DuplicateEdges() {
        // 构造图：a -> b (2条边), b -> c
        graph.addEdge("a", "b");
        graph.addEdge("a", "b"); // 添加重复边
        graph.addEdge("b", "c");

        List<String> bridges = graph.getBridgeWords("a", "c");

        assertEquals("应有1个桥接词", 1, bridges.size());
        assertTrue("b应该是桥接词", bridges.contains("b"));
    }

    /**
     * 测试用例9：空图
     */
    @Test
    public void testGetBridgeWords_EmptyGraph() {
        List<String> bridges = graph.getBridgeWords("any", "word");

        assertNull("空图中任何单词都不存在应返回null", bridges);
    }

    /**
     * 测试用例10：单节点图
     */
    @Test
    public void testGetBridgeWords_SingleNode() {
        graph.addEdge("hello", "hello");

        List<String> bridges = graph.getBridgeWords("hello", "hello");

        // 一个节点自环的情况
        assertFalse("自环路径应该返回结果", bridges == null);
    }
}
