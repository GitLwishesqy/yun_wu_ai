@echo off
chcp 65001 >nul
echo ========================================
echo   云悟英语 - 后端服务启动
echo ========================================

REM 杀掉占用 2667 端口的旧进程
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :2667') do (
    taskkill /f /pid %%a >nul 2>&1
)

REM 打包 + 启动
cd /d %~dp0
call mvn package -DskipTests -q -pl yunwu-bootstrap -am
if %ERRORLEVEL% NEQ 0 (
    echo [FAIL] Build failed!
    pause
    exit /b 1
)

echo Starting on port 2667...
java -jar yunwu-bootstrap\target\yunwu-bootstrap-1.0.0-SNAPSHOT.jar
pause
