package com.yunwu.clazz.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("class_assignments")
public class ClassAssignment implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id;
    private Long classId; private Long teacherId; private String title; private String description;
    private String assignmentType; private Long sceneId; private String contentRef;
    private LocalDateTime dueDate; private LocalDateTime deletedAt;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getClassId() { return classId; } public void setClassId(Long classId) { this.classId = classId; }
    public Long getTeacherId() { return teacherId; } public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public String getAssignmentType() { return assignmentType; } public void setAssignmentType(String assignmentType) { this.assignmentType = assignmentType; }
    public Long getSceneId() { return sceneId; } public void setSceneId(Long sceneId) { this.sceneId = sceneId; }
    public String getContentRef() { return contentRef; } public void setContentRef(String contentRef) { this.contentRef = contentRef; }
    public LocalDateTime getDueDate() { return dueDate; } public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getDeletedAt() { return deletedAt; } public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
