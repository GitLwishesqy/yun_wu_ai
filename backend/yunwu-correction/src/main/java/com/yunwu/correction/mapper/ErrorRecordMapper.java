package com.yunwu.correction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.correction.entity.ErrorRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ErrorRecordMapper extends BaseMapper<ErrorRecord> {

    /** 查询用户薄弱点 (按 total_count 排序) */
    @Select("SELECT * FROM error_records WHERE user_id = #{userId} " +
            "AND mastery_status IN ('LEARNING', 'REVIEWING') " +
            "ORDER BY total_count DESC")
    List<ErrorRecord> selectWeaknesses(@Param("userId") Long userId);

    /** 待复习的错误 (艾宾浩斯) */
    @Select("SELECT * FROM error_records WHERE user_id = #{userId} " +
            "AND next_review_at <= NOW() " +
            "AND mastery_status IN ('LEARNING', 'REVIEWING') " +
            "ORDER BY next_review_at ASC LIMIT #{limit}")
    List<ErrorRecord> selectDueForReview(@Param("userId") Long userId,
                                          @Param("limit") int limit);

    /** 查找或创建错误记录 */
    @Select("SELECT * FROM error_records WHERE user_id = #{userId} " +
            "AND error_type = #{errorType} AND error_subtype = #{errorSubtype} " +
            "AND error_pattern = #{errorPattern} LIMIT 1")
    ErrorRecord findByPattern(@Param("userId") Long userId,
                               @Param("errorType") String errorType,
                               @Param("errorSubtype") String errorSubtype,
                               @Param("errorPattern") String errorPattern);

    /** 更新掌握状态 */
    @Update("UPDATE error_records SET mastery_status = #{status}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int updateMasteryStatus(@Param("id") Long id, @Param("status") String status);

    /** 更新下次复习时间 */
    @Update("UPDATE error_records SET next_review_at = #{nextReview}, " +
            "review_count = review_count + 1, updated_at = NOW() WHERE id = #{id}")
    int scheduleNextReview(@Param("id") Long id,
                            @Param("nextReview") LocalDateTime nextReview);
}
