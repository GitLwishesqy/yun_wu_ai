"""
发音评测引擎 — LangGraph 节点 (音素级发音评估)
"""
import json
from typing import List
from langchain_core.messages import HumanMessage, SystemMessage
from models.schemas import CorrectionItem
from agent.llm_service import get_llm, invoke_llm_with_retry
from utils.logger import logger


PRONUNCIATION_PROMPT = """你是一个英语发音评测专家。分析用户输入文本中可能的发音错误。

用户英语水平: {cefr_level}

常见的发音错误类型 (中国人学英语):
- TH_SOUND: th 发成 s/z/f/d (如 "think" → "sink")
- V_W_CONFUSION: v/w 混淆 (如 "very" → "wery")
- R_L_CONFUSION: r/l 混淆 (如 "rice" → "lice")
- LONG_SHORT_VOWEL: 长短元音混淆 (如 "ship" → "sheep")
- FINAL_CONSONANT: 末尾辅音被添加元音 (如 "book" → "book-uh")
- STRESS_PATTERN: 重音位置错误
- TONE_INTONATION: 语调问题
- NASAL_SOUND: 鼻音混淆 (n/ng)

用户输入: "{user_message}"

请返回 JSON 数组，每个可能发音错误的单词包含:
- error_type: "PRONUNCIATION"
- error_subtype: 具体子类 (TH_SOUND, V_W_CONFUSION 等)
- severity: LOW / MEDIUM / HIGH
- original_text: 单词原文
- corrected_text: 正确发音描述
- explanation: 用中文解释发音要领 (舌头位置、嘴型等)
- improvement_tip: 具体练习建议

规则:
1. 最多返回 5 个可能发音错误的单词
2. 只分析实词 (名词/动词/形容词)，跳过介词/冠词
3. A1 水平不纠正细微发音差异，只纠正严重影响理解的错误
4. 只返回 JSON 数组，不要任何其他文字
"""


def evaluate_pronunciation(state: dict) -> dict:
    """
    发音评测 — LangGraph 节点
    与 detect_errors 互补，专注发音维度
    """
    from utils.state_helper import _get
    user_message = _get(state, "user_message", "")
    lp = _get(state, "learner_profile", {})
    cefr_level = (lp or {}).get("cefr_level", "A1") if isinstance(lp, dict) else "A1"

    # 太短的输入跳过
    words = user_message.strip().split()
    if len(words) < 2:
        return {"pronunciation_issues": []}

    try:
        llm = get_llm(temperature=0.1, max_tokens=512)
        prompt = PRONUNCIATION_PROMPT.format(
            cefr_level=cefr_level,
            user_message=user_message
        )

        messages = [
            SystemMessage(content="你是专业的英语发音教练。你必须只返回一个有效的 JSON 数组，不能有任何其他内容。没有发现发音问题返回 []。"),
            HumanMessage(content=prompt)
        ]

        response = invoke_llm_with_retry(llm, messages)
        issues = _parse_pronunciation_response(response.content)

        # 合并到 corrections 中 (与语法纠错合并)
        existing = state.get("corrections", [])
        all_corrections = list(existing) if existing else []
        all_corrections.extend(issues)

        logger.info("pronunciation_evaluated",
                     extra={"issues_found": len(issues)})
        return {"corrections": all_corrections}
    except Exception as e:
        logger.warning("pronunciation_eval_failed", extra={"error": str(e)})
        return {}


def _parse_pronunciation_response(content: str) -> List[dict]:
    """解析发音评测 JSON 响应"""
    try:
        content = content.strip()
        if "```json" in content:
            content = content.split("```json")[1].split("```")[0].strip()
        elif "```" in content:
            content = content.split("```")[1].split("```")[0].strip()

        data = json.loads(content)
        if isinstance(data, list):
            return data
        if isinstance(data, dict) and "issues" in data:
            return data["issues"]
    except Exception:
        pass
    return []
