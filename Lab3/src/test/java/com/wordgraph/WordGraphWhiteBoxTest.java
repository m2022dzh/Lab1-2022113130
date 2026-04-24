package com.wordgraph;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 白盒测试：使用基本路径法测试重构后的 getShortestPath 方法。
 * 针对主函数，圈复杂度：V(G) = 4 个判定点 + 1 = 5
 * * 核心基本路径（独立路径）：
 * P1: from 或 to 不存在 -> 提前返回错误
 * P2: from == to -> 返回距离 0
 * P3: 节点存在但不可达 (dist == INFINITY) -> 返回无路径
 * P4: 正常到达 -> 执行 Dijkstra 并成功拼接路径
 *
 *
 */
public class WordGraphWhiteBoxTest {
    private WordGraph graph;

    @Before
    public void setUp() {
        graph = new WordGraph();
    }

    // ============ 基本路径 1：节点不存在 ============

    /**
     * TC1 / P1a: from 单词不存在 (覆盖复合条件的前半部分 true)
     */
    @Test
    public void testGetShortestPath_FromNotExist() {
        graph.addEdge("hello", "world");
        String result = graph.getShortestPath("notexist", "world");
        assertTrue("应返回错误消息", result.contains("No") && result.contains("notexist"));
    }

    /**
     * TC2 / P1b: to 单词不存在 (覆盖复合条件的后半部分 true)
     */
    @Test
    public void testGetShortestPath_ToNotExist() {
        graph.addEdge("hello", "world");
        String result = graph.getShortestPath("hello", "notexist");
        assertTrue("应返回错误消息", result.contains("No") && result.contains("notexist"));
    }

    // ============ 基本路径 2：起点终点相同 ============

    /**
     * TC3 / P2: from == to（同一个单词）
     * 覆盖判定：from.equals(to) 为 true
     */
    @Test
    public void testGetShortestPath_SameWord() {
        graph.getOrCreateNode("hello"); // 确保节点存在
        String result = graph.getShortestPath("hello", "hello");
        assertEquals("应返回同一单词的距离0", "hello (distance 0)", result);
    }

    // ============ 基本路径 3：节点存在但不可达 ============

    /**
     * TC4 / P3: 无路径（目标节点存在但不可达）
     * 覆盖判定：dist.get(to) == Double.POSITIVE_INFINITY 为 true
     */
    @Test
    public void testGetShortestPath_NoPath() {
        graph.getOrCreateNode("hello");
        graph.getOrCreateNode("test");
        // 没有添加边，两点不连通
        String result = graph.getShortestPath("hello", "test");
        assertTrue("应返回无路径消息", result.contains("No path from hello to test!"));
    }

    // ============ 基本路径 4：成功找到路径 ============
    // (以下用例在主函数中走的是同一条路径，但覆盖了内部 Dijkstra 的不同分支逻辑)

    /**
     * TC5 / P4 (基础): 存在直接路径（距离为1）
     * 覆盖判定：正常跑通算法并返回
     */
    @Test
    public void testGetShortestPath_DirectPath() {
        graph.addEdge("hello", "world");
        String result = graph.getShortestPath("hello", "world");
        assertEquals("应返回直接路径", "hello -> world (distance 1.0)", result);
    }

    /**
     * TC6 / P4 (变体): 存在多跳路径
     */
    @Test
    public void testGetShortestPath_MultiHopPath() {
        graph.addEdge("hello", "beautiful");
        graph.addEdge("beautiful", "world");

        String result = graph.getShortestPath("hello", "world");

        assertTrue("应包含所有中间节点", result.contains("hello")
                && result.contains("beautiful") && result.contains("world"));
        assertTrue("距离应为2.0", result.contains("2.0"));
    }

    /**
     * TC7 / P4 (变体): 多条路径选择最短
     */
    @Test
    public void testGetShortestPath_MultiPath_ChooseShortest() {
        graph.addEdge("a", "b");
        graph.addEdge("b", "d");
        graph.addEdge("a", "c");
        graph.addEdge("c", "d");

        // 假设通过某种方式 c->d 的权重更大，或者纯粹测试等价最短路径
        // 在均权情况下，返回任意一条合法最短路径均可
        String result = graph.getShortestPath("a", "d");

        assertTrue("应包含起点和终点", result.contains("a") && result.contains("d"));
        assertTrue("距离应为2.0", result.contains("2.0"));
    }

    /**
     * TC8 / P4 (变体): 重边的影响（权重累加测试）
     * 注意：这段逻辑取决于你的 addEdge 具体实现（是覆盖还是累加出现次数作为权重）
     * 假设每次 addEdge 会使得该边权值 + 1
     */
    @Test
    public void testGetShortestPath_WithDuplicateEdges() {
        graph.addEdge("hello", "world");
        graph.addEdge("hello", "world"); // 此时 hello->world 权重变为 2
        graph.addEdge("hello", "world"); // 权重变为 3
        graph.addEdge("world", "test");  // 权重 1

        String result = graph.getShortestPath("hello", "test");

        assertTrue("应返回路径", result.contains("hello")
                && result.contains("world") && result.contains("test"));
        // 距离应为 3 + 1 = 4.0
        assertTrue("距离应为4.0", result.contains("4.0"));
    }
}