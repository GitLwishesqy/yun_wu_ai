package com.yunwu.notification.service;

import com.yunwu.common.context.UserContext;
import com.yunwu.notification.entity.Notification;
import com.yunwu.notification.mapper.NotificationMapper;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationMapper mapper;
    public NotificationService(NotificationMapper mapper) { this.mapper = mapper; }

    public List<Notification> list(Boolean isRead, int page, int size) {
        return mapper.selectByUser(UserContext.getUserId(), isRead, size, (page - 1) * size);
    }
    public Map<String, Object> unreadCount() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total_unread", mapper.countUnread(UserContext.getUserId()));
        Map<String, Integer> byType = new LinkedHashMap<>();
        for (var row : mapper.countUnreadByType(UserContext.getUserId()))
            byType.put(String.valueOf(row.get("notification_type")), ((Number) row.get("cnt")).intValue());
        r.put("by_type", byType);
        return r;
    }
    public void markRead(Long id) { mapper.markRead(id); }
    public void markAllRead() { mapper.markAllRead(UserContext.getUserId()); log.info("[Notif] 全部已读"); }
}
