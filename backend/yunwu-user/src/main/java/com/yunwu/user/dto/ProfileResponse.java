package com.yunwu.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 学习档案响应
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponse {

    private Long id;
    private Long userId;
    private Integer estimatedVocabularySize;
    private String cefrLevel;
    private String chinaStandardLevel;
    private String weaknesses;
    private Integer totalLearningDays;
    private Integer totalSessionCount;
    private Integer totalLearningMinutes;
    private Integer totalWordsSpoken;
    private BigDecimal avgAccuracyRate;
    private Integer streakDays;
    private Integer maxStreakDays;
    private LocalDate lastLearningDate;
    private String preferredTopics;
    private String learningGoal;
    private java.util.List<LevelHistoryItem> levelHistory;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LevelHistoryItem {
        private String previousCefr;
        private String newCefr;
        private String changeReason;
        private String createdAt;

        public String getPreviousCefr() { return previousCefr; }
        public void setPreviousCefr(String previousCefr) { this.previousCefr = previousCefr; }
        public String getNewCefr() { return newCefr; }
        public void setNewCefr(String newCefr) { this.newCefr = newCefr; }
        public String getChangeReason() { return changeReason; }
        public void setChangeReason(String changeReason) { this.changeReason = changeReason; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }

    // ==================== Getters & Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getEstimatedVocabularySize() { return estimatedVocabularySize; }
    public void setEstimatedVocabularySize(Integer estimatedVocabularySize) { this.estimatedVocabularySize = estimatedVocabularySize; }
    public String getCefrLevel() { return cefrLevel; }
    public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
    public String getChinaStandardLevel() { return chinaStandardLevel; }
    public void setChinaStandardLevel(String chinaStandardLevel) { this.chinaStandardLevel = chinaStandardLevel; }
    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }
    public Integer getTotalLearningDays() { return totalLearningDays; }
    public void setTotalLearningDays(Integer totalLearningDays) { this.totalLearningDays = totalLearningDays; }
    public Integer getTotalSessionCount() { return totalSessionCount; }
    public void setTotalSessionCount(Integer totalSessionCount) { this.totalSessionCount = totalSessionCount; }
    public Integer getTotalLearningMinutes() { return totalLearningMinutes; }
    public void setTotalLearningMinutes(Integer totalLearningMinutes) { this.totalLearningMinutes = totalLearningMinutes; }
    public Integer getTotalWordsSpoken() { return totalWordsSpoken; }
    public void setTotalWordsSpoken(Integer totalWordsSpoken) { this.totalWordsSpoken = totalWordsSpoken; }
    public BigDecimal getAvgAccuracyRate() { return avgAccuracyRate; }
    public void setAvgAccuracyRate(BigDecimal avgAccuracyRate) { this.avgAccuracyRate = avgAccuracyRate; }
    public Integer getStreakDays() { return streakDays; }
    public void setStreakDays(Integer streakDays) { this.streakDays = streakDays; }
    public Integer getMaxStreakDays() { return maxStreakDays; }
    public void setMaxStreakDays(Integer maxStreakDays) { this.maxStreakDays = maxStreakDays; }
    public LocalDate getLastLearningDate() { return lastLearningDate; }
    public void setLastLearningDate(LocalDate lastLearningDate) { this.lastLearningDate = lastLearningDate; }
    public String getPreferredTopics() { return preferredTopics; }
    public void setPreferredTopics(String preferredTopics) { this.preferredTopics = preferredTopics; }
    public String getLearningGoal() { return learningGoal; }
    public void setLearningGoal(String learningGoal) { this.learningGoal = learningGoal; }
    public java.util.List<LevelHistoryItem> getLevelHistory() { return levelHistory; }
    public void setLevelHistory(java.util.List<LevelHistoryItem> levelHistory) { this.levelHistory = levelHistory; }
}
