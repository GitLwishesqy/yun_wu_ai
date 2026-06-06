package com.yunwu.incentive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.incentive.entity.CheckIn;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {
    @Select("SELECT * FROM check_ins WHERE user_id = #{userId} AND check_in_date = #{date}")
    CheckIn findByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Select("SELECT * FROM check_ins WHERE user_id = #{userId} AND check_in_date >= #{start} AND check_in_date <= #{end} ORDER BY check_in_date")
    List<CheckIn> selectByMonth(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Select("SELECT check_in_date FROM check_ins WHERE user_id = #{userId} ORDER BY check_in_date DESC LIMIT 1")
    LocalDate findLastDate(@Param("userId") Long userId);
}
