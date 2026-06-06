-- ============================================
-- 云悟英语 (YunWu English) 数据库初始化脚本
-- PostgreSQL 16+
-- 版本: v1.0
-- 日期: 2026-06-06
-- ============================================

-- 扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
CREATE EXTENSION IF NOT EXISTS "btree_gin";

-- ============================================
-- 1. 用户与认证
-- ============================================

-- 1.1 用户核心表
CREATE TABLE users (
    id                  BIGSERIAL       PRIMARY KEY,
    phone               VARCHAR(20)     NOT NULL,
    phone_encrypted     VARCHAR(200),
    email               VARCHAR(200),
    password_hash       VARCHAR(255),
    nickname            VARCHAR(100),
    avatar_url          VARCHAR(500),
    role                VARCHAR(20)     NOT NULL DEFAULT 'STUDENT',
    grade_level         VARCHAR(10),
    grade_detail        VARCHAR(20),
    cefr_level          VARCHAR(4),
    status              VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    real_name_verified  BOOLEAN         NOT NULL DEFAULT FALSE,
    real_name           VARCHAR(100),
    id_card_encrypted   VARCHAR(500),
    daily_limit_minutes INT             DEFAULT 60,
    voice_preference    VARCHAR(10)     DEFAULT 'FEMALE',
    speech_rate         DECIMAL(3,2)   DEFAULT 1.00,
    ui_font_scale       DECIMAL(3,2)   DEFAULT 1.00,
    theme_mode          VARCHAR(10)     DEFAULT 'LIGHT',
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    last_login_at       TIMESTAMPTZ,
    last_login_ip       VARCHAR(50),
    deleted_at          TIMESTAMPTZ
);

CREATE UNIQUE INDEX uk_users_phone       ON users(phone) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_role              ON users(role) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_grade_level       ON users(grade_level) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_cefr_level        ON users(cefr_level) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_status            ON users(status);
CREATE INDEX idx_users_created_at        ON users(created_at);

-- 1.2 认证令牌表
CREATE TABLE user_auth_tokens (
    id                  BIGSERIAL       PRIMARY KEY,
    user_id             BIGINT          NOT NULL REFERENCES users(id),
    access_token        VARCHAR(500)    NOT NULL,
    refresh_token       VARCHAR(500)    NOT NULL,
    token_type          VARCHAR(20)     NOT NULL DEFAULT 'BEARER',
    device_info         VARCHAR(500),
    ip_address          VARCHAR(50),
    expires_at          TIMESTAMPTZ     NOT NULL,
    refresh_expires_at  TIMESTAMPTZ     NOT NULL,
    revoked_at          TIMESTAMPTZ,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_auth_tokens_user      ON user_auth_tokens(user_id);
CREATE INDEX idx_auth_tokens_access    ON user_auth_tokens(access_token);
CREATE INDEX idx_auth_tokens_refresh   ON user_auth_tokens(refresh_token);

-- 1.3 验证码表
CREATE TABLE user_verification_codes (
    id              BIGSERIAL       PRIMARY KEY,
    phone           VARCHAR(20)     NOT NULL,
    code            VARCHAR(10)     NOT NULL,
    purpose         VARCHAR(30)     NOT NULL,
    ip_address      VARCHAR(50),
    expires_at      TIMESTAMPTZ     NOT NULL,
    verified_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_vcode_phone_purpose ON user_verification_codes(phone, purpose);
CREATE INDEX idx_vcode_expires        ON user_verification_codes(expires_at);

-- ============================================
-- 2. 学习档案
-- ============================================

-- 2.1 学习档案表
CREATE TABLE learner_profiles (
    id                         BIGSERIAL       PRIMARY KEY,
    user_id                    BIGINT          NOT NULL UNIQUE REFERENCES users(id),
    estimated_vocabulary_size  INT             DEFAULT 0,
    cefr_level                 VARCHAR(4),
    china_standard_level       VARCHAR(10),
    weaknesses                 JSONB           DEFAULT '{}',
    total_learning_days        INT             DEFAULT 0,
    total_session_count        INT             DEFAULT 0,
    total_learning_minutes     INT             DEFAULT 0,
    total_words_spoken         INT             DEFAULT 0,
    avg_accuracy_rate          DECIMAL(5,2)   DEFAULT 0.00,
    streak_days                INT             DEFAULT 0,
    max_streak_days            INT             DEFAULT 0,
    last_learning_date         DATE,
    preferred_topics           JSONB           DEFAULT '[]',
    learning_goal              VARCHAR(500),
    created_at                 TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at                 TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_learner_user     ON learner_profiles(user_id);
CREATE INDEX idx_learner_cefr     ON learner_profiles(cefr_level);
CREATE INDEX idx_learner_streak   ON learner_profiles(streak_days DESC);

-- 2.2 等级变化历史
CREATE TABLE learner_level_history (
    id                  BIGSERIAL       PRIMARY KEY,
    user_id             BIGINT          NOT NULL REFERENCES users(id),
    previous_cefr       VARCHAR(4),
    new_cefr            VARCHAR(4)      NOT NULL,
    change_reason       VARCHAR(50),
    source_session_id   BIGINT,
    note                VARCHAR(500),
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_level_history_user ON learner_level_history(user_id, created_at DESC);

-- ============================================
-- 3. 陪练核心
-- ============================================

-- 3.1 场景模板表
CREATE TABLE scene_templates (
    id                  BIGSERIAL       PRIMARY KEY,
    name                VARCHAR(200)    NOT NULL,
    name_en             VARCHAR(200),
    description         VARCHAR(1000),
    description_en      VARCHAR(1000),
    category            VARCHAR(30)     NOT NULL,
    grade_level         VARCHAR(10),
    difficulty          INT             NOT NULL DEFAULT 1,
    cefr_level          VARCHAR(4),
    roles               JSONB           NOT NULL,
    keywords            JSONB           DEFAULT '[]',
    target_sentences    JSONB           DEFAULT '[]',
    opening_dialogue    JSONB,
    max_rounds          INT             DEFAULT 30,
    estimated_duration  INT             DEFAULT 15,
    is_published        BOOLEAN         NOT NULL DEFAULT FALSE,
    version             INT             NOT NULL DEFAULT 1,
    tags                JSONB           DEFAULT '[]',
    created_by          BIGINT          REFERENCES users(id),
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMPTZ
);

CREATE INDEX idx_scene_category    ON scene_templates(category) WHERE deleted_at IS NULL;
CREATE INDEX idx_scene_grade       ON scene_templates(grade_level) WHERE deleted_at IS NULL;
CREATE INDEX idx_scene_difficulty  ON scene_templates(difficulty) WHERE deleted_at IS NULL;
CREATE INDEX idx_scene_published   ON scene_templates(is_published) WHERE deleted_at IS NULL;
CREATE INDEX idx_scene_tags        ON scene_templates USING GIN (tags);

-- 3.2 陪练会话表
CREATE TABLE coach_sessions (
    id                      BIGSERIAL       PRIMARY KEY,
    user_id                 BIGINT          NOT NULL REFERENCES users(id),
    scene_id                BIGINT          REFERENCES scene_templates(id),
    session_type            VARCHAR(20)     NOT NULL DEFAULT 'SCENE',
    title                   VARCHAR(300),
    status                  VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    message_count           INT             DEFAULT 0,
    user_message_count      INT             DEFAULT 0,
    ai_message_count        INT             DEFAULT 0,
    correction_count        INT             DEFAULT 0,
    total_tokens_used       BIGINT          DEFAULT 0,
    started_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    ended_at                TIMESTAMPTZ,
    duration_seconds        INT,
    difficulty_snapshot     INT,
    cefr_level_snapshot     VARCHAR(4),
    metadata                JSONB           DEFAULT '{}',
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sessions_user    ON coach_sessions(user_id, created_at DESC);
CREATE INDEX idx_sessions_scene   ON coach_sessions(scene_id);
CREATE INDEX idx_sessions_status  ON coach_sessions(status);
CREATE INDEX idx_sessions_date    ON coach_sessions(created_at DESC);

-- 3.3 会话消息表 (分区表)
CREATE TABLE coach_messages (
    id                  BIGSERIAL,
    session_id          BIGINT          NOT NULL,
    role                VARCHAR(10)     NOT NULL,
    content             TEXT            NOT NULL,
    content_type        VARCHAR(20)     DEFAULT 'TEXT',
    audio_url           VARCHAR(500),
    audio_duration      INT,
    model_name          VARCHAR(100),
    prompt_tokens       INT,
    completion_tokens   INT,
    latency_ms          INT,
    has_correction      BOOLEAN         DEFAULT FALSE,
    sequence_num        INT             NOT NULL,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- 初始分区
CREATE TABLE coach_messages_y2026m06 PARTITION OF coach_messages
    FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');
CREATE TABLE coach_messages_y2026m07 PARTITION OF coach_messages
    FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');
CREATE TABLE coach_messages_y2026m08 PARTITION OF coach_messages
    FOR VALUES FROM ('2026-08-01') TO ('2026-09-01');

CREATE INDEX idx_messages_session   ON coach_messages(session_id, sequence_num);
CREATE INDEX idx_messages_created   ON coach_messages(created_at);

-- 3.4 纠错记录表
CREATE TABLE corrections (
    id                      BIGSERIAL       PRIMARY KEY,
    message_id              BIGINT          NOT NULL,
    session_id              BIGINT          NOT NULL,
    user_id                 BIGINT          NOT NULL,
    error_type              VARCHAR(30)     NOT NULL,
    error_subtype           VARCHAR(50),
    severity                VARCHAR(10)     NOT NULL DEFAULT 'MEDIUM',
    original_text           TEXT            NOT NULL,
    error_span              VARCHAR(500),
    corrected_text          TEXT            NOT NULL,
    explanation             TEXT,
    explanation_en          TEXT,
    improvement_tip         TEXT,
    related_rule            VARCHAR(500),
    correction_strategy     VARCHAR(20)     DEFAULT 'IMMEDIATE',
    was_reviewed            BOOLEAN         DEFAULT FALSE,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_corrections_message    ON corrections(message_id);
CREATE INDEX idx_corrections_session    ON corrections(session_id);
CREATE INDEX idx_corrections_user_type  ON corrections(user_id, error_type);
CREATE INDEX idx_corrections_user_date  ON corrections(user_id, created_at DESC);

-- ============================================
-- 4. 错误追踪与词汇
-- ============================================

-- 4.1 错误汇总记录
CREATE TABLE error_records (
    id                  BIGSERIAL       PRIMARY KEY,
    user_id             BIGINT          NOT NULL REFERENCES users(id),
    error_type          VARCHAR(30)     NOT NULL,
    error_subtype       VARCHAR(50),
    error_pattern       VARCHAR(500),
    total_count         INT             DEFAULT 1,
    correct_count       INT             DEFAULT 0,
    last_error_at       TIMESTAMPTZ,
    last_correct_at     TIMESTAMPTZ,
    mastery_status      VARCHAR(20)     DEFAULT 'LEARNING',
    next_review_at      TIMESTAMPTZ,
    review_count        INT             DEFAULT 0,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, error_type, error_subtype, error_pattern)
);

CREATE INDEX idx_error_records_user     ON error_records(user_id);
CREATE INDEX idx_error_records_review   ON error_records(next_review_at)
    WHERE mastery_status IN ('LEARNING', 'REVIEWING');

-- 4.2 用户词汇表
CREATE TABLE user_vocabulary (
    id                  BIGSERIAL       PRIMARY KEY,
    user_id             BIGINT          NOT NULL REFERENCES users(id),
    word                VARCHAR(200)    NOT NULL,
    word_lower          VARCHAR(200)    NOT NULL,
    translation         VARCHAR(500),
    status              VARCHAR(20)     NOT NULL DEFAULT 'NEW',
    seen_count          INT             DEFAULT 1,
    used_count          INT             DEFAULT 0,
    error_count         INT             DEFAULT 0,
    correct_count       INT             DEFAULT 0,
    first_seen_at       TIMESTAMPTZ     DEFAULT NOW(),
    last_seen_at        TIMESTAMPTZ     DEFAULT NOW(),
    last_reviewed_at    TIMESTAMPTZ,
    next_review_at      TIMESTAMPTZ,
    source_session_id   BIGINT,
    source_type         VARCHAR(30),
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, word_lower)
);

CREATE INDEX idx_user_vocab_user     ON user_vocabulary(user_id);
CREATE INDEX idx_user_vocab_status   ON user_vocabulary(user_id, status);
CREATE INDEX idx_user_vocab_review   ON user_vocabulary(user_id, next_review_at)
    WHERE status IN ('LEARNING', 'REVIEWING');

-- 4.3 公共词汇库
CREATE TABLE vocabulary_library (
    id                      BIGSERIAL       PRIMARY KEY,
    word                    VARCHAR(200)    NOT NULL UNIQUE,
    word_lower              VARCHAR(200)    NOT NULL UNIQUE,
    pronunciation           VARCHAR(200),
    pronunciation_audio_url VARCHAR(500),
    translation             VARCHAR(500)    NOT NULL,
    part_of_speech          VARCHAR(50),
    definition_en           TEXT,
    cefr_level              VARCHAR(4),
    china_grade             VARCHAR(20),
    difficulty              INT             DEFAULT 1,
    example_sentences       JSONB           DEFAULT '[]',
    common_collocations     JSONB           DEFAULT '[]',
    exam_tags               JSONB           DEFAULT '[]',
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_vocab_lib_cefr  ON vocabulary_library(cefr_level);
CREATE INDEX idx_vocab_lib_grade ON vocabulary_library(china_grade);
CREATE INDEX idx_vocab_lib_exam  ON vocabulary_library USING GIN (exam_tags);

-- ============================================
-- 5. 评测系统
-- ============================================

CREATE TABLE evaluations (
    id                  BIGSERIAL       PRIMARY KEY,
    user_id             BIGINT          NOT NULL REFERENCES users(id),
    session_id          BIGINT          REFERENCES coach_sessions(id),
    eval_type           VARCHAR(30)     NOT NULL,
    dimension_scores    JSONB           NOT NULL,
    overall_score       DECIMAL(5,2)    NOT NULL,
    strengths           JSONB           DEFAULT '[]',
    weaknesses          JSONB           DEFAULT '[]',
    suggestions         JSONB           DEFAULT '[]',
    feedback_summary    TEXT,
    previous_score      DECIMAL(5,2),
    improvement         DECIMAL(5,2),
    model_name          VARCHAR(100),
    eval_duration_ms    INT,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_eval_user       ON evaluations(user_id, created_at DESC);
CREATE INDEX idx_eval_session    ON evaluations(session_id);
CREATE INDEX idx_eval_type       ON evaluations(user_id, eval_type);

-- ============================================
-- 6. 学习报告
-- ============================================

CREATE TABLE learning_reports (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    period_type     VARCHAR(20)     NOT NULL,
    period_start    DATE            NOT NULL,
    period_end      DATE            NOT NULL,
    report_data     JSONB           NOT NULL,
    is_read         BOOLEAN         DEFAULT FALSE,
    generated_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, period_type, period_start)
);

CREATE INDEX idx_report_user_period ON learning_reports(user_id, period_type, period_start DESC);

-- ============================================
-- 7. 学习激励
-- ============================================

-- 7.1 打卡记录
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

-- 7.2 积分记录
CREATE TABLE points_records (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    points          INT             NOT NULL,
    balance_after   INT             NOT NULL,
    action_type     VARCHAR(30)     NOT NULL,
    action_desc     VARCHAR(300),
    reference_id    BIGINT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_points_user      ON points_records(user_id, created_at DESC);
CREATE INDEX idx_points_action    ON points_records(action_type);

-- 7.3 成就定义
CREATE TABLE achievements (
    id              BIGSERIAL       PRIMARY KEY,
    code            VARCHAR(50)     NOT NULL UNIQUE,
    name            VARCHAR(200)    NOT NULL,
    name_en         VARCHAR(200),
    description     VARCHAR(500),
    icon_url        VARCHAR(500),
    category        VARCHAR(30)     NOT NULL,
    points_reward   INT             DEFAULT 0,
    is_secret       BOOLEAN         DEFAULT FALSE,
    condition_json  JSONB           NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- 7.4 用户成就
CREATE TABLE user_achievements (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    achievement_id  BIGINT          NOT NULL REFERENCES achievements(id),
    progress        JSONB           DEFAULT '{}',
    is_completed    BOOLEAN         DEFAULT FALSE,
    completed_at    TIMESTAMPTZ,
    notified        BOOLEAN         DEFAULT FALSE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, achievement_id)
);

CREATE INDEX idx_user_achieve_user       ON user_achievements(user_id);
CREATE INDEX idx_user_achieve_completed  ON user_achievements(user_id, is_completed);

-- ============================================
-- 8. 家长与绑定
-- ============================================

CREATE TABLE parent_student_bindings (
    id                          BIGSERIAL       PRIMARY KEY,
    parent_id                   BIGINT          NOT NULL REFERENCES users(id),
    student_id                  BIGINT          NOT NULL REFERENCES users(id),
    binding_status              VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    relationship                VARCHAR(20),
    can_view_report             BOOLEAN         DEFAULT TRUE,
    can_set_time_limit          BOOLEAN         DEFAULT TRUE,
    can_manage_payment          BOOLEAN         DEFAULT TRUE,
    daily_time_limit_minutes    INT             DEFAULT 60,
    monthly_budget_limit        DECIMAL(10,2),
    requested_at                TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    approved_at                 TIMESTAMPTZ,
    unbounded_at                TIMESTAMPTZ,
    created_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    UNIQUE (parent_id, student_id)
);

CREATE INDEX idx_binding_parent   ON parent_student_bindings(parent_id);
CREATE INDEX idx_binding_student  ON parent_student_bindings(student_id);
CREATE INDEX idx_binding_status   ON parent_student_bindings(binding_status);

-- ============================================
-- 9. 班级系统
-- ============================================

CREATE TABLE classes (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(200)    NOT NULL,
    teacher_id      BIGINT          NOT NULL REFERENCES users(id),
    description     VARCHAR(500),
    grade_level     VARCHAR(10),
    invite_code     VARCHAR(20)     UNIQUE,
    student_count   INT             DEFAULT 0,
    is_archived     BOOLEAN         DEFAULT FALSE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_classes_teacher ON classes(teacher_id) WHERE deleted_at IS NULL;

CREATE TABLE class_rosters (
    id              BIGSERIAL       PRIMARY KEY,
    class_id        BIGINT          NOT NULL REFERENCES classes(id),
    student_id      BIGINT          NOT NULL REFERENCES users(id),
    joined_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    left_at         TIMESTAMPTZ,
    UNIQUE (class_id, student_id)
);

CREATE INDEX idx_roster_class    ON class_rosters(class_id);
CREATE INDEX idx_roster_student  ON class_rosters(student_id);

CREATE TABLE class_assignments (
    id              BIGSERIAL       PRIMARY KEY,
    class_id        BIGINT          NOT NULL REFERENCES classes(id),
    teacher_id      BIGINT          NOT NULL REFERENCES users(id),
    title           VARCHAR(300)    NOT NULL,
    description     TEXT,
    assignment_type VARCHAR(30)     NOT NULL,
    scene_id        BIGINT          REFERENCES scene_templates(id),
    content_ref     VARCHAR(500),
    due_date        TIMESTAMPTZ,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_assignment_class ON class_assignments(class_id);
CREATE INDEX idx_assignment_due   ON class_assignments(due_date)
    WHERE deleted_at IS NULL AND due_date IS NOT NULL;

CREATE TABLE assignment_submissions (
    id              BIGSERIAL       PRIMARY KEY,
    assignment_id   BIGINT          NOT NULL REFERENCES class_assignments(id),
    student_id      BIGINT          NOT NULL REFERENCES users(id),
    session_id      BIGINT          REFERENCES coach_sessions(id),
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    score           DECIMAL(5,2),
    feedback        TEXT,
    submitted_at    TIMESTAMPTZ,
    graded_at       TIMESTAMPTZ,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    UNIQUE (assignment_id, student_id)
);

CREATE INDEX idx_submission_assignment ON assignment_submissions(assignment_id);
CREATE INDEX idx_submission_student    ON assignment_submissions(student_id);

-- ============================================
-- 10. 内容资源
-- ============================================

CREATE TABLE content_assets (
    id                  BIGSERIAL       PRIMARY KEY,
    asset_type          VARCHAR(30)     NOT NULL,
    title               VARCHAR(300),
    title_en            VARCHAR(300),
    description         TEXT,
    file_url            VARCHAR(500),
    file_size           BIGINT,
    duration_seconds    INT,
    transcript          TEXT,
    content_json        JSONB,
    grade_level         VARCHAR(10),
    difficulty          INT             DEFAULT 1,
    cefr_level          VARCHAR(4),
    scene_id            BIGINT          REFERENCES scene_templates(id),
    tags                JSONB           DEFAULT '[]',
    view_count          BIGINT          DEFAULT 0,
    avg_rating          DECIMAL(3,2),
    is_published        BOOLEAN         DEFAULT FALSE,
    created_by          BIGINT          REFERENCES users(id),
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMPTZ
);

CREATE INDEX idx_asset_type   ON content_assets(asset_type) WHERE deleted_at IS NULL;
CREATE INDEX idx_asset_grade  ON content_assets(grade_level, difficulty) WHERE deleted_at IS NULL;
CREATE INDEX idx_asset_scene  ON content_assets(scene_id);

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
    questions       JSONB           NOT NULL,
    tags            JSONB           DEFAULT '[]',
    is_published    BOOLEAN         DEFAULT FALSE,
    created_by      BIGINT          REFERENCES users(id),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_reading_grade ON reading_passages(grade_level, difficulty) WHERE deleted_at IS NULL;

CREATE TABLE writing_prompts (
    id                  BIGSERIAL       PRIMARY KEY,
    title               VARCHAR(300)    NOT NULL,
    prompt              TEXT            NOT NULL,
    prompt_en           TEXT,
    word_limit_min      INT,
    word_limit_max      INT,
    time_limit_minutes  INT,
    grade_level         VARCHAR(10),
    difficulty          INT             DEFAULT 1,
    cefr_level          VARCHAR(4),
    scoring_rubric      JSONB,
    tags                JSONB           DEFAULT '[]',
    is_published        BOOLEAN         DEFAULT FALSE,
    created_by          BIGINT          REFERENCES users(id),
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMPTZ
);

-- ============================================
-- 11. 安全与审核
-- ============================================

CREATE TABLE content_review_logs (
    id                  BIGSERIAL       PRIMARY KEY,
    user_id             BIGINT          REFERENCES users(id),
    session_id          BIGINT          REFERENCES coach_sessions(id),
    message_id          BIGINT,
    review_target       VARCHAR(20)     NOT NULL,
    content_type        VARCHAR(20)     NOT NULL,
    original_content    TEXT,
    content_hash        VARCHAR(64),
    review_source       VARCHAR(30)     NOT NULL,
    review_result       VARCHAR(20)     NOT NULL,
    risk_level          VARCHAR(20),
    risk_labels         JSONB           DEFAULT '[]',
    action_taken        VARCHAR(50),
    modified_content    TEXT,
    reviewer_id         BIGINT          REFERENCES users(id),
    review_note         TEXT,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_review_result     ON content_review_logs(review_result);
CREATE INDEX idx_review_user       ON content_review_logs(user_id, created_at DESC);
CREATE INDEX idx_review_created    ON content_review_logs(created_at DESC);

-- 审计日志 (分区表)
CREATE TABLE audit_logs (
    id              BIGSERIAL,
    user_id         BIGINT,
    username        VARCHAR(100),
    user_role       VARCHAR(20),
    action          VARCHAR(50)     NOT NULL,
    resource_type   VARCHAR(50),
    resource_id     BIGINT,
    action_detail   JSONB           DEFAULT '{}',
    ip_address      VARCHAR(50),
    user_agent      VARCHAR(500),
    session_id      VARCHAR(200),
    trace_id        VARCHAR(100),
    result          VARCHAR(20)     NOT NULL DEFAULT 'SUCCESS',
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

CREATE TABLE audit_logs_y2026m06 PARTITION OF audit_logs
    FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');
CREATE TABLE audit_logs_y2026m07 PARTITION OF audit_logs
    FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');

CREATE INDEX idx_audit_user       ON audit_logs(user_id);
CREATE INDEX idx_audit_action     ON audit_logs(action);
CREATE INDEX idx_audit_resource   ON audit_logs(resource_type, resource_id);
CREATE INDEX idx_audit_created    ON audit_logs(created_at DESC);

CREATE TABLE sensitive_words (
    id              BIGSERIAL       PRIMARY KEY,
    word            VARCHAR(200)    NOT NULL UNIQUE,
    category        VARCHAR(50)     NOT NULL,
    risk_level      VARCHAR(20)     NOT NULL DEFAULT 'HIGH',
    is_regex        BOOLEAN         DEFAULT FALSE,
    is_active       BOOLEAN         DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sw_category ON sensitive_words(category);
CREATE INDEX idx_sw_active   ON sensitive_words(is_active);

-- ============================================
-- 12. 系统配置
-- ============================================

CREATE TABLE system_configs (
    id              BIGSERIAL       PRIMARY KEY,
    config_key      VARCHAR(100)    NOT NULL UNIQUE,
    config_value    TEXT            NOT NULL,
    config_type     VARCHAR(20)     NOT NULL DEFAULT 'STRING',
    description     VARCHAR(500),
    is_encrypted    BOOLEAN         DEFAULT FALSE,
    is_public       BOOLEAN         DEFAULT FALSE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE TABLE ai_model_configs (
    id                      BIGSERIAL       PRIMARY KEY,
    model_code              VARCHAR(50)     NOT NULL UNIQUE,
    model_name              VARCHAR(100)    NOT NULL,
    provider                VARCHAR(50)     NOT NULL,
    api_base_url            VARCHAR(500),
    api_key_encrypted       VARCHAR(500),
    model_version           VARCHAR(50),
    default_temperature     DECIMAL(3,2)   DEFAULT 0.7,
    default_max_tokens      INT             DEFAULT 4096,
    default_top_p           DECIMAL(3,2)   DEFAULT 0.9,
    supported_tasks         JSONB           DEFAULT '[]',
    priority                INT             DEFAULT 0,
    rate_limit_per_minute   INT,
    max_daily_tokens        BIGINT,
    is_active               BOOLEAN         DEFAULT TRUE,
    health_status           VARCHAR(20)     DEFAULT 'UNKNOWN',
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- AI 调用日志 (分区表)
CREATE TABLE ai_usage_logs (
    id                  BIGSERIAL,
    user_id             BIGINT,
    session_id          BIGINT,
    model_code          VARCHAR(50)     NOT NULL,
    task_type           VARCHAR(30)     NOT NULL,
    prompt_tokens       INT,
    completion_tokens   INT,
    total_tokens        INT,
    latency_ms          INT,
    is_fallback         BOOLEAN         DEFAULT FALSE,
    cost_usd            DECIMAL(10,6),
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

CREATE TABLE ai_usage_logs_y2026m06 PARTITION OF ai_usage_logs
    FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');
CREATE TABLE ai_usage_logs_y2026m07 PARTITION OF ai_usage_logs
    FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');

CREATE INDEX idx_ai_usage_user   ON ai_usage_logs(user_id);
CREATE INDEX idx_ai_usage_model  ON ai_usage_logs(model_code);
CREATE INDEX idx_ai_usage_date   ON ai_usage_logs(created_at DESC);

-- ============================================
-- 13. 通知系统
-- ============================================

CREATE TABLE notifications (
    id                  BIGSERIAL       PRIMARY KEY,
    user_id             BIGINT          NOT NULL REFERENCES users(id),
    title               VARCHAR(300)    NOT NULL,
    content             TEXT,
    notification_type   VARCHAR(30)     NOT NULL,
    reference_type      VARCHAR(50),
    reference_id        BIGINT,
    is_read             BOOLEAN         DEFAULT FALSE,
    read_at             TIMESTAMPTZ,
    push_channel        JSONB           DEFAULT '[]',
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notif_user_unread ON notifications(user_id, is_read, created_at DESC);
CREATE INDEX idx_notif_created     ON notifications(created_at DESC);

-- ============================================
-- 14. 学习计划
-- ============================================

CREATE TABLE learning_plans (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    name            VARCHAR(200)    NOT NULL,
    description     TEXT,
    plan_type       VARCHAR(30)     NOT NULL DEFAULT 'AI_GENERATED',
    start_date      DATE            NOT NULL,
    end_date        DATE            NOT NULL,
    target_level    VARCHAR(4),
    total_items     INT             DEFAULT 0,
    completed_items INT             DEFAULT 0,
    is_active       BOOLEAN         DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_plan_user ON learning_plans(user_id, is_active);

CREATE TABLE learning_plan_items (
    id                  BIGSERIAL       PRIMARY KEY,
    plan_id             BIGINT          NOT NULL REFERENCES learning_plans(id),
    user_id             BIGINT          NOT NULL REFERENCES users(id),
    item_type           VARCHAR(30)     NOT NULL,
    item_ref_id         BIGINT,
    item_name           VARCHAR(300),
    scheduled_date      DATE            NOT NULL,
    estimated_minutes   INT,
    is_completed        BOOLEAN         DEFAULT FALSE,
    completed_at        TIMESTAMPTZ,
    points_reward       INT             DEFAULT 0,
    sort_order          INT             DEFAULT 0,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_plan_item_plan       ON learning_plan_items(plan_id, sort_order);
CREATE INDEX idx_plan_item_user_date  ON learning_plan_items(user_id, scheduled_date);

-- ============================================
-- 初始数据
-- ============================================

-- 系统配置
INSERT INTO system_configs (config_key, config_value, config_type, description) VALUES
('max_session_rounds', '50', 'NUMBER', '单次会话最大轮次'),
('max_daily_sessions', '20', 'NUMBER', '单用户每日最大会话数'),
('max_input_length', '2000', 'NUMBER', '用户输入最大字符数'),
('content_review_enabled', 'true', 'BOOLEAN', '是否启用内容审核'),
('default_daily_limit_minutes', '60', 'NUMBER', '默认每日时长限制(分钟)'),
('points_per_session', '10', 'NUMBER', '每次完成陪练基础积分'),
('points_per_checkin', '5', 'NUMBER', '每日打卡积分'),
('points_per_perfect_score', '20', 'NUMBER', '满分额外积分');

-- AI 模型配置
INSERT INTO ai_model_configs (model_code, model_name, provider, supported_tasks, priority, is_active) VALUES
('qwen-turbo', '通义千问 Turbo', 'ALIBABA', '["CHAT","TRANSLATION"]', 1, TRUE),
('qwen-max', '通义千问 Max', 'ALIBABA', '["CHAT","EVALUATION","CORRECTION"]', 2, TRUE),
('deepseek-v3', 'DeepSeek V3', 'DEEPSEEK', '["CHAT","CORRECTION","EVALUATION"]', 3, TRUE);

-- 预置成就
INSERT INTO achievements (code, name, name_en, description, category, points_reward, condition_json) VALUES
('FIRST_SESSION', '初次见面', 'First Steps', '完成第一次AI陪练', 'LEARNING', 10, '{"type":"session_count","threshold":1}'),
('STREAK_3', '三天打鱼', '3-Day Streak', '连续学习3天', 'STREAK', 20, '{"type":"streak_days","threshold":3}'),
('STREAK_7', '周而不息', '7-Day Streak', '连续学习7天', 'STREAK', 50, '{"type":"streak_days","threshold":7}'),
('STREAK_30', '月学不辍', '30-Day Streak', '连续学习30天', 'STREAK', 200, '{"type":"streak_days","threshold":30}'),
('VOCAB_50', '词汇新手', '50 Words', '累计学习50个新单词', 'VOCABULARY', 30, '{"type":"vocabulary_count","threshold":50}'),
('VOCAB_500', '词汇达人', '500 Words', '累计学习500个新单词', 'VOCABULARY', 100, '{"type":"vocabulary_count","threshold":500}'),
('SESSION_10', '勤学苦练', '10 Sessions', '完成10次陪练', 'LEARNING', 50, '{"type":"session_count","threshold":10}'),
('PERFECT_SCORE', '满分达成', 'Perfect Score', '单次评测获得满分', 'ACCURACY', 100, '{"type":"perfect_score","threshold":1}'),
('TEN_CORRECTIONS', '知错能改', 'Error Corrector', '查看10条纠错建议', 'LEARNING', 15, '{"type":"correction_reviewed","threshold":10}'),
('FIVE_SCENES', '场景探索者', 'Scene Explorer', '完成5个不同场景的陪练', 'LEARNING', 30, '{"type":"scene_variety","threshold":5}');

-- ============================================
-- DDL 结束
-- ============================================
