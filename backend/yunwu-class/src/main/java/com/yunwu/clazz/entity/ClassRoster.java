package com.yunwu.clazz.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("class_rosters")
public class ClassRoster implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id;
    private Long classId; private Long studentId; private LocalDateTime joinedAt; private LocalDateTime leftAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getClassId() { return classId; } public void setClassId(Long classId) { this.classId = classId; }
    public Long getStudentId() { return studentId; } public void setStudentId(Long studentId) { this.studentId = studentId; }
    public LocalDateTime getJoinedAt() { return joinedAt; } public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    public LocalDateTime getLeftAt() { return leftAt; } public void setLeftAt(LocalDateTime leftAt) { this.leftAt = leftAt; }
}
