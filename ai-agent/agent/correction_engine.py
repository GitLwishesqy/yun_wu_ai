"""
纠错引擎 — 作为 LangGraph 的独立节点 (容错增强版)
"""
import json
from typing import List
from langchain_core.messages import HumanMessage, SystemMessage

from models.schemas import CorrectionItem
from agent.llm_service import get_llm, invoke_llm_with_retry
from agent.prompt_templates import build_correction_prompt
from utils.logger import logger


def detect_errors(state: dict) -> dict:
    """
    检测用户输入中的错误 (带完整错误处理 + 降级)
    """
    user_message = state.get("user_message", "")
    learner_profile = state.get("learner_profile", {})
    cefr_level = _extract_cefr(learner_profile)

    # 太短的消息跳过纠错
    if len(user_message.strip().split()) < 2:
        return {"corrections": []}

    try:
        llm = get_llm(temperature=0.2, max_tokens=512)
        prompt = build_correction_prompt(user_message, cefr_level)

        messages = [
            SystemMessage(content="你是专业的英语纠错专家。只返回 JSON 数组，不要任何其他文字。"),
            HumanMessage(content=prompt)
        ]

        # 使用带重试的 LLM 调用
        response = invoke_llm_with_retry(llm, messages)

        corrections = _parse_correction_response(response.content)
        logger.info("correction_completed",
                     extra={"error_count": len(corrections)})
        return {"corrections": corrections}

    except Exception as e:
        # 静默失败 — 纠错不可用不影响主对话
        logger.error("correction_failed_gracefully",
                     extra={"error": str(e), "user_message_len": len(user_message)})
        return {"corrections": []}   # 降级: 返回空纠错列表


def _extract_cefr(learner_profile) -> str:
    if isinstance(learner_profile, dict):
        return learner_profile.get("cefr_level", "A1")
    elif hasattr(learner_profile, "cefr_level"):
        return learner_profile.cefr_level
    return "A1"


def _parse_correction_response(content: str) -> List[CorrectionItem]:
    """解析 LLM 返回的纠错 JSON (多策略解析)"""
    content = content.strip()

    # 策略 1: 提取 markdown 代码块中的 JSON
    if "```json" in content:
        try:
            json_str = content.split("```json")[1].split("```")[0].strip()
            data = json.loads(json_str)
            if isinstance(data, list):
                return [CorrectionItem(**item) for item in data]
        except Exception:
            pass

    # 策略 2: 提取任意 markdown 代码块
    if "```" in content:
        try:
            json_str = content.split("```")[1].split("```")[0].strip()
            data = json.loads(json_str)
            if isinstance(data, list):
                return [CorrectionItem(**item) for item in data]
        except Exception:
            pass

    # 策略 3: 直接解析整段内容
    try:
        data = json.loads(content)
        if isinstance(data, list):
            return [CorrectionItem(**item) for item in data]
        if isinstance(data, dict) and "corrections" in data:
            return [CorrectionItem(**item) for item in data["corrections"]]
    except Exception:
        pass

    logger.warning("correction_parse_failed",
                   extra={"content_preview": content[:200]})
    return []
