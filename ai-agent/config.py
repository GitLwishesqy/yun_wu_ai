"""
云悟英语 — AI 智能体配置 (安全加固版)
"""
import os
from dotenv import load_dotenv

load_dotenv()

# ==================== LLM 配置 ====================
LLM_PROVIDER = os.getenv("LLM_PROVIDER", "openai")
LLM_MODEL = os.getenv("LLM_MODEL", "gpt-4o")
LLM_API_KEY = os.getenv("LLM_API_KEY", "")
LLM_BASE_URL = os.getenv("LLM_BASE_URL", "")
LLM_TEMPERATURE = float(os.getenv("LLM_TEMPERATURE", "0.7"))
LLM_MAX_TOKENS = int(os.getenv("LLM_MAX_TOKENS", "1024"))
LLM_TIMEOUT = int(os.getenv("LLM_TIMEOUT", "30"))

# LLM 重试配置
LLM_RETRY_MAX_ATTEMPTS = int(os.getenv("LLM_RETRY_MAX_ATTEMPTS", "3"))
LLM_RETRY_MIN_WAIT = float(os.getenv("LLM_RETRY_MIN_WAIT", "1.0"))       # 秒
LLM_RETRY_MAX_WAIT = float(os.getenv("LLM_RETRY_MAX_WAIT", "10.0"))      # 秒
LLM_RETRY_BACKOFF = float(os.getenv("LLM_RETRY_BACKOFF", "2.0"))          # 指数退避倍数

# ==================== 服务配置 ====================
HOST = os.getenv("HOST", "0.0.0.0")
PORT = int(os.getenv("PORT", "8000"))
DEBUG = os.getenv("DEBUG", "false").lower() == "true"

# ==================== 安全配置 ====================
# CORS 白名单 (逗号分隔)
CORS_ALLOWED_ORIGINS = os.getenv(
    "CORS_ALLOWED_ORIGINS",
    "http://localhost:3000,http://localhost:5173"
).split(",")

# API 限流 (令牌桶)
RATE_LIMIT_ENABLED = os.getenv("RATE_LIMIT_ENABLED", "true").lower() == "true"
RATE_LIMIT_REQUESTS_PER_MINUTE = int(os.getenv("RATE_LIMIT_REQUESTS_PER_MINUTE", "30"))
RATE_LIMIT_BURST_SIZE = int(os.getenv("RATE_LIMIT_BURST_SIZE", "10"))

# ==================== Redis 配置 (状态持久化 + 限流) ====================
REDIS_ENABLED = os.getenv("REDIS_ENABLED", "false").lower() == "true"
REDIS_HOST = os.getenv("REDIS_HOST", "localhost")
REDIS_PORT = int(os.getenv("REDIS_PORT", "6379"))
REDIS_DB = int(os.getenv("REDIS_DB", "1"))
REDIS_PASSWORD = os.getenv("REDIS_PASSWORD", "")
REDIS_PREFIX = "yunwu:ai-agent:"

# ==================== 纠错配置 ====================
CORRECTION_ENABLED = os.getenv("CORRECTION_ENABLED", "true").lower() == "true"

# ==================== 可观测性配置 ====================
LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")
LOG_FORMAT = os.getenv("LOG_FORMAT", "json")       # json / text
TRACING_ENABLED = os.getenv("TRACING_ENABLED", "true").lower() == "true"
