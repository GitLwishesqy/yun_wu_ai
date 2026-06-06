"""
LLM 服务 — 统一抽象，支持 OpenAI / DeepSeek / Qwen 等多模型
"""
import config
from langchain_openai import ChatOpenAI
from langchain_core.messages import SystemMessage, HumanMessage, AIMessage
from typing import List, Optional
import logging

logger = logging.getLogger(__name__)


def create_llm(
    temperature: Optional[float] = None,
    max_tokens: Optional[int] = None
) -> ChatOpenAI:
    """
    创建 LLM 实例 — 通过 base_url 支持多模型
    """
    kwargs = {
        "model": config.LLM_MODEL,
        "api_key": config.LLM_API_KEY,
        "temperature": temperature or config.LLM_TEMPERATURE,
        "max_tokens": max_tokens or config.LLM_MAX_TOKENS,
        "timeout": config.LLM_TIMEOUT,
    }

    # 国内模型通过自定义 base_url 接入
    if config.LLM_PROVIDER == "deepseek":
        kwargs["base_url"] = config.LLM_BASE_URL or "https://api.deepseek.com/v1"
    elif config.LLM_PROVIDER == "qwen":
        kwargs["base_url"] = config.LLM_BASE_URL or "https://dashscope.aliyuncs.com/compatible-mode/v1"
    elif config.LLM_BASE_URL:
        kwargs["base_url"] = config.LLM_BASE_URL

    logger.info(f"创建 LLM: provider={config.LLM_PROVIDER}, model={config.LLM_MODEL}")
    return ChatOpenAI(**kwargs)


def build_messages(
    system_prompt: str,
    conversation_history: List[dict],
    latest_user_message: str
) -> list:
    """构建 LLM 消息列表"""
    messages = [SystemMessage(content=system_prompt)]

    for msg in conversation_history:
        if msg.get("role") == "USER":
            messages.append(HumanMessage(content=msg.get("content", "")))
        elif msg.get("role") == "AI":
            messages.append(AIMessage(content=msg.get("content", "")))

    messages.append(HumanMessage(content=latest_user_message))
    return messages


def extract_token_usage(response) -> dict:
    """从 LangChain 响应中提取 Token 用量"""
    try:
        usage = response.response_metadata.get("token_usage", {})
        return {
            "prompt_tokens": usage.get("prompt_tokens", 0),
            "completion_tokens": usage.get("completion_tokens", 0),
            "total_tokens": usage.get("total_tokens", 0),
        }
    except Exception:
        return {"prompt_tokens": 0, "completion_tokens": 0, "total_tokens": 0}
