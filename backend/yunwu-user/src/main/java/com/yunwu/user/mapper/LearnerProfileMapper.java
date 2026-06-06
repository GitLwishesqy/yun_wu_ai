package com.yunwu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.user.entity.LearnerProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 学习档案 Mapper
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Mapper
public interface LearnerProfileMapper extends BaseMapper<LearnerProfile> {

    /**
     * 根据用户 ID 查询学习档案
     */
    @Select("SELECT * FROM learner_profiles WHERE user_id = #{userId}")
    LearnerProfile selectByUserId(@Param("userId") Long userId);
}
