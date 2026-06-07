package com.yunwu.user.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习档案实体 — 对应 learner_profiles 表
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@TableName("learner_profiles")
public class LearnerProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户 ID (一对一) */
    private Long userId;

    /** 估算词汇量 */
    private Integer estimatedVocabularySize;

    /** CEFR 等级 */
    private String cefrLevel;

    /** 新课标等级 */
    private String chinaStandardLevel;

    /** 薄弱点 JSON */
    private String weaknesses;

    /** 累计学习天数 */
    private Integer totalLearningDays;

    /** 累计会话数 */
    private Integer totalSessionCount;

    /** 累计学习时长 (分钟) */
    private Integer totalLearningMinutes;

    /** 累计开口词汇数 */
    private Integer totalWordsSpoken;

    /** 平均正确率 */
    private BigDecimal avgAccuracyRate;

    /** 连续打卡天数 */
    private Integer streakDays;

    /** 历史最大连续天数 */
    private Integer maxStreakDays;

    /** 最后学习日期 */
    private LocalDate lastLearningDate;

    /** 偏好主题 JSON 数组 */
    private String preferredTopics;

    /** 学习目标 */
    private String learningGoal;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
