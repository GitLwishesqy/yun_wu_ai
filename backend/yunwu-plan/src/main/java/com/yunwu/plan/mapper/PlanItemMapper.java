package com.yunwu.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.plan.entity.PlanItem;
import org.apache.ibatis.annotations.*; import java.time.LocalDate; import java.util.List;

@Mapper
public interface PlanItemMapper extends BaseMapper<PlanItem> {
    @Select("SELECT * FROM learning_plan_items WHERE plan_id = #{planId} ORDER BY sort_order")
    List<PlanItem> selectByPlan(@Param("planId") Long planId);
    @Select("SELECT * FROM learning_plan_items WHERE plan_id = #{planId} AND scheduled_date = #{date} ORDER BY sort_order")
    List<PlanItem> selectByPlanAndDate(@Param("planId") Long planId, @Param("date") LocalDate date);
}
