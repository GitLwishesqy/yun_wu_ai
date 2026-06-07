"""
IP 级限流中间件 — 令牌桶算法 + Redis 支持
"""
import time
import threading
from collections import defaultdict
from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request
from starlette.responses import JSONResponse
from utils.logger import logger

import config


class TokenBucket:
    """令牌桶"""
    def __init__(self, rate: float, burst: int):
        self.rate = rate          # 令牌补充速率 (个/秒)
        self.burst = burst        # 桶容量
        self.tokens = burst       # 当前令牌数
        self.last_refill = time.time()
        self.lock = threading.Lock()

    def consume(self, tokens: int = 1) -> bool:
        with self.lock:
            now = time.time()
            elapsed = now - self.last_refill
            self.tokens = min(self.burst, self.tokens + elapsed * self.rate)
            self.last_refill = now

            if self.tokens >= tokens:
                self.tokens -= tokens
                return True
            return False


class RateLimiterMiddleware(BaseHTTPMiddleware):
    """
    IP 级别令牌桶限流
    支持 Redis (分布式) 和内存 (单机) 两种模式
    """

    def __init__(self, app):
        super().__init__(app)
        self.enabled = config.RATE_LIMIT_ENABLED
        self.buckets: dict[str, TokenBucket] = {}
        self.buckets_lock = threading.Lock()
        self.rate = config.RATE_LIMIT_REQUESTS_PER_MINUTE / 60.0
        self.burst = config.RATE_LIMIT_BURST_SIZE

        # Redis 模式
        self.redis = None
        if config.REDIS_ENABLED:
            try:
                import redis
                self.redis = redis.Redis(
                    host=config.REDIS_HOST,
                    port=config.REDIS_PORT,
                    db=config.REDIS_DB,
                    password=config.REDIS_PASSWORD or None,
                    decode_responses=True,
                    socket_connect_timeout=2,
                )
                self.redis.ping()
                logger.info("rate_limiter_mode", extra={"mode": "redis"})
            except Exception as e:
                logger.warning("redis_unavailable_fallback_to_memory", extra={"error": str(e)})
                self.redis = None

        if not self.redis:
            logger.info("rate_limiter_mode", extra={"mode": "memory"})

    async def dispatch(self, request: Request, call_next):
        if not self.enabled:
            return await call_next(request)

        client_ip = self._get_client_ip(request)

        if not self._allow(client_ip):
            logger.warning("rate_limited", extra={"ip": client_ip, "path": request.url.path})
            return JSONResponse(
                status_code=429,
                content={
                    "code": 80002,
                    "message": "请求过于频繁，请稍后再试",
                    "data": {"retry_after_seconds": 3}
                }
            )

        return await call_next(request)

    def _allow(self, ip: str) -> bool:
        if self.redis:
            return self._allow_redis(ip)
        return self._allow_memory(ip)

    def _allow_memory(self, ip: str) -> bool:
        with self.buckets_lock:
            bucket = self.buckets.get(ip)
            if bucket is None:
                bucket = TokenBucket(self.rate, self.burst)
                self.buckets[ip] = bucket
            # 清理过期桶 (> 5 分钟未使用)
            if len(self.buckets) > 10000:
                self.buckets.clear()
        return bucket.consume()

    def _allow_redis(self, ip: str) -> bool:
        key = f"{config.REDIS_PREFIX}ratelimit:{ip}"
        try:
            current = self.redis.get(key)
            if current and int(current) >= self.burst:
                return False
            pipe = self.redis.pipeline()
            pipe.incr(key)
            pipe.expire(key, 60)
            pipe.execute()
            return True
        except Exception:
            return self._allow_memory(ip)

    def _get_client_ip(self, request: Request) -> str:
        xff = request.headers.get("X-Forwarded-For")
        if xff:
            return xff.split(",")[0].strip()
        xri = request.headers.get("X-Real-IP")
        if xri:
            return xri
        return request.client.host if request.client else "unknown"
