"""
Pydantic 数据模型 — Java 后端 ↔ Python AI 智能体的通信协议
"""
from pydantic import BaseModel, Field
from typing import Optional, List, Dict
from enum import Enum


# ==================== 请求模型 ====================

class SceneContext(BaseModel):
    """场景上下文"""
    id: Optional[int] = None
    name: str = ""
    name_en: str = ""
    difficulty: int = 1
    cefr_level: str = "A1"
    roles: List[Dict[str, str]] = []
    keywords: List[Dict[str, str]] = []
    target_sentences: List[Dict[str, str]] = []


class LearnerProfile(BaseModel):
    """学习者档案"""
    cefr_level: str = "A1"
    vocabulary_size: int = 0
    weaknesses: Dict[str, float] = {}   # {"grammar": 0.4, "pronunciation": 0.6}
    learning_goal: str = ""


class HistoryMessage(BaseModel):
    """历史消息"""
    role: str          # USER / AI
    content: str


class ChatRequest(BaseModel):
    """对话请求 — Java 后端 → AI 智能体"""
    session_id: str = Field(..., description="会话ID")
    user_id: Optional[int] = None
    user_message: str = Field(..., description="用户最新输入")
    scene: Optional[SceneContext] = None
    learner_profile: Optional[LearnerProfile] = None
    conversation_history: List[HistoryMessage] = []


# ==================== 响应模型 ====================

class CorrectionItem(BaseModel):
    """纠错项"""
    error_type: str = ""            # GRAMMAR / PRONUNCIATION / VOCABULARY / LOGIC / COLLOCATION
    error_subtype: str = ""         # TENSE / PREPOSITION / TH_SOUND / ...
    severity: str = "MEDIUM"        # LOW / MEDIUM / HIGH / CRITICAL
    original_text: str = ""
    error_span: str = ""
    corrected_text: str = ""
    explanation: str = ""
    improvement_tip: str = ""
    related_rule: str = ""
    correction_strategy: str = "IMMEDIATE"


class ChatResponse(BaseModel):
    """对话响应 — AI 智能体 → Java 后端"""
    ai_message: str = ""
    corrections: List[CorrectionItem] = []
    model_name: str = ""
    prompt_tokens: int = 0
    completion_tokens: int = 0
    total_tokens: int = 0


# ==================== LangGraph 内部 State ====================

class CoachState(BaseModel):
    """LangGraph 状态 — 在图的节点间流转"""
    # 输入
    session_id: str = ""
    user_id: Optional[int] = None
    user_message: str = ""
    scene: Optional[SceneContext] = None
    learner_profile: Optional[LearnerProfile] = None
    conversation_history: List[HistoryMessage] = []

    # 中间结果
    intent: str = ""                # chat / end_session / help
    ai_response: str = ""
    corrections: List[CorrectionItem] = []

    # Token 统计
    prompt_tokens: int = 0
    completion_tokens: int = 0
    total_tokens: int = 0
    model_name: str = ""
