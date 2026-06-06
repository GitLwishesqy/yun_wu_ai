# 云悟英语 — 数据库设计文档

> **版本**: v1.0
> **日期**: 2026-06-06
> **数据库**: PostgreSQL 16+
> **字符集**: UTF-8 (utf8mb4)
> **排序规则**: zh_CN.UTF-8
> **ORM**: MyBatis-Plus / Spring Data JPA

---

## 目录

1. [设计原则](#1-设计原则)
2. [ER 图](#2-er-图)
3. [表结构详述](#3-表结构详述)
4. [索引策略](#4-索引策略)
5. [分区策略](#5-分区策略)
6. [数据字典](#6-数据字典)
7. [初始化脚本](#7-初始化脚本)

---

## 1. 设计原则

| 原则 | 说明 |
|------|------|
| **主键策略** | 核心业务表使用 `BIGSERIAL` (自增，高性能)；关联表使用 `UUID` (分布式友好) |
| **软删除** | 用户敏感数据使用 `deleted_at` 软删除，日志类表不软删除 |
| **审计字段** | 所有表包含 `created_at`、`updated_at`，敏感表额外 `created_by`、`updated_by` |
| **时区统一** | 所有时间戳使用 `TIMESTAMPTZ` (带时区)，应用层统一 UTC |
| **JSONB** | 灵活结构使用 JSONB 列 (评测维度、报告内容、场景角色等) |
| **范式** | 遵循 3NF，适当反范式 (学习报告预计算存储) 优化查询 |
| **命名** | 表名小写下划线复数形式；列名小写下划线单数；索引 `idx_表名_列名`；唯一约束 `uk_表名_列名` |

---

## 2. ER 图

```
                                    ┌─────────────────────┐
                                    │      users           │
                                    │  用户核心表           │
                                    └──────────┬──────────┘
                                               │
          ┌────────────────────────────────────┼────────────────────────────────────┐
          │                                    │                                    │
          ▼                                    ▼                                    ▼
┌──────────────────┐               ┌──────────────────┐               ┌──────────────────┐
│ learner_profiles │               │  coach_sessions  │               │  user_vocabulary │
│   学习档案        │               │    陪练会话       │               │    用户词汇表     │
└────────┬─────────┘               └────────┬─────────┘               └──────────────────┘
         │                                  │
         │ 1:1                              │ 1:N
         ▼                                  ▼
┌──────────────────┐               ┌──────────────────┐
│error_records     │               │  coach_messages  │
│  错误汇总记录     │               │    会话消息       │
└──────────────────┘               └────────┬─────────┘
                                            │ 1:N
                                            ▼
                                   ┌──────────────────┐
                                   │   corrections    │
                                   │    纠错记录       │
                                   └──────────────────┘

┌──────────────────┐               ┌──────────────────┐
│ scene_templates  │               │   evaluations    │
│   场景模板        │               │    评测记录       │
└──────────────────┘               └──────────────────┘

┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│     parents      │     │ parent_student   │     │ classes +        │
│   家长信息        │←───→│   _bindings      │     │ class_rosters    │
└──────────────────┘     │  家长学生绑定     │     │ 班级 + 花名册     │
                         └──────────────────┘     └──────────────────┘

┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│   check_ins      │     │  points_records  │     │   achievements   │
│   打卡记录        │     │   积分记录        │     │   成就徽章        │
└──────────────────┘     └──────────────────┘     └──────────────────┘

┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│  audit_logs      │     │ content_review   │     │ system_configs   │
│  审计日志         │     │   _logs          │     │  系统配置         │
└──────────────────┘     │ 内容审核日志      │     └──────────────────┘
                         └──────────────────┘
```

---

## 3. 表结构详述

### 3.1 用户与认证

#### 3.1.1 `users` — 用户核心表

```sql
CREATE TABLE users (
    id              BIGSERIAL       PRIMARY KEY,
    phone           VARCHAR(20)     NOT NULL,
    phone_encrypted VARCHAR(200),                              -- 加密存储手机号
    email           VARCHAR(200),
    password_hash   VARCHAR(255),                              -- bcrypt 哈希
    nickname        VARCHAR(100),
    avatar_url      VARCHAR(500),

    -- 角色与学段
    role            VARCHAR(20)     NOT NULL DEFAULT 'STUDENT', -- STUDENT/PARENT/TEACHER/ADMIN/SUPER_ADMIN
    grade_level     VARCHAR(10),                               -- ELEMENTARY/JUNIOR/SENIOR/ADULT
    grade_detail    VARCHAR(20),                               -- 具体年级 GRADE_1 ~ GRADE_12, UNIVERSITY, WORKING
    cefr_level      VARCHAR(4),                                -- CEFR等级 A1/A2/B1/B2/C1/C2

    -- 状态
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE', -- ACTIVE/INACTIVE/SUSPENDED/BANNED
    real_name_verified BOOLEAN      NOT NULL DEFAULT FALSE,    -- 实名认证
    real_name        VARCHAR(100),
    id_card_encrypted VARCHAR(500),                            -- 加密存储身份证号

    -- 配置
    daily_limit_minutes INT         DEFAULT 60,                -- 每日时长限制(分钟)
    voice_preference VARCHAR(10)    DEFAULT 'FEMALE',          -- FEMALE/MALE
    speech_rate      DECIMAL(3,2)  DEFAULT 1.00,              -- 语速 0.5-2.0
    ui_font_scale    DECIMAL(3,2)  DEFAULT 1.00,              -- 字体缩放
    theme_mode       VARCHAR(10)    DEFAULT 'LIGHT',           -- LIGHT/DARK/AUTO

    -- 审计
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    last_login_at    TIMESTAMPTZ,
    last_login_ip    VARCHAR(50),
    deleted_at       TIMESTAMPTZ                              -- 软删除
);

-- 索引
CREATE UNIQUE INDEX uk_users_phone ON users(phone) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_role ON users(role) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_grade_level ON users(grade_level) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_cefr_level ON users(cefr_level) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);
```

#### 3.1.2 `user_auth_tokens` — 认证令牌表

```sql
CREATE TABLE user_auth_tokens (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    access_token    VARCHAR(500)    NOT NULL,
    refresh_token   VARCHAR(500)    NOT NULL,
    token_type      VARCHAR(20)     NOT NULL DEFAULT 'BEARER',
    device_info     VARCHAR(500),                              -- 设备信息 JSON
    ip_address      VARCHAR(50),

    expires_at      TIMESTAMPTZ    NOT NULL,
    refresh_expires_at TIMESTAMPTZ NOT NULL,
    revoked_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_auth_tokens_user ON user_auth_tokens(user_id);
CREATE INDEX idx_auth_tokens_access ON user_auth_tokens(access_token);
CREATE INDEX idx_auth_tokens_refresh ON user_auth_tokens(refresh_token);
CREATE INDEX idx_auth_tokens_expires ON user_auth_tokens(expires_at);
```

#### 3.1.3 `user_verification_codes` — 验证码表

```sql
CREATE TABLE user_verification_codes (
    id              BIGSERIAL       PRIMARY KEY,
    phone           VARCHAR(20)     NOT NULL,
    code            VARCHAR(10)     NOT NULL,
    purpose         VARCHAR(30)     NOT NULL,                   -- LOGIN/REGISTER/RESET_PASSWORD/BIND
    ip_address      VARCHAR(50),
    expires_at      TIMESTAMPTZ    NOT NULL,
    verified_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_vcode_phone_purpose ON user_verification_codes(phone, purpose);
CREATE INDEX idx_vcode_expires ON user_verification_codes(expires_at);
```

---

### 3.2 学习档案

#### 3.2.1 `learner_profiles` — 学习档案表

```sql
CREATE TABLE learner_profiles (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL UNIQUE REFERENCES users(id),

    -- 能力评估
    estimated_vocabulary_size INT    DEFAULT 0,                 -- 估算词汇量
    cefr_level      VARCHAR(4),                                -- 当前CEFR等级
    china_standard_level VARCHAR(10),                          -- 新课标等级 1-9

    -- 薄弱点 (JSONB)
    weaknesses      JSONB           DEFAULT '{}',              -- {"grammar": 0.3, "pronunciation": 0.6, "vocabulary": 0.2, "fluency": 0.4, "logic": 0.1}

    -- 学习统计
    total_learning_days     INT     DEFAULT 0,
    total_session_count     INT     DEFAULT 0,
    total_learning_minutes  INT     DEFAULT 0,
    total_words_spoken      INT     DEFAULT 0,
    avg_accuracy_rate       DECIMAL(5,2) DEFAULT 0.00,

    -- 连续学习
    streak_days             INT     DEFAULT 0,
    max_streak_days         INT     DEFAULT 0,
    last_learning_date      DATE,

    -- 偏好
    preferred_topics        JSONB   DEFAULT '[]',              -- ["travel", "food", "technology"]
    learning_goal           VARCHAR(500),

    created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_learner_user ON learner_profiles(user_id);
CREATE INDEX idx_learner_cefr ON learner_profiles(cefr_level);
CREATE INDEX idx_learner_streak ON learner_profiles(streak_days DESC);
```

#### 3.2.2 `learner_level_history` — 等级变化历史

```sql
CREATE TABLE learner_level_history (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    previous_cefr   VARCHAR(4),
    new_cefr        VARCHAR(4)      NOT NULL,
    change_reason   VARCHAR(50),                                -- INITIAL_TEST/AUTO_ADJUST/MANUAL/PERIODIC_REVIEW
    source_session_id BIGINT,                                   -- 触发调整的会话ID
    note            VARCHAR(500),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_level_history_user ON learner_level_history(user_id, created_at DESC);
```

---

### 3.3 陪练核心

#### 3.3.1 `scene_templates` — 场景模板表

```sql
CREATE TABLE scene_templates (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(200)    NOT NULL,
    name_en          VARCHAR(200),
    description     VARCHAR(1000),
    description_en   VARCHAR(1000),

    -- 分类
    category        VARCHAR(30)     NOT NULL,                   -- DAILY_LIFE/ACADEMIC/BUSINESS/EXAM/TRAVEL/SOCIAL
    grade_level     VARCHAR(10),                                -- ELEMENTARY/JUNIOR/SENIOR/ADULT
    difficulty      INT             NOT NULL DEFAULT 1,         -- 1-9
    cefr_level      VARCHAR(4),                                 -- A1/A2/B1/B2/C1/C2

    -- 场景内容 (JSONB)
    roles           JSONB           NOT NULL,                   -- [{"name":"服务员","name_en":"Waiter","description":"..."}]
    keywords        JSONB           DEFAULT '[]',              -- [{"word":"hamburger","translation":"汉堡包","difficulty":1}]
    target_sentences JSONB          DEFAULT '[]',              -- [{"sentence":"I would like...","explanation":"用于点餐"}]
    opening_dialogue JSONB,                                     -- 开场对话引导

    -- 配置
    max_rounds      INT             DEFAULT 30,                 -- 建议最大对话轮次
    estimated_duration INT          DEFAULT 15,                 -- 预计时长(分钟)
    is_published    BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INT             NOT NULL DEFAULT 1,

    -- 标签
    tags            JSONB           DEFAULT '[]',              -- ["ket","pet","ielts","daily","formal"]

    -- 审计
    created_by      BIGINT          REFERENCES users(id),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_scene_category ON scene_templates(category) WHERE deleted_at IS NULL;
CREATE INDEX idx_scene_grade ON scene_templates(grade_level) WHERE deleted_at IS NULL;
CREATE INDEX idx_scene_difficulty ON scene_templates(difficulty) WHERE deleted_at IS NULL;
CREATE INDEX idx_scene_published ON scene_templates(is_published) WHERE deleted_at IS NULL;
CREATE INDEX idx_scene_tags ON scene_templates USING GIN (tags);
```

#### 3.3.2 `coach_sessions` — 陪练会话表

```sql
CREATE TABLE coach_sessions (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    scene_id        BIGINT          REFERENCES scene_templates(id),

    -- 会话信息
    session_type    VARCHAR(20)     NOT NULL DEFAULT 'SCENE',   -- SCENE/FREE/CUSTOM/EXAM
    title           VARCHAR(300),
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE/PAUSED/COMPLETED/CANCELLED

    -- 对话统计
    message_count   INT             DEFAULT 0,
    user_message_count INT          DEFAULT 0,
    ai_message_count   INT          DEFAULT 0,
    correction_count   INT          DEFAULT 0,
    total_tokens_used  BIGINT       DEFAULT 0,

    -- 时间
    started_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    ended_at        TIMESTAMPTZ,
    duration_seconds INT,

    -- 会话配置快照
    difficulty_snapshot INT,                                    -- 会话开始时的难度等级
    cefr_level_snapshot VARCHAR(4),                             -- 会话开始时的CEFR等级

    -- 元数据
    metadata        JSONB           DEFAULT '{}',              -- {"device":"web","network":"wifi","voice_enabled":true}

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sessions_user ON coach_sessions(user_id, created_at DESC);
CREATE INDEX idx_sessions_scene ON coach_sessions(scene_id);
CREATE INDEX idx_sessions_status ON coach_sessions(status);
CREATE INDEX idx_sessions_date ON coach_sessions(created_at DESC);
```

#### 3.3.3 `coach_messages` — 会话消息表 (大表，需分区)

```sql
CREATE TABLE coach_messages (
    id              BIGSERIAL,
    session_id      BIGINT          NOT NULL REFERENCES coach_sessions(id),

    -- 消息内容
    role            VARCHAR(10)     NOT NULL,                   -- USER/AI/SYSTEM
    content         TEXT            NOT NULL,
    content_type    VARCHAR(20)     DEFAULT 'TEXT',            -- TEXT/AUDIO/IMAGE/MIXED
    audio_url       VARCHAR(500),
    audio_duration  INT,                                        -- 语音时长(秒)

    -- AI 相关
    model_name      VARCHAR(100),                               -- 使用的模型
    prompt_tokens   INT,
    completion_tokens INT,
    latency_ms      INT,                                        -- AI响应延迟

    -- 纠错标记
    has_correction  BOOLEAN         DEFAULT FALSE,

    -- 顺序
    sequence_num    INT             NOT NULL,                   -- 会话内消息序号

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- 按月分区 (初始化脚本中创建)
CREATE TABLE coach_messages_y2026m06 PARTITION OF coach_messages
    FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');
CREATE TABLE coach_messages_y2026m07 PARTITION OF coach_messages
    FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');

CREATE INDEX idx_messages_session ON coach_messages(session_id, sequence_num);
CREATE INDEX idx_messages_created ON coach_messages(created_at);
```

#### 3.3.4 `corrections` — 纠错记录表

```sql
CREATE TABLE corrections (
    id              BIGSERIAL       PRIMARY KEY,
    message_id      BIGINT          NOT NULL,
    session_id      BIGINT          NOT NULL,
    user_id         BIGINT          NOT NULL,

    -- 错误信息
    error_type      VARCHAR(30)     NOT NULL,                   -- GRAMMAR/PRONUNCIATION/VOCABULARY/LOGIC/COLLOCATION
    error_subtype   VARCHAR(50),                                -- TENSE/PREPOSITION/ARTICLE/TH_SOUND/VOWEL/...
    severity        VARCHAR(10)     NOT NULL DEFAULT 'MEDIUM',  -- LOW/MEDIUM/HIGH/CRITICAL

    -- 内容
    original_text   TEXT            NOT NULL,                   -- 用户原始表达
    error_span      VARCHAR(500),                               -- 具体的错误片段
    corrected_text  TEXT            NOT NULL,                   -- 纠正后的表达
    explanation     TEXT,                                       -- 中文解释
    explanation_en  TEXT,                                       -- 英文解释

    -- 建议
    improvement_tip TEXT,                                       -- 改进建议
    related_rule    VARCHAR(500),                               -- 关联语法规则

    -- 处理方式
    correction_strategy VARCHAR(20) DEFAULT 'IMMEDIATE',        -- IMMEDIATE/DELAYED/SKIPPED/REVIEW_LATER
    was_reviewed    BOOLEAN         DEFAULT FALSE,              -- 用户是否查看了纠错

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_corrections_message ON corrections(message_id);
CREATE INDEX idx_corrections_session ON corrections(session_id);
CREATE INDEX idx_corrections_user_type ON corrections(user_id, error_type);
CREATE INDEX idx_corrections_user_date ON corrections(user_id, created_at DESC);
```

---

### 3.4 错误追踪与词汇

#### 3.4.1 `error_records` — 错误汇总记录

```sql
CREATE TABLE error_records (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),

    -- 错误分类
    error_type      VARCHAR(30)     NOT NULL,
    error_subtype   VARCHAR(50),
    error_pattern   VARCHAR(500),                               -- 错误模式 (如 "forget_past_tense")

    -- 统计
    total_count     INT             DEFAULT 1,
    correct_count   INT             DEFAULT 0,
    last_error_at   TIMESTAMPTZ,
    last_correct_at TIMESTAMPTZ,

    -- 学习状态
    mastery_status  VARCHAR(20)     DEFAULT 'LEARNING',        -- LEARNING/REVIEWING/MASTERED/ARCHIVED
    next_review_at  TIMESTAMPTZ,                                -- 艾宾浩斯下次复习时间
    review_count    INT             DEFAULT 0,

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    UNIQUE (user_id, error_type, error_subtype, error_pattern)
);

CREATE INDEX idx_error_records_user ON error_records(user_id);
CREATE INDEX idx_error_records_review ON error_records(next_review_at) WHERE mastery_status IN ('LEARNING', 'REVIEWING');
```

#### 3.4.2 `user_vocabulary` — 用户词汇表

```sql
CREATE TABLE user_vocabulary (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    word            VARCHAR(200)    NOT NULL,
    word_lower      VARCHAR(200)    NOT NULL,                   -- 小写索引
    translation     VARCHAR(500),

    -- 状态
    status          VARCHAR(20)     NOT NULL DEFAULT 'NEW',     -- NEW/LEARNING/REVIEWING/KNOWN/MASTERED

    -- 统计
    seen_count      INT             DEFAULT 1,
    used_count      INT             DEFAULT 0,
    error_count     INT             DEFAULT 0,
    correct_count   INT             DEFAULT 0,

    -- 时间
    first_seen_at   TIMESTAMPTZ     DEFAULT NOW(),
    last_seen_at    TIMESTAMPTZ     DEFAULT NOW(),
    last_reviewed_at TIMESTAMPTZ,
    next_review_at  TIMESTAMPTZ,                                -- 艾宾浩斯下次复习

    -- 来源
    source_session_id BIGINT,
    source_type     VARCHAR(30),                                -- CONVERSATION/READING/LISTENING/MANUAL

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    UNIQUE (user_id, word_lower)
);

CREATE INDEX idx_user_vocab_user ON user_vocabulary(user_id);
CREATE INDEX idx_user_vocab_status ON user_vocabulary(user_id, status);
CREATE INDEX idx_user_vocab_review ON user_vocabulary(user_id, next_review_at) WHERE status IN ('LEARNING', 'REVIEWING');
```

#### 3.4.3 `vocabulary_library` — 词汇库 (公共)

```sql
CREATE TABLE vocabulary_library (
    id              BIGSERIAL       PRIMARY KEY,
    word            VARCHAR(200)    NOT NULL UNIQUE,
    word_lower      VARCHAR(200)    NOT NULL UNIQUE,
    pronunciation   VARCHAR(200),                               -- IPA 音标 /haɪ/
    pronunciation_audio_url VARCHAR(500),

    -- 释义
    translation     VARCHAR(500)    NOT NULL,                   -- 中文释义
    part_of_speech  VARCHAR(50),                                -- noun/verb/adjective/...
    definition_en   TEXT,

    -- 分级
    cefr_level      VARCHAR(4),                                 -- A1/A2/B1/B2/C1/C2
    china_grade     VARCHAR(20),                                -- 适合年级
    difficulty      INT             DEFAULT 1,

    -- 示例
    example_sentences JSONB         DEFAULT '[]',              -- [{"en":"...","zh":"..."}]

    -- 关联
    common_collocations JSONB       DEFAULT '[]',              -- 常见搭配

    -- 考纲
    exam_tags       JSONB           DEFAULT '[]',              -- ["KET","PET","IELTS","TOEFL","CET4","CET6"]

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_vocab_lib_cefr ON vocabulary_library(cefr_level);
CREATE INDEX idx_vocab_lib_grade ON vocabulary_library(china_grade);
CREATE INDEX idx_vocab_lib_exam ON vocabulary_library USING GIN (exam_tags);
```

---

### 3.5 评测系统

#### 3.5.1 `evaluations` — 评测记录表

```sql
CREATE TABLE evaluations (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    session_id      BIGINT          REFERENCES coach_sessions(id),
    eval_type       VARCHAR(30)     NOT NULL,                   -- SESSION/DAILY/WEEKLY/DIAGNOSTIC/EXAM_MOCK

    -- 多维度评分 (JSONB)
    dimension_scores JSONB          NOT NULL,                   -- {"grammar":85,"pronunciation":72,"vocabulary":80,"fluency":76,"logic":82,"content":88}
    overall_score   DECIMAL(5,2)    NOT NULL,

    -- 详细反馈
    strengths       JSONB           DEFAULT '[]',              -- ["Good use of past tense","Rich vocabulary"]
    weaknesses      JSONB           DEFAULT '[]',              -- ["Needs work on th-sound","Article usage is inconsistent"]
    suggestions     JSONB           DEFAULT '[]',              -- [{"title":"Practice past tense","action":"..."}]
    feedback_summary TEXT,

    -- 对比
    previous_score  DECIMAL(5,2),                               -- 上次评测分数
    improvement     DECIMAL(5,2),                               -- 进步分差

    -- 评测元数据
    model_name      VARCHAR(100),
    eval_duration_ms INT,                                       -- AI评测耗时

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_eval_user ON evaluations(user_id, created_at DESC);
CREATE INDEX idx_eval_session ON evaluations(session_id);
CREATE INDEX idx_eval_type ON evaluations(user_id, eval_type);
```

---

### 3.6 学习报告

#### 3.6.1 `learning_reports` — 学习报告表

```sql
CREATE TABLE learning_reports (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),

    -- 报告周期
    period_type     VARCHAR(20)     NOT NULL,                   -- DAILY/WEEKLY/MONTHLY/SEMESTER
    period_start    DATE            NOT NULL,
    period_end      DATE            NOT NULL,

    -- 报告内容 (JSONB — 预计算存储)
    report_data     JSONB           NOT NULL,
    /*
    {
        "summary": "本周你完成了12次陪练...",
        "stats": {
            "total_sessions": 12,
            "total_minutes": 180,
            "total_messages": 245,
            "avg_score": 82.5,
            "score_change": 3.2
        },
        "dimension_breakdown": {...},
        "top_errors": [...],
        "top_vocabulary": [...],
        "achievements_unlocked": [...],
        "comparison_to_last_period": {...},
        "next_period_suggestions": [...]
    }
    */

    is_read         BOOLEAN         DEFAULT FALSE,
    generated_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    UNIQUE (user_id, period_type, period_start)
);

CREATE INDEX idx_report_user_period ON learning_reports(user_id, period_type, period_start DESC);
```

---

### 3.7 学习激励

#### 3.7.1 `check_ins` — 打卡记录

```sql
CREATE TABLE check_ins (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    check_in_date   DATE            NOT NULL,
    check_in_time   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    streak_count    INT             NOT NULL,
    reward_points   INT             DEFAULT 0,
    note            VARCHAR(300),

    UNIQUE (user_id, check_in_date)
);

CREATE INDEX idx_checkin_user_date ON check_ins(user_id, check_in_date DESC);
```

#### 3.7.2 `points_records` — 积分记录

```sql
CREATE TABLE points_records (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),

    points          INT             NOT NULL,                    -- 正数入账/负数消费
    balance_after   INT             NOT NULL,                    -- 变动后余额

    -- 来源/用途
    action_type     VARCHAR(30)     NOT NULL,                    -- CHECK_IN/COMPLETE_SESSION/PERFECT_SCORE/ACHIEVEMENT/PURCHASE_SCENE/EXCHANGE
    action_desc     VARCHAR(300),
    reference_id    BIGINT,                                      -- 关联业务ID

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_points_user ON points_records(user_id, created_at DESC);
CREATE INDEX idx_points_action ON points_records(action_type);
```

#### 3.7.3 `achievements` — 成就定义

```sql
CREATE TABLE achievements (
    id              BIGSERIAL       PRIMARY KEY,
    code            VARCHAR(50)     NOT NULL UNIQUE,             -- FIRST_SESSION/STREAK_7/VOCAB_100/PERFECT_PRONUNCIATION
    name            VARCHAR(200)    NOT NULL,
    name_en         VARCHAR(200),
    description     VARCHAR(500),
    icon_url        VARCHAR(500),
    category        VARCHAR(30)     NOT NULL,                    -- LEARNING/STREAK/VOCABULARY/ACCURACY/SOCIAL
    points_reward   INT             DEFAULT 0,
    is_secret       BOOLEAN         DEFAULT FALSE,               -- 隐藏成就
    condition_json  JSONB           NOT NULL,                    -- {"type":"session_count","threshold":10}

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);
```

#### 3.7.4 `user_achievements` — 用户成就

```sql
CREATE TABLE user_achievements (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    achievement_id  BIGINT          NOT NULL REFERENCES achievements(id),
    progress        JSONB           DEFAULT '{}',               -- {"current":7,"target":10}
    is_completed    BOOLEAN         DEFAULT FALSE,
    completed_at    TIMESTAMPTZ,
    notified        BOOLEAN         DEFAULT FALSE,              -- 是否已通知用户

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    UNIQUE (user_id, achievement_id)
);

CREATE INDEX idx_user_achieve_user ON user_achievements(user_id);
CREATE INDEX idx_user_achieve_completed ON user_achievements(user_id, is_completed);
```

---

### 3.8 家长与绑定

#### 3.8.1 `parent_student_bindings` — 家长学生绑定

```sql
CREATE TABLE parent_student_bindings (
    id              BIGSERIAL       PRIMARY KEY,
    parent_id       BIGINT          NOT NULL REFERENCES users(id),
    student_id      BIGINT          NOT NULL REFERENCES users(id),

    -- 状态
    binding_status  VARCHAR(20)     NOT NULL DEFAULT 'PENDING', -- PENDING/ACTIVE/REJECTED/UNBOUND
    relationship    VARCHAR(20),                                -- FATHER/MOTHER/GUARDIAN/OTHER

    -- 控制权限
    can_view_report     BOOLEAN     DEFAULT TRUE,
    can_set_time_limit  BOOLEAN     DEFAULT TRUE,
    can_manage_payment  BOOLEAN     DEFAULT TRUE,
    daily_time_limit_minutes INT     DEFAULT 60,
    monthly_budget_limit DECIMAL(10,2),

    requested_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    approved_at     TIMESTAMPTZ,
    unbounded_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    UNIQUE (parent_id, student_id)
);

CREATE INDEX idx_binding_parent ON parent_student_bindings(parent_id);
CREATE INDEX idx_binding_student ON parent_student_bindings(student_id);
CREATE INDEX idx_binding_status ON parent_student_bindings(binding_status);
```

---

### 3.9 班级系统 (教师端)

#### 3.9.1 `classes` — 班级表

```sql
CREATE TABLE classes (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(200)    NOT NULL,
    teacher_id      BIGINT          NOT NULL REFERENCES users(id),
    description     VARCHAR(500),
    grade_level     VARCHAR(10),
    invite_code     VARCHAR(20)     UNIQUE,                      -- 班级邀请码

    student_count   INT             DEFAULT 0,
    is_archived     BOOLEAN         DEFAULT FALSE,

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_classes_teacher ON classes(teacher_id) WHERE deleted_at IS NULL;
```

#### 3.9.2 `class_rosters` — 班级花名册

```sql
CREATE TABLE class_rosters (
    id              BIGSERIAL       PRIMARY KEY,
    class_id        BIGINT          NOT NULL REFERENCES classes(id),
    student_id      BIGINT          NOT NULL REFERENCES users(id),
    joined_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    left_at         TIMESTAMPTZ,

    UNIQUE (class_id, student_id)
);

CREATE INDEX idx_roster_class ON class_rosters(class_id);
CREATE INDEX idx_roster_student ON class_rosters(student_id);
```

#### 3.9.3 `class_assignments` — 班级任务

```sql
CREATE TABLE class_assignments (
    id              BIGSERIAL       PRIMARY KEY,
    class_id        BIGINT          NOT NULL REFERENCES classes(id),
    teacher_id      BIGINT          NOT NULL REFERENCES users(id),

    title           VARCHAR(300)    NOT NULL,
    description     TEXT,
    assignment_type VARCHAR(30)     NOT NULL,                    -- COACH_SESSION/READING/LISTENING/WRITING/CUSTOM
    scene_id        BIGINT          REFERENCES scene_templates(id),
    content_ref     VARCHAR(500),                                -- 引用内容 ID 或 URL

    due_date        TIMESTAMPTZ,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_assignment_class ON class_assignments(class_id);
CREATE INDEX idx_assignment_due ON class_assignments(due_date) WHERE deleted_at IS NULL AND due_date IS NOT NULL;
```

#### 3.9.4 `assignment_submissions` — 任务提交

```sql
CREATE TABLE assignment_submissions (
    id              BIGSERIAL       PRIMARY KEY,
    assignment_id   BIGINT          NOT NULL REFERENCES class_assignments(id),
    student_id      BIGINT          NOT NULL REFERENCES users(id),
    session_id      BIGINT          REFERENCES coach_sessions(id),

    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',  -- PENDING/SUBMITTED/GRADED/LATE
    score           DECIMAL(5,2),
    feedback        TEXT,
    submitted_at    TIMESTAMPTZ,
    graded_at       TIMESTAMPTZ,

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    UNIQUE (assignment_id, student_id)
);

CREATE INDEX idx_submission_assignment ON assignment_submissions(assignment_id);
CREATE INDEX idx_submission_student ON assignment_submissions(student_id);
```

---

### 3.10 内容资源

#### 3.10.1 `content_assets` — 内容资源表

```sql
CREATE TABLE content_assets (
    id              BIGSERIAL       PRIMARY KEY,
    asset_type      VARCHAR(30)     NOT NULL,                    -- READING/LISTENING/IMAGE/AUDIO/VIDEO
    title           VARCHAR(300),
    title_en        VARCHAR(300),
    description     TEXT,

    -- 文件
    file_url        VARCHAR(500),
    file_size       BIGINT,
    duration_seconds INT,                                        -- 音视频时长

    -- 内容
    transcript      TEXT,                                        -- 音频文本/字幕
    content_json    JSONB,                                       -- 结构化内容

    -- 分级
    grade_level     VARCHAR(10),
    difficulty      INT             DEFAULT 1,
    cefr_level      VARCHAR(4),

    -- 关联
    scene_id        BIGINT          REFERENCES scene_templates(id),

    -- 标签
    tags            JSONB           DEFAULT '[]',

    -- 统计
    view_count      BIGINT          DEFAULT 0,
    avg_rating      DECIMAL(3,2),

    is_published    BOOLEAN         DEFAULT FALSE,
    created_by      BIGINT          REFERENCES users(id),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_asset_type ON content_assets(asset_type) WHERE deleted_at IS NULL;
CREATE INDEX idx_asset_grade ON content_assets(grade_level, difficulty) WHERE deleted_at IS NULL;
CREATE INDEX idx_asset_scene ON content_assets(scene_id);
```

#### 3.10.2 `reading_passages` — 阅读理解文章

```sql
CREATE TABLE reading_passages (
    id              BIGSERIAL       PRIMARY KEY,
    title           VARCHAR(300)    NOT NULL,
    content         TEXT            NOT NULL,
    word_count      INT,
    source          VARCHAR(500),
    source_url      VARCHAR(500),

    grade_level     VARCHAR(10),
    difficulty      INT             DEFAULT 1,
    cefr_level      VARCHAR(4),

    -- 题目
    questions       JSONB           NOT NULL,
    /*
    [{
        "type": "multiple_choice",
        "question": "What is the main idea?",
        "options": ["A...", "B...", "C...", "D..."],
        "answer": "B",
        "explanation": "..."
    }]
    */

    tags            JSONB           DEFAULT '[]',
    is_published    BOOLEAN         DEFAULT FALSE,

    created_by      BIGINT          REFERENCES users(id),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_reading_grade ON reading_passages(grade_level, difficulty) WHERE deleted_at IS NULL;
```

#### 3.10.3 `writing_prompts` — 写作题目

```sql
CREATE TABLE writing_prompts (
    id              BIGSERIAL       PRIMARY KEY,
    title           VARCHAR(300)    NOT NULL,
    prompt          TEXT            NOT NULL,
    prompt_en       TEXT,
    word_limit_min  INT,
    word_limit_max  INT,
    time_limit_minutes INT,

    grade_level     VARCHAR(10),
    difficulty      INT             DEFAULT 1,
    cefr_level      VARCHAR(4),

    -- 评分标准
    scoring_rubric  JSONB,

    tags            JSONB           DEFAULT '[]',
    is_published    BOOLEAN         DEFAULT FALSE,

    created_by      BIGINT          REFERENCES users(id),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);
```

---

### 3.11 安全与审核

#### 3.11.1 `content_review_logs` — 内容审核日志

```sql
CREATE TABLE content_review_logs (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          REFERENCES users(id),
    session_id      BIGINT          REFERENCES coach_sessions(id),
    message_id      BIGINT,

    -- 审核内容
    review_target   VARCHAR(20)     NOT NULL,                    -- USER_INPUT/AI_OUTPUT
    content_type    VARCHAR(20)     NOT NULL,                    -- TEXT/AUDIO/IMAGE
    original_content TEXT,
    content_hash    VARCHAR(64),                                 -- SHA-256

    -- 审核结果
    review_source   VARCHAR(30)     NOT NULL,                    -- ALIYUN/YIDUN/INTERNAL_RULE/MANUAL
    review_result   VARCHAR(20)     NOT NULL,                    -- PASS/BLOCK/REVIEW/MODIFIED
    risk_level      VARCHAR(20),                                 -- LOW/MEDIUM/HIGH/CRITICAL
    risk_labels     JSONB           DEFAULT '[]',              -- ["politics","violence","porn","abuse"]

    -- 处理
    action_taken    VARCHAR(50),                                 -- ALLOWED/BLOCKED/REPLACED/FLAGGED
    modified_content TEXT,                                       -- 修改后的内容
    reviewer_id     BIGINT          REFERENCES users(id),        -- 人工审核员
    review_note     TEXT,

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_review_result ON content_review_logs(review_result);
CREATE INDEX idx_review_user ON content_review_logs(user_id, created_at DESC);
CREATE INDEX idx_review_created ON content_review_logs(created_at DESC);
```

#### 3.11.2 `audit_logs` — 审计日志 (大表，需分区)

```sql
CREATE TABLE audit_logs (
    id              BIGSERIAL,
    user_id         BIGINT,
    username        VARCHAR(100),
    user_role       VARCHAR(20),

    -- 操作
    action          VARCHAR(50)     NOT NULL,                    -- LOGIN/LOGOUT/CREATE_SESSION/DELETE_DATA/CHANGE_PERMISSION/...
    resource_type   VARCHAR(50),                                -- USER/SESSION/SCENE/CLASS/CONFIG
    resource_id     BIGINT,
    action_detail   JSONB           DEFAULT '{}',              -- 操作详情

    -- 上下文
    ip_address      VARCHAR(50),
    user_agent      VARCHAR(500),
    session_id      VARCHAR(200),
    trace_id        VARCHAR(100),                               -- 分布式追踪ID

    -- 结果
    result          VARCHAR(20)     NOT NULL DEFAULT 'SUCCESS', -- SUCCESS/FAILURE/DENIED

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

CREATE TABLE audit_logs_y2026m06 PARTITION OF audit_logs
    FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');

CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_resource ON audit_logs(resource_type, resource_id);
CREATE INDEX idx_audit_created ON audit_logs(created_at DESC);
```

#### 3.11.3 `sensitive_words` — 敏感词库

```sql
CREATE TABLE sensitive_words (
    id              BIGSERIAL       PRIMARY KEY,
    word            VARCHAR(200)    NOT NULL UNIQUE,
    category        VARCHAR(50)     NOT NULL,                    -- POLITICS/PORN/VIOLENCE/ABUSE/HATE/SPAM
    risk_level      VARCHAR(20)     NOT NULL DEFAULT 'HIGH',    -- LOW/MEDIUM/HIGH/CRITICAL
    is_regex        BOOLEAN         DEFAULT FALSE,              -- 是否正则表达式
    is_active       BOOLEAN         DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sw_category ON sensitive_words(category);
CREATE INDEX idx_sw_active ON sensitive_words(is_active);
```

---

### 3.12 系统配置

#### 3.12.1 `system_configs` — 系统配置

```sql
CREATE TABLE system_configs (
    id              BIGSERIAL       PRIMARY KEY,
    config_key      VARCHAR(100)    NOT NULL UNIQUE,
    config_value    TEXT            NOT NULL,
    config_type     VARCHAR(20)     NOT NULL DEFAULT 'STRING',    -- STRING/NUMBER/BOOLEAN/JSON
    description     VARCHAR(500),
    is_encrypted    BOOLEAN         DEFAULT FALSE,               -- 是否加密存储
    is_public       BOOLEAN         DEFAULT FALSE,               -- 是否前端可读
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);
```

#### 3.12.2 `ai_model_configs` — AI 模型配置

```sql
CREATE TABLE ai_model_configs (
    id              BIGSERIAL       PRIMARY KEY,
    model_code      VARCHAR(50)     NOT NULL UNIQUE,             -- QWEN-TURBO/GPT-4O/CLAUDE-OPUS/DEEPSEEK-V3
    model_name      VARCHAR(100)    NOT NULL,
    provider        VARCHAR(50)     NOT NULL,                    -- ALIBABA/OPENAI/ANTHROPIC/DEEPSEEK/LOCAL

    -- 连接配置
    api_base_url    VARCHAR(500),
    api_key_encrypted VARCHAR(500),
    model_version   VARCHAR(50),

    -- 参数默认值
    default_temperature DECIMAL(3,2) DEFAULT 0.7,
    default_max_tokens  INT DEFAULT 4096,
    default_top_p       DECIMAL(3,2) DEFAULT 0.9,

    -- 用途
    supported_tasks JSONB           DEFAULT '[]',              -- ["CHAT","CORRECTION","EVALUATION","TRANSLATION"]
    priority        INT             DEFAULT 0,                  -- 优先级 (Fallback 顺序)

    -- 限流
    rate_limit_per_minute INT,
    max_daily_tokens BIGINT,

    -- 状态
    is_active       BOOLEAN         DEFAULT TRUE,
    health_status   VARCHAR(20)     DEFAULT 'UNKNOWN',          -- HEALTHY/DEGRADED/DOWN

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);
```

#### 3.12.3 `ai_usage_logs` — AI 调用日志

```sql
CREATE TABLE ai_usage_logs (
    id              BIGSERIAL,
    user_id         BIGINT,
    session_id      BIGINT,
    model_code      VARCHAR(50)     NOT NULL,
    task_type       VARCHAR(30)     NOT NULL,                    -- CHAT/CORRECTION/EVALUATION/TRANSLATION/ASR/TTS

    -- Token 统计
    prompt_tokens   INT,
    completion_tokens INT,
    total_tokens    INT,

    -- 性能
    latency_ms      INT,
    is_fallback     BOOLEAN         DEFAULT FALSE,              -- 是否走了 Fallback

    -- 成本 (可选)
    cost_usd        DECIMAL(10,6),

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

CREATE TABLE ai_usage_logs_y2026m06 PARTITION OF ai_usage_logs
    FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');

CREATE INDEX idx_ai_usage_user ON ai_usage_logs(user_id);
CREATE INDEX idx_ai_usage_model ON ai_usage_logs(model_code);
CREATE INDEX idx_ai_usage_date ON ai_usage_logs(created_at DESC);
```

---

### 3.13 通知系统

#### 3.13.1 `notifications` — 通知表

```sql
CREATE TABLE notifications (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),

    -- 通知内容
    title           VARCHAR(300)    NOT NULL,
    content         TEXT,
    notification_type VARCHAR(30)   NOT NULL,                    -- SYSTEM/ACHIEVEMENT/REMINDER/REPORT/WARNING/MARKETING

    -- 关联
    reference_type  VARCHAR(50),
    reference_id    BIGINT,

    -- 状态
    is_read         BOOLEAN         DEFAULT FALSE,
    read_at         TIMESTAMPTZ,

    -- 推送
    push_channel    JSONB           DEFAULT '[]',              -- ["IN_APP","SMS","WECHAT"]

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notif_user_unread ON notifications(user_id, is_read, created_at DESC);
CREATE INDEX idx_notif_created ON notifications(created_at DESC);
```

---

### 3.14 学习计划

#### 3.14.1 `learning_plans` — 学习计划

```sql
CREATE TABLE learning_plans (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),

    name            VARCHAR(200)    NOT NULL,
    description     TEXT,
    plan_type       VARCHAR(30)     NOT NULL DEFAULT 'AI_GENERATED', -- AI_GENERATED/MANUAL/TEACHER_ASSIGNED

    -- 时间范围
    start_date      DATE            NOT NULL,
    end_date        DATE            NOT NULL,
    target_level    VARCHAR(4),                                  -- 目标CEFR等级

    -- 进度
    total_items     INT             DEFAULT 0,
    completed_items INT             DEFAULT 0,
    is_active       BOOLEAN         DEFAULT TRUE,

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_plan_user ON learning_plans(user_id, is_active);
```

#### 3.14.2 `learning_plan_items` — 学习计划项

```sql
CREATE TABLE learning_plan_items (
    id              BIGSERIAL       PRIMARY KEY,
    plan_id         BIGINT          NOT NULL REFERENCES learning_plans(id),
    user_id         BIGINT          NOT NULL REFERENCES users(id),

    item_type       VARCHAR(30)     NOT NULL,                    -- COACH_SESSION/READING/LISTENING/WRITING/VOCAB_REVIEW
    item_ref_id     BIGINT,                                      -- 引用内容ID (scene_id/reading_id等)
    item_name       VARCHAR(300),

    scheduled_date  DATE            NOT NULL,
    estimated_minutes INT,
    is_completed    BOOLEAN         DEFAULT FALSE,
    completed_at    TIMESTAMPTZ,
    points_reward   INT             DEFAULT 0,

    -- 顺序
    sort_order      INT             DEFAULT 0,

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_plan_item_plan ON learning_plan_items(plan_id, sort_order);
CREATE INDEX idx_plan_item_user_date ON learning_plan_items(user_id, scheduled_date);
```

---

## 4. 索引策略

### 4.1 索引设计原则

1. **WHERE 条件列** → B-Tree 索引
2. **JOIN 外键列** → B-Tree 索引
3. **排序列 (ORDER BY)** → B-Tree 索引
4. **JSONB 查询** → GIN 索引
5. **全文搜索** → GIN + tsvector
6. **部分索引** → WHERE 条件过滤 (软删除、状态筛选)
7. **复合索引** → 遵循最左前缀原则

### 4.2 复合索引总览

| 表 | 索引 | 覆盖查询场景 |
|----|------|-------------|
| `users` | `(role, status)` | 按角色筛选在线用户 |
| `coach_sessions` | `(user_id, created_at DESC)` | 用户会话列表 (最常用) |
| `coach_sessions` | `(user_id, status)` | 用户活跃会话查询 |
| `corrections` | `(user_id, error_type)` | 按错误类型统计 |
| `corrections` | `(user_id, created_at DESC)` | 用户纠错历史 |
| `evaluations` | `(user_id, eval_type)` | 按评测类型查询 |
| `error_records` | `(user_id, mastery_status)` | 待复习错误查询 |
| `user_vocabulary` | `(user_id, status)` | 按掌握状态筛选 |

### 4.3 性能注意事项

- **避免过多索引**：写入性能下降，每个表建议不超过 6-8 个索引
- **定期 ANALYZE**：通过 pg_cron 定时执行 `ANALYZE` 更新统计信息
- **索引监控**：定期检查 `pg_stat_user_indexes` 排查未使用索引
- **覆盖索引**：高频查询考虑 INCLUDE 子句覆盖 SELECT 列

---

## 5. 分区策略

### 5.1 分区表

| 表 | 分区键 | 分区粒度 | 原因 |
|----|--------|---------|------|
| `coach_messages` | `created_at` | 月 | 消息量大，按时间范围查询为主 |
| `audit_logs` | `created_at` | 月 | 日志量大，按时间范围查询为主 |
| `ai_usage_logs` | `created_at` | 月 | Token 消耗记录量大 |

### 5.2 自动分区维护

```sql
-- 通过 pg_cron 或应用层定时任务执行

-- 1. 提前创建下月分区
CREATE TABLE coach_messages_y2026m08 PARTITION OF coach_messages
    FOR VALUES FROM ('2026-08-01') TO ('2026-09-01');

-- 2. 归档/删除旧分区 (保留 12 个月)
DROP TABLE IF EXISTS coach_messages_y2025m06;

-- 3. 定期对活跃分区执行 VACUUM ANALYZE
```

### 5.3 数据保留策略

| 表 | 保留期限 | 处理方式 |
|----|---------|---------|
| `coach_messages` | 12 个月 | 到期分区删除 |
| `audit_logs` | 24 个月 | 到期分区归档后删除 |
| `ai_usage_logs` | 12 个月 | 到期分区删除 |
| `content_review_logs` | 24 个月 | 到期归档 |
| `notifications` | 6 个月 | 定时清理已读通知 |
| 语音文件 (OSS) | 6 个月 | 定期清理 + 用户主动删除 |

---

## 6. 数据字典

### 6.1 枚举值汇总

| 字段 | 枚举值 |
|------|--------|
| `users.role` | `STUDENT`, `PARENT`, `TEACHER`, `ADMIN`, `SUPER_ADMIN` |
| `users.grade_level` | `ELEMENTARY`, `JUNIOR`, `SENIOR`, `ADULT` |
| `users.cefr_level` | `A1`, `A2`, `B1`, `B2`, `C1`, `C2` |
| `users.status` | `ACTIVE`, `INACTIVE`, `SUSPENDED`, `BANNED` |
| `coach_sessions.session_type` | `SCENE`, `FREE`, `CUSTOM`, `EXAM` |
| `coach_sessions.status` | `ACTIVE`, `PAUSED`, `COMPLETED`, `CANCELLED` |
| `coach_messages.role` | `USER`, `AI`, `SYSTEM` |
| `corrections.error_type` | `GRAMMAR`, `PRONUNCIATION`, `VOCABULARY`, `LOGIC`, `COLLOCATION` |
| `corrections.correction_strategy` | `IMMEDIATE`, `DELAYED`, `SKIPPED`, `REVIEW_LATER` |
| `evaluations.eval_type` | `SESSION`, `DAILY`, `WEEKLY`, `DIAGNOSTIC`, `EXAM_MOCK` |
| `learning_reports.period_type` | `DAILY`, `WEEKLY`, `MONTHLY`, `SEMESTER` |
| `user_vocabulary.status` | `NEW`, `LEARNING`, `REVIEWING`, `KNOWN`, `MASTERED` |
| `content_review_logs.review_target` | `USER_INPUT`, `AI_OUTPUT` |
| `content_review_logs.review_result` | `PASS`, `BLOCK`, `REVIEW`, `MODIFIED` |

### 6.2 JSONB 列汇总

| 表 | 列 | 示例 |
|----|-----|------|
| `learner_profiles` | `weaknesses` | `{"grammar":0.3,"pronunciation":0.6}` |
| `scene_templates` | `roles` | `[{"name":"服务员","name_en":"Waiter"}]` |
| `scene_templates` | `keywords` | `[{"word":"hamburger","translation":"汉堡包"}]` |
| `evaluations` | `dimension_scores` | `{"grammar":85,"pronunciation":72}` |
| `learning_reports` | `report_data` | 完整报告 JSON 结构 |
| `corrections` | — | (纯文本字段，不存 JSONB) |

### 6.3 加密字段汇总

| 表 | 字段 | 加密算法 | 说明 |
|----|------|---------|------|
| `users` | `phone_encrypted` | AES-256-GCM | 手机号加密存储 |
| `users` | `id_card_encrypted` | AES-256-GCM | 身份证号加密 |
| `ai_model_configs` | `api_key_encrypted` | AES-256-GCM | API Key 加密 |

---

## 7. 初始化脚本

### 7.1 数据库创建

```sql
-- 创建数据库
CREATE DATABASE yunwu_english
    WITH
    ENCODING = 'UTF8'
    LC_COLLATE = 'zh_CN.UTF-8'
    LC_CTYPE = 'zh_CN.UTF-8'
    TEMPLATE = template0
    CONNECTION LIMIT = 200;

-- 连接数据库后设置
ALTER DATABASE yunwu_english SET timezone TO 'UTC';
ALTER DATABASE yunwu_english SET default_transaction_isolation TO 'read committed';
```

### 7.2 扩展安装

```sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";       -- UUID 生成
CREATE EXTENSION IF NOT EXISTS "pgcrypto";        -- 加密函数
CREATE EXTENSION IF NOT EXISTS "pg_trgm";         -- 模糊搜索
CREATE EXTENSION IF NOT EXISTS "btree_gin";       -- GIN 索引支持
```

### 7.3 初始数据

```sql
-- 超级管理员 (密码: admin123! → bcrypt hash)
INSERT INTO users (phone, nickname, role, status, real_name_verified)
VALUES ('13800000000', '超级管理员', 'SUPER_ADMIN', 'ACTIVE', TRUE);

-- 系统配置初始化
INSERT INTO system_configs (config_key, config_value, config_type, description) VALUES
('max_session_rounds', '50', 'NUMBER', '单次会话最大轮次'),
('max_daily_sessions', '20', 'NUMBER', '单用户每日最大会话数'),
('max_input_length', '2000', 'NUMBER', '用户输入最大字符数'),
('content_review_enabled', 'true', 'BOOLEAN', '是否启用内容审核'),
('default_daily_limit_minutes', '60', 'NUMBER', '默认每日时长限制(分钟)'),
('points_per_session', '10', 'NUMBER', '每次完成陪练基础积分'),
('points_per_checkin', '5', 'NUMBER', '每日打卡积分');

-- AI 模型初始配置
INSERT INTO ai_model_configs (model_code, model_name, provider, supported_tasks, priority, is_active) VALUES
('qwen-turbo', '通义千问 Turbo', 'ALIBABA', '["CHAT","TRANSLATION"]', 1, TRUE),
('qwen-max', '通义千问 Max', 'ALIBABA', '["CHAT","EVALUATION","CORRECTION"]', 2, TRUE),
('deepseek-v3', 'DeepSeek V3', 'DEEPSEEK', '["CHAT","CORRECTION","EVALUATION"]', 3, TRUE);
```

---

> **变更记录**

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|---------|------|
| v1.0 | 2026-06-06 | 初始版本，完整数据库设计 | 罗淇育 |
