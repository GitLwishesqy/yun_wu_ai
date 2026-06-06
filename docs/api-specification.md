# 云悟英语 — RESTful API 接口文档

> **版本**: v1.0
> **日期**: 2026-06-06
> **Base URL**: `https://api.yunwu-english.com/api/v1`
> **Content-Type**: `application/json; charset=utf-8`

---

## 目录

1. [API 设计规范](#1-api-设计规范)
2. [通用数据结构](#2-通用数据结构)
3. [鉴权模块 — Auth](#3-鉴权模块)
4. [用户模块 — Users](#4-用户模块)
5. [陪练核心 — Coach](#5-陪练核心)
6. [场景模板 — Scenes](#6-场景模板)
7. [纠错与反馈 — Corrections](#7-纠错与反馈)
8. [评测模块 — Evaluations](#8-评测模块)
9. [学习报告 — Reports](#9-学习报告)
10. [词汇模块 — Vocabulary](#10-词汇模块)
11. [激励模块 — Incentives](#11-激励模块)
12. [听力/阅读/写作 — Skills](#12-听力阅读写作)
13. [家长模块 — Parent](#13-家长模块)
14. [班级模块 — Classes](#14-班级模块)
15. [通知模块 — Notifications](#15-通知模块)
16. [学习计划 — Plans](#16-学习计划)
17. [管理后台 — Admin](#17-管理后台)
18. [WebSocket API](#18-websocket-api)
19. [错误码参考](#19-错误码参考)

---

## 1. API 设计规范

### 1.1 URL 命名

```
# 资源用复数名词
GET    /api/v1/users              # 查询列表
POST   /api/v1/users              # 创建
GET    /api/v1/users/{id}         # 查询详情
PUT    /api/v1/users/{id}         # 全量更新
PATCH  /api/v1/users/{id}         # 部分更新
DELETE /api/v1/users/{id}         # 删除

# 子资源
GET    /api/v1/users/{id}/sessions       # 用户的会话列表
GET    /api/v1/sessions/{id}/messages    # 会话的消息列表

# 动作 (非 CRUD)
POST   /api/v1/sessions/{id}/complete    # 完成会话
POST   /api/v1/sessions/{id}/pause       # 暂停会话
POST   /api/v1/check-ins                 # 打卡
```

### 1.2 版本管理

- URL 路径版本: `/api/v1/`, `/api/v2/`
- 兼容原则: 新增字段不破坏旧版本，废弃字段提前 2 个版本通知

### 1.3 鉴权方式

```
Authorization: Bearer <access_token>
```

- Access Token 有效期: **2 小时**
- Refresh Token 有效期: **30 天**
- Token 过期返回 `401`，前端自动用 Refresh Token 换新

### 1.4 统一响应格式

```json
// 成功
{
    "code": 0,
    "message": "success",
    "data": { ... },
    "timestamp": 1717632000000,
    "trace_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}

// 列表
{
    "code": 0,
    "message": "success",
    "data": {
        "items": [ ... ],
        "pagination": {
            "page": 1,
            "size": 20,
            "total": 156,
            "total_pages": 8
        }
    },
    "timestamp": 1717632000000,
    "trace_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}

// 错误
{
    "code": 40101,
    "message": "Access token expired",
    "data": null,
    "timestamp": 1717632000000,
    "trace_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

### 1.5 分页规范

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `page` | int | 1 | 页码 (从1开始) |
| `size` | int | 20 | 每页条数 (最大 100) |
| `sort` | string | `created_at,desc` | 排序字段和方向 |
| `q` | string | — | 全文搜索关键词 |

### 1.6 HTTP 状态码使用

| 状态码 | 含义 | 使用场景 |
|--------|------|---------|
| 200 | OK | GET/PUT/PATCH 成功 |
| 201 | Created | POST 创建成功 |
| 204 | No Content | DELETE 成功 |
| 400 | Bad Request | 参数校验失败 |
| 401 | Unauthorized | Token 无效或过期 |
| 403 | Forbidden | 权限不足 |
| 404 | Not Found | 资源不存在 |
| 409 | Conflict | 资源冲突 (如重复绑定) |
| 422 | Unprocessable Entity | 业务逻辑错误 |
| 429 | Too Many Requests | 触发限流 |
| 500 | Internal Server Error | 服务端异常 |

---

## 2. 通用数据结构

### 2.1 用户简要信息

```json
{
    "id": 1001,
    "nickname": "小明",
    "avatar_url": "https://cdn.yunwu-english.com/avatars/1001.jpg",
    "role": "STUDENT",
    "grade_level": "ELEMENTARY",
    "cefr_level": "A1"
}
```

### 2.2 学段/等级枚举

| 字段 | 枚举值 |
|------|--------|
| `grade_level` | `ELEMENTARY` / `JUNIOR` / `SENIOR` / `ADULT` |
| `cefr_level` | `A1` / `A2` / `B1` / `B2` / `C1` / `C2` |
| `role` | `STUDENT` / `PARENT` / `TEACHER` / `ADMIN` / `SUPER_ADMIN` |

### 2.3 时间格式

所有时间字段使用 ISO 8601 UTC 格式: `"2026-06-06T10:30:00Z"`

---

## 3. 鉴权模块

### 3.1 发送验证码

```
POST /api/v1/auth/send-code
```

**Request**:
```json
{
    "phone": "13800000001",
    "purpose": "LOGIN"
}
```
`purpose`: `LOGIN` / `REGISTER` / `RESET_PASSWORD` / `BIND`

**Response** (201):
```json
{
    "code": 0,
    "message": "验证码已发送",
    "data": {
        "expires_in": 300,
        "retry_after": 60
    }
}
```

**限流**: 同一手机号 60s 内只能发送 1 次，每小时 5 次，每天 10 次。

### 3.2 手机号登录/注册

```
POST /api/v1/auth/login
```

**Request**:
```json
{
    "phone": "13800000001",
    "code": "123456",
    "device_info": {
        "platform": "WEB",
        "device_name": "Chrome/Windows",
        "user_agent": "Mozilla/5.0..."
    }
}
```

**Response** (200):
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "user": {
            "id": 1001,
            "nickname": "小明",
            "avatar_url": null,
            "role": "STUDENT",
            "grade_level": "ELEMENTARY",
            "cefr_level": "A1",
            "status": "ACTIVE",
            "is_new_user": true
        },
        "tokens": {
            "access_token": "eyJhbGciOiJIUzI1NiIs...",
            "refresh_token": "eyJhbGciOiJIUzI1NiIs...",
            "token_type": "Bearer",
            "expires_in": 7200,
            "refresh_expires_in": 2592000
        }
    }
}
```

> **注意**: 如果手机号未注册，自动注册为新用户，`is_new_user` 为 `true`，前端引导完成学段选择。

### 3.3 刷新 Token

```
POST /api/v1/auth/refresh
```

**Request**:
```json
{
    "refresh_token": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response** (200): 同登录返回的 `data.tokens` 结构。

### 3.4 登出

```
POST /api/v1/auth/logout
```

**Request**:
```json
{
    "refresh_token": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response** (204): 无内容。

---

## 4. 用户模块

### 4.1 获取当前用户信息

```
GET /api/v1/users/me
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "id": 1001,
        "phone": "138****0001",
        "email": null,
        "nickname": "小明",
        "avatar_url": null,
        "role": "STUDENT",
        "grade_level": "ELEMENTARY",
        "grade_detail": "GRADE_3",
        "cefr_level": "A1",
        "status": "ACTIVE",
        "real_name_verified": false,
        "daily_limit_minutes": 60,
        "voice_preference": "FEMALE",
        "speech_rate": 1.0,
        "ui_font_scale": 1.0,
        "theme_mode": "LIGHT",
        "created_at": "2026-05-01T08:00:00Z"
    }
}
```

### 4.2 更新当前用户信息

```
PATCH /api/v1/users/me
```

**Request**:
```json
{
    "nickname": "小明爱英语",
    "grade_level": "ELEMENTARY",
    "grade_detail": "GRADE_3",
    "voice_preference": "FEMALE",
    "speech_rate": 0.8,
    "theme_mode": "LIGHT",
    "daily_limit_minutes": 45
}
```

**Response** (200): 同 4.1 返回结构。

### 4.3 获取学习者档案

```
GET /api/v1/users/me/profile
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "estimated_vocabulary_size": 200,
        "cefr_level": "A1",
        "china_standard_level": "2",
        "weaknesses": {
            "pronunciation": 0.6,
            "grammar": 0.4,
            "vocabulary": 0.5,
            "fluency": 0.3,
            "logic": 0.1
        },
        "total_learning_days": 30,
        "total_session_count": 20,
        "total_learning_minutes": 300,
        "total_words_spoken": 1200,
        "avg_accuracy_rate": 78.50,
        "streak_days": 5,
        "max_streak_days": 12,
        "last_learning_date": "2026-06-05",
        "preferred_topics": ["animals", "food", "sports"],
        "learning_goal": "能够用英语进行简单自我介绍",
        "level_history": [
            {
                "previous_cefr": null,
                "new_cefr": "A1",
                "change_reason": "INITIAL_TEST",
                "created_at": "2026-05-01T08:00:00Z"
            }
        ]
    }
}
```

### 4.4 更新学习目标

```
PATCH /api/v1/users/me/profile
```

**Request**:
```json
{
    "learning_goal": "能够用英语进行简单的自我介绍和日常对话",
    "preferred_topics": ["animals", "food", "sports", "school"]
}
```

---

## 5. 陪练核心 (重点模块)

### 5.1 创建陪练会话

```
POST /api/v1/sessions
```

**Request**:
```json
{
    "scene_id": 1,
    "session_type": "SCENE"
}
```
`sesssion_type`: `SCENE` / `FREE` / `CUSTOM` / `EXAM`

**Response** (201):
```json
{
    "code": 0,
    "data": {
        "id": 5001,
        "scene_id": 1,
        "session_type": "SCENE",
        "title": "餐厅点餐",
        "status": "ACTIVE",
        "scene_info": {
            "id": 1,
            "name": "餐厅点餐",
            "name_en": "Ordering at a Restaurant",
            "roles": [
                {"name": "服务员", "name_en": "Waiter"}
            ],
            "keywords": [
                {"word": "hamburger", "translation": "汉堡包"},
                {"word": "fries", "translation": "薯条"}
            ],
            "difficulty": 1,
            "cefr_level": "A1"
        },
        "started_at": "2026-06-06T10:30:00Z"
    }
}
```

### 5.2 结束陪练会话

```
POST /api/v1/sessions/{id}/complete
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "id": 5001,
        "status": "COMPLETED",
        "duration_seconds": 485,
        "message_count": 18,
        "user_message_count": 9,
        "ai_message_count": 9,
        "correction_count": 5,
        "total_tokens_used": 12450,
        "session_summary": {
            "topic": "餐厅点餐",
            "key_vocabulary_used": ["hamburger", "fries", "cola", "menu"],
            "grammar_points_practiced": ["I would like...", "Can I have...?"],
            "highlights": ["正确使用了 'I would like to order' 句型"],
            "improvement_areas": ["注意 hamburger 的发音 /ˈhæmbɜːrɡər/"]
        },
        "evaluation": {
            "id": 3001,
            "overall_score": 82.5,
            "dimension_scores": {
                "grammar": 85,
                "pronunciation": 72,
                "vocabulary": 80,
                "fluency": 76,
                "logic": 82
            }
        },
        "ended_at": "2026-06-06T10:38:05Z"
    }
}
```

### 5.3 获取会话列表

```
GET /api/v1/sessions?page=1&size=20&status=COMPLETED&sort=created_at,desc
```

**Query Parameters**:

| 参数 | 类型 | 说明 |
|------|------|------|
| `status` | string | `ACTIVE` / `COMPLETED` / `CANCELLED` |
| `session_type` | string | `SCENE` / `FREE` / `EXAM` |
| `scene_id` | int | 按场景筛选 |
| `date_from` | date | 开始日期 |
| `date_to` | date | 结束日期 |
| `page` | int | 页码 |
| `size` | int | 每页条数 |
| `sort` | string | 排序 |

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "items": [
            {
                "id": 5001,
                "scene_id": 1,
                "scene_name": "餐厅点餐",
                "session_type": "SCENE",
                "status": "COMPLETED",
                "title": "餐厅点餐",
                "message_count": 18,
                "correction_count": 5,
                "duration_seconds": 485,
                "overall_score": 82.5,
                "started_at": "2026-06-06T10:30:00Z",
                "ended_at": "2026-06-06T10:38:05Z"
            }
        ],
        "pagination": {
            "page": 1,
            "size": 20,
            "total": 45,
            "total_pages": 3
        }
    }
}
```

### 5.4 获取会话详情

```
GET /api/v1/sessions/{id}
```

**Response** (200): 同 5.2 完整结构。

### 5.5 获取会话消息列表

```
GET /api/v1/sessions/{id}/messages?page=1&size=50
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "items": [
            {
                "id": 10001,
                "session_id": 5001,
                "role": "AI",
                "content": "Good evening! Welcome to our restaurant. What can I get for you today?",
                "content_type": "TEXT",
                "audio_url": "https://cdn.yunwu-english.com/audio/ai_10001.mp3",
                "audio_duration": 4.2,
                "sequence_num": 1,
                "created_at": "2026-06-06T10:30:05Z"
            },
            {
                "id": 10002,
                "session_id": 5001,
                "role": "USER",
                "content": "I want a hamburger please.",
                "content_type": "TEXT",
                "audio_url": "https://cdn.yunwu-english.com/audio/user_10002.mp3",
                "audio_duration": 2.8,
                "has_correction": true,
                "correction": {
                    "id": 2001,
                    "error_type": "GRAMMAR",
                    "error_subtype": "TENSE",
                    "severity": "LOW",
                    "original_text": "I want a hamburger please.",
                    "corrected_text": "I would like a hamburger, please.",
                    "explanation": "\"I would like\" 比 \"I want\" 更礼貌，在餐厅点餐时更常用哦！",
                    "improvement_tip": "试试用 \"I'd like\" 替代 \"I want\" 来表达请求"
                },
                "sequence_num": 2,
                "created_at": "2026-06-06T10:30:18Z"
            },
            {
                "id": 10003,
                "session_id": 5001,
                "role": "AI",
                "content": "Sure! A hamburger sounds great. Would you like fries or a drink with that?",
                "content_type": "TEXT",
                "audio_url": null,
                "sequence_num": 3,
                "created_at": "2026-06-06T10:30:22Z"
            }
        ],
        "pagination": {
            "page": 1,
            "size": 50,
            "total": 18,
            "total_pages": 1
        }
    }
}
```

### 5.6 发送消息 (核心接口)

```
POST /api/v1/sessions/{id}/messages
```

**Request** (文本消息):
```json
{
    "content": "I would like a cola too.",
    "content_type": "TEXT"
}
```

**Request** (语音消息 — 先上传音频到 OSS，再提交):
```json
{
    "content": null,
    "content_type": "AUDIO",
    "audio_url": "https://cdn.yunwu-english.com/audio/user_msg_20260606_103500.mp3",
    "audio_duration": 3.5
}
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "user_message": {
            "id": 10004,
            "role": "USER",
            "content": "I would like a cola too.",
            "content_type": "TEXT",
            "has_correction": false,
            "correction": null,
            "sequence_num": 4,
            "created_at": "2026-06-06T10:35:00Z"
        },
        "ai_message": {
            "id": 10005,
            "role": "AI",
            "content": "Great choice! So that's one hamburger and one cola. Will that be all?",
            "content_type": "TEXT",
            "audio_url": "https://cdn.yunwu-english.com/audio/ai_10005.mp3",
            "audio_duration": 5.1,
            "sequence_num": 5,
            "created_at": "2026-06-06T10:35:03Z"
        },
        "session_status": "ACTIVE",
        "tokens_used_this_turn": 450
    }
}
```

> **流式返回**: 当 `?stream=true` 时，响应为 SSE (Server-Sent Events) 流。每块数据包含 `{"type":"token","content":"..."}` 增量输出；纠错信息在 `{"type":"correction","data":{...}}` 中异步返回。

### 5.7 暂停/恢复会话

```
POST /api/v1/sessions/{id}/pause
POST /api/v1/sessions/{id}/resume
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "id": 5001,
        "status": "PAUSED"
    }
}
```

---

## 6. 场景模板

### 6.1 场景列表

```
GET /api/v1/scenes?category=DAILY_LIFE&grade_level=ELEMENTARY&difficulty=1&page=1&size=20
```

**Query Parameters**:

| 参数 | 类型 | 说明 |
|------|------|------|
| `category` | string | `DAILY_LIFE` / `TRAVEL` / `ACADEMIC` / `BUSINESS` / `EXAM` / `SOCIAL` |
| `grade_level` | string | 学段筛选 |
| `difficulty` | int | 难度 1-9 |
| `cefr_level` | string | CEFR 等级 |
| `q` | string | 搜索关键词 |
| `tags` | string | 逗号分隔标签 (如 `ket,pet`) |
| `is_published` | bool | 是否已发布 (管理员) |

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "items": [
            {
                "id": 1,
                "name": "餐厅点餐",
                "name_en": "Ordering at a Restaurant",
                "description": "在餐厅用英语点餐的对话练习",
                "category": "DAILY_LIFE",
                "grade_level": "ELEMENTARY",
                "difficulty": 1,
                "cefr_level": "A1",
                "estimated_duration": 15,
                "tags": ["daily", "food"],
                "usage_count": 1230,
                "avg_rating": 4.5
            }
        ],
        "pagination": { "page": 1, "size": 20, "total": 15, "total_pages": 1 }
    }
}
```

### 6.2 场景详情

```
GET /api/v1/scenes/{id}
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "id": 1,
        "name": "餐厅点餐",
        "name_en": "Ordering at a Restaurant",
        "description": "在餐厅用英语点餐的对话练习",
        "description_en": "Practice ordering food at a restaurant in English",
        "category": "DAILY_LIFE",
        "grade_level": "ELEMENTARY",
        "difficulty": 1,
        "cefr_level": "A1",
        "roles": [
            {"name": "服务员", "name_en": "Waiter", "description": "餐厅服务员"}
        ],
        "keywords": [
            {"word": "hamburger", "translation": "汉堡包", "difficulty": 1},
            {"word": "fries", "translation": "薯条", "difficulty": 1},
            {"word": "cola", "translation": "可乐", "difficulty": 1},
            {"word": "menu", "translation": "菜单", "difficulty": 1}
        ],
        "target_sentences": [
            {"sentence": "I would like to order...", "explanation": "用于点餐"},
            {"sentence": "Can I have the menu, please?", "explanation": "请求菜单"}
        ],
        "opening_dialogue": {
            "ai_first": "Good evening! Welcome to our restaurant. What can I get for you today?"
        },
        "max_rounds": 30,
        "estimated_duration": 15,
        "tags": ["daily", "food", "beginner"],
        "version": 1,
        "created_by": {
            "id": 6,
            "nickname": "张老师"
        },
        "created_at": "2026-05-15T08:00:00Z"
    }
}
```

### 6.3 创建/更新/删除场景 (教师/管理员)

```
POST   /api/v1/admin/scenes              # 创建
PUT    /api/v1/admin/scenes/{id}          # 更新
DELETE /api/v1/admin/scenes/{id}          # 删除
PATCH  /api/v1/admin/scenes/{id}/publish  # 发布
```

---

## 7. 纠错与反馈

### 7.1 获取会话纠错记录

```
GET /api/v1/sessions/{id}/corrections
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "items": [
            {
                "id": 2001,
                "message_id": 10002,
                "error_type": "GRAMMAR",
                "error_subtype": "TENSE",
                "severity": "LOW",
                "original_text": "I want a hamburger please.",
                "error_span": "I want",
                "corrected_text": "I would like a hamburger, please.",
                "explanation": "\"I would like\" 比 \"I want\" 更礼貌",
                "improvement_tip": "在餐厅等正式场合，用 \"I'd like\" 替代 \"I want\"",
                "related_rule": "礼貌表达 — Would like vs Want",
                "correction_strategy": "DELAYED",
                "was_reviewed": true,
                "created_at": "2026-06-06T10:30:19Z"
            }
        ],
        "summary": {
            "total": 5,
            "by_type": {
                "GRAMMAR": 2,
                "PRONUNCIATION": 2,
                "VOCABULARY": 1
            },
            "by_severity": {
                "LOW": 3,
                "MEDIUM": 2
            }
        }
    }
}
```

### 7.2 标记纠错已查看

```
PATCH /api/v1/corrections/{id}/read
```

**Response** (204)。

### 7.3 获取用户薄弱点分析

```
GET /api/v1/users/me/error-analysis
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "radar": {
            "grammar": 0.4,
            "pronunciation": 0.6,
            "vocabulary": 0.5,
            "fluency": 0.3,
            "logic": 0.1
        },
        "top_errors": [
            {
                "error_type": "PRONUNCIATION",
                "error_subtype": "TH_SOUND",
                "error_pattern": "th→s",
                "total_count": 15,
                "mastery_status": "LEARNING",
                "last_error_at": "2026-06-05T18:00:00Z"
            },
            {
                "error_type": "GRAMMAR",
                "error_subtype": "PAST_TENSE",
                "error_pattern": "forget_past_tense",
                "total_count": 12,
                "mastery_status": "REVIEWING",
                "last_error_at": "2026-06-04T15:30:00Z"
            }
        ],
        "recent_improvements": [
            {"error_type": "VOCABULARY", "trend": "DOWN", "change_pct": -25}
        ]
    }
}
```

---

## 8. 评测模块

### 8.1 获取评测记录列表

```
GET /api/v1/evaluations?eval_type=SESSION&page=1&size=20
```

### 8.2 获取单次评测详情

```
GET /api/v1/evaluations/{id}
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "id": 3001,
        "session_id": 5001,
        "eval_type": "SESSION",
        "dimension_scores": {
            "grammar": 85,
            "pronunciation": 72,
            "vocabulary": 80,
            "fluency": 76,
            "logic": 82,
            "content": 88
        },
        "overall_score": 82.5,
        "strengths": [
            "Good use of polite expressions (\"I would like\")",
            "Rich food-related vocabulary"
        ],
        "weaknesses": [
            "\"th\" sound needs practice — said \"s\" instead of \"th\"",
            "Article usage is inconsistent"
        ],
        "suggestions": [
            {
                "title": "Practice 'th' sound",
                "action": "Try the pronunciation drill: \"the\", \"this\", \"that\", \"thank you\"",
                "resource_url": "/api/v1/skills/pronunciation/drills/th-sound"
            },
            {
                "title": "Review articles (a/an/the)",
                "action": "Review the grammar card on article usage",
                "resource_url": "/api/v1/skills/grammar/articles"
            }
        ],
        "feedback_summary": "你这次表现不错！词汇用得很丰富，但要注意一些发音细节。建议重点练习 'th' 的发音和冠词的使用。加油！",
        "previous_score": 79.0,
        "improvement": 3.5,
        "created_at": "2026-06-06T10:38:05Z"
    }
}
```

---

## 9. 学习报告

### 9.1 获取学习报告列表

```
GET /api/v1/reports?period_type=WEEKLY&page=1&size=10
```

### 9.2 获取最新学习报告

```
GET /api/v1/reports/latest?period_type=WEEKLY
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "id": 4001,
        "period_type": "WEEKLY",
        "period_start": "2026-06-01",
        "period_end": "2026-06-07",
        "is_read": false,
        "report_data": {
            "summary": "本周你完成了12次陪练，累计学习180分钟，评测平均分82.5分，比上周提高了3.2分！太棒了！",
            "stats": {
                "total_sessions": 12,
                "total_minutes": 180,
                "total_messages": 245,
                "total_corrections": 28,
                "avg_score": 82.5,
                "score_change": 3.2,
                "new_vocabulary": 34
            },
            "dimension_trend": {
                "grammar": {"current": 85, "change": 2},
                "pronunciation": {"current": 72, "change": 5},
                "vocabulary": {"current": 80, "change": 4},
                "fluency": {"current": 76, "change": 1},
                "logic": {"current": 82, "change": 3}
            },
            "daily_breakdown": [
                {"date": "2026-06-01", "sessions": 2, "minutes": 30},
                {"date": "2026-06-02", "sessions": 2, "minutes": 25},
                {"date": "2026-06-03", "sessions": 3, "minutes": 45},
                {"date": "2026-06-04", "sessions": 2, "minutes": 35},
                {"date": "2026-06-05", "sessions": 3, "minutes": 45}
            ],
            "top_errors": [
                {"type": "TH_SOUND", "count": 8, "trend": "DOWN"},
                {"type": "PAST_TENSE", "count": 6, "trend": "STABLE"}
            ],
            "scene_distribution": [
                {"scene_name": "餐厅点餐", "count": 4},
                {"scene_name": "自我介绍", "count": 3},
                {"scene_name": "商场购物", "count": 2}
            ],
            "comparison_to_last_period": {
                "score_change": 3.2,
                "session_change": 2,
                "time_change": 30
            },
            "next_period_suggestions": [
                "重点练习 'th' 发音，每天5分钟发音训练",
                "尝试挑战难度 2 的场景",
                "这周学会的34个新单词记得复习哦"
            ]
        },
        "generated_at": "2026-06-08T02:00:00Z"
    }
}
```

### 9.3 标记报告已读

```
PATCH /api/v1/reports/{id}/read
```

### 9.4 导出报告 PDF

```
GET /api/v1/reports/{id}/export?format=pdf
```
返回 PDF 文件流 (Content-Type: application/pdf)。

---

## 10. 词汇模块

### 10.1 我的词汇本

```
GET /api/v1/vocabulary/my?status=LEARNING&sort=last_seen_at,desc&page=1&size=50
```

**Query Parameters**: `status` (NEW/LEARNING/REVIEWING/KNOWN/MASTERED), `q` (搜索), `sort`

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "items": [
            {
                "id": 6001,
                "word": "hamburger",
                "translation": "汉堡包",
                "status": "LEARNING",
                "seen_count": 5,
                "used_count": 3,
                "error_count": 1,
                "correct_count": 2,
                "last_seen_at": "2026-06-06T10:30:00Z",
                "next_review_at": "2026-06-08T10:30:00Z"
            }
        ],
        "stats": {
            "total": 200,
            "new": 50,
            "learning": 80,
            "reviewing": 40,
            "known": 25,
            "mastered": 5
        },
        "pagination": { "page": 1, "size": 50, "total": 200, "total_pages": 4 }
    }
}
```

### 10.2 标记词汇状态

```
PATCH /api/v1/vocabulary/{id}/status
```

**Request**:
```json
{
    "status": "KNOWN"
}
```

### 10.3 待复习词汇

```
GET /api/v1/vocabulary/review-due?limit=20
```

返回 `next_review_at <= NOW()` 的词汇列表。

### 10.4 查询词汇库

```
GET /api/v1/vocabulary/library?q=hamburger&cefr_level=A1
```

返回 `vocabulary_library` 表内容。

---

## 11. 激励模块

### 11.1 每日打卡

```
POST /api/v1/check-ins
```

**Response** (201):
```json
{
    "code": 0,
    "data": {
        "id": 7001,
        "check_in_date": "2026-06-06",
        "streak_count": 6,
        "reward_points": 5,
        "total_points": 350
    }
}
```

### 11.2 打卡日历

```
GET /api/v1/check-ins/calendar?month=2026-06
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "month": "2026-06",
        "checked_dates": ["2026-06-01", "2026-06-02", "2026-06-03", "2026-06-04", "2026-06-05"],
        "current_streak": 5,
        "total_check_ins_this_month": 5,
        "missed_dates": []
    }
}
```

### 11.3 积分记录

```
GET /api/v1/points/records?page=1&size=20
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "total_points": 350,
        "items": [
            {
                "id": 8001,
                "points": 5,
                "balance_after": 350,
                "action_type": "CHECK_IN",
                "action_desc": "每日打卡",
                "created_at": "2026-06-06T08:00:00Z"
            },
            {
                "id": 8002,
                "points": 10,
                "balance_after": 345,
                "action_type": "COMPLETE_SESSION",
                "action_desc": "完成陪练 — 餐厅点餐",
                "reference_id": 5001,
                "created_at": "2026-06-06T10:38:05Z"
            }
        ],
        "pagination": { "page": 1, "size": 20, "total": 120, "total_pages": 6 }
    }
}
```

### 11.4 成就列表

```
GET /api/v1/achievements
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "completed": [
            {
                "id": 1,
                "code": "FIRST_SESSION",
                "name": "初次见面",
                "name_en": "First Steps",
                "icon_url": "/icons/achievements/first-session.svg",
                "completed_at": "2026-05-01T10:00:00Z"
            }
        ],
        "in_progress": [
            {
                "id": 2,
                "code": "STREAK_7",
                "name": "周而不息",
                "icon_url": "/icons/achievements/streak-7.svg",
                "progress": {"current": 5, "target": 7},
                "progress_pct": 71.4
            }
        ],
        "locked": [
            {
                "id": 3,
                "code": "STREAK_30",
                "name": "月学不辍",
                "icon_url": "/icons/achievements/streak-30.svg",
                "is_secret": false
            }
        ]
    }
}
```

---

## 12. 听力/阅读/写作

### 12.1 听力材料列表

```
GET /api/v1/skills/listening?grade_level=ELEMENTARY&difficulty=1&page=1&size=20
```

### 12.2 听力材料详情

```
GET /api/v1/skills/listening/{id}
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "id": 9001,
        "title": "在超市购物",
        "asset_type": "LISTENING",
        "audio_url": "https://cdn.yunwu-english.com/listening/supermarket.mp3",
        "duration_seconds": 120,
        "transcript": "A: Excuse me, where can I find the milk?\nB: It's in aisle 3, on the left.\nA: Thank you!\n...",
        "questions": [
            {
                "type": "multiple_choice",
                "question": "Where is the milk?",
                "options": ["Aisle 2", "Aisle 3", "Aisle 4", "Aisle 5"],
                "answer": "Aisle 3"
            }
        ],
        "difficulty": 1,
        "cefr_level": "A1"
    }
}
```

### 12.3 提交听力答案

```
POST /api/v1/skills/listening/{id}/submit
```

**Request**:
```json
{
    "answers": [
        {"question_index": 0, "answer": "Aisle 3"}
    ]
}
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "score": 80,
        "total_questions": 5,
        "correct_count": 4,
        "results": [
            {"question_index": 0, "correct": true, "explanation": null},
            {"question_index": 1, "correct": false, "correct_answer": "B", "explanation": "..."}
        ]
    }
}
```

### 12.4 阅读材料列表/详情/提交

```
GET    /api/v1/skills/reading?page=1&size=20
GET    /api/v1/skills/reading/{id}
POST   /api/v1/skills/reading/{id}/submit
```

### 12.5 写作题目列表/详情/提交

```
GET    /api/v1/skills/writing?page=1&size=20
GET    /api/v1/skills/writing/{id}
POST   /api/v1/skills/writing/{id}/submit
```

**提交请求**:
```json
{
    "content": "My favorite season is spring. In spring, the weather is warm and flowers bloom...",
    "time_taken_seconds": 1200
}
```

**AI 批改结果**:
```json
{
    "code": 0,
    "data": {
        "submission_id": 10001,
        "score": 78.5,
        "dimension_scores": {
            "grammar": 80,
            "structure": 75,
            "content": 82,
            "vocabulary": 77
        },
        "corrections": [
            {
                "original": "the weather is warm",
                "corrected": "the weather becomes warm",
                "type": "GRAMMAR",
                "explanation": "..."
            }
        ],
        "polished_version": "My favorite season is spring. During spring, the weather becomes pleasantly warm and colorful flowers begin to bloom...",
        "feedback_summary": "文章结构清晰，词汇使用恰当。注意时态一致性和连接词的使用。"
    }
}
```

### 12.6 发音练习 (跟读模仿)

```
GET    /api/v1/skills/pronunciation/drills?type=TH_SOUND
POST   /api/v1/skills/pronunciation/evaluate
```

**评测请求**:
```json
{
    "reference_text": "The weather is nice today.",
    "audio_url": "https://cdn.yunwu-english.com/audio/pron_20260606.mp3"
}
```

**评测结果**:
```json
{
    "code": 0,
    "data": {
        "overall_score": 75,
        "word_scores": [
            {"word": "The", "score": 65, "phoneme_issues": ["ð→d"]},
            {"word": "weather", "score": 70, "phoneme_issues": []},
            {"word": "is", "score": 90, "phoneme_issues": []},
            {"word": "nice", "score": 85, "phoneme_issues": []},
            {"word": "today", "score": 80, "phoneme_issues": []}
        ],
        "suggestion": "'The' 的发音需要注意 'th' 音，把舌头放在上下齿之间"
    }
}
```

---

## 13. 家长模块

### 13.1 获取绑定申请列表

```
GET /api/v1/parent/bindings?status=PENDING
```

### 13.2 发起绑定请求

```
POST /api/v1/parent/bindings
```

**Request**:
```json
{
    "student_phone": "13800000001",
    "relationship": "MOTHER"
}
```

### 13.3 审批绑定请求 (学生端操作)

```
POST /api/v1/parent/bindings/{id}/approve
POST /api/v1/parent/bindings/{id}/reject
```

### 13.4 查看绑定学生数据

```
GET /api/v1/parent/students/{student_id}/overview
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "student": {
            "id": 1001,
            "nickname": "小明",
            "grade_level": "ELEMENTARY",
            "cefr_level": "A1"
        },
        "today_stats": {
            "sessions": 2,
            "minutes": 30,
            "remaining_minutes": 30,
            "check_in": true
        },
        "weekly_stats": {
            "sessions": 12,
            "minutes": 180,
            "avg_score": 82.5,
            "score_change": 3.2
        },
        "weaknesses": {
            "pronunciation": 0.6,
            "grammar": 0.4
        },
        "recent_sessions": [
            {
                "id": 5001,
                "scene_name": "餐厅点餐",
                "score": 82.5,
                "duration_minutes": 8,
                "date": "2026-06-06"
            }
        ]
    }
}
```

### 13.5 更新绑定设置

```
PATCH /api/v1/parent/bindings/{id}/settings
```

**Request**:
```json
{
    "daily_time_limit_minutes": 45,
    "monthly_budget_limit": 200.00,
    "can_view_report": true,
    "can_set_time_limit": true,
    "can_manage_payment": true
}
```

---

## 14. 班级模块 (教师端)

### 14.1 班级 CRUD

```
GET    /api/v1/classes                         # 我的班级列表
GET    /api/v1/classes/{id}                    # 班级详情
POST   /api/v1/classes                         # 创建班级
PUT    /api/v1/classes/{id}                    # 更新班级
DELETE /api/v1/classes/{id}                    # 解散班级
```

### 14.2 班级花名册管理

```
GET    /api/v1/classes/{id}/roster             # 花名册列表
POST   /api/v1/classes/{id}/roster             # 添加学生 (通过手机号)
DELETE /api/v1/classes/{id}/roster/{student_id} # 移除学生
```

### 14.3 布置/查看任务

```
GET    /api/v1/classes/{id}/assignments        # 任务列表
POST   /api/v1/classes/{id}/assignments        # 布置任务
GET    /api/v1/assignments/{id}/submissions    # 查看提交情况
```

**布置任务请求**:
```json
{
    "title": "练习餐厅点餐场景",
    "description": "每位同学至少完成一次餐厅点餐场景的陪练",
    "assignment_type": "COACH_SESSION",
    "scene_id": 1,
    "due_date": "2026-06-13T23:59:59Z"
}
```

### 14.4 班级数据看板

```
GET /api/v1/classes/{id}/dashboard
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "class_name": "六年级(1)班",
        "student_count": 35,
        "active_students_this_week": 30,
        "avg_weekly_sessions": 8.5,
        "avg_weekly_minutes": 120,
        "avg_score": 78.5,
        "completion_rate": 85.0,
        "top_students": [
            {"student_id": 1001, "nickname": "小明", "score": 92, "sessions": 15}
        ],
        "common_weaknesses": [
            {"type": "PRONUNCIATION", "pct": 60},
            {"type": "GRAMMAR", "pct": 40}
        ]
    }
}
```

---

## 15. 通知模块

### 15.1 通知列表

```
GET /api/v1/notifications?is_read=false&page=1&size=20
```

### 15.2 标记已读

```
PATCH /api/v1/notifications/{id}/read
PATCH /api/v1/notifications/read-all
```

### 15.3 未读数量

```
GET /api/v1/notifications/unread-count
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "total_unread": 3,
        "by_type": {
            "ACHIEVEMENT": 1,
            "REMINDER": 1,
            "SYSTEM": 1
        }
    }
}
```

---

## 16. 学习计划

### 16.1 当前计划

```
GET /api/v1/plans/active
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "id": 11001,
        "name": "6月英语提升计划",
        "plan_type": "AI_GENERATED",
        "start_date": "2026-06-01",
        "end_date": "2026-06-30",
        "target_level": "A2",
        "progress": {
            "total_items": 30,
            "completed_items": 8,
            "completion_pct": 26.7
        },
        "today_items": [
            {
                "id": 12001,
                "item_type": "COACH_SESSION",
                "item_name": "场景陪练 — 餐厅点餐",
                "scene_id": 1,
                "is_completed": false,
                "estimated_minutes": 15,
                "points_reward": 10
            },
            {
                "id": 12002,
                "item_type": "VOCAB_REVIEW",
                "item_name": "复习10个核心词汇",
                "is_completed": true,
                "estimated_minutes": 10,
                "points_reward": 5
            }
        ]
    }
}
```

### 16.2 计划历史

```
GET /api/v1/plans?page=1&size=10&is_active=false
```

### 16.3 AI 生成计划

```
POST /api/v1/plans/generate
```

**Request**:
```json
{
    "name": "7月暑假特训计划",
    "start_date": "2026-07-01",
    "end_date": "2026-07-31",
    "target_level": "A2",
    "focus_areas": ["pronunciation", "fluency"],
    "weekly_effort": "MEDIUM"
}
```
`weekly_effort`: `LIGHT` (每天10分钟) / `MEDIUM` (每天20分钟) / `INTENSIVE` (每天30分钟)

**Response** (201): 返回生成的计划结构 (同 16.1)。

---

## 17. 管理后台

### 17.1 用户管理

```
GET    /api/v1/admin/users?role=STUDENT&status=ACTIVE&page=1&size=20
PATCH  /api/v1/admin/users/{id}/status        # 封禁/解封
GET    /api/v1/admin/users/{id}/detail        # 用户详情 (含敏感信息)
POST   /api/v1/admin/users/{id}/data-export   # 导出用户数据
DELETE /api/v1/admin/users/{id}               # 删除用户 (软删除)
```

### 17.2 内容审核

```
GET    /api/v1/admin/reviews?review_result=BLOCK&page=1&size=20
POST   /api/v1/admin/reviews/{id}/approve    # 放行
POST   /api/v1/admin/reviews/{id}/block      # 屏蔽
POST   /api/v1/admin/reviews/{id}/modify     # 修改后放行
```

### 17.3 敏感词管理

```
GET    /api/v1/admin/sensitive-words?page=1&size=50
POST   /api/v1/admin/sensitive-words
DELETE /api/v1/admin/sensitive-words/{id}
```

### 17.4 AI 模型管理

```
GET    /api/v1/admin/ai-models
POST   /api/v1/admin/ai-models
PATCH  /api/v1/admin/ai-models/{id}
PATCH  /api/v1/admin/ai-models/{id}/toggle   # 启用/禁用
```

### 17.5 系统配置

```
GET    /api/v1/admin/configs
PATCH  /api/v1/admin/configs/{key}
```

### 17.6 平台数据统计

```
GET /api/v1/admin/statistics?period=WEEKLY
```

**Response** (200):
```json
{
    "code": 0,
    "data": {
        "users": {
            "total": 12500,
            "new_this_week": 320,
            "active_this_week": 4500,
            "by_role": {"STUDENT": 12000, "PARENT": 400, "TEACHER": 80, "ADMIN": 20}
        },
        "sessions": {
            "total_this_week": 32000,
            "avg_duration_minutes": 12.5,
            "avg_score": 76.8
        },
        "ai_usage": {
            "total_tokens_this_week": 125000000,
            "total_cost_usd": 1250.00,
            "by_model": [
                {"model": "qwen-turbo", "tokens": 80000000, "cost": 400.00},
                {"model": "deepseek-v3", "tokens": 45000000, "cost": 850.00}
            ]
        },
        "retention": {
            "d1": 65.5,
            "d7": 42.0,
            "d30": 28.5
        },
        "content_review": {
            "total_reviewed": 50000,
            "blocked": 120,
            "block_rate": 0.24
        }
    }
}
```

### 17.7 审计日志

```
GET /api/v1/admin/audit-logs?user_id=1001&action=LOGIN&date_from=2026-06-01&date_to=2026-06-07&page=1&size=50
```

---

## 18. WebSocket API

陪练对话支持 WebSocket 连接，用于实时消息推送。

### 18.1 连接

```
wss://api.yunwu-english.com/ws/coach?token=<access_token>&session_id=5001
```

### 18.2 客户端 → 服务端消息

```json
// 发送文本消息
{
    "type": "message",
    "content": "I would like a cola too.",
    "content_type": "TEXT"
}

// 发送语音消息
{
    "type": "message",
    "content_type": "AUDIO",
    "audio_url": "https://cdn.yunwu-english.com/audio/xxx.mp3",
    "audio_duration": 3.5
}

// 客户端心跳
{
    "type": "ping"
}

// 打字状态
{
    "type": "typing",
    "is_typing": true
}
```

### 18.3 服务端 → 客户端消息

```json
// AI 增量回复 (流式)
{
    "type": "token",
    "content": "Sure",
    "sequence_num": 5,
    "is_final": false
}

// AI 完整一条消息发送完毕
{
    "type": "message_complete",
    "message": {
        "id": 10005,
        "content": "Sure! A hamburger sounds great...",
        "audio_url": "https://cdn.yunwu-english.com/audio/ai_10005.mp3",
        "sequence_num": 5,
        "created_at": "2026-06-06T10:35:03Z"
    }
}

// 纠错反馈 (异步)
{
    "type": "correction",
    "data": {
        "message_id": 10004,
        "correction": {
            "id": 2002,
            "error_type": "GRAMMAR",
            "original_text": "I want...",
            "corrected_text": "I would like...",
            "explanation": "..."
        }
    }
}

// 会话状态变化
{
    "type": "session_event",
    "event": "DIFFICULTY_CHANGED",
    "data": {
        "previous_difficulty": 1,
        "new_difficulty": 2,
        "reason": "连续5轮无错误，自动提升难度"
    }
}

// 错误
{
    "type": "error",
    "code": 40001,
    "message": "Session not found"
}

// 服务端心跳响应
{
    "type": "pong"
}
```

---

## 19. 错误码参考

### 19.1 错误码分段

| 段 | 模块 | 范围 |
|----|------|------|
| 0 | 成功 | 0 |
| 1xxxx | 通用错误 | 10001-19999 |
| 2xxxx | 鉴权错误 | 20001-29999 |
| 3xxxx | 用户错误 | 30001-39999 |
| 4xxxx | 陪练错误 | 40001-49999 |
| 5xxxx | 评测错误 | 50001-59999 |
| 6xxxx | 内容审核 | 60001-69999 |
| 7xxxx | 家长/班级 | 70001-79999 |
| 8xxxx | 系统/限流 | 80001-89999 |
| 9xxxx | 第三方服务 | 90001-99999 |

### 19.2 常见错误码

| 错误码 | HTTP 状态 | 说明 |
|--------|-----------|------|
| 0 | 200/201 | 成功 |
| 10001 | 400 | 参数校验失败 |
| 10002 | 404 | 资源不存在 |
| 10003 | 409 | 资源冲突 |
| 10004 | 422 | 业务逻辑错误 |
| 10005 | 500 | 服务器内部错误 |
| 20001 | 401 | Token 缺失 |
| 20002 | 401 | Token 过期 |
| 20003 | 401 | Token 无效 |
| 20004 | 401 | Refresh Token 过期 |
| 20005 | 403 | 权限不足 |
| 20006 | 429 | 验证码发送太频繁 |
| 20007 | 400 | 验证码错误 |
| 30001 | 404 | 用户不存在 |
| 30002 | 403 | 用户已被封禁 |
| 30003 | 400 | 手机号已注册 |
| 30004 | 422 | 实名认证未通过 |
| 40001 | 404 | 会话不存在 |
| 40002 | 422 | 会话已结束 |
| 40003 | 422 | 会话达到最大轮次 |
| 40004 | 422 | 每日会话次数已用完 |
| 40005 | 422 | 每日时长已用完 |
| 40006 | 422 | 输入内容超长 |
| 50001 | 422 | 评测服务不可用 |
| 50002 | 422 | 该会话无可评测内容 |
| 60001 | 422 | 输入内容包含敏感词 |
| 60002 | 422 | AI 输出被审核拦截 |
| 70001 | 409 | 已存在相同的绑定关系 |
| 70002 | 404 | 绑定关系不存在 |
| 80001 | 429 | 全局限流 |
| 80002 | 429 | 用户限流 |
| 80003 | 503 | 系统维护中 |
| 90001 | 502 | LLM 服务不可用 |
| 90002 | 502 | ASR 服务不可用 |
| 90003 | 502 | TTS 服务不可用 |
| 90004 | 504 | LLM 服务超时 |

### 19.3 错误响应示例

```json
{
    "code": 40005,
    "message": "今日学习时长已用完 (60/60分钟)，明天再来吧！",
    "data": {
        "limit_minutes": 60,
        "used_minutes": 60,
        "remaining_minutes": 0,
        "reset_at": "2026-06-07T00:00:00Z"
    },
    "timestamp": 1717632000000,
    "trace_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

---

## 附录 A: 接口总览速查表

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/auth/send-code` | 发送验证码 | 公开 |
| POST | `/auth/login` | 手机号登录 | 公开 |
| POST | `/auth/refresh` | 刷新 Token | 公开 |
| POST | `/auth/logout` | 登出 | 登录 |
| GET | `/users/me` | 当前用户信息 | 登录 |
| PATCH | `/users/me` | 更新用户信息 | 登录 |
| GET | `/users/me/profile` | 学习档案 | 学生 |
| PATCH | `/users/me/profile` | 更新学习目标 | 学生 |
| POST | `/sessions` | 创建陪练会话 | 学生 |
| POST | `/sessions/{id}/complete` | 结束会话 | 学生 |
| GET | `/sessions` | 会话列表 | 学生 |
| GET | `/sessions/{id}` | 会话详情 | 学生 |
| GET | `/sessions/{id}/messages` | 会话消息 | 学生 |
| POST | `/sessions/{id}/messages` | 发送消息 (核心) | 学生 |
| POST | `/sessions/{id}/pause` | 暂停会话 | 学生 |
| POST | `/sessions/{id}/resume` | 恢复会话 | 学生 |
| GET | `/scenes` | 场景列表 | 登录 |
| GET | `/scenes/{id}` | 场景详情 | 登录 |
| GET | `/sessions/{id}/corrections` | 纠错记录 | 学生 |
| GET | `/users/me/error-analysis` | 薄弱点分析 | 学生 |
| GET | `/evaluations` | 评测列表 | 学生 |
| GET | `/evaluations/{id}` | 评测详情 | 学生 |
| GET | `/reports` | 报告列表 | 学生 |
| GET | `/reports/latest` | 最新报告 | 学生 |
| GET | `/vocabulary/my` | 我的词汇本 | 学生 |
| GET | `/vocabulary/review-due` | 待复习词汇 | 学生 |
| GET | `/vocabulary/library` | 词汇库查询 | 登录 |
| POST | `/check-ins` | 打卡 | 学生 |
| GET | `/check-ins/calendar` | 打卡日历 | 学生 |
| GET | `/points/records` | 积分记录 | 学生 |
| GET | `/achievements` | 成就列表 | 登录 |
| GET | `/skills/listening` | 听力材料 | 学生 |
| GET | `/skills/reading` | 阅读材料 | 学生 |
| GET | `/skills/writing` | 写作题目 | 学生 |
| POST | `/skills/pronunciation/evaluate` | 发音评测 | 学生 |
| GET | `/parent/bindings` | 绑定列表 | 家长 |
| POST | `/parent/bindings` | 发起绑定 | 家长 |
| GET | `/parent/students/{id}/overview` | 学生总览 | 家长 |
| GET | `/classes` | 班级列表 | 教师 |
| POST | `/classes` | 创建班级 | 教师 |
| GET | `/classes/{id}/dashboard` | 班级看板 | 教师 |
| GET | `/notifications` | 通知列表 | 登录 |
| GET | `/plans/active` | 当前学习计划 | 学生 |
| POST | `/plans/generate` | AI生成计划 | 学生 |
| GET | `/admin/users` | 用户管理 | 管理员 |
| GET | `/admin/reviews` | 内容审核 | 管理员 |
| GET | `/admin/statistics` | 数据统计 | 管理员 |
| GET | `/admin/audit-logs` | 审计日志 | 管理员 |

---

> **文档版本**: v1.0 | **日期**: 2026-06-06 | **作者**: 罗淇育
> **后续所有后端开发以此 API 文档为基准。**
