package com.yunwu.notification.entity;

import com.baomidou.mybatisplus.annotation.*; import java.io.Serializable; import java.time.LocalDateTime;

@TableName("notifications")
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id; private Long userId; private String title; private String content;
    private String notificationType; private String referenceType; private Long referenceId;
    private Boolean isRead; private LocalDateTime readAt; private String pushChannel;
    private LocalDateTime createdAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; } public void setContent(String content) { this.content = content; }
    public String getNotificationType() { return notificationType; } public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
    public String getReferenceType() { return referenceType; } public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
    public Long getReferenceId() { return referenceId; } public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public Boolean getIsRead() { return isRead; } public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public LocalDateTime getReadAt() { return readAt; } public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    public String getPushChannel() { return pushChannel; } public void setPushChannel(String pushChannel) { this.pushChannel = pushChannel; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
