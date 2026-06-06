package com.yunwu.coach.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 陪练会话实体 — 对应 coach_sessions 表
 */
@TableName("coach_sessions")
public class CoachSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long sceneId;
    private String sessionType;
    private String title;
    private String status;
    private Integer messageCount;
    private Integer userMessageCount;
    private Integer aiMessageCount;
    private Integer correctionCount;
    private Long totalTokensUsed;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Integer durationSeconds;
    private Integer difficultySnapshot;
    private String cefrLevelSnapshot;
    private String metadata;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // ==================== Getters & Setters ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getSceneId() { return sceneId; }
    public void setSceneId(Long sceneId) { this.sceneId = sceneId; }
    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }
    public Integer getUserMessageCount() { return userMessageCount; }
    public void setUserMessageCount(Integer userMessageCount) { this.userMessageCount = userMessageCount; }
    public Integer getAiMessageCount() { return aiMessageCount; }
    public void setAiMessageCount(Integer aiMessageCount) { this.aiMessageCount = aiMessageCount; }
    public Integer getCorrectionCount() { return correctionCount; }
    public void setCorrectionCount(Integer correctionCount) { this.correctionCount = correctionCount; }
    public Long getTotalTokensUsed() { return totalTokensUsed; }
    public void setTotalTokensUsed(Long totalTokensUsed) { this.totalTokensUsed = totalTokensUsed; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
    public Integer getDifficultySnapshot() { return difficultySnapshot; }
    public void setDifficultySnapshot(Integer difficultySnapshot) { this.difficultySnapshot = difficultySnapshot; }
    public String getCefrLevelSnapshot() { return cefrLevelSnapshot; }
    public void setCefrLevelSnapshot(String cefrLevelSnapshot) { this.cefrLevelSnapshot = cefrLevelSnapshot; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
