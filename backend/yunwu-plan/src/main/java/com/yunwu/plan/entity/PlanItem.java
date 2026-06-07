package com.yunwu.plan.entity;

import com.baomidou.mybatisplus.annotation.*; import java.io.Serializable; import java.time.LocalDate; import java.time.LocalDateTime;

@TableName("learning_plan_items")
public class PlanItem implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id; private Long planId; private Long userId;
    private String itemType; private Long itemRefId; private String itemName;
    private LocalDate scheduledDate; private Integer estimatedMinutes;
    private Boolean isCompleted; private LocalDateTime completedAt; private Integer pointsReward; private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getPlanId() { return planId; } public void setPlanId(Long planId) { this.planId = planId; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getItemType() { return itemType; } public void setItemType(String itemType) { this.itemType = itemType; }
    public Long getItemRefId() { return itemRefId; } public void setItemRefId(Long itemRefId) { this.itemRefId = itemRefId; }
    public String getItemName() { return itemName; } public void setItemName(String itemName) { this.itemName = itemName; }
    public LocalDate getScheduledDate() { return scheduledDate; } public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    public Integer getEstimatedMinutes() { return estimatedMinutes; } public void setEstimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }
    public Boolean getIsCompleted() { return isCompleted; } public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
    public LocalDateTime getCompletedAt() { return completedAt; } public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public Integer getPointsReward() { return pointsReward; } public void setPointsReward(Integer pointsReward) { this.pointsReward = pointsReward; }
    public Integer getSortOrder() { return sortOrder; } public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
