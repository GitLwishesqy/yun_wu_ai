package com.yunwu.correction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.correction.entity.Correction;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CorrectionMapper extends BaseMapper<Correction> {

    /** 会话纠错记录 */
    @Select("SELECT * FROM corrections WHERE session_id = #{sessionId} ORDER BY created_at ASC")
    List<Correction> selectBySessionId(@Param("sessionId") Long sessionId);

    /** 用户纠错历史 (分页) */
    @Select("SELECT * FROM corrections WHERE user_id = #{userId} " +
            "AND (#{errorType, jdbcType=VARCHAR} IS NULL OR error_type = #{errorType, jdbcType=VARCHAR}) " +
            "ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<Correction> selectByUserId(@Param("userId") Long userId,
                                     @Param("errorType") String errorType,
                                     @Param("limit") int limit,
                                     @Param("offset") int offset);

    /** 按错误类型统计 */
    @Select("SELECT error_type, COUNT(*) as cnt FROM corrections " +
            "WHERE user_id = #{userId} " +
            "AND created_at >= #{since} " +
            "GROUP BY error_type ORDER BY cnt DESC")
    List<Map<String, Object>> countByType(@Param("userId") Long userId,
                                           @Param("since") java.time.LocalDateTime since);

    /** 按子类型统计 */
    @Select("SELECT error_subtype, COUNT(*) as cnt FROM corrections " +
            "WHERE user_id = #{userId} AND error_type = #{errorType} " +
            "AND created_at >= #{since} " +
            "GROUP BY error_subtype ORDER BY cnt DESC")
    List<Map<String, Object>> countBySubtype(@Param("userId") Long userId,
                                              @Param("errorType") String errorType,
                                              @Param("since") java.time.LocalDateTime since);

    /** 每日错误趋势 */
    @Select("SELECT DATE(created_at) as date, COUNT(*) as cnt FROM corrections " +
            "WHERE user_id = #{userId} AND created_at >= #{since} " +
            "GROUP BY DATE(created_at) ORDER BY date")
    List<Map<String, Object>> dailyTrend(@Param("userId") Long userId,
                                          @Param("since") java.time.LocalDateTime since);

    /** 标记会话内所有纠错已查看 */
    @Update("UPDATE corrections SET was_reviewed = TRUE WHERE session_id = #{sessionId}")
    int markReviewedBySession(@Param("sessionId") Long sessionId);

    /** 标记单条已查看 */
    @Update("UPDATE corrections SET was_reviewed = TRUE WHERE id = #{id}")
    int markReviewed(@Param("id") Long id);
}
