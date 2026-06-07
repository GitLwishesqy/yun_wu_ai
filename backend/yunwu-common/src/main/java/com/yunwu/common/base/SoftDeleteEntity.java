package com.yunwu.common.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

/**
 * 支持软删除的实体基类
 *
 * @author YunWu Team
 * @since 1.0.0
 */
public abstract class SoftDeleteEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 删除时间 (非空表示已删除) */
    @JsonIgnore
    private LocalDateTime deletedAt;

    /** 创建者 ID */
    private Long createdBy;

    /** 更新者 ID */
    private Long updatedBy;

    // ==================== Getters & Setters ====================

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /** 是否已删除 */
    @JsonIgnore
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
