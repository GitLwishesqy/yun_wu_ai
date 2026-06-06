package com.yunwu.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.report.entity.LearningReport;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LearningReportMapper extends BaseMapper<LearningReport> {

    @Select("SELECT * FROM learning_reports WHERE user_id = #{userId} AND period_type = #{periodType} ORDER BY period_start DESC LIMIT #{limit} OFFSET #{offset}")
    List<LearningReport> selectByUserId(@Param("userId") Long userId, @Param("periodType") String periodType, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM learning_reports WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    /** 获取最新报告 */
    @Select("SELECT * FROM learning_reports WHERE user_id = #{userId} AND period_type = #{periodType} ORDER BY period_start DESC LIMIT 1")
    LearningReport selectLatest(@Param("userId") Long userId, @Param("periodType") String periodType);

    /** 标记已读 */
    @Update("UPDATE learning_reports SET is_read = TRUE WHERE id = #{id}")
    int markRead(@Param("id") Long id);

    /** 检查指定周期是否已生成 */
    @Select("SELECT COUNT(*) FROM learning_reports WHERE user_id = #{userId} AND period_type = #{periodType} AND period_start = #{periodStart}")
    int exists(@Param("userId") Long userId, @Param("periodType") String periodType, @Param("periodStart") LocalDate periodStart);
}
