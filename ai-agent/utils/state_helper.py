"""State 访问器 — 兼容 dict 和 Pydantic 对象"""
def _get(obj, key, default=None):
    if isinstance(obj, dict):
        return obj.get(key, default)
    return getattr(obj, key, default)
