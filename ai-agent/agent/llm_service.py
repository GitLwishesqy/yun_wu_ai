"""
LLM 服务 — 单例模式 + tenacity 重试
"""
import config
from langchain_openai import ChatOpenAI
from langchain_core.messages import SystemMessage, HumanMessage, AIMessage
from tenacity import (
    retry, stop_after_attempt, wait_exponential,
    retry_if_exception_type, before_sleep_log
)
from typing import List, Optional
from utils.logger import logger

# ==================== LLM 单例 (性能优化) ====================

_llm_instance: Optional[ChatOpenAI] = None
_llm_instance_lock = __import__('threading').Lock()


def get_llm(
    temperature: Optional[float] = None,
    max_tokens: Optional[int] = None,
    force_new: bool = False
) -> ChatOpenAI:
    """
    获取 LLM 单例 — 避免每次请求重复创建实例
    """
    global _llm_instance

    if _llm_instance is not None and not force_new:
        # 如果需要不同的 temperature，克隆一份 (但共享底层连接池)
        if temperature is not None and temperature != config.LLM_TEMPERATURE:
            return _build_llm(temperature, max_tokens)
        if max_tokens is not None and max_tokens != config.LLM_MAX_TOKENS:
            return _build_llm(temperature, max_tokens)
        return _llm_instance

    with _llm_instance_lock:
        if _llm_instance is None or force_new:
            _llm_instance = _build_llm(
                temperature or config.LLM_TEMPERATURE,
                max_tokens or config.LLM_MAX_TOKENS
            )
    return _llm_instance


def _build_llm(temperature: float, max_tokens: int) -> ChatOpenAI:
    kwargs = {
        "model": config.LLM_MODEL,
        "api_key": config.LLM_API_KEY,
        "temperature": temperature,
        "max_tokens": max_tokens,
        "timeout": config.LLM_TIMEOUT,
        "request_timeout": config.LLM_TIMEOUT,
    }
    # TODO:需要接入真实的路径以及key，目前先使用默认的openai路径
    if config.LLM_PROVIDER == "deepseek":
        kwargs["base_url"] = config.LLM_BASE_URL or "https://api.deepseek.com/v1"
    elif config.LLM_PROVIDER == "qwen":
        kwargs["base_url"] = config.LLM_BASE_URL or "https://dashscope.aliyuncs.com/compatible-mode/v1"
    elif config.LLM_BASE_URL:
        kwargs["base_url"] = config.LLM_BASE_URL

    logger.info("llm_instance_created",
                extra={"provider": config.LLM_PROVIDER, "model": config.LLM_MODEL})
    return ChatOpenAI(**kwargs)


# ==================== 带重试的调用 (可靠性) ====================

@retry(
    stop=stop_after_attempt(config.LLM_RETRY_MAX_ATTEMPTS),
    wait=wait_exponential(
        multiplier=config.LLM_RETRY_BACKOFF,
        min=config.LLM_RETRY_MIN_WAIT,
        max=config.LLM_RETRY_MAX_WAIT,
    ),
    retry=retry_if_exception_type((
        ConnectionError, TimeoutError,
        __import__('requests').exceptions.Timeout,
        __import__('requests').exceptions.ConnectionError,
        __import__('urllib3.exceptions').TimeoutError,
        __import__('urllib3.exceptions').ProtocolError,
    )),
    before_sleep=before_sleep_log(logger, __import__('logging').WARNING),
    reraise=True,
)
def invoke_llm_with_retry(llm: ChatOpenAI, messages: list):
    """带指数退避重试的 LLM 调用"""
    logger.debug("llm_invoke", extra={"message_count": len(messages)})
    return llm.invoke(messages)


# ==================== 消息构建 ====================

def build_messages(
    system_prompt: str,
    conversation_history: List[dict],
    latest_user_message: str
) -> list:
    messages = [SystemMessage(content=system_prompt)]
    for msg in (conversation_history or []):
        role = msg.get("role", "")
        content = msg.get("content", "")
        if role == "USER":
            messages.append(HumanMessage(content=content))
        elif role == "AI":
            messages.append(AIMessage(content=content))
    messages.append(HumanMessage(content=latest_user_message))
    return messages


def extract_token_usage(response) -> dict:
    try:
        usage = response.response_metadata.get("token_usage", {})
        return {
            "prompt_tokens": usage.get("prompt_tokens", 0),
            "completion_tokens": usage.get("completion_tokens", 0),
            "total_tokens": usage.get("total_tokens", 0),
        }
    except Exception:
        return {"prompt_tokens": 0, "completion_tokens": 0, "total_tokens": 0}
