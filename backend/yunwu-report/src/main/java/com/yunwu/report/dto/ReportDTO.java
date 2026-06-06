package com.yunwu.report.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReportDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Detail {
        private Long id;
        private String periodType;
        private LocalDate periodStart;
        private LocalDate periodEnd;
        private Boolean isRead;
        private LocalDateTime generatedAt;
        private ReportData reportData;

        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getPeriodType() { return periodType; } public void setPeriodType(String periodType) { this.periodType = periodType; }
        public LocalDate getPeriodStart() { return periodStart; } public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
        public LocalDate getPeriodEnd() { return periodEnd; } public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
        public Boolean getIsRead() { return isRead; } public void setIsRead(Boolean isRead) { this.isRead = isRead; }
        public LocalDateTime getGeneratedAt() { return generatedAt; } public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
        public ReportData getReportData() { return reportData; } public void setReportData(ReportData reportData) { this.reportData = reportData; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListItem {
        private Long id;
        private String periodType;
        private LocalDate periodStart;
        private LocalDate periodEnd;
        private Boolean isRead;
        private LocalDateTime generatedAt;

        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getPeriodType() { return periodType; } public void setPeriodType(String periodType) { this.periodType = periodType; }
        public LocalDate getPeriodStart() { return periodStart; } public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
        public LocalDate getPeriodEnd() { return periodEnd; } public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
        public Boolean getIsRead() { return isRead; } public void setIsRead(Boolean isRead) { this.isRead = isRead; }
        public LocalDateTime getGeneratedAt() { return generatedAt; } public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    }

    /** 报告数据 — 子结构 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReportData {
        private String summary;
        private StatsInfo stats;
        private Map<String, DimensionChange> dimensionTrend;
        private List<DayBreakdown> dailyBreakdown;
        private List<ErrorItem> topErrors;
        private List<SceneItem> sceneDistribution;
        private Map<String, Object> comparisonToLastPeriod;
        private List<String> nextPeriodSuggestions;

        public String getSummary() { return summary; } public void setSummary(String summary) { this.summary = summary; }
        public StatsInfo getStats() { return stats; } public void setStats(StatsInfo stats) { this.stats = stats; }
        public Map<String, DimensionChange> getDimensionTrend() { return dimensionTrend; } public void setDimensionTrend(Map<String, DimensionChange> dimensionTrend) { this.dimensionTrend = dimensionTrend; }
        public List<DayBreakdown> getDailyBreakdown() { return dailyBreakdown; } public void setDailyBreakdown(List<DayBreakdown> dailyBreakdown) { this.dailyBreakdown = dailyBreakdown; }
        public List<ErrorItem> getTopErrors() { return topErrors; } public void setTopErrors(List<ErrorItem> topErrors) { this.topErrors = topErrors; }
        public List<SceneItem> getSceneDistribution() { return sceneDistribution; } public void setSceneDistribution(List<SceneItem> sceneDistribution) { this.sceneDistribution = sceneDistribution; }
        public Map<String, Object> getComparisonToLastPeriod() { return comparisonToLastPeriod; } public void setComparisonToLastPeriod(Map<String, Object> comparisonToLastPeriod) { this.comparisonToLastPeriod = comparisonToLastPeriod; }
        public List<String> getNextPeriodSuggestions() { return nextPeriodSuggestions; } public void setNextPeriodSuggestions(List<String> nextPeriodSuggestions) { this.nextPeriodSuggestions = nextPeriodSuggestions; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StatsInfo {
        private int totalSessions;
        private int totalMinutes;
        private int totalMessages;
        private int totalCorrections;
        private double avgScore;
        private double scoreChange;
        private int newVocabulary;

        public int getTotalSessions() { return totalSessions; } public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
        public int getTotalMinutes() { return totalMinutes; } public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }
        public int getTotalMessages() { return totalMessages; } public void setTotalMessages(int totalMessages) { this.totalMessages = totalMessages; }
        public int getTotalCorrections() { return totalCorrections; } public void setTotalCorrections(int totalCorrections) { this.totalCorrections = totalCorrections; }
        public double getAvgScore() { return avgScore; } public void setAvgScore(double avgScore) { this.avgScore = avgScore; }
        public double getScoreChange() { return scoreChange; } public void setScoreChange(double scoreChange) { this.scoreChange = scoreChange; }
        public int getNewVocabulary() { return newVocabulary; } public void setNewVocabulary(int newVocabulary) { this.newVocabulary = newVocabulary; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DimensionChange {
        private double current;
        private double change;
        public double getCurrent() { return current; } public void setCurrent(double current) { this.current = current; }
        public double getChange() { return change; } public void setChange(double change) { this.change = change; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DayBreakdown {
        private String date;
        private int sessions;
        private int minutes;
        public String getDate() { return date; } public void setDate(String date) { this.date = date; }
        public int getSessions() { return sessions; } public void setSessions(int sessions) { this.sessions = sessions; }
        public int getMinutes() { return minutes; } public void setMinutes(int minutes) { this.minutes = minutes; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorItem {
        private String type;
        private int count;
        private String trend;
        public String getType() { return type; } public void setType(String type) { this.type = type; }
        public int getCount() { return count; } public void setCount(int count) { this.count = count; }
        public String getTrend() { return trend; } public void setTrend(String trend) { this.trend = trend; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SceneItem {
        private String sceneName;
        private int count;
        public String getSceneName() { return sceneName; } public void setSceneName(String sceneName) { this.sceneName = sceneName; }
        public int getCount() { return count; } public void setCount(int count) { this.count = count; }
    }
}
