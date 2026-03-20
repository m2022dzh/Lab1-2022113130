import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
// R3 modification
// C4 branch modification
// B1 branch modification

// B1 branch modification (second)

// C4 branch
// 手动修改
// B1 branch - redo

// C4 branch - redo

public class Main {
    public static void main(String[] args) {
        System.out.println("我的图项目启动啦！");
        // ========== 测试读取文件 ==========
        // 1. 创建 Scanner 对象，用来读取用户在控制台输入的文字
        Scanner scanner = new Scanner(System.in);
        // 2. 提示用户输入文件路径
        System.out.print("请输入文件路径：");
        String path = scanner.nextLine(); // 读取一行输入（比如 C:\test.txt）

        // 3. 调用 readWordsFromFile 方法，传入路径，得到单词数组
        String[] words = readWordsFromFile(path);

        // 4. 如果数组长度为0（没读到单词），就结束程序
        if (words.length == 0) {
            System.out.println("没有读取到任何单词，程序结束。");
            return; // 退出 main 方法
        }
        // 构建图
        WordGraph graph = new WordGraph();
        for (int i = 0; i < words.length - 1; i++) {
            graph.addEdge(words[i], words[i + 1]);
        }
        System.out.println("图构建完成。");

        // ***********************************菜单循环
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
                graph.printGraph(); // 打印图
            } else if (choice.equals("2")) {
                System.out.print("请输入第一个单词：");
                String word1 = scanner.nextLine().trim().toLowerCase();
                System.out.print("请输入第二个单词：");
                String word2 = scanner.nextLine().trim().toLowerCase();

                List<String> bridges = graph.getBridgeWords(word1, word2);

                if (bridges == null) {
                    // 至少一个单词不在图中
                    if (!graph.containsWord(word1) && !graph.containsWord(word2)) {
                        System.out.println("No " + word1 + " and " + word2 + " in the graph!");
                    } else if (!graph.containsWord(word1)) {
                        System.out.println("No " + word1 + " in the graph!");
                    } else {
                        System.out.println("No " + word2 + " in the graph!");
                    }
                } else if (bridges.isEmpty()) {
                    System.out.println("No bridge words from " + word1 + " to " + word2 + "!");
                } else {
                    // 格式化输出，例如 "xxx, yyy, and zzz"
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
                    System.out.println(
                            "The bridge words from " + word1 + " to " + word2 + " are: " + sb.toString() + ".");
                }
            } else if (choice.equals("3")) {
                System.out.println("开始随机游走...");
                String walkPath = graph.randomWalk();
                System.out.println("游走路径：" + walkPath);

                // 将路径写入文件（可选，实验要求写入磁盘）
                try (java.io.FileWriter fw = new java.io.FileWriter("random_walk_result.txt")) {
                    fw.write(walkPath);
                    System.out.println("结果已保存到 random_walk_result.txt");
                } catch (java.io.IOException e) {
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
                System.out.println("单词 \"" + word + "\" 的 PageRank 值为: " + pr);
            } else if (choice.equals("7")) {
                System.out.println("程序结束。");
                break;
            } else {
                System.out.println("无效选项，请重新输入。");
            }
        }

    }

    // ========== 读取文件并清洗单词的方法 ==========
    // 这个方法放在 main 方法的后面，但仍在类的大括号内
    public static String[] readWordsFromFile(String filePath) {
        // 用 StringBuilder 来拼接文件中的所有内容
        StringBuilder content = new StringBuilder();

        // 尝试打开文件并读取
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(" "); // 将换行替换为空格
            }
        } catch (IOException e) {
            // 如果出错（比如文件不存在），打印错误信息
            System.out.println("文件读取失败: " + e.getMessage());
            return new String[0]; // 返回空数组
        }

        // 将全部内容转成小写，并把所有非字母字符替换为空格
        String cleaned = content.toString().toLowerCase().replaceAll("[^a-z]", " ");

        // 按空格分割，并过滤掉空字符串（比如多个连续空格产生的空串）
        String[] words = cleaned.split("\\s+");

        return words; // 返回单词数组
    }

    // 功能1 & 2: 展示有向图 (G是图的数据类型，你需要自己定义)
    public static void showDirectedGraph(Object G) {
        //
    }

    // 功能3: 查询桥接词
    public static String queryBridgeWords(String word1, String word2) {
        return null; // 先让它能编译通过
    }

    // 功能4: 根据桥接词生成新文本
    public static String generateNewText(String inputText) {
        return null;
    }

    // 功能5: 计算最短路径
    public static String calcShortestPath(String word1, String word2) {
        return null;
    }

    // 功能6: 计算PageRank (d固定0.85)
    public static Double calcPageRank(String word) {
        return 0.0;
    }

    // 功能7: 随机游走
    public static String randomWalk() {
        return null;
    }

}