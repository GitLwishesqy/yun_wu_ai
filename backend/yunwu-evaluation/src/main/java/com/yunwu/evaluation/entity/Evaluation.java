package com.yunwu.evaluation.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("evaluations")
public class Evaluation implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long sessionId;
    private String evalType;
    private String dimensionScores;   // JSON
    private BigDecimal overallScore;
    private String strengths;         // JSON
    private String weaknesses;        // JSON
    private String suggestions;       // JSON
    private String feedbackSummary;
    private BigDecimal previousScore;
    private BigDecimal improvement;
    private String modelName;
    private Integer evalDurationMs;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public Long getSessionId() { return sessionId; } public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getEvalType() { return evalType; } public void setEvalType(String evalType) { this.evalType = evalType; }
    public String getDimensionScores() { return dimensionScores; } public void setDimensionScores(String dimensionScores) { this.dimensionScores = dimensionScores; }
    public BigDecimal getOverallScore() { return overallScore; } public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }
    public String getStrengths() { return strengths; } public void setStrengths(String strengths) { this.strengths = strengths; }
    public String getWeaknesses() { return weaknesses; } public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }
    public String getSuggestions() { return suggestions; } public void setSuggestions(String suggestions) { this.suggestions = suggestions; }
    public String getFeedbackSummary() { return feedbackSummary; } public void setFeedbackSummary(String feedbackSummary) { this.feedbackSummary = feedbackSummary; }
    public BigDecimal getPreviousScore() { return previousScore; } public void setPreviousScore(BigDecimal previousScore) { this.previousScore = previousScore; }
    public BigDecimal getImprovement() { return improvement; } public void setImprovement(BigDecimal improvement) { this.improvement = improvement; }
    public String getModelName() { return modelName; } public void setModelName(String modelName) { this.modelName = modelName; }
    public Integer getEvalDurationMs() { return evalDurationMs; } public void setEvalDurationMs(Integer evalDurationMs) { this.evalDurationMs = evalDurationMs; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
