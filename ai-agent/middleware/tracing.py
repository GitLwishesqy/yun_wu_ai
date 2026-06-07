"""
分布式追踪中间件 — 请求级 trace_id 注入
"""
import uuid
import time
from contextvars import ContextVar
from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request
from starlette.responses import Response
from utils.logger import logger

# 异步安全的上下文变量
_trace_id_var: ContextVar[str] = ContextVar("trace_id", default="")
_request_start_var: ContextVar[float] = ContextVar("request_start", default=0.0)


def get_current_trace_id() -> str:
    return _trace_id_var.get()


class TracingMiddleware(BaseHTTPMiddleware):
    """
    每个请求注入 trace_id，记录耗时
    """

    async def dispatch(self, request: Request, call_next):
        # 优先使用上游传入的 trace_id，否则生成新的
        trace_id = request.headers.get("X-Trace-Id") or uuid.uuid4().hex[:16]
        _trace_id_var.set(trace_id)
        _request_start_var.set(time.time())

        # 注入到响应头，方便前端/Java后端串联
        response: Response = await call_next(request)
        response.headers["X-Trace-Id"] = trace_id

        elapsed_ms = (time.time() - _request_start_var.get()) * 1000
        logger.info(
            "request_completed",
            extra={
                "method": request.method,
                "path": request.url.path,
                "status": response.status_code,
                "elapsed_ms": round(elapsed_ms, 2),
            }
        )
        return response
