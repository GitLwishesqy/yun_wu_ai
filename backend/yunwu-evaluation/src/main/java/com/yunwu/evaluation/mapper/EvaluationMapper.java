package com.yunwu.evaluation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.evaluation.entity.Evaluation;
import org.apache.ibatis.annotations.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface EvaluationMapper extends BaseMapper<Evaluation> {

    @Select("SELECT * FROM evaluations WHERE user_id = #{userId} AND (#{evalType} IS NULL OR eval_type = #{evalType}) ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<Evaluation> selectByUserId(@Param("userId") Long userId, @Param("evalType") String evalType, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM evaluations WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    /** 最近N次评测的 overall_score 趋势 */
    @Select("SELECT overall_score, created_at FROM evaluations WHERE user_id = #{userId} AND eval_type = 'SESSION' ORDER BY created_at DESC LIMIT #{limit}")
    List<Map<String, Object>> selectRecentScores(@Param("userId") Long userId, @Param("limit") int limit);

    /** 平均分 */
    @Select("SELECT COALESCE(AVG(overall_score), 0) FROM evaluations WHERE user_id = #{userId} AND eval_type = 'SESSION'")
    BigDecimal selectAvgScore(@Param("userId") Long userId);

    /** 最高分 */
    @Select("SELECT COALESCE(MAX(overall_score), 0) FROM evaluations WHERE user_id = #{userId} AND eval_type = 'SESSION'")
    BigDecimal selectMaxScore(@Param("userId") Long userId);

    /** 本月评测次数 */
    @Select("SELECT COUNT(*) FROM evaluations WHERE user_id = #{userId} AND created_at >= DATE_TRUNC('month', CURRENT_DATE)")
    int countThisMonth(@Param("userId") Long userId);

    /** 按 eval_type 统计 */
    @Select("SELECT eval_type, COUNT(*) as cnt, ROUND(AVG(overall_score), 1) as avg_score FROM evaluations WHERE user_id = #{userId} GROUP BY eval_type")
    List<Map<String, Object>> statsByType(@Param("userId") Long userId);
}
