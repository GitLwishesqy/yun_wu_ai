package com.yunwu.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("learning_reports")
public class LearningReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String periodType;     // DAILY/WEEKLY/MONTHLY/SEMESTER
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String reportData;     // JSON
    private Boolean isRead;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime generatedAt;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getPeriodType() { return periodType; } public void setPeriodType(String periodType) { this.periodType = periodType; }
    public LocalDate getPeriodStart() { return periodStart; } public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; } public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    public String getReportData() { return reportData; } public void setReportData(String reportData) { this.reportData = reportData; }
    public Boolean getIsRead() { return isRead; } public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public LocalDateTime getGeneratedAt() { return generatedAt; } public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
