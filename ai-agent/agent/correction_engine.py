"""
纠错引擎 — 作为 LangGraph 的独立节点
负责分析用户输入并生成结构化纠错信息
"""
import json
import logging
from typing import List
from langchain_core.messages import HumanMessage, SystemMessage

from models.schemas import CorrectionItem
from agent.llm_service import create_llm, extract_token_usage
from agent.prompt_templates import build_correction_prompt

logger = logging.getLogger(__name__)


def detect_errors(state: dict) -> dict:
    """
    检测用户输入中的错误 — LangGraph 节点函数

    输入: state (CoachState 的字典形式)
    输出: 更新的 state (含 corrections)
    """
    user_message = state.get("user_message", "")
    learner_profile = state.get("learner_profile", {})
    cefr_level = "A1"

    if isinstance(learner_profile, dict):
        cefr_level = learner_profile.get("cefr_level", "A1")
    elif hasattr(learner_profile, "cefr_level"):
        cefr_level = learner_profile.cefr_level

    # 太短的消息不纠错
    if len(user_message.strip().split()) < 2:
        return {"corrections": []}

    try:
        llm = create_llm(temperature=0.2, max_tokens=512)
        prompt = build_correction_prompt(user_message, cefr_level)

        messages = [
            SystemMessage(content="你是专业的英语纠错专家。只返回 JSON 数组，不要任何其他文字。"),
            HumanMessage(content=prompt)
        ]

        response = llm.invoke(messages)

        # 解析 JSON 响应
        corrections = _parse_correction_response(response.content)
        logger.info(f"纠错引擎检测到 {len(corrections)} 个错误")
        return {"corrections": corrections}

    except Exception as e:
        logger.error(f"纠错引擎异常: {e}")
        return {"corrections": []}


def _parse_correction_response(content: str) -> List[CorrectionItem]:
    """解析 LLM 返回的纠错 JSON"""
    try:
        # 提取 JSON 数组
        content = content.strip()
        if "```json" in content:
            content = content.split("```json")[1].split("```")[0]
        elif "```" in content:
            content = content.split("```")[1].split("```")[0]

        data = json.loads(content)
        if isinstance(data, list):
            return [CorrectionItem(**item) for item in data]
    except (json.JSONDecodeError, Exception) as e:
        logger.warning(f"纠错 JSON 解析失败: {e}, raw={content[:200]}")
    return []
