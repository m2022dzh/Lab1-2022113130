@echo off
REM Lab3 Build and Test Script for Windows

setlocal enabledelayedexpansion

REM 设置路径变量
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PROJECT_DIR=%cd%
set SRC_DIR=%PROJECT_DIR%\src\main\java
set TEST_DIR=%PROJECT_DIR%\src\test\java
set OUTPUT_DIR=%PROJECT_DIR%\target\classes
set TEST_OUTPUT_DIR=%PROJECT_DIR%\target\test-classes

REM 创建输出目录
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"
if not exist "%TEST_OUTPUT_DIR%" mkdir "%TEST_OUTPUT_DIR%"

echo ========================================
echo Step 1: Compiling source code...
echo ========================================

"%JAVA_HOME%\bin\javac" -encoding UTF-8 -d "%OUTPUT_DIR%" ^
  "%SRC_DIR%\com\wordgraph\WordNode.java" ^
  "%SRC_DIR%\com\wordgraph\WordGraph.java" ^
  "%SRC_DIR%\com\wordgraph\Main.java"

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    exit /b 1
)
echo Source code compiled successfully.

echo.
echo ========================================
echo Step 2: Compiling test code...
echo ========================================

REM 下载JUnit如果还没有的话
if not exist "%PROJECT_DIR%\lib\junit-4.13.2.jar" (
    echo Downloading JUnit...
    mkdir "%PROJECT_DIR%\lib"
    REM 这需要网络连接
    echo Please manually download JUnit 4.13.2 and Hamcrest core JARs
    echo Place them in: %PROJECT_DIR%\lib\
    exit /b 1
)

"%JAVA_HOME%\bin\javac" -encoding UTF-8 -cp "%OUTPUT_DIR%;%PROJECT_DIR%\lib\*" ^
  -d "%TEST_OUTPUT_DIR%" ^
  "%TEST_DIR%\com\wordgraph\WordGraphBlackBoxTest.java" ^
  "%TEST_DIR%\com\wordgraph\WordGraphWhiteBoxTest.java"

if %ERRORLEVEL% NEQ 0 (
    echo Test compilation failed!
    exit /b 1
)
echo Test code compiled successfully.

echo.
echo ========================================
echo Step 3: Running JUnit tests...
echo ========================================

"%JAVA_HOME%\bin\java" -cp "%TEST_OUTPUT_DIR%;%OUTPUT_DIR%;%PROJECT_DIR%\lib\*" ^
  org.junit.runner.JUnitCore com.wordgraph.WordGraphBlackBoxTest

if %ERRORLEVEL% NEQ 0 (
    echo Tests may have failed. Check output above.
)

echo.
echo Build process completed!
echo.
