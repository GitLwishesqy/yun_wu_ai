package com.yunwu.correction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 纠错记录实体 — 对应 corrections 表
 */
@TableName("corrections")
public class Correction implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long messageId;
    private Long sessionId;
    private Long userId;
    private String errorType;
    private String errorSubtype;
    private String severity;
    private String originalText;
    private String errorSpan;
    private String correctedText;
    private String explanation;
    private String explanationEn;
    private String improvementTip;
    private String relatedRule;
    private String correctionStrategy;
    private Boolean wasReviewed;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
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
    public String getExplanationEn() { return explanationEn; }
    public void setExplanationEn(String explanationEn) { this.explanationEn = explanationEn; }
    public String getImprovementTip() { return improvementTip; }
    public void setImprovementTip(String improvementTip) { this.improvementTip = improvementTip; }
    public String getRelatedRule() { return relatedRule; }
    public void setRelatedRule(String relatedRule) { this.relatedRule = relatedRule; }
    public String getCorrectionStrategy() { return correctionStrategy; }
    public void setCorrectionStrategy(String correctionStrategy) { this.correctionStrategy = correctionStrategy; }
    public Boolean getWasReviewed() { return wasReviewed; }
    public void setWasReviewed(Boolean wasReviewed) { this.wasReviewed = wasReviewed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
