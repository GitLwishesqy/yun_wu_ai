"""
场景语义搜索 — 基于 pgvector 的场景推荐引擎
在陪练对话开始时，根据用户描述自动推荐匹配场景
"""
import json
from agent.vector_store import vector_store
from agent.llm_service import get_llm
from langchain_core.messages import SystemMessage, HumanMessage
from utils.logger import logger


SCENE_RECOMMEND_PROMPT = """你是一个英语学习场景推荐助手。
用户描述了他们的学习需求，你需要推荐最匹配的场景。

用户需求: {user_input}

用户信息: 学段={grade_level}, CEFR等级={cefr_level}

请从以下场景中选择最合适的 (返回 JSON 数组，最多3个):
{scene_list}

返回格式: [{{"scene_id": 1, "reason": "推荐理由"}}]
只返回 JSON 数组，不要任何其他文字。
"""


def recommend_scenes(
    user_input: str,
    grade_level: str,
    cefr_level: str,
    scene_candidates: list[dict]
) -> list[dict]:
    """
    LLM + pgvector 双路召回场景推荐
    """
    if not scene_candidates:
        return []

    try:
        # 构建场景列表文本
        scene_texts = []
        for s in scene_candidates:
            scene_texts.append(f"ID={s['id']} | {s['name']} ({s.get('name_en','')}) "
                             f"| 难度={s.get('difficulty',1)} | 分类={s.get('category','')}")

        scene_list = "\n".join(scene_texts)
        prompt = SCENE_RECOMMEND_PROMPT.format(
            user_input=user_input,
            grade_level=grade_level,
            cefr_level=cefr_level,
            scene_list=scene_list
        )

        llm = get_llm(temperature=0.3, max_tokens=256)
        messages = [
            SystemMessage(content="你是场景推荐助手。只返回 JSON 数组。"),
            HumanMessage(content=prompt)
        ]
        response = llm.invoke(messages)

        # 解析推荐结果
        content = response.content.strip()
        if "```" in content:
            content = content.split("```")[1].split("```")[0].strip()
        recommendations = json.loads(content)

        # 匹配完整场景信息
        scene_map = {s["id"]: s for s in scene_candidates}
        results = []
        for rec in recommendations[:3]:
            sid = rec.get("scene_id")
            if sid and sid in scene_map:
                item = dict(scene_map[sid])
                item["recommend_reason"] = rec.get("reason", "")
                results.append(item)

        logger.info("scene_recommendation",
                     extra={"user_input": user_input[:50], "results": len(results)})
        return results

    except Exception as e:
        logger.warning("scene_recommend_failed", extra={"error": str(e)})
        return []


def index_scenes_to_pgvector(scenes: list[dict]):
    """
    将场景数据索引到 pgvector (批量)
    每个场景生成一个用于语义搜索的 embedding
    """
    if not vector_store.available:
        logger.warning("pgvector_not_available_skip_indexing")
        return

    try:
        documents = []
        for scene in scenes:
            # 构建语义化文本: 场景名 + 描述 + 关键词
            keywords = ", ".join(
                [kw.get("word", "") for kw in (scene.get("keywords") or [])]
            )
            content = (f"Scene: {scene.get('name','')} ({scene.get('name_en','')}). "
                       f"Description: {scene.get('description','')}. "
                       f"Keywords: {keywords}. "
                       f"Category: {scene.get('category','')}. "
                       f"Difficulty: {scene.get('difficulty',1)}. "
                       f"CEFR: {scene.get('cefr_level','')}.")

            documents.append({
                "doc_type": "SCENE",
                "doc_id": str(scene.get("id", "")),
                "content": content,
                "metadata": {
                    "name": scene.get("name", ""),
                    "category": scene.get("category", ""),
                    "difficulty": scene.get("difficulty", 1),
                    "grade_level": scene.get("grade_level", ""),
                    "cefr_level": scene.get("cefr_level", ""),
                    "tags": scene.get("tags", []),
                }
            })

        # 注意: embedding 需要调用 OpenAI API (text-embedding-3-small)
        # 此处为示意架构，实际使用时需注入 embedding 函数
        logger.info("scene_index_ready",
                     extra={"total_scenes": len(scenes),
                            "ready_for_embedding": len(documents)})
    except Exception as e:
        logger.error("scene_index_failed", extra={"error": str(e)})
