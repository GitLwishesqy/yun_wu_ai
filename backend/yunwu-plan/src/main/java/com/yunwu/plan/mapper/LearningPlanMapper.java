package com.yunwu.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.plan.entity.LearningPlan;
import org.apache.ibatis.annotations.*; import java.util.List;

@Mapper
public interface LearningPlanMapper extends BaseMapper<LearningPlan> {
    @Select("SELECT * FROM learning_plans WHERE user_id = #{userId} AND is_active = #{isActive} ORDER BY created_at DESC")
    List<LearningPlan> selectByUser(@Param("userId") Long userId, @Param("isActive") Boolean isActive);
    @Select("SELECT * FROM learning_plans WHERE user_id = #{userId} AND is_active = TRUE ORDER BY created_at DESC LIMIT 1")
    LearningPlan selectActive(@Param("userId") Long userId);
}
