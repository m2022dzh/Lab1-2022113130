package com.wordgraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * 单词有向图应用的主程序。
 * 提供交互式菜单，支持图构建、查询、游走等功能。
 *
 * @author lab-team
 * @version 1.0
 */
public class Main {
    /**
     * 程序入口点。
     *
     * @param args 命令行参数（此程序不使用）
     */
    public static void main(String[] args) {
        System.out.println("我的图项目启动啦！");

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        try {
            System.out.print("请输入文件路径：");
            String path = scanner.nextLine();

            String[] words = readWordsFromFile(path);

            if (words.length == 0) {
                System.out.println("没有读取到任何单词，程序结束。");
                return;
            }

            WordGraph graph = new WordGraph();
            for (int i = 0; i < words.length - 1; i++) {
                graph.addEdge(words[i], words[i + 1]);
            }
            System.out.println("图构建完成。");

            // 菜单循环
            while (true) {
                System.out.println("\n请选择功能：");
                System.out.println("1. 展示有向图");
                System.out.println("2. 查询桥接词");
                System.out.println("3. 随机游走");
                System.out.println("4. 根据桥接词生成新文本");
                System.out.println("5. 计算最短路径");
                System.out.println("6. 计算 PageRank");
                System.out.println("7. 退出");
                System.out.print("输入选项（1-7）：");
                String choice = scanner.nextLine();

                if (choice.equals("1")) {
                    graph.printGraph();
                } else if (choice.equals("2")) {
                    System.out.print("请输入第一个单词：");
                    String word1 = scanner.nextLine().trim().toLowerCase();
                    System.out.print("请输入第二个单词：");
                    String word2 = scanner.nextLine().trim().toLowerCase();

                    List<String> bridges = graph.getBridgeWords(word1, word2);

                    if (bridges == null) {
                        if (!graph.containsWord(word1)
                                && !graph.containsWord(word2)) {
                            System.out.println("No " + word1 + " and " + word2
                                    + " in the graph!");
                        } else if (!graph.containsWord(word1)) {
                            System.out.println("No " + word1 + " in the graph!");
                        } else {
                            System.out.println("No " + word2 + " in the graph!");
                        }
                    } else if (bridges.isEmpty()) {
                        System.out.println(
                                "No bridge words from " + word1 + " to " + word2
                                        + "!");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < bridges.size(); i++) {
                            if (i > 0) {
                                if (i == bridges.size() - 1) {
                                    sb.append(" and ");
                                } else {
                                    sb.append(", ");
                                }
                            }
                            sb.append(bridges.get(i));
                        }
                        System.out.println("The bridge words from " + word1
                                + " to " + word2 + " are: " + sb.toString()
                                + ".");
                    }
                } else if (choice.equals("3")) {
                    System.out.println("开始随机游走...");
                    String walkPath = graph.randomWalk();
                    System.out.println("游走路径：" + walkPath);

                    try (FileWriter fw = new FileWriter(
                            "random_walk_result.txt", StandardCharsets.UTF_8)) {
                        fw.write(walkPath);
                        System.out.println("结果已保存到 random_walk_result.txt");
                    } catch (IOException e) {
                        System.out.println("保存文件失败：" + e.getMessage());
                    }
                } else if (choice.equals("4")) {
                    System.out.print("请输入一行新文本：");
                    String inputText = scanner.nextLine();
                    String newText = graph.generateNewText(inputText);
                    System.out.println("生成的新文本：");
                    System.out.println(newText);
                } else if (choice.equals("5")) {
                    System.out.print("请输入第一个单词：");
                    String word1 = scanner.nextLine().trim().toLowerCase();
                    System.out.print("请输入第二个单词：");
                    String word2 = scanner.nextLine().trim().toLowerCase();
                    String result = graph.getShortestPath(word1, word2);
                    System.out.println(result);
                } else if (choice.equals("6")) {
                    System.out.print("请输入要计算 PageRank 的单词：");
                    String word = scanner.nextLine().trim().toLowerCase();
                    double pr = graph.calcPageRank(word);
                    System.out.println("单词 \"" + word + "\" 的 PageRank 值为: "
                            + pr);
                } else if (choice.equals("7")) {
                    System.out.println("程序结束。");
                    break;
                } else {
                    System.out.println("无效选项，请重新输入。");
                }
            }
        } finally {
            scanner.close();
        }
    }

    /**
     * 从文件读取单词。清洗文本（小写、去除非字母字符）并拆分为单词数组。
     *
     * @param filePath 文件路径
     * @return 单词数组
     */
    public static String[] readWordsFromFile(String filePath) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(
                new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(" ");
            }
        } catch (IOException e) {
            System.out.println("文件读取失败: " + e.getMessage());
            return new String[0];
        }

        String cleaned = content.toString().toLowerCase()
                .replaceAll("[^a-z]", " ");
        String[] words = cleaned.split("\\s+");

        return words;
    }
}
