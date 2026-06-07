@echo off
chcp 65001 >nul
echo ========================================
echo   云悟英语 - 后端服务启动
echo ========================================
cd /d %~dp0

echo [1/2] Building...
call mvn package -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo [FAIL] Build failed!
    pause
    exit /b 1
)

echo [2/2] Starting on port 2667...
echo.
java -jar yunwu-bootstrap\target\yunwu-bootstrap-1.0.0-SNAPSHOT.jar
pause
