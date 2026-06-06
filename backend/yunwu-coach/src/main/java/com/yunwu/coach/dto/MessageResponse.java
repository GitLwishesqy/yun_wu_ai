package com.yunwu.coach.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * 消息响应 (含纠错信息)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {

    private Long id;
    private Long sessionId;
    private String role;
    private String content;
    private String contentType;
    private String audioUrl;
    private Integer audioDuration;
    private Boolean hasCorrection;
    private Integer sequenceNum;
    private LocalDateTime createdAt;

    // AI 消息专属
    private String modelName;
    private Integer tokensUsed;

    // 纠错信息 (仅 USER 消息可能有)
    private CorrectionInfo correction;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CorrectionInfo {
        private Long id;
        private String errorType;
        private String errorSubtype;
        private String severity;
        private String originalText;
        private String errorSpan;
        private String correctedText;
        private String explanation;
        private String improvementTip;
        private String relatedRule;
        private String correctionStrategy;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getErrorType() { return errorType; }
        public void setErrorType(String errorType) { this.errorType = errorType; }
        public String getErrorSubtype() { return errorSubtype; }
        public void setErrorSubtype(String errorSubtype) { this.errorSubtype = errorSubtype; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getOriginalText() { return originalText; }
        public void setOriginalText(String originalText) { this.originalText = originalText; }
        public String getErrorSpan() { return errorSpan; }
        public void setErrorSpan(String errorSpan) { this.errorSpan = errorSpan; }
        public String getCorrectedText() { return correctedText; }
        public void setCorrectedText(String correctedText) { this.correctedText = correctedText; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
        public String getImprovementTip() { return improvementTip; }
        public void setImprovementTip(String improvementTip) { this.improvementTip = improvementTip; }
        public String getRelatedRule() { return relatedRule; }
        public void setRelatedRule(String relatedRule) { this.relatedRule = relatedRule; }
        public String getCorrectionStrategy() { return correctionStrategy; }
        public void setCorrectionStrategy(String correctionStrategy) { this.correctionStrategy = correctionStrategy; }
    }

    // ==================== Getters & Setters ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public Integer getAudioDuration() { return audioDuration; }
    public void setAudioDuration(Integer audioDuration) { this.audioDuration = audioDuration; }
    public Boolean getHasCorrection() { return hasCorrection; }
    public void setHasCorrection(Boolean hasCorrection) { this.hasCorrection = hasCorrection; }
    public Integer getSequenceNum() { return sequenceNum; }
    public void setSequenceNum(Integer sequenceNum) { this.sequenceNum = sequenceNum; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public Integer getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(Integer tokensUsed) { this.tokensUsed = tokensUsed; }
    public CorrectionInfo getCorrection() { return correction; }
    public void setCorrection(CorrectionInfo correction) { this.correction = correction; }
}
