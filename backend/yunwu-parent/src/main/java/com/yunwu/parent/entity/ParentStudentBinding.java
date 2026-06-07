package com.yunwu.parent.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("parent_student_bindings")
public class ParentStudentBinding implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id;
    private Long parentId; private Long studentId; private String bindingStatus; private String relationship;
    private Boolean canViewReport; private Boolean canSetTimeLimit; private Boolean canManagePayment;
    private Integer dailyTimeLimitMinutes; private BigDecimal monthlyBudgetLimit;
    private LocalDateTime requestedAt; private LocalDateTime approvedAt; private LocalDateTime unboundedAt;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; } public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getStudentId() { return studentId; } public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getBindingStatus() { return bindingStatus; } public void setBindingStatus(String bindingStatus) { this.bindingStatus = bindingStatus; }
    public String getRelationship() { return relationship; } public void setRelationship(String relationship) { this.relationship = relationship; }
    public Boolean getCanViewReport() { return canViewReport; } public void setCanViewReport(Boolean canViewReport) { this.canViewReport = canViewReport; }
    public Boolean getCanSetTimeLimit() { return canSetTimeLimit; } public void setCanSetTimeLimit(Boolean canSetTimeLimit) { this.canSetTimeLimit = canSetTimeLimit; }
    public Boolean getCanManagePayment() { return canManagePayment; } public void setCanManagePayment(Boolean canManagePayment) { this.canManagePayment = canManagePayment; }
    public Integer getDailyTimeLimitMinutes() { return dailyTimeLimitMinutes; } public void setDailyTimeLimitMinutes(Integer dailyTimeLimitMinutes) { this.dailyTimeLimitMinutes = dailyTimeLimitMinutes; }
    public BigDecimal getMonthlyBudgetLimit() { return monthlyBudgetLimit; } public void setMonthlyBudgetLimit(BigDecimal monthlyBudgetLimit) { this.monthlyBudgetLimit = monthlyBudgetLimit; }
    public LocalDateTime getRequestedAt() { return requestedAt; } public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; } public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public LocalDateTime getUnboundedAt() { return unboundedAt; } public void setUnboundedAt(LocalDateTime unboundedAt) { this.unboundedAt = unboundedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
