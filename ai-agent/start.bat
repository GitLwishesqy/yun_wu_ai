@echo off
chcp 65001 >nul
echo ========================================
echo   云悟英语 AI 智能体 - 启动
echo ========================================

REM 杀掉占用 2668 端口的旧进程
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :2668') do (
    taskkill /f /pid %%a >nul 2>&1
)

REM 启动
cd /d %~dp0
python main.py
pause
