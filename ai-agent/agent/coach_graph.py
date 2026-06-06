"""
陪练智能体 — LangGraph 状态图 (安全加固版)
"""
import config
from langgraph.graph import StateGraph, END
from langgraph.checkpoint.memory import MemorySaver
from langgraph.checkpoint.redis import RedisSaver

from models.schemas import CoachState, ChatResponse
from agent.llm_service import (
    get_llm, build_messages, extract_token_usage,
    invoke_llm_with_retry, enforce_response_length
)
from agent.prompt_templates import build_system_prompt
from agent.correction_engine import detect_errors
from agent.intent_analyzer import analyze_intent
from agent.pronunciation_evaluator import evaluate_pronunciation
from utils.logger import logger
from utils.sanitizer import sanitize_history, sanitize_user_message


# ==================== 辅助: 安全提取 ====================

def _safe_get(obj, key, default=None):
    """安全提取 — 统一处理 dict 和 object 两种类型"""
    if obj is None:
        return default
    if isinstance(obj, dict):
        return obj.get(key, default)
    return getattr(obj, key, default) if hasattr(obj, key) else default


def _extract_scene(scene) -> tuple:
    """统一提取场景信息 (消除 dict/object 双重分支冗余)"""
    return (
        _safe_get(scene, "name", "自由对话"),
        _safe_get(scene, "name_en", "Free Talk"),
        int(_safe_get(scene, "difficulty", 1) or 1),
        (_safe_get(scene, "roles", []) or [{}])[0].get("name", "英语陪练")
            if isinstance(_safe_get(scene, "roles", []), list)
            and len(_safe_get(scene, "roles", [])) > 0
            else "英语陪练",
        _safe_get(scene, "keywords", []),
        _safe_get(scene, "target_sentences", []),
    )


# ==================== 节点: 生成回复 ====================

def generate_response(state: CoachState) -> dict:
    """构建 System Prompt → 调用 LLM → 返回回复"""
    lp = state.learner_profile or {}
    grade_level = _safe_get(lp, "grade_level", "ELEMENTARY")
    cefr_level = _safe_get(lp, "cefr_level", "A1")
    weaknesses = str(_safe_get(lp, "weaknesses", "{}"))

    (scene_name, scene_name_en, difficulty,
     ai_role, keywords, target_sentences) = _extract_scene(state.scene)

    system_prompt = build_system_prompt(
        grade_level=grade_level, scene_name=scene_name,
        scene_name_en=scene_name_en, difficulty=difficulty,
        ai_role=ai_role, user_role="学生",
        keywords=_fmt_keywords(keywords),
        target_sentences=_fmt_sentences(target_sentences),
        weaknesses=weaknesses
    )

    # 脱敏历史
    safe_history = sanitize_history(state.conversation_history or [])
    safe_message = sanitize_user_message(state.user_message)

    messages = build_messages(system_prompt, safe_history, safe_message)

    # 使用对话专用的 max_tokens 控制
    llm = get_llm(max_tokens=256)
    response = invoke_llm_with_retry(llm, messages)

    # 兜底截断
    ai_text = enforce_response_length(response.content, "chat")
    token_info = extract_token_usage(response)

    logger.info("response_generated",
                 extra={"tokens": token_info.get("total_tokens", 0),
                        "token_stats_ok": token_info.get("token_stats_available", False)})
    return {
        "ai_response": ai_text,
        "model_name": llm.model_name,
        **token_info
    }


def _fmt_keywords(keywords) -> str:
    if not keywords:
        return ""
    lines = []
    for kw in (keywords if isinstance(keywords, list) else []):
        if isinstance(kw, dict):
            lines.append(f"- {kw.get('word','')} ({kw.get('translation','')})")
    return "\n".join(lines)


def _fmt_sentences(sentences) -> str:
    if not sentences:
        return ""
    lines = []
    for s in (sentences if isinstance(sentences, list) else []):
        if isinstance(s, dict):
            lines.append(f"- {s.get('sentence','')} — {s.get('explanation','')}")
    return "\n".join(lines)


# ==================== 检查点 ====================

def _build_checkpointer():
    if config.REDIS_ENABLED:
        try:
            import redis
            client = redis.Redis(
                host=config.REDIS_HOST, port=config.REDIS_PORT,
                db=config.REDIS_DB, password=config.REDIS_PASSWORD or None,
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
    workflow = StateGraph(CoachState)
    workflow.add_node("analyze_intent", analyze_intent)
    workflow.add_node("generate_response", generate_response)
    workflow.add_node("detect_errors", detect_errors)
    workflow.add_node("evaluate_pronunciation", evaluate_pronunciation)
    workflow.set_entry_point("analyze_intent")
    workflow.add_edge("analyze_intent", "generate_response")
    workflow.add_edge("generate_response", "detect_errors")
    workflow.add_edge("detect_errors", "evaluate_pronunciation")
    workflow.add_edge("evaluate_pronunciation", END)
    checkpointer = _build_checkpointer()
    graph = workflow.compile(checkpointer=checkpointer)
    logger.info("graph_compiled")
    return graph


coach_graph = build_coach_graph()
