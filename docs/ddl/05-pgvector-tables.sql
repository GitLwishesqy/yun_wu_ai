-- ============================================
-- pgvector 向量表 DDL (PostgreSQL)
-- 必须先启用 pgvector 扩展: CREATE EXTENSION vector;
-- ============================================

-- 启用扩展
CREATE EXTENSION IF NOT EXISTS vector;

-- 向量文档表 (场景知识库、纠错案例)
CREATE TABLE vector_documents (
    id              BIGSERIAL       PRIMARY KEY,
    doc_type        VARCHAR(30)     NOT NULL,          -- SCENE / GRAMMAR / VOCAB / CORRECTION
    doc_id          VARCHAR(100),                      -- 关联业务 ID
    content         TEXT            NOT NULL,          -- 文本内容
    metadata_json   JSONB           DEFAULT '{}',      -- 元数据 (难度、学段、标签)
    embedding       vector(1536)    NOT NULL           -- text-embedding-3-small 1536维向量

    -- created_at 可选
    -- created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 嵌入缓存表 (避免重复计算 embedding，节约 API 费用)
CREATE TABLE embedding_cache (
    id              BIGSERIAL       PRIMARY KEY,
    text_hash       VARCHAR(64)     UNIQUE NOT NULL,   -- SHA-256
    text            TEXT            NOT NULL,
    embedding       vector(1536)    NOT NULL
);

-- 索引
CREATE INDEX idx_vd_type        ON vector_documents(doc_type);
CREATE INDEX idx_vd_doc_id      ON vector_documents(doc_id);

-- pgvector IVFFlat 索引 (加速近似搜索，建议数据量 > 1000 条后创建)
-- 数据量大时，用 IVFFlat 替代精确搜索:
-- CREATE INDEX idx_vd_embedding_ivfflat ON vector_documents
--     USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

-- 嵌入缓存的精确搜索索引 (缓存表数据量小，精确搜索即可)
CREATE INDEX idx_ec_hash ON embedding_cache(text_hash);

COMMENT ON TABLE vector_documents IS '向量文档 — 场景知识库、纠错案例、语法规则';
COMMENT ON TABLE embedding_cache IS '嵌入缓存 — 避免重复计算 text-embedding';
