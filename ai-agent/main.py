"""
云悟英语 — AI 智能体微服务入口
FastAPI + LangGraph

启动方式:
    python main.py
    或
    uvicorn main:app --host 0.0.0.0 --port 8000 --reload

API:
    POST /api/v1/chat         — 陪练对话 (核心)
    GET  /api/v1/health       — 健康检查
    GET  /api/v1/info         — 模型信息
"""
import logging
import uuid
from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware

import config
from models.schemas import ChatRequest, ChatResponse, CoachState
from agent.coach_graph import coach_graph

# ==================== 日志 ====================

logging.basicConfig(
    level=logging.DEBUG if config.DEBUG else logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s"
)
logger = logging.getLogger(__name__)


# ==================== 应用生命周期 ====================

@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info(f"[AI-Agent] 启动完成, provider={config.LLM_PROVIDER}, "
                f"model={config.LLM_MODEL}")
    yield
    logger.info("[AI-Agent] 关闭")


app = FastAPI(
    title="云悟英语 AI 智能体",
    description="基于 LangGraph 的英语陪练智能体微服务",
    version="1.0.0",
    lifespan=lifespan
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ==================== API 端点 ====================

@app.post("/api/v1/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    """
    陪练对话 — 核心 API
    Java 后端调用此接口获取 AI 回复和纠错信息
    """
    try:
        # 构建 LangGraph 状态
        initial_state = CoachState(
            session_id=request.session_id,
            user_id=request.user_id,
            user_message=request.user_message,
            scene=request.scene,
            learner_profile=request.learner_profile,
            conversation_history=request.conversation_history or []
        )

        # 配置 (thread_id 用于状态持久化和多轮对话)
        config_dict = {
            "configurable": {
                "thread_id": request.session_id,
                "user_id": str(request.user_id or "anonymous")
            }
        }

        # 执行 LangGraph
        logger.info(f"[Chat] session={request.session_id}, msg={request.user_message[:80]}...")
        result = await coach_graph.ainvoke(
            initial_state.model_dump(),
            config_dict
        )

        # 构建响应
        return ChatResponse(
            ai_message=result.get("ai_response", ""),
            corrections=result.get("corrections", []),
            model_name=result.get("model_name", config.LLM_MODEL),
            prompt_tokens=result.get("prompt_tokens", 0),
            completion_tokens=result.get("completion_tokens", 0),
            total_tokens=result.get("total_tokens", 0)
        )

    except Exception as e:
        logger.error(f"[Chat] 处理失败: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"AI 服务错误: {str(e)}")


@app.get("/api/v1/health")
async def health():
    """健康检查"""
    return {
        "status": "healthy",
        "provider": config.LLM_PROVIDER,
        "model": config.LLM_MODEL
    }


@app.get("/api/v1/info")
async def info():
    """模型信息"""
    return {
        "provider": config.LLM_PROVIDER,
        "model": config.LLM_MODEL,
        "temperature": config.LLM_TEMPERATURE,
        "max_tokens": config.LLM_MAX_TOKENS,
        "correction_enabled": config.CORRECTION_ENABLED,
        "version": "1.0.0"
    }


# ==================== 入口 ====================

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host=config.HOST, port=config.PORT, reload=config.DEBUG)
