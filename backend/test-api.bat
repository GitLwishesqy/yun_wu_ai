@echo off
chcp 65001 >nul
echo ========================================
echo   云悟英语 - API 链路测试
echo ========================================
echo.

REM 测试 1: 发送验证码
echo [1/5] 发送验证码...
curl -s -X POST http://localhost:2667/api/v1/auth/send-code -H "Content-Type: application/json" -d "{\"phone\":\"13800000001\",\"purpose\":\"LOGIN\"}"
echo.

REM 测试 2: 登录
echo [2/5] 登录...
curl -s -X POST http://localhost:2667/api/v1/auth/login -H "Content-Type: application/json" -d "{\"phone\":\"13800000001\",\"code\":\"123456\"}" > %TEMP%\login.json
echo.

REM 提取 TOKEN (需要 jq 或手动复制)
echo [3/5] 获取场景列表（请手动替换 TOKEN）...
echo curl -s http://localhost:2667/api/v1/scenes -H "Authorization: Bearer TOKEN"

echo.
echo [4/5] 创建会话（请手动替换 TOKEN）...
echo curl -s -X POST http://localhost:2667/api/v1/sessions -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN" -d "{\"sceneId\":1,\"sessionType\":\"SCENE\"}"

echo.
echo [5/5] 发送消息（请手动替换 TOKEN 和 SESSION_ID）...
echo curl -s -X POST http://localhost:2667/api/v1/sessions/SESSION_ID/messages -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN" -d "{\"content\":\"Hello\",\"contentType\":\"TEXT\"}"

echo.
echo ========================================
echo   API 测试完成
echo ========================================
pause
