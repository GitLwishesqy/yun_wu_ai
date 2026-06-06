package com.yunwu.correction.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class CorrectionDTO {

    // ==================== 纠错项 ====================
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Item {
        private Long id;
        private Long sessionId;
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
        private Boolean wasReviewed;
        private LocalDateTime createdAt;

        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public Long getSessionId() { return sessionId; } public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
        public String getErrorType() { return errorType; } public void setErrorType(String errorType) { this.errorType = errorType; }
        public String getErrorSubtype() { return errorSubtype; } public void setErrorSubtype(String errorSubtype) { this.errorSubtype = errorSubtype; }
        public String getSeverity() { return severity; } public void setSeverity(String severity) { this.severity = severity; }
        public String getOriginalText() { return originalText; } public void setOriginalText(String originalText) { this.originalText = originalText; }
        public String getErrorSpan() { return errorSpan; } public void setErrorSpan(String errorSpan) { this.errorSpan = errorSpan; }
        public String getCorrectedText() { return correctedText; } public void setCorrectedText(String correctedText) { this.correctedText = correctedText; }
        public String getExplanation() { return explanation; } public void setExplanation(String explanation) { this.explanation = explanation; }
        public String getImprovementTip() { return improvementTip; } public void setImprovementTip(String improvementTip) { this.improvementTip = improvementTip; }
        public String getRelatedRule() { return relatedRule; } public void setRelatedRule(String relatedRule) { this.relatedRule = relatedRule; }
        public String getCorrectionStrategy() { return correctionStrategy; } public void setCorrectionStrategy(String correctionStrategy) { this.correctionStrategy = correctionStrategy; }
        public Boolean getWasReviewed() { return wasReviewed; } public void setWasReviewed(Boolean wasReviewed) { this.wasReviewed = wasReviewed; }
        public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    // ==================== 错误分析 ====================
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorAnalysis {
        private Map<String, Double> radar;          // {grammar:0.4, pronunciation:0.6, ...}
        private List<TypeStat> topErrors;
        private List<TrendPoint> dailyTrend;
        private List<WeakPoint> weakPoints;
        private List<ReviewItem> reviewDue;

        public Map<String, Double> getRadar() { return radar; } public void setRadar(Map<String, Double> radar) { this.radar = radar; }
        public List<TypeStat> getTopErrors() { return topErrors; } public void setTopErrors(List<TypeStat> topErrors) { this.topErrors = topErrors; }
        public List<TrendPoint> getDailyTrend() { return dailyTrend; } public void setDailyTrend(List<TrendPoint> dailyTrend) { this.dailyTrend = dailyTrend; }
        public List<WeakPoint> getWeakPoints() { return weakPoints; } public void setWeakPoints(List<WeakPoint> weakPoints) { this.weakPoints = weakPoints; }
        public List<ReviewItem> getReviewDue() { return reviewDue; } public void setReviewDue(List<ReviewItem> reviewDue) { this.reviewDue = reviewDue; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TypeStat {
        private String errorType;
        private String errorSubtype;
        private int count;
        private String masteryStatus;

        public String getErrorType() { return errorType; } public void setErrorType(String errorType) { this.errorType = errorType; }
        public String getErrorSubtype() { return errorSubtype; } public void setErrorSubtype(String errorSubtype) { this.errorSubtype = errorSubtype; }
        public int getCount() { return count; } public void setCount(int count) { this.count = count; }
        public String getMasteryStatus() { return masteryStatus; } public void setMasteryStatus(String masteryStatus) { this.masteryStatus = masteryStatus; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TrendPoint {
        private String date;
        private int count;

        public String getDate() { return date; } public void setDate(String date) { this.date = date; }
        public int getCount() { return count; } public void setCount(int count) { this.count = count; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WeakPoint {
        private String errorType;
        private String errorSubtype;
        private String errorPattern;
        private int totalCount;
        private String masteryStatus;
        private LocalDateTime lastErrorAt;

        public String getErrorType() { return errorType; } public void setErrorType(String errorType) { this.errorType = errorType; }
        public String getErrorSubtype() { return errorSubtype; } public void setErrorSubtype(String errorSubtype) { this.errorSubtype = errorSubtype; }
        public String getErrorPattern() { return errorPattern; } public void setErrorPattern(String errorPattern) { this.errorPattern = errorPattern; }
        public int getTotalCount() { return totalCount; } public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public String getMasteryStatus() { return masteryStatus; } public void setMasteryStatus(String masteryStatus) { this.masteryStatus = masteryStatus; }
        public LocalDateTime getLastErrorAt() { return lastErrorAt; } public void setLastErrorAt(LocalDateTime lastErrorAt) { this.lastErrorAt = lastErrorAt; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReviewItem {
        private Long id;
        private String errorType;
        private String errorSubtype;
        private String errorPattern;
        private LocalDateTime nextReviewAt;
        private int reviewCount;

        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getErrorType() { return errorType; } public void setErrorType(String errorType) { this.errorType = errorType; }
        public String getErrorSubtype() { return errorSubtype; } public void setErrorSubtype(String errorSubtype) { this.errorSubtype = errorSubtype; }
        public String getErrorPattern() { return errorPattern; } public void setErrorPattern(String errorPattern) { this.errorPattern = errorPattern; }
        public LocalDateTime getNextReviewAt() { return nextReviewAt; } public void setNextReviewAt(LocalDateTime nextReviewAt) { this.nextReviewAt = nextReviewAt; }
        public int getReviewCount() { return reviewCount; } public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    }

    // ==================== 请求 ====================
    public static class ReviewRequest {
        private Long errorRecordId;
        private String masteryStatus;  // REVIEWING / MASTERED

        public Long getErrorRecordId() { return errorRecordId; } public void setErrorRecordId(Long errorRecordId) { this.errorRecordId = errorRecordId; }
        public String getMasteryStatus() { return masteryStatus; } public void setMasteryStatus(String masteryStatus) { this.masteryStatus = masteryStatus; }
    }

    // ==================== 汇总 ====================
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SessionCorrections {
        private List<Item> items;
        private Summary summary;

        public List<Item> getItems() { return items; } public void setItems(List<Item> items) { this.items = items; }
        public Summary getSummary() { return summary; } public void setSummary(Summary summary) { this.summary = summary; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Summary {
        private int total;
        private Map<String, Integer> byType;
        private Map<String, Integer> bySeverity;

        public int getTotal() { return total; } public void setTotal(int total) { this.total = total; }
        public Map<String, Integer> getByType() { return byType; } public void setByType(Map<String, Integer> byType) { this.byType = byType; }
        public Map<String, Integer> getBySeverity() { return bySeverity; } public void setBySeverity(Map<String, Integer> bySeverity) { this.bySeverity = bySeverity; }
    }
}
