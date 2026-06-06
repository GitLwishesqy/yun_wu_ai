package com.yunwu.incentive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.incentive.entity.PointsRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PointsRecordMapper extends BaseMapper<PointsRecord> {
    @Select("SELECT COALESCE(SUM(points), 0) FROM points_records WHERE user_id = #{userId}")
    int sumByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM points_records WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<PointsRecord> selectRecent(@Param("userId") Long userId, @Param("limit") int limit);
}
