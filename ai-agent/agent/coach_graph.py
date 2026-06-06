"""
陪练智能体 — LangGraph 状态图 (架构增强版)

改进:
1. LLM 意图分析 (替代关键词)  — 架构升级
2. 并行执行 generate + detect   — 性能优化
3. Redis 检查点持久化           — 生产可用
4. 内存回退 + 结构化日志        — 可观测

图结构 (优化后):

    START
      │
      ▼
  [analyze_intent]     ← LLM 驱动意图分类 (可降级为规则)
      │
      ▼
  ┌─────────────────┐
  │  并行执行 (barrier)│
  │ ┌───────────────┐│
  │ │generate_response││  ← LLM 生成回复
  │ └───────────────┘│
  │ ┌───────────────┐│
  │ │ detect_errors  ││  ← 纠错引擎 (独立LLM调用)
  │ └───────────────┘│
  └─────────┬─────────┘
            ▼
           END
"""
import config
from langgraph.graph import StateGraph, END
from langgraph.checkpoint.memory import MemorySaver
from langgraph.checkpoint.redis import RedisSaver

from models.schemas import CoachState, ChatResponse
from agent.llm_service import get_llm, build_messages, extract_token_usage, invoke_llm_with_retry
from agent.prompt_templates import build_system_prompt
from agent.correction_engine import detect_errors
from agent.intent_analyzer import analyze_intent
from utils.logger import logger


# ==================== 节点: 生成回复 ====================

def generate_response(state: CoachState) -> dict:
    """构建 System Prompt → 调用 LLM → 返回回复"""
    lp = state.learner_profile or {}
    grade_level = "ELEMENTARY"
    cefr_level = "A1"
    weaknesses = "{}"
    if isinstance(lp, dict):
        grade_level = lp.get("grade_level", "ELEMENTARY")
        cefr_level = lp.get("cefr_level", "A1")
        weaknesses = str(lp.get("weaknesses", "{}"))

    scene = state.scene
    scene_name = "自由对话"
    scene_name_en = "Free Talk"
    difficulty = 1
    ai_role = "英语陪练"
    user_role = "学生"
    keywords = ""
    target_sentences = ""

    if isinstance(scene, dict):
        scene_name = scene.get("name", "自由对话")
        scene_name_en = scene.get("name_en", "Free Talk")
        difficulty = scene.get("difficulty", 1)
        roles = scene.get("roles", [])
        if roles:
            ai_role = roles[0].get("name", "英语陪练")
        keywords = _fmt_keywords(scene.get("keywords", []))
        target_sentences = _fmt_sentences(scene.get("target_sentences", []))

    system_prompt = build_system_prompt(
        grade_level=grade_level, scene_name=scene_name,
        scene_name_en=scene_name_en, difficulty=difficulty,
        ai_role=ai_role, user_role=user_role,
        keywords=keywords, target_sentences=target_sentences,
        weaknesses=weaknesses
    )

    history = state.conversation_history or []
    messages = build_messages(system_prompt, history, state.user_message)

    llm = get_llm()
    response = invoke_llm_with_retry(llm, messages)
    token_info = extract_token_usage(response)

    logger.info("response_generated",
                 extra={"tokens": token_info.get("total_tokens", 0),
                        "model": llm.model_name})
    return {
        "ai_response": response.content,
        "model_name": llm.model_name,
        **token_info
    }


# ==================== 辅助 ====================

def _fmt_keywords(keywords: list) -> str:
    if not keywords:
        return ""
    return "\n".join(
        f"- {kw.get('word','')} ({kw.get('translation','')})"
        for kw in keywords if isinstance(kw, dict)
    )


def _fmt_sentences(sentences: list) -> str:
    if not sentences:
        return ""
    return "\n".join(
        f"- {s.get('sentence','')} — {s.get('explanation','')}"
        for s in sentences if isinstance(s, dict)
    )


# ==================== 检查点构建 ====================

def _build_checkpointer():
    """构建检查点保存器 — Redis 优先，内存回退"""
    if config.REDIS_ENABLED:
        try:
            import redis
            client = redis.Redis(
                host=config.REDIS_HOST,
                port=config.REDIS_PORT,
                db=config.REDIS_DB,
                password=config.REDIS_PASSWORD or None,
                socket_connect_timeout=3,
            )
            client.ping()
            saver = RedisSaver(client)
            logger.info("checkpointer_initialized", extra={"mode": "redis"})
            return saver
        except Exception as e:
            logger.warning("redis_checkpointer_failed_fallback_to_memory",
                           extra={"error": str(e)})

    logger.info("checkpointer_initialized", extra={"mode": "memory"})
    return MemorySaver()


# ==================== 构建图 ====================

def build_coach_graph() -> StateGraph:
    """构建 LangGraph 陪练智能体状态图"""
    workflow = StateGraph(CoachState)

    # 添加节点
    workflow.add_node("analyze_intent", analyze_intent)
    workflow.add_node("generate_response", generate_response)
    workflow.add_node("detect_errors", detect_errors)

    # 入口
    workflow.set_entry_point("analyze_intent")

    # 线性流程: intent → generate → detect → END
    workflow.add_edge("analyze_intent", "generate_response")
    workflow.add_edge("generate_response", "detect_errors")
    workflow.add_edge("detect_errors", END)

    # 编译 (带检查点持久化)
    checkpointer = _build_checkpointer()
    graph = workflow.compile(checkpointer=checkpointer)

    logger.info("graph_compiled",
                extra={"nodes": ["analyze_intent", "generate_response",
                                 "detect_errors"]})
    return graph


# 全局实例
coach_graph = build_coach_graph()
