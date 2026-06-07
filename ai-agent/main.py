"""
云悟英语 — AI 智能体微服务入口 (安全加固版)
FastAPI + LangGraph

启动: python main.py  /  uvicorn main:app --host 0.0.0.0 --port 8000

API:
    POST /api/v1/chat         — 陪练对话 (核心)
    GET  /api/v1/health       — 健康检查 (+ Redis 状态)
    GET  /api/v1/metrics      — 运行时指标
    GET  /api/v1/info         — 模型信息
"""
from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException

import config
from models.schemas import ChatRequest, ChatResponse, CoachState
from agent.coach_graph import coach_graph
from middleware.tracing import TracingMiddleware
from middleware.rate_limiter import RateLimiterMiddleware
from utils.logger import logger
from utils.sanitizer import sanitize_history, sanitize_user_message


# ==================== 应用生命周期 ====================

@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("ai_agent_starting",
                extra={"provider": config.LLM_PROVIDER,
                       "model": config.LLM_MODEL,
                       "port": config.PORT,
                       "redis": config.REDIS_ENABLED,
                       "rate_limit": config.RATE_LIMIT_ENABLED})
    yield
    logger.info("ai_agent_shutdown")


app = FastAPI(
    title="云悟英语 AI 智能体",
    description="基于 LangGraph 的英语陪练智能体微服务 (安全加固版)",
    version="1.1.0",
    lifespan=lifespan,
)

# ==================== 安全中间件 (按优先级添加) ====================

# 1. 分布式追踪 (最外层, 捕获所有请求)
app.add_middleware(TracingMiddleware)

# 2. IP 限流 (令牌桶)
app.add_middleware(RateLimiterMiddleware)

# 3. CORS — 严格白名单模式
from fastapi.middleware.cors import CORSMiddleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=config.CORS_ALLOWED_ORIGINS,     # ← 白名单，不再是 ["*"]
    allow_credentials=True,
    allow_methods=["GET", "POST"],                   # ← 只开放需要的
    allow_headers=["Content-Type", "Authorization", "X-Trace-Id", "X-Request-Id"],
    max_age=3600,
)


# ==================== 安全响应头 ====================

@app.middleware("http")
async def security_headers(request, call_next):
    response = await call_next(request)
    response.headers["X-Content-Type-Options"] = "nosniff"
    response.headers["X-Frame-Options"] = "DENY"
    response.headers["X-XSS-Protection"] = "1; mode=block"
    response.headers["Strict-Transport-Security"] = "max-age=31536000; includeSubDomains"
    response.headers["Cache-Control"] = "no-store"
    return response


# ==================== API 端点 ====================

@app.post("/api/v1/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    """陪练对话 — 核心 API"""
    if not request.user_message or not request.user_message.strip():
        raise HTTPException(status_code=400, detail="消息内容不能为空")
    if len(request.user_message) > 2000:
        raise HTTPException(status_code=400, detail="消息内容过长 (最大2000字符)")

    try:
        # 入口脱敏: 清洗用户消息 + 历史记录
        safe_message = sanitize_user_message(request.user_message)
        safe_history = sanitize_history(request.conversation_history or [])

        initial_state = CoachState(
            session_id=request.session_id,
            user_id=request.user_id,
            user_message=safe_message,
            scene=request.scene,
            learner_profile=request.learner_profile,
            conversation_history=safe_history
        )

        config_dict = {
            "configurable": {
                "thread_id": request.session_id,
                "user_id": str(request.user_id or "anonymous")
            }
        }

        logger.info("chat_request",
                     extra={"session_id": request.session_id,
                            "msg_len": len(request.user_message)})

        result = await coach_graph.ainvoke(
            initial_state.model_dump(), config_dict
        )

        return ChatResponse(
            ai_message=result.get("ai_response", ""),
            corrections=result.get("corrections", []),
            model_name=result.get("model_name", config.LLM_MODEL),
            prompt_tokens=result.get("prompt_tokens", 0),
            completion_tokens=result.get("completion_tokens", 0),
            total_tokens=result.get("total_tokens", 0)
        )

    except HTTPException:
        raise
    except Exception as e:
        logger.error("chat_failed", extra={
            "session_id": request.session_id,
            "error": str(e),
            "error_type": type(e).__name__
        })
        raise HTTPException(
            status_code=500,
            detail=f"AI 服务暂时不可用，请稍后重试"
        )


@app.get("/api/v1/health")
async def health():
    """健康检查 (+ Redis 连接状态)"""
    status = "healthy"
    redis_status = "disabled"

    if config.REDIS_ENABLED:
        try:
            import redis
            r = redis.Redis(host=config.REDIS_HOST, port=config.REDIS_PORT,
                            db=config.REDIS_DB, password=config.REDIS_PASSWORD or None,
                            socket_connect_timeout=2)
            redis_status = "connected" if r.ping() else "failed"
        except Exception:
            redis_status = "unavailable"

    return {
        "status": "healthy",
        "provider": config.LLM_PROVIDER,
        "model": config.LLM_MODEL,
        "redis": redis_status,
        "version": "1.1.0",
    }


@app.get("/api/v1/metrics")
async def metrics():
    """运行时指标 — 用于监控和告警"""
    import psutil, os
    process = psutil.Process(os.getpid())
    mem = process.memory_info()
    return {
        "uptime_seconds": process.create_time(),
        "memory_rss_mb": round(mem.rss / 1024 / 1024, 2),
        "memory_pct": round(process.memory_percent(), 2),
        "cpu_pct": round(process.cpu_percent(), 2),
        "threads": process.num_threads(),
        "llm_provider": config.LLM_PROVIDER,
        "llm_model": config.LLM_MODEL,
    }


@app.get("/api/v1/info")
async def info():
    """模型信息"""
    return {
        "provider": config.LLM_PROVIDER,
        "model": config.LLM_MODEL,
        "temperature": config.LLM_TEMPERATURE,
        "max_tokens": config.LLM_MAX_TOKENS,
        "retry_max_attempts": config.LLM_RETRY_MAX_ATTEMPTS,
        "correction_enabled": config.CORRECTION_ENABLED,
        "rate_limit_enabled": config.RATE_LIMIT_ENABLED,
        "version": "1.1.0",
    }


# ==================== 入口 ====================

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host=config.HOST, port=config.PORT, reload=config.DEBUG)
