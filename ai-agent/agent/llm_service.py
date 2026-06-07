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
from typing import List, Optional, Tuple
from utils.logger import logger

# ==================== LLM 单例 ====================

_llm_instance: Optional[ChatOpenAI] = None
_llm_instance_lock = __import__('threading').Lock()


def get_llm(
    temperature: Optional[float] = None,
    max_tokens: Optional[int] = None,
    force_new: bool = False
) -> ChatOpenAI:
    global _llm_instance
    if _llm_instance is not None and not force_new:
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
    if config.LLM_PROVIDER == "deepseek":
        kwargs["base_url"] = config.LLM_BASE_URL or "https://api.deepseek.com/v1"
    elif config.LLM_PROVIDER == "qwen":
        kwargs["base_url"] = config.LLM_BASE_URL or "https://dashscope.aliyuncs.com/compatible-mode/v1"
    elif config.LLM_BASE_URL:
        kwargs["base_url"] = config.LLM_BASE_URL

    # 敏感信息: 不打印 API Key
    logger.info("llm_instance_created",
                extra={"provider": config.LLM_PROVIDER, "model": config.LLM_MODEL})
    return ChatOpenAI(**kwargs)


# ==================== 带重试的调用 ====================

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
    logger.debug("llm_invoke", extra={"message_count": len(messages)})
    return llm.invoke(messages)


# ==================== 响应长度管控 ====================

# 各模块的 max_tokens 硬限制 (防止 LLM 超长输出)
RESPONSE_MAX_TOKENS = {
    "chat": 256,              # 对话回复
    "correction": 384,         # 纠错 JSON
    "intent": 48,              # 意图分类
    "pronunciation": 384,      # 发音评测 JSON
}

# 响应内容最大字符数 (兜底截断)
MAX_RESPONSE_CHARS = {
    "chat": 500,
    "correction": 2000,
    "intent": 200,
    "pronunciation": 2000,
}


def enforce_response_length(content: str, module: str = "chat") -> str:
    """
    强制截断超长响应 (兜底机制)
    返回 (截断后内容, 是否被截断)
    """
    limit = MAX_RESPONSE_CHARS.get(module, 500)
    if len(content) > limit:
        logger.warning("response_truncated",
                       extra={"module": module, "original_len": len(content),
                              "truncated_to": limit})
        return content[:limit]
    return content


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


# ==================== Token 统计 (精确化) ====================

class TokenStats:
    """Token 统计结果 — 区分"成功0"和"失败" """
    def __init__(self, prompt: int, completion: int, total: int, success: bool):
        self.prompt_tokens = prompt
        self.completion_tokens = completion
        self.total_tokens = total
        self.success = success

    def to_dict(self) -> dict:
        return {
            "prompt_tokens": self.prompt_tokens,
            "completion_tokens": self.completion_tokens,
            "total_tokens": self.total_tokens,
            "token_stats_available": self.success,
        }


def extract_token_usage(response) -> dict:
    """精确化 Token 提取 — 区分统计失败和真的 0"""
    try:
        usage = response.response_metadata.get("token_usage", {})
        prompt = usage.get("prompt_tokens")
        completion = usage.get("completion_tokens")
        total = usage.get("total_tokens")

        # 关键: 检查是否真的获取到了数据
        if prompt is not None and completion is not None:
            return TokenStats(prompt, completion,
                              total if total is not None else prompt + completion,
                              success=True).to_dict()

        # 尝试从 AIMessage 的 additional_kwargs 获取
        add_kw = getattr(response, "additional_kwargs", {})
        usage2 = add_kw.get("token_usage", {})
        prompt2 = usage2.get("prompt_tokens")
        completion2 = usage2.get("completion_tokens")
        if prompt2 is not None and completion2 is not None:
            return TokenStats(prompt2, completion2,
                              prompt2 + completion2, success=True).to_dict()

        # 确实没拿到 — 标记为不可用
        return TokenStats(0, 0, 0, success=False).to_dict()
    except Exception:
        return TokenStats(0, 0, 0, success=False).to_dict()
