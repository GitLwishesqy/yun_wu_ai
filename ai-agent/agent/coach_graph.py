"""
陪练智能体 — LangGraph 状态图

图结构:
    START
      │
      ▼
  [analyze_intent]  ─── 分析用户意图 (正常对话 / 结束会话 / 求助)
      │
      ▼
  [generate_response] ─── 调用 LLM 生成英语陪练回复
      │
      ▼
  [detect_errors]  ─── 纠错引擎 (检测语法/词汇/发音错误)
      │
      ▼
     END

LangGraph 优势:
1. 状态持久化 — 支持断点恢复和检查点
2. 并行节点 — 未来可并行调用纠错和评估
3. 条件路由 — 根据意图动态选择路径
4. 流式输出 — 支持 token 级别的流式响应
"""
import logging
from typing import Literal
from langgraph.graph import StateGraph, END
from langgraph.checkpoint.memory import MemorySaver

from models.schemas import CoachState, ChatResponse
from agent.llm_service import create_llm, build_messages, extract_token_usage
from agent.prompt_templates import build_system_prompt
from agent.correction_engine import detect_errors

logger = logging.getLogger(__name__)


# ==================== 节点函数 ====================

def analyze_intent(state: CoachState) -> dict:
    """
    节点 1: 分析用户意图
    判断用户是想正常对话、结束会话、还是需要帮助
    """
    user_message = state.user_message.lower().strip()

    # 简单规则判断意图
    intent = "chat"
    end_keywords = ["goodbye", "bye", "thank you", "see you", "结束", "拜拜",
                     "that's all", "i'm done"]
    help_keywords = ["help", "what does", "什么意思", "怎么读", "怎么说",
                      "我不懂", "i don't understand", "how to say"]

    if any(kw in user_message for kw in end_keywords):
        intent = "end_session"
    elif any(kw in user_message for kw in help_keywords):
        intent = "help"

    logger.info(f"[Node:analyze_intent] intent={intent}, msg={user_message[:50]}...")
    return {"intent": intent}


def generate_response(state: CoachState) -> dict:
    """
    节点 2: 生成陪练回复
    构建 System Prompt → 调用 LLM → 返回回复
    """
    # 提取学习者信息
    lp = state.learner_profile or {}
    grade_level = "ELEMENTARY"
    cefr_level = "A1"
    weaknesses = "{}"

    if isinstance(lp, dict):
        grade_level = lp.get("grade_level", "ELEMENTARY")
        cefr_level = lp.get("cefr_level", "A1")
        weaknesses = str(lp.get("weaknesses", "{}"))
    elif hasattr(lp, "cefr_level"):
        grade_level = getattr(lp, "grade_level", "ELEMENTARY")
        cefr_level = lp.cefr_level
        weaknesses = str(getattr(lp, "weaknesses", "{}"))

    # 提取场景信息
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
            user_role = "学生"
        keywords = _format_keywords(scene.get("keywords", []))
        target_sentences = _format_sentences(scene.get("target_sentences", []))
    elif hasattr(scene, "name"):
        scene_name = scene.name
        scene_name_en = getattr(scene, "name_en", "Free Talk")
        difficulty = scene.difficulty

    # 构建 System Prompt
    system_prompt = build_system_prompt(
        grade_level=grade_level,
        scene_name=scene_name,
        scene_name_en=scene_name_en,
        difficulty=difficulty,
        ai_role=ai_role,
        user_role=user_role,
        keywords=keywords,
        target_sentences=target_sentences,
        weaknesses=weaknesses
    )

    # 构建消息
    history = state.conversation_history or []
    messages = build_messages(system_prompt, history, state.user_message)

    # 调用 LLM
    llm = create_llm()
    response = llm.invoke(messages)

    # 提取 Token 用量
    token_info = extract_token_usage(response)

    logger.info(f"[Node:generate_response] tokens={token_info.get('total_tokens', 0)}")
    return {
        "ai_response": response.content,
        "model_name": llm.model_name,
        **token_info
    }


# ==================== 辅助函数 ====================

def _format_keywords(keywords: list) -> str:
    if not keywords:
        return ""
    lines = []
    for kw in keywords:
        if isinstance(kw, dict):
            lines.append(f"- {kw.get('word', '')} ({kw.get('translation', '')})")
    return "\n".join(lines)


def _format_sentences(sentences: list) -> str:
    if not sentences:
        return ""
    lines = []
    for s in sentences:
        if isinstance(s, dict):
            lines.append(f"- {s.get('sentence', '')} — {s.get('explanation', '')}")
    return "\n".join(lines)


# ==================== 构建图 ====================

def build_coach_graph() -> StateGraph:
    """
    构建 LangGraph 陪练智能体状态图
    """
    # 创建图
    workflow = StateGraph(CoachState)

    # 添加节点
    workflow.add_node("analyze_intent", analyze_intent)
    workflow.add_node("generate_response", generate_response)
    workflow.add_node("detect_errors", detect_errors)

    # 设置入口
    workflow.set_entry_point("analyze_intent")

    # 连接边 (线性流程)
    workflow.add_edge("analyze_intent", "generate_response")
    workflow.add_edge("generate_response", "detect_errors")
    workflow.add_edge("detect_errors", END)

    # 编译 (带内存检查点，支持状态持久化)
    memory = MemorySaver()
    graph = workflow.compile(checkpointer=memory)

    logger.info("[LangGraph] 陪练智能体图构建完成")
    return graph


# ==================== 全局实例 ====================

coach_graph = build_coach_graph()
