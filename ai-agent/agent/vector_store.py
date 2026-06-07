"""
pgvector 向量存储 — 替代 ChromaDB
用于场景模板检索、知识库 RAG、历史对话相似度搜索

架构:
  - PostgreSQL + pgvector 扩展 (复用现有 PG 实例)
  - 无需独立部署 ChromaDB 服务
  - SQLAlchemy ORM + pgvector 扩展类型
"""
import json
import config
from typing import List, Optional, Dict, Any
from sqlalchemy import (
    create_engine, Column, BigInteger, String, Text, Float,
    DateTime, Index, text as sa_text
)
from sqlalchemy.orm import Session, declarative_base, sessionmaker
from sqlalchemy.dialects.postgresql import JSONB
from pgvector.sqlalchemy import Vector
from utils.logger import logger

Base = declarative_base()

# ==================== ORM 模型 ====================

class VectorDocument(Base):
    """向量文档表 — 场景知识、语法规则、纠错案例"""
    __tablename__ = "vector_documents"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    doc_type = Column(String(30), nullable=False, index=True)       # SCENE / GRAMMAR / VOCAB / CORRECTION
    doc_id = Column(String(100))                                     # 关联业务 ID
    content = Column(Text, nullable=False)                           # 文本内容
    metadata_json = Column(JSONB, default={})                        # 元数据 (难度/学段/标签)

    # 使用 text-embedding-3-small 的 1536 维向量
    embedding = Column(Vector(1536), nullable=False)

    def __repr__(self):
        return f"<VectorDocument(id={self.id}, type={self.doc_type})>"


class EmbeddingCache(Base):
    """文本嵌入缓存表 — 避免重复计算 embedding"""
    __tablename__ = "embedding_cache"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    text_hash = Column(String(64), unique=True, nullable=False, index=True)  # SHA-256
    text = Column(Text, nullable=False)
    embedding = Column(Vector(1536), nullable=False)

    def __repr__(self):
        return f"<EmbeddingCache(hash={self.text_hash[:8]})>"


# ==================== 向量存储服务 ====================

class PgVectorStore:
    """pgvector 向量存储 — 统一向量操作入口"""

    def __init__(self):
        self.engine = None
        self.SessionLocal = None
        self._initialized = False
        self._init_store()

    def _init_store(self):
        """初始化数据库连接"""
        if not config.PGVECTOR_ENABLED:
            logger.info("pgvector_disabled")
            return

        db_url = config.PGVECTOR_DATABASE_URL
        if not db_url:
            logger.warning("pgvector_no_database_url")
            return

        try:
            self.engine = create_engine(
                db_url,
                pool_size=5,
                max_overflow=10,
                pool_pre_ping=True,        # 连接健康检查
                pool_recycle=3600,         # 1小时回收
            )
            self.SessionLocal = sessionmaker(bind=self.engine, autoflush=False)
            self._initialized = True
            logger.info("pgvector_initialized",
                        extra={"host": self.engine.url.host, "db": self.engine.url.database})
        except Exception as e:
            logger.error("pgvector_init_failed", extra={"error": str(e)})
            self._initialized = False

    @property
    def available(self) -> bool:
        return self._initialized and self.SessionLocal is not None

    # ==================== 文档写入 ====================

    def add_documents(self, documents: List[Dict[str, Any]],
                       embeddings: List[List[float]]) -> List[int]:
        """批量写入文档 + 向量"""
        if not self.available:
            return []
        ids = []
        with self.SessionLocal() as session:
            try:
                for doc, emb in zip(documents, embeddings):
                    vd = VectorDocument(
                        doc_type=doc.get("doc_type", "UNKNOWN"),
                        doc_id=doc.get("doc_id"),
                        content=doc.get("content", ""),
                        metadata_json=doc.get("metadata", {}),
                        embedding=emb,
                    )
                    session.add(vd)
                    session.flush()
                    ids.append(vd.id)
                session.commit()
                logger.info("pgvector_documents_added", extra={"count": len(ids)})
            except Exception as e:
                session.rollback()
                logger.error("pgvector_add_failed", extra={"error": str(e)})
        return ids

    # ==================== 相似度搜索 ====================

    def similarity_search(
        self,
        query_embedding: List[float],
        top_k: int = 5,
        doc_type: Optional[str] = None,
        min_score: float = 0.7,
    ) -> List[Dict[str, Any]]:
        """
        向量相似度搜索 (余弦距离)

        Returns:
            [{doc_id, content, metadata, score, doc_type}, ...]
        """
        if not self.available:
            return []

        with self.SessionLocal() as session:
            try:
                # pgvector <=> 运算符 = 余弦距离
                query = session.query(
                    VectorDocument,
                    (1 - VectorDocument.embedding.cosine_distance(query_embedding)).label("score")
                ).filter(
                    (1 - VectorDocument.embedding.cosine_distance(query_embedding)) >= min_score
                )

                if doc_type:
                    query = query.filter(VectorDocument.doc_type == doc_type)

                results = query.order_by(
                    VectorDocument.embedding.cosine_distance(query_embedding)
                ).limit(top_k).all()

                return [{
                    "doc_id": row.VectorDocument.doc_id,
                    "content": row.VectorDocument.content,
                    "metadata": row.VectorDocument.metadata_json,
                    "doc_type": row.VectorDocument.doc_type,
                    "score": round(float(row.score), 4),
                } for row in results]
            except Exception as e:
                logger.error("pgvector_search_failed", extra={"error": str(e)})
                return []

    # ==================== 嵌入缓存 ====================

    def cached_embed(self, text: str,
                      embed_func) -> Optional[List[float]]:
        """带缓存的文本嵌入 (避免重复计算)"""
        if not self.available:
            return None

        text_hash = _sha256(text)
        with self.SessionLocal() as session:
            cached = session.query(EmbeddingCache).filter_by(
                text_hash=text_hash).first()
            if cached:
                return list(cached.embedding)

        # 未命中 — 调用嵌入函数并缓存
        embedding = embed_func(text)
        if embedding:
            with self.SessionLocal() as session:
                try:
                    ec = EmbeddingCache(
                        text_hash=text_hash, text=text, embedding=embedding)
                    session.add(ec)
                    session.commit()
                except Exception:
                    session.rollback()  # 并发插入重复 hash 时忽略
            return embedding
        return None

    # ==================== 维护 ====================

    def delete_by_doc_id(self, doc_id: str):
        if not self.available:
            return
        with self.SessionLocal() as session:
            session.query(VectorDocument).filter_by(doc_id=doc_id).delete()
            session.commit()

    def delete_by_type(self, doc_type: str):
        if not self.available:
            return
        with self.SessionLocal() as session:
            session.query(VectorDocument).filter_by(doc_type=doc_type).delete()
            session.commit()

    def stats(self) -> dict:
        if not self.available:
            return {"status": "disabled"}
        with self.SessionLocal() as session:
            return {
                "status": "connected",
                "total_documents": session.query(VectorDocument).count(),
                "cached_embeddings": session.query(EmbeddingCache).count(),
                "by_type": {
                    row[0]: row[1]
                    for row in session.query(
                        VectorDocument.doc_type,
                        __import__('sqlalchemy').func.count()
                    ).group_by(VectorDocument.doc_type).all()
                }
            }


def _sha256(text: str) -> str:
    import hashlib
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


# 全局单例
vector_store = PgVectorStore()
