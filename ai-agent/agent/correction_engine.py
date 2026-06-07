"""
纠错引擎 — LangGraph 节点 (Prompt 加强版)
"""
import json
import re
from typing import List
from langchain_core.messages import HumanMessage, SystemMessage

from models.schemas import CorrectionItem
from agent.llm_service import get_llm, invoke_llm_with_retry, enforce_response_length
from agent.prompt_templates import build_correction_prompt
from utils.logger import logger

# 强约束 System Prompt — 防止 LLM 跑偏输出非 JSON
CORRECTION_SYSTEM_PROMPT = """你是专业的英语纠错专家。你必须严格遵守以下规则:

1. **输出格式**: 你必须只返回一个有效的 JSON 数组，不能有任何其他内容
2. **不要**: 不要加解释文字、markdown 标记、代码块标记
3. **空结果**: 如果没有发现错误，返回: []
4. **JSON 结构**: 每个错误对象包含以下字段:
   - error_type: "GRAMMAR" | "PRONUNCIATION" | "VOCABULARY" | "LOGIC" | "COLLOCATION"
   - error_subtype: 具体子类，如 "TENSE", "PREPOSITION"
   - severity: "LOW" | "MEDIUM" | "HIGH"
   - original_text: 用户原始表达
   - error_span: 具体出错片段
   - corrected_text: 纠正后表达
   - explanation: 简短中文解释 (不超过30字)
   - improvement_tip: 改进建议
   - related_rule: 相关语法规则
   - correction_strategy: "IMMEDIATE" | "DELAYED" | "SKIPPED"

示例输出: [{"error_type":"GRAMMAR","error_subtype":"TENSE","severity":"MEDIUM","original_text":"I go yesterday","error_span":"go","corrected_text":"I went yesterday","explanation":"yesterday表示过去，动词用过去式went","improvement_tip":"用过去式来描述过去的动作","related_rule":"一般过去时","correction_strategy":"IMMEDIATE"}]

现在，严格按照以上要求，分析下面的学生输入。只返回 JSON 数组，不返回任何其他内容。"""


from utils.state_helper import _get

def detect_errors(state) -> dict:
    user_message = _get(state, "user_message", "")
    lp = _get(state, "learner_profile", {})
    cefr_level = (lp or {}).get("cefr_level", "A1") if isinstance(lp, dict) else "A1"

    if len(user_message.strip().split()) < 2:
        return {"corrections": []}

    try:
        llm = get_llm(temperature=0.2, max_tokens=384)
        prompt = build_correction_prompt(user_message, cefr_level)

        messages = [
            SystemMessage(content=CORRECTION_SYSTEM_PROMPT),
            HumanMessage(content=prompt)
        ]

        response = invoke_llm_with_retry(llm, messages)
        content = enforce_response_length(response.content, "correction")

        corrections = _parse_correction_response(content)
        logger.info("correction_completed",
                     extra={"error_count": len(corrections)})
        return {"corrections": corrections}

    except Exception as e:
        logger.error("correction_failed_gracefully",
                     extra={"error": str(e)})
        return {"corrections": []}


def _parse_correction_response(content: str) -> List[CorrectionItem]:
    """多策略 JSON 解析 — 层层降级"""
    content = content.strip()

    # 策略 1: 尝试直接解析
    result = _try_parse_json(content)
    if result is not None:
        return result

    # 策略 2: 提取 JSON 数组 (正则兜底)
    json_match = re.search(r'\[.*\]', content, re.DOTALL)
    if json_match:
        result = _try_parse_json(json_match.group())
        if result is not None:
            return result

    # 策略 3: 尝试用文本前后缀去除
    for marker in ["```json", "```"]:
        if marker in content:
            parts = content.split(marker)
            if len(parts) >= 3:
                result = _try_parse_json(parts[1].strip())
                if result is not None:
                    return result

    # 策略 4: LLM 完全跑偏 — 记录并返回空
    logger.warning("correction_parse_all_strategies_failed",
                   extra={"content_preview": content[:300],
                          "content_len": len(content)})
    return []


def _try_parse_json(json_str: str) -> List[CorrectionItem] | None:
    """尝试解析 JSON，返回 None 表示失败"""
    try:
        data = json.loads(json_str)
        if isinstance(data, list):
            items = []
            for item in data:
                if isinstance(item, dict):
                    items.append(CorrectionItem(**item))
            return items
        if isinstance(data, dict) and isinstance(data.get("corrections"), list):
            return [CorrectionItem(**item) for item in data["corrections"]]
    except (json.JSONDecodeError, TypeError, ValueError):
        pass
    return None
