"""
云悟英语 — AI 智能体配置
"""
import os
from dotenv import load_dotenv

load_dotenv()

# LLM 配置
LLM_PROVIDER = os.getenv("LLM_PROVIDER", "openai")       # openai / deepseek / qwen
LLM_MODEL = os.getenv("LLM_MODEL", "gpt-4o")
LLM_API_KEY = os.getenv("LLM_API_KEY", "")
LLM_BASE_URL = os.getenv("LLM_BASE_URL", "")              # 自定义 endpoint
LLM_TEMPERATURE = float(os.getenv("LLM_TEMPERATURE", "0.7"))
LLM_MAX_TOKENS = int(os.getenv("LLM_MAX_TOKENS", "1024"))
LLM_TIMEOUT = int(os.getenv("LLM_TIMEOUT", "30"))

# 服务配置
HOST = os.getenv("HOST", "0.0.0.0")
PORT = int(os.getenv("PORT", "8000"))
DEBUG = os.getenv("DEBUG", "false").lower() == "true"

# 纠错配置
CORRECTION_ENABLED = os.getenv("CORRECTION_ENABLED", "true").lower() == "true"
CORRECTION_GRAMMAR_WEIGHT = 0.4
CORRECTION_PRONUNCIATION_WEIGHT = 0.3
CORRECTION_VOCABULARY_WEIGHT = 0.3
