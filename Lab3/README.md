# Lab3 代码审查与单元测试

## 项目概述

本项目基于Lab1的单词有向图程序，进行了完整的代码质量审查和单元测试设计。

### 目录结构

```
Lab3/
├── pom.xml                           # Maven项目配置
├── config/
│   └── checkstyle.xml               # Checkstyle规则配置
├── src/
│   ├── main/java/com/wordgraph/
│   │   ├── WordNode.java            # 修复后的单词节点类
│   │   ├── WordGraph.java           # 修复后的图类
│   │   └── Main.java                # 修复后的主程序
│   └── test/java/com/wordgraph/
│       ├── WordGraphBlackBoxTest.java    # 黑盒测试（等价类划分）
│       └── WordGraphWhiteBoxTest.java    # 白盒测试（基本路径法）
├── CODE_REVIEW_REPORT.md            # 代码审查报告
└── README.md                         # 本文件
```

## 快速开始

### 前置要求

- **Java JDK 11+** （已检查：Java 17）
- **Maven 3.6+** （用于构建、测试和检查）
- 或 **VS Code + 扩展**（Java, Maven等）

### 方法1：使用Maven（推荐）

#### 1. 安装Maven

**Windows:**
1. 下载 Maven (https://maven.apache.org/download.cgi)
2. 解压到某个目录（如 C:\apache-maven-3.8.1）
3. 添加到系统环境变量 PATH
4. 验证：`mvn -version`

**Linux/Mac:**
```bash
brew install maven            # macOS
sudo apt install maven        # Ubuntu/Debian
mvn -version                  # 验证
```

#### 2. 编译源代码

```bash
cd Lab3
mvn clean compile
```

#### 3. 运行代码检查

```bash
# Checkstyle 代码规范检查
mvn checkstyle:check

# SpotBugs 潜在缺陷检查
mvn com.github.spotbugs:spotbugs-maven-plugin:check

# 生成报告
mvn site
```

#### 4. 运行单元测试

```bash
# 运行所有测试
mvn test

# 运行特定测试
mvn test -Dtest=WordGraphBlackBoxTest
mvn test -Dtest=WordGraphWhiteBoxTest

# 生成覆盖率报告
mvn jacoco:report
# 报告位置：target/site/jacoco/index.html
```

### 方法2：在IDEA/Eclipse中运行

#### IntelliJ IDEA

1. 打开项目 → File → Open → 选择 Lab3 文件夹
2. 配置 Checkstyle：
   - 安装插件：Settings → Plugins → 搜索"Checkstyle-IDEA"
   - 配置规则：Project Settings → Checkstyle → 添加 config/checkstyle.xml
3. 配置 SpotBugs：
   - 安装插件：Settings → Plugins → 搜索"SpotBugs"
4. 运行测试：
   - 右键 WordGraphBlackBoxTest.java → Run
   - 右键 WordGraphWhiteBoxTest.java → Run
5. 查看覆盖率：
   - Right-click → Run with Coverage

#### Eclipse

1. 导入项目：File → Import → Existing Maven Projects
2. 配置 Checkstyle：
   - 安装插件：Help → Install New Software → http://eclipse-cs.sf.net/update
   - Project → Properties → Checkstyle → 添加配置
3. 运行测试：
   - Right-click → Run As → JUnit Test
4. 查看覆盖率：
   - 安装 EclEmma 插件
   - Right-click → Coverage As → JUnit Test

### 方法3：VS Code + 命令行

#### Step 1: 使用javac编译

```bash
cd Lab3

# 创建输出目录
mkdir -p target/classes target/test-classes

# 编译源代码
javac -encoding UTF-8 -d target/classes \
  src/main/java/com/wordgraph/*.java

# 下载 JUnit 和 Hamcrest
# 创建 lib 目录并下载：
# - junit-4.13.2.jar
# - hamcrest-core-1.3.jar

# 编译测试代码
javac -encoding UTF-8 \
  -cp "target/classes:lib/*" \
  -d target/test-classes \
  src/test/java/com/wordgraph/*.java
```

#### Step 2: 运行测试

```bash
java -cp "target/classes:target/test-classes:lib/*" \
  org.junit.runner.JUnitCore com.wordgraph.WordGraphBlackBoxTest

java -cp "target/classes:target/test-classes:lib/*" \
  org.junit.runner.JUnitCore com.wordgraph.WordGraphWhiteBoxTest
```

## 代码修复说明

### 发现的主要问题及修复

#### 1. 文档缺失
- **问题**：所有类和方法缺少Javadoc
- **修复**：添加了全面的Javadoc文档
- **示例**：
  ```java
  /**
   * 计算两个单词之间的最短路径（使用Dijkstra算法）。
   *
   * @param from 起始单词
   * @param to 目标单词
   * @return 最短路径字符串
   */
  public String getShortestPath(String from, String to)
  ```

#### 2. 资源泄漏
- **问题**：Scanner 未关闭
- **修复**：使用 try-with-resources
  ```java
  Scanner scanner = new Scanner(System.in);
  try {
      // 使用 scanner
  } finally {
      scanner.close();
  }
  ```

#### 3. 性能问题
- **问题**：Random 在循环中重复创建
- **修复**：作为实例变量创建一次
  ```java
  private Random random;  // 实例变量
  
  public WordGraph() {
      random = new Random();  // 只创建一次
  }
  ```

#### 4. 空指针风险
- **问题**：getRandomWord() 可能返回null但未检查
- **修复**：添加null检查
  ```java
  String current = getRandomWord();
  if (current == null) {
      return "图中没有节点";
  }
  ```

## 测试设计详解

### 黑盒测试 - getBridgeWords (10个测试用例)

**测试类**：`WordGraphBlackBoxTest.java`

等价类划分策略：
1. **有效类**：两个单词存在且有桥接词
2. **有效类**：两个单词存在但无桥接词
3. **无效类**：第一个词不存在
4. **无效类**：第二个词不存在
5. **无效类**：都不存在
6. **特殊情况**：同一个单词
7. **复杂情况**：多个桥接词
8. **复杂情况**：重复边处理

### 白盒测试 - getShortestPath (12个测试用例)

**测试类**：`WordGraphWhiteBoxTest.java`

基本路径覆盖：
1. from不存在 → 错误消息
2. to不存在 → 错误消息
3. from == to → 距离0
4. 直接路径 → 一跳
5. 多跳路径 → 多个中间节点
6. 无路径 → 错误消息
7. 多条路径选最短
8. 重边影响权重
9. 环路处理
10. 单节点图
11. 多个等价最短路径
12. 权重差异大的选择

## 测试执行

### 运行所有测试

```bash
mvn test
```

预期输出：
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.wordgraph.WordGraphBlackBoxTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.wordgraph.WordGraphWhiteBoxTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] -------------------------------------------------------
[INFO] BUILD SUCCESS
```

### 查看覆盖率

```bash
mvn test jacoco:report
```

报告位置：`target/site/jacoco/index.html`

预期覆盖率：
- 语句覆盖：95%+
- 分支覆盖：90%+
- 行覆盖：95%+

## Git操作指南

### 创建和使用分支

```bash
# 创建黑盒测试分支
git checkout -b lab3-blackbox
git add -A
git commit -m "Add black-box tests using equivalence class partitioning"
git push origin lab3-blackbox

# 创建白盒测试分支
git checkout -b lab3-whitebox
git add -A
git commit -m "Add white-box tests using basic path method"
git push origin lab3-whitebox

# 合并到master
git checkout master
git merge lab3-blackbox
git merge lab3-whitebox
git push origin master
```

## 代码质量指标

### Checkstyle 检查结果

```
[INFO] There are no issues to report with Checkstyle.
```

### SpotBugs 检查结果

```
[INFO] BUILD SUCCESS
```

### JUnit 测试结果

```
Tests run: 22, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.x sec
```

## 项目文件对应表

| 文件 | 对应需求 | 说明 |
|------|--------|------|
| CODE_REVIEW_REPORT.md | 代码审查 | 详细的问题清单和修复方案 |
| config/checkstyle.xml | Checkstyle配置 | 代码规范检查规则 |
| pom.xml | Maven配置 | 包含所有依赖和插件 |
| src/main/java/... | 修复后的源代码 | Lab1代码的改进版本 |
| WordGraphBlackBoxTest.java | 黑盒测试 | 10个等价类测试用例 |
| WordGraphWhiteBoxTest.java | 白盒测试 | 12个基本路径测试用例 |
| build.bat | 构建脚本 | Windows批处理构建脚本 |

## 常见问题

### Q: 如何在VS Code中运行测试？
A: 安装"Test Explorer UI"扩展，可视化运行测试。

### Q: Maven下载依赖很慢？
A: 配置阿里云Maven镜像：https://developer.aliyun.com/mvn/guide

### Q: 如何只运行某个测试类？
A: `mvn test -Dtest=WordGraphBlackBoxTest`

### Q: 覆盖率没有达到预期？
A: 检查是否所有分支都被测试覆盖，查看 target/site/jacoco/index.html

## 后续改进方向

1. **集成测试**：添加端到端集成测试
2. **性能测试**：测试大规模图的处理性能
3. **模糊测试**：使用随机输入测试稳定性
4. **持续集成**：集成GitHub Actions或Jenkins

## 参考资源

- JUnit官方文档：https://junit.org/junit4/
- Checkstyle规则：https://checkstyle.sourceforge.io/checks.html
- SpotBugs模式：https://spotbugs.readthedocs.io/
- JaCoCo覆盖率：https://www.jacoco.org/jacoco/trunk/doc/

---

**Lab版本**：3.0  
**对应Java版本**：11+
