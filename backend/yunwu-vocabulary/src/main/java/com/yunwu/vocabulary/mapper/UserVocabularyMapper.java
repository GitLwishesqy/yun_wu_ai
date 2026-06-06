package com.yunwu.vocabulary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.vocabulary.entity.UserVocabulary;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserVocabularyMapper extends BaseMapper<UserVocabulary> {

    @Select("SELECT * FROM user_vocabulary WHERE user_id = #{userId} AND status = #{status} ORDER BY last_seen_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<UserVocabulary> selectByStatus(@Param("userId") Long userId, @Param("status") String status, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM user_vocabulary WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM user_vocabulary WHERE user_id = #{userId} AND word_lower = #{wordLower}")
    UserVocabulary findByWord(@Param("userId") Long userId, @Param("wordLower") String wordLower);

    /** 按状态统计 */
    @Select("SELECT status, COUNT(*) as cnt FROM user_vocabulary WHERE user_id = #{userId} GROUP BY status")
    List<Map<String, Object>> statsByStatus(@Param("userId") Long userId);

    /** 待复习 (艾宾浩斯) */
    @Select("SELECT * FROM user_vocabulary WHERE user_id = #{userId} AND next_review_at <= NOW() AND status IN ('LEARNING','REVIEWING') ORDER BY next_review_at ASC LIMIT #{limit}")
    List<UserVocabulary> selectDueForReview(@Param("userId") Long userId, @Param("limit") int limit);

    @Update("UPDATE user_vocabulary SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Update("UPDATE user_vocabulary SET next_review_at = #{nextReview}, last_reviewed_at = NOW(), updated_at = NOW() WHERE id = #{id}")
    int scheduleNextReview(@Param("id") Long id, @Param("nextReview") LocalDateTime nextReview);

    /** 搜索 */
    @Select("SELECT * FROM user_vocabulary WHERE user_id = #{userId} AND (word LIKE CONCAT('%',#{q},'%') OR translation LIKE CONCAT('%',#{q},'%')) ORDER BY last_seen_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<UserVocabulary> search(@Param("userId") Long userId, @Param("q") String q, @Param("limit") int limit, @Param("offset") int offset);
}
