package com.yunwu.incentive.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("user_achievements")
public class UserAchievement implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId; private Long achievementId; private String progress;
    private Boolean isCompleted; private LocalDateTime completedAt; private Boolean notified;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public Long getAchievementId() { return achievementId; } public void setAchievementId(Long achievementId) { this.achievementId = achievementId; }
    public String getProgress() { return progress; } public void setProgress(String progress) { this.progress = progress; }
    public Boolean getIsCompleted() { return isCompleted; } public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
    public LocalDateTime getCompletedAt() { return completedAt; } public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public Boolean getNotified() { return notified; } public void setNotified(Boolean notified) { this.notified = notified; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
