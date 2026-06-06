package com.yunwu.evaluation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class EvaluationDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Detail {
        private Long id;
        private Long sessionId;
        private String evalType;
        private Map<String, BigDecimal> dimensionScores;
        private BigDecimal overallScore;
        private List<String> strengths;
        private List<String> weaknesses;
        private List<Map<String, String>> suggestions;
        private String feedbackSummary;
        private BigDecimal previousScore;
        private BigDecimal improvement;
        private LocalDateTime createdAt;

        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public Long getSessionId() { return sessionId; } public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
        public String getEvalType() { return evalType; } public void setEvalType(String evalType) { this.evalType = evalType; }
        public Map<String, BigDecimal> getDimensionScores() { return dimensionScores; } public void setDimensionScores(Map<String, BigDecimal> dimensionScores) { this.dimensionScores = dimensionScores; }
        public BigDecimal getOverallScore() { return overallScore; } public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }
        public List<String> getStrengths() { return strengths; } public void setStrengths(List<String> strengths) { this.strengths = strengths; }
        public List<String> getWeaknesses() { return weaknesses; } public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }
        public List<Map<String, String>> getSuggestions() { return suggestions; } public void setSuggestions(List<Map<String, String>> suggestions) { this.suggestions = suggestions; }
        public String getFeedbackSummary() { return feedbackSummary; } public void setFeedbackSummary(String feedbackSummary) { this.feedbackSummary = feedbackSummary; }
        public BigDecimal getPreviousScore() { return previousScore; } public void setPreviousScore(BigDecimal previousScore) { this.previousScore = previousScore; }
        public BigDecimal getImprovement() { return improvement; } public void setImprovement(BigDecimal improvement) { this.improvement = improvement; }
        public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListItem {
        private Long id;
        private Long sessionId;
        private String evalType;
        private BigDecimal overallScore;
        private BigDecimal improvement;
        private LocalDateTime createdAt;

        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public Long getSessionId() { return sessionId; } public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
        public String getEvalType() { return evalType; } public void setEvalType(String evalType) { this.evalType = evalType; }
        public BigDecimal getOverallScore() { return overallScore; } public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }
        public BigDecimal getImprovement() { return improvement; } public void setImprovement(BigDecimal improvement) { this.improvement = improvement; }
        public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Overview {
        private int totalEvaluations;
        private BigDecimal avgScore;
        private BigDecimal maxScore;
        private int thisMonthCount;
        private BigDecimal latestScore;
        private BigDecimal scoreChange;               // 最近 vs 上次的差值
        private List<ScoreTrend> recentTrend;
        private Map<String, BigDecimal> dimensionAverages;
        private List<Map<String, Object>> statsByType;

        public int getTotalEvaluations() { return totalEvaluations; } public void setTotalEvaluations(int totalEvaluations) { this.totalEvaluations = totalEvaluations; }
        public BigDecimal getAvgScore() { return avgScore; } public void setAvgScore(BigDecimal avgScore) { this.avgScore = avgScore; }
        public BigDecimal getMaxScore() { return maxScore; } public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }
        public int getThisMonthCount() { return thisMonthCount; } public void setThisMonthCount(int thisMonthCount) { this.thisMonthCount = thisMonthCount; }
        public BigDecimal getLatestScore() { return latestScore; } public void setLatestScore(BigDecimal latestScore) { this.latestScore = latestScore; }
        public BigDecimal getScoreChange() { return scoreChange; } public void setScoreChange(BigDecimal scoreChange) { this.scoreChange = scoreChange; }
        public List<ScoreTrend> getRecentTrend() { return recentTrend; } public void setRecentTrend(List<ScoreTrend> recentTrend) { this.recentTrend = recentTrend; }
        public Map<String, BigDecimal> getDimensionAverages() { return dimensionAverages; } public void setDimensionAverages(Map<String, BigDecimal> dimensionAverages) { this.dimensionAverages = dimensionAverages; }
        public List<Map<String, Object>> getStatsByType() { return statsByType; } public void setStatsByType(List<Map<String, Object>> statsByType) { this.statsByType = statsByType; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ScoreTrend {
        private BigDecimal overallScore;
        private LocalDateTime createdAt;

        public BigDecimal getOverallScore() { return overallScore; } public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }
        public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
