package com.yunwu.correction.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 错误汇总记录 — 对应 error_records 表 (艾宾浩斯复习)
 */
@TableName("error_records")
public class ErrorRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String errorType;
    private String errorSubtype;
    private String errorPattern;
    private Integer totalCount;
    private Integer correctCount;
    private LocalDateTime lastErrorAt;
    private LocalDateTime lastCorrectAt;
    private String masteryStatus;    // LEARNING / REVIEWING / MASTERED / ARCHIVED
    private LocalDateTime nextReviewAt;
    private Integer reviewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getErrorType() { return errorType; }
    public void setErrorType(String errorType) { this.errorType = errorType; }
    public String getErrorSubtype() { return errorSubtype; }
    public void setErrorSubtype(String errorSubtype) { this.errorSubtype = errorSubtype; }
    public String getErrorPattern() { return errorPattern; }
    public void setErrorPattern(String errorPattern) { this.errorPattern = errorPattern; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getCorrectCount() { return correctCount; }
    public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }
    public LocalDateTime getLastErrorAt() { return lastErrorAt; }
    public void setLastErrorAt(LocalDateTime lastErrorAt) { this.lastErrorAt = lastErrorAt; }
    public LocalDateTime getLastCorrectAt() { return lastCorrectAt; }
    public void setLastCorrectAt(LocalDateTime lastCorrectAt) { this.lastCorrectAt = lastCorrectAt; }
    public String getMasteryStatus() { return masteryStatus; }
    public void setMasteryStatus(String masteryStatus) { this.masteryStatus = masteryStatus; }
    public LocalDateTime getNextReviewAt() { return nextReviewAt; }
    public void setNextReviewAt(LocalDateTime nextReviewAt) { this.nextReviewAt = nextReviewAt; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
