"""
数据脱敏工具 — 历史记录清洗、敏感信息过滤
"""
import re
import json
from typing import List, Dict


# 敏感信息正则 (电话号码、邮箱、身份证、API Key 等)
PHONE_PATTERN = re.compile(r'\b1[3-9]\d{9}\b')
EMAIL_PATTERN = re.compile(r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b')
ID_CARD_PATTERN = re.compile(r'\b\d{17}[\dXx]\b')
API_KEY_PATTERN = re.compile(r'\b(sk-[A-Za-z0-9]{20,})\b')


def sanitize_text(text: str) -> str:
    """对单条文本进行脱敏处理"""
    if not text:
        return text
    text = PHONE_PATTERN.sub('[PHONE]', text)
    text = EMAIL_PATTERN.sub('[EMAIL]', text)
    text = ID_CARD_PATTERN.sub('[ID_CARD]', text)
    text = API_KEY_PATTERN.sub('[API_KEY]', text)
    return text


def sanitize_history(history: List[Dict[str, str]]) -> List[Dict[str, str]]:
    """
    脱敏对话历史
    - 过滤空消息
    - 脱敏敏感信息
    - 截断超长消息（防止历史膨胀导致 Token 超量）
    """
    if not history:
        return []

    cleaned = []
    for msg in history:
        if not isinstance(msg, dict):
            continue
        role = msg.get("role", "")
        content = msg.get("content", "")

        # 跳过空消息
        if not content or not content.strip():
            continue

        # 脱敏
        content = sanitize_text(content)

        # 截断超长单条消息 (>2000 字符)
        if len(content) > 2000:
            content = content[:1997] + "..."

        cleaned.append({"role": role, "content": content})

    # 限制总条数 (最近 40 条)
    if len(cleaned) > 40:
        cleaned = cleaned[-40:]

    return cleaned


def sanitize_user_message(message: str) -> str:
    """脱敏用户输入 + 截断"""
    if not message:
        return ""
    message = sanitize_text(message)
    if len(message) > 2000:
        message = message[:1997] + "..."
    return message
