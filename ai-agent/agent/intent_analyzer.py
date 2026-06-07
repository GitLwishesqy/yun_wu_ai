"""
意图分析器 — LLM 驱动
"""
import json
from langchain_core.messages import HumanMessage, SystemMessage
from agent.llm_service import get_llm, invoke_llm_with_retry
from utils.logger import logger
from utils.state_helper import _get

INTENT_SYSTEM_PROMPT = """你是一个对话意图分类器。分析用户输入，返回一个 JSON，包含以下字段:
- intent: "chat" (正常对话), "end_session" (想结束), "help" (求助/不懂), "feedback" (请求反馈)
- confidence: 0.0-1.0 的置信度

用户英语水平: {cefr_level}

只返回 JSON，不要任何其他文字。
示例: {{"intent": "chat", "confidence": 0.95}}
"""

def analyze_intent(state) -> dict:
    user_message = _get(state, 'user_message', '').strip()
    if not user_message:
        return {"intent": "chat"}
    lp = _get(state, 'learner_profile', {})
    cefr_level = (lp or {}).get('cefr_level', 'A1') if isinstance(lp, dict) else 'A1'
    try:
        llm = get_llm(temperature=0.0, max_tokens=64)
        messages = [SystemMessage(content=INTENT_SYSTEM_PROMPT.format(cefr_level=cefr_level)),
                     HumanMessage(content=f'用户输入: "{user_message}"')]
        response = invoke_llm_with_retry(llm, messages)
        content = response.content.strip()
        if "```" in content: content = content.split("```")[1].split("```")[0].strip()
        result = json.loads(content)
        return {"intent": result.get("intent", "chat")}
    except Exception:
        return {"intent": _fallback_intent(user_message)}

def _fallback_intent(msg: str) -> str:
    msg = msg.lower().strip()
    if any(k in msg for k in ["goodbye","bye","see you","拜拜","结束"]): return "end_session"
    if any(k in msg for k in ["help","什么意思","怎么读","i don't understand"]): return "help"
    return "chat"
