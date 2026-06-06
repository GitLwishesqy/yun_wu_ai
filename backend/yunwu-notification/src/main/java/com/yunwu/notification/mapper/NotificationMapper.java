package com.yunwu.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.notification.entity.Notification;
import org.apache.ibatis.annotations.*; import java.util.List; import java.util.Map;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    @Select("SELECT * FROM notifications WHERE user_id = #{userId} AND (#{isRead} IS NULL OR is_read = #{isRead}) ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<Notification> selectByUser(@Param("userId") Long userId, @Param("isRead") Boolean isRead, @Param("limit") int limit, @Param("offset") int offset);
    @Select("SELECT COUNT(*) FROM notifications WHERE user_id = #{userId} AND is_read = FALSE")
    int countUnread(@Param("userId") Long userId);
    @Select("SELECT notification_type, COUNT(*) as cnt FROM notifications WHERE user_id = #{userId} AND is_read = FALSE GROUP BY notification_type")
    List<Map<String, Object>> countUnreadByType(@Param("userId") Long userId);
    @Update("UPDATE notifications SET is_read = TRUE, read_at = NOW() WHERE id = #{id}")
    int markRead(@Param("id") Long id);
    @Update("UPDATE notifications SET is_read = TRUE, read_at = NOW() WHERE user_id = #{userId} AND is_read = FALSE")
    int markAllRead(@Param("userId") Long userId);
}
