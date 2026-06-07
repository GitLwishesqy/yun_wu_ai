package com.yunwu.coach.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * 会话响应
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponse {

    private Long id;
    private Long sceneId;
    private String sessionType;
    private String title;
    private String status;
    private Integer messageCount;
    private Integer correctionCount;
    private Integer durationSeconds;
    private Long totalTokensUsed;
    private SceneInfo sceneInfo;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String summary;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SceneInfo {
        private Long id;
        private String name;
        private String nameEn;
        private Integer difficulty;
        private String cefrLevel;
        private java.util.List<java.util.Map<String, String>> roles;
        private java.util.List<java.util.Map<String, String>> keywords;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getNameEn() { return nameEn; }
        public void setNameEn(String nameEn) { this.nameEn = nameEn; }
        public Integer getDifficulty() { return difficulty; }
        public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
        public String getCefrLevel() { return cefrLevel; }
        public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
        public java.util.List<java.util.Map<String, String>> getRoles() { return roles; }
        public void setRoles(java.util.List<java.util.Map<String, String>> roles) { this.roles = roles; }
        public java.util.List<java.util.Map<String, String>> getKeywords() { return keywords; }
        public void setKeywords(java.util.List<java.util.Map<String, String>> keywords) { this.keywords = keywords; }
    }

    // ==================== Getters & Setters ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Integer getCorrectionCount() { return correctionCount; }
    public void setCorrectionCount(Integer correctionCount) { this.correctionCount = correctionCount; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
    public Long getTotalTokensUsed() { return totalTokensUsed; }
    public void setTotalTokensUsed(Long totalTokensUsed) { this.totalTokensUsed = totalTokensUsed; }
    public SceneInfo getSceneInfo() { return sceneInfo; }
    public void setSceneInfo(SceneInfo sceneInfo) { this.sceneInfo = sceneInfo; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
