package com.yunwu.clazz.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("classes")
public class ClassEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id;
    private String name; private Long teacherId; private String description;
    private String gradeLevel; private String inviteCode; private Integer studentCount;
    private Boolean isArchived; private LocalDateTime deletedAt;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public Long getTeacherId() { return teacherId; } public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public String getGradeLevel() { return gradeLevel; } public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
    public String getInviteCode() { return inviteCode; } public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    public Integer getStudentCount() { return studentCount; } public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }
    public Boolean getIsArchived() { return isArchived; } public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }
    public LocalDateTime getDeletedAt() { return deletedAt; } public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
