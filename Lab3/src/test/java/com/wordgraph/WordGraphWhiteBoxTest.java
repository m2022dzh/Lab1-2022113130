package com.wordgraph;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 白盒测试：使用基本路径法测试 getShortestPath 方法。
 *
 * 下面是 getShortestPath 的关键决策点：
 * 1. 检查两个单词是否都存在 (if语句)
 * 2. 检查是否是同一个单词 (if语句)
 * 3. Dijkstra循环条件 (!pq.isEmpty)
 * 4. 是否找到目标 (if u.equals(to))
 * 5. 检查可达性 (if dist.get(u) == INFINITY)
 * 6. 检查路径是否存在 (if !prev.containsKey(to))
 * 7. 格式化输出
 *
 * 基本路径（独立路径）：
 * P1: from或to不存在 -> No "X" or "Y" in the graph!
 * P2: from == to -> from (distance 0)
 * P3: from存在, to存在, 有路径 -> X -> Y -> Z ... (distance d)
 * P4: from存在, to存在, 无路径 -> No path from X to Y!
 * P5: 到达目标提前退出
 *
 * @author lab-team
 */
public class WordGraphWhiteBoxTest {
    private WordGraph graph;

    @Before
    public void setUp() {
        graph = new WordGraph();
    }

    // ============ 基本路径测试 ============

    /**
     * 基本路径P1：from单词不存在
     * 输入：from="notexist", to="world"
     * 预期：返回包含"notexist"的错误消息
     */
    @Test
    public void testGetShortestPath_FromNotExist() {
        graph.addEdge("hello", "world");

        String result = graph.getShortestPath("notexist", "world");

        assertTrue("应返回错误消息", result.contains("No")
                || result.contains("notexist"));
    }

    /**
     * 基本路径P2：to单词不存在
     * 输入：from="hello", to="notexist"
     * 预期：返回包含"notexist"的错误消息
     */
    @Test
    public void testGetShortestPath_ToNotExist() {
        graph.addEdge("hello", "world");

        String result = graph.getShortestPath("hello", "notexist");

        assertTrue("应返回错误消息", result.contains("No")
                || result.contains("notexist"));
    }

    /**
     * 基本路径P3：from == to（同一个单词）
     * 输入：from="hello", to="hello"
     * 预期：返回 "hello (distance 0)"
     */
    @Test
    public void testGetShortestPath_SameWord() {
        graph.addEdge("hello", "world");

        String result = graph.getShortestPath("hello", "hello");

        assertEquals("应返回同一单词的距离0", "hello (distance 0)", result);
    }

    /**
     * 基本路径P4：存在直接路径（距离为1）
     * 图：hello -> world
     * 输入：from="hello", to="world"
     * 预期：返回 "hello -> world (distance 1)"
     */
    @Test
    public void testGetShortestPath_DirectPath() {
        graph.addEdge("hello", "world");

        String result = graph.getShortestPath("hello", "world");

        assertEquals("应返回直接路径", "hello -> world (distance 1.0)", result);
    }

    /**
     * 基本路径P5：存在多跳路径
     * 图：hello -> beautiful -> world
     * 需要经过中间节点
     */
    @Test
    public void testGetShortestPath_MultiHopPath() {
        graph.addEdge("hello", "beautiful");
        graph.addEdge("beautiful", "world");

        String result = graph.getShortestPath("hello", "world");

        assertTrue("应包含所有中间节点", result.contains("hello")
                && result.contains("beautiful") && result.contains("world"));
        assertTrue("应包含距离", result.contains("distance"));
        assertTrue("距离应为2", result.contains("2.0"));
    }

    /**
     * 基本路径P6：无路径（目标节点存在但不可达）
     * 图：hello -> world, test -> result (两个孤立的连通分量)
     * 输入：from="hello", to="test"
     * 预期：返回 "No path from hello to test!"
     */
    @Test
    public void testGetShortestPath_NoPath() {
        graph.addEdge("hello", "world");
        graph.addEdge("test", "result");

        String result = graph.getShortestPath("hello", "test");

        assertTrue("应返回无路径消息", result.contains("No path"));
    }

    /**
     * 基本路径P7：多条路径选择最短
     * 图：
     * a -> b (权重1) -> d
     * a -> c (权重2) -> d
     * 应选择 a -> b -> d（距离2）而不是 a -> c -> d（距离3）
     */
    @Test
    public void testGetShortestPath_MultiPath_ChooseShortest() {
        graph.addEdge("a", "b");
        graph.addEdge("b", "d");
        graph.addEdge("a", "c");
        graph.addEdge("c", "d");

        String result = graph.getShortestPath("a", "d");

        // 距离应为2 (a->b->d)
        assertTrue("应选择最短路径", result.contains("a")
                && result.contains("b") && result.contains("d"));
        assertTrue("距离应为2", result.contains("2.0"));
    }

    /**
     * 基本路径P8：重边的影响
     * 图：hello -> world (3条边), world -> test (1条边)
     * 总距离应为 3 + 1 = 4
     */
    @Test
    public void testGetShortestPath_WithDuplicateEdges() {
        graph.addEdge("hello", "world");
        graph.addEdge("hello", "world");
        graph.addEdge("hello", "world");
        graph.addEdge("world", "test");

        String result = graph.getShortestPath("hello", "test");

        assertTrue("应返回路径", result.contains("hello")
                && result.contains("world") && result.contains("test"));
        // 距离应为 3 + 1 = 4
        assertTrue("距离应为4", result.contains("4.0"));
    }

    /**
     * 基本路径P9：环路图
     * 图：a -> b -> c -> a (形成环)
     * 从a到c的最短路径应该是 a -> b -> c
     */
    @Test
    public void testGetShortestPath_CyclicGraph() {
        graph.addEdge("a", "b");
        graph.addEdge("b", "c");
        graph.addEdge("c", "a");

        String result = graph.getShortestPath("a", "c");

        assertEquals("应返回最短路径", "a -> b -> c (distance 2.0)", result);
    }

    /**
     * 基本路径P10：单节点
     * 图只有一个节点hello，无出边
     */
    @Test
    public void testGetShortestPath_SingleNode() {
        graph.addEdge("hello", "hello");

        String result = graph.getShortestPath("hello", "hello");

        assertEquals("同一节点距离为0", "hello (distance 0)", result);
    }

    /**
     * 基本路径P11：复杂的更短路径选择
     * 图：
     * start -> a -> end (距离2)
     * start -> b -> end (距离2)
     * start -> c -> end (距离2)
     * 任选一条即可
     */
    @Test
    public void testGetShortestPath_MultipleShortestPaths() {
        graph.addEdge("start", "a");
        graph.addEdge("a", "end");
        graph.addEdge("start", "b");
        graph.addEdge("b", "end");

        String result = graph.getShortestPath("start", "end");

        assertTrue("应返回其中一条最短路径", result.contains("start")
                && result.contains("end"));
        assertTrue("距离应为2", result.contains("2.0"));
    }

    /**
     * 基本路径P12：权重差异大的路径选择
     * 图：
     * a -> b -> c (距离2)
     * a -> d -> c (距离10)
     * 应选择第一条
     */
    @Test
    public void testGetShortestPath_WeightedPath() {
        graph.addEdge("a", "b");
        graph.addEdge("b", "c");
        // 添加10条边以增加权重
        for (int i = 0; i < 10; i++) {
            graph.addEdge("a", "d");
        }
        graph.addEdge("d", "c");

        String result = graph.getShortestPath("a", "c");

        // 最短路径应该是 a -> b -> c (距离2)
        assertTrue("应选择权重轻的路径", result.contains("b"));
        assertTrue("距离应为2", result.contains("2.0"));
    }
}
