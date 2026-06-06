"""
结构化日志 — JSON 格式 + 追踪 ID
"""
import logging
import sys
import time
from pythonjsonlogger import jsonlogger
import config


class TraceIdFilter(logging.Filter):
    """注入 trace_id 到每一条日志"""
    def filter(self, record):
        from middleware.tracing import get_current_trace_id
        record.trace_id = get_current_trace_id()
        return True


def setup_logger(name: str = "yunwu-ai") -> logging.Logger:
    """初始化结构化日志"""
    logger = logging.getLogger(name)
    logger.setLevel(getattr(logging, config.LOG_LEVEL))

    if logger.handlers:
        return logger

    if config.LOG_FORMAT == "json":
        handler = logging.StreamHandler(sys.stdout)
        formatter = jsonlogger.JsonFormatter(
            fmt="%(asctime)s %(levelname)s %(name)s %(trace_id)s %(message)s",
            datefmt="%Y-%m-%dT%H:%M:%S",
        )
        handler.setFormatter(formatter)
        handler.addFilter(TraceIdFilter())
        logger.addHandler(handler)
    else:
        handler = logging.StreamHandler(sys.stdout)
        handler.setFormatter(logging.Formatter(
            "[%(asctime)s] [%(levelname)s] [%(trace_id)s] %(name)s: %(message)s",
            datefmt="%Y-%m-%d %H:%M:%S"
        ))
        handler.addFilter(TraceIdFilter())
        logger.addHandler(handler)

    return logger


class SensitiveDataFilter(logging.Filter):
    """过滤日志中的敏感字段"""
    SENSITIVE_KEYS = {"api_key", "access_key", "secret", "password",
                      "token", "credential", "authorization"}

    def filter(self, record):
        if hasattr(record, "extra") and isinstance(record.extra, dict):
            for key in list(record.extra.keys()):
                key_lower = key.lower()
                if any(sk in key_lower for sk in self.SENSITIVE_KEYS):
                    record.extra[key] = "***REDACTED***"
        return True


# 全局 logger
logger = setup_logger()
logger.addFilter(SensitiveDataFilter())

