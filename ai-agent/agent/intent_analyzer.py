"""
意图分析器 — LLM 驱动 (替代简单关键词匹配)
"""
from langchain_core.messages import HumanMessage, SystemMessage
from agent.llm_service import get_llm, invoke_llm_with_retry
from utils.logger import logger


INTENT_SYSTEM_PROMPT = """你是一个对话意图分类器。分析用户输入，返回一个 JSON，包含以下字段:
- intent: "chat" (正常对话), "end_session" (想结束), "help" (求助/不懂), "feedback" (请求反馈)
- confidence: 0.0-1.0 的置信度

用户英语水平: {cefr_level}

只返回 JSON，不要任何其他文字。
示例: {{"intent": "chat", "confidence": 0.95}}
"""


def analyze_intent(state: dict) -> dict:
    """
    LLM 驱动的意图分析 — LangGraph 节点函数
    解决关键词匹配无法处理复杂语义的问题
    """
    user_message = state.get("user_message", "").strip()
    if not user_message:
        return {"intent": "chat"}

    learner_profile = state.get("learner_profile", {})
    cefr_level = "A1"
    if isinstance(learner_profile, dict):
        cefr_level = learner_profile.get("cefr_level", "A1")

    try:
        llm = get_llm(temperature=0.0, max_tokens=64)
        prompt = INTENT_SYSTEM_PROMPT.format(cefr_level=cefr_level)

        messages = [
            SystemMessage(content=prompt),
            HumanMessage(content=f'用户输入: "{user_message}"')
        ]
        response = invoke_llm_with_retry(llm, messages)

        # 解析 JSON
        import json
        content = response.content.strip()
        if "```" in content:
            content = content.split("```")[1].split("```")[0].strip()
        result = json.loads(content)
        intent = result.get("intent", "chat")
        confidence = result.get("confidence", 0.5)

        logger.info("intent_analyzed",
                     extra={"intent": intent, "confidence": confidence})
        return {"intent": intent}
    except Exception as e:
        # LLM 意图分析失败 → 降级为容错规则
        logger.warning("intent_llm_failed_fallback_to_rules",
                       extra={"error": str(e)})
        return {"intent": _fallback_intent(user_message)}


def _fallback_intent(user_message: str) -> str:
    """容错规则 — LLM 不可用时的降级方案"""
    msg = user_message.lower().strip()
    end_kw = ["goodbye", "bye", "see you", "拜拜", "结束", "i'm done"]
    help_kw = ["help", "什么意思", "怎么读", "怎么说", "i don't understand"]
    if any(kw in msg for kw in end_kw):
        return "end_session"
    if any(kw in msg for kw in help_kw):
        return "help"
    return "chat"
