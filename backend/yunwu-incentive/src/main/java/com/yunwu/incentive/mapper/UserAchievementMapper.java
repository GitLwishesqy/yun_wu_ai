package com.yunwu.incentive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.incentive.entity.UserAchievement;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserAchievementMapper extends BaseMapper<UserAchievement> {

    @Select("SELECT * FROM user_achievements WHERE user_id = #{userId}")
    List<UserAchievement> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM user_achievements WHERE user_id = #{userId} AND achievement_id = #{achievementId}")
    UserAchievement findByUserAndId(@Param("userId") Long userId, @Param("achievementId") Long achievementId);

    @Select("SELECT u.id as user_id, u.nickname, u.avatar_url, COALESCE(SUM(pr.points), 0) as total_pts " +
            "FROM users u LEFT JOIN points_records pr ON u.id = pr.user_id " +
            "WHERE u.role = 'STUDENT' AND u.deleted_at IS NULL " +
            "GROUP BY u.id ORDER BY total_pts DESC LIMIT #{limit}")
    List<Map<String, Object>> selectLeaderboard(@Param("limit") int limit);
}
