-- ============================================
-- 云悟英语 — 短信发送日志表 DDL
-- 用于生产环境短信审计追溯 (合规要求: 保留6个月以上)
-- ============================================

CREATE TABLE sms_send_logs (
    id              BIGSERIAL       PRIMARY KEY,
    phone           VARCHAR(20)     NOT NULL,
    phone_encrypted VARCHAR(200),                              -- 加密存储手机号

    -- 发送内容
    template_code   VARCHAR(50)     NOT NULL,                   -- 短信模板 CODE
    template_params JSONB           DEFAULT '{}',              -- 模板参数
    code_hash       VARCHAR(64),                               -- 验证码 SHA-256 (不存明文)

    -- 发送结果
    provider        VARCHAR(30)     NOT NULL,                   -- ALIYUN/TENCENT/MOCK
    send_status     VARCHAR(20)     NOT NULL,                   -- PENDING/SUCCESS/FAILED
    provider_msg_id VARCHAR(200),                               -- 服务商返回的消息 ID
    error_code      VARCHAR(50),                                -- 服务商错误码
    error_message   VARCHAR(500),                               -- 错误描述

    -- 上下文
    purpose         VARCHAR(30)     NOT NULL,                   -- LOGIN/REGISTER/RESET_PASSWORD/BIND
    ip_address      VARCHAR(50),
    user_agent      VARCHAR(500),
    request_id      VARCHAR(100),                               -- 链路追踪 ID

    -- 计费
    fee_count       INT             DEFAULT 1,                  -- 计费条数 (长短信拆分)

    -- 时间
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- 索引
CREATE INDEX idx_sms_log_phone ON sms_send_logs(phone, created_at DESC);
CREATE INDEX idx_sms_log_status ON sms_send_logs(send_status);
CREATE INDEX idx_sms_log_provider ON sms_send_logs(provider, created_at DESC);
CREATE INDEX idx_sms_log_purpose ON sms_send_logs(purpose);
CREATE INDEX idx_sms_log_created ON sms_send_logs(created_at DESC);

-- 注释
COMMENT ON TABLE sms_send_logs IS '短信发送日志 — 合规追溯，保留6个月以上';
COMMENT ON COLUMN sms_send_logs.code_hash IS '验证码 SHA-256 哈希，不存明文';
COMMENT ON COLUMN sms_send_logs.phone_encrypted IS '手机号 AES-256-GCM 加密存储';
