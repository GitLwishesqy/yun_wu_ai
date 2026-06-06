package com.yunwu.coach.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.coach.entity.CoachSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CoachSessionMapper extends BaseMapper<CoachSession> {

    @Select("SELECT COUNT(*) FROM coach_sessions " +
            "WHERE user_id = #{userId} AND DATE(started_at) = CURRENT_DATE " +
            "AND status = 'COMPLETED'")
    int countTodayCompleted(@Param("userId") Long userId);

    @Select("SELECT COALESCE(SUM(duration_seconds), 0) FROM coach_sessions " +
            "WHERE user_id = #{userId} AND DATE(started_at) = CURRENT_DATE " +
            "AND status = 'COMPLETED'")
    int sumTodayDurationSeconds(@Param("userId") Long userId);
}
