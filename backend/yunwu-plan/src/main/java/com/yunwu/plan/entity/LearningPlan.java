package com.yunwu.plan.entity;

import com.baomidou.mybatisplus.annotation.*; import java.io.Serializable; import java.time.LocalDate; import java.time.LocalDateTime;

@TableName("learning_plans")
public class LearningPlan implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id; private Long userId; private String name; private String description;
    private String planType; private LocalDate startDate; private LocalDate endDate; private String targetLevel;
    private Integer totalItems; private Integer completedItems; private Boolean isActive;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public String getPlanType() { return planType; } public void setPlanType(String planType) { this.planType = planType; }
    public LocalDate getStartDate() { return startDate; } public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; } public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getTargetLevel() { return targetLevel; } public void setTargetLevel(String targetLevel) { this.targetLevel = targetLevel; }
    public Integer getTotalItems() { return totalItems; } public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
    public Integer getCompletedItems() { return completedItems; } public void setCompletedItems(Integer completedItems) { this.completedItems = completedItems; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
