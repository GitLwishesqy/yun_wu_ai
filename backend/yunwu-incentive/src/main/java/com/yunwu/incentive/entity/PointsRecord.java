package com.yunwu.incentive.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("points_records")
public class PointsRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId; private Integer points; private Integer balanceAfter;
    private String actionType; private String actionDesc; private Long referenceId;
    private LocalDateTime createdAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public Integer getPoints() { return points; } public void setPoints(Integer points) { this.points = points; }
    public Integer getBalanceAfter() { return balanceAfter; } public void setBalanceAfter(Integer balanceAfter) { this.balanceAfter = balanceAfter; }
    public String getActionType() { return actionType; } public void setActionType(String actionType) { this.actionType = actionType; }
    public String getActionDesc() { return actionDesc; } public void setActionDesc(String actionDesc) { this.actionDesc = actionDesc; }
    public Long getReferenceId() { return referenceId; } public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
