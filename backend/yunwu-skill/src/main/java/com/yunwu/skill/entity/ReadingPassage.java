package com.yunwu.skill.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("reading_passages")
public class ReadingPassage implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id;
    private String title; private String content; private Integer wordCount; private String source; private String sourceUrl;
    private String gradeLevel; private Integer difficulty; private String cefrLevel;
    private String questions; private String tags; private Boolean isPublished; private Long createdBy; private LocalDateTime deletedAt;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; } public void setContent(String content) { this.content = content; }
    public Integer getWordCount() { return wordCount; } public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }
    public String getSource() { return source; } public void setSource(String source) { this.source = source; }
    public String getSourceUrl() { return sourceUrl; } public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public String getGradeLevel() { return gradeLevel; } public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
    public Integer getDifficulty() { return difficulty; } public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    public String getCefrLevel() { return cefrLevel; } public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
    public String getQuestions() { return questions; } public void setQuestions(String questions) { this.questions = questions; }
    public String getTags() { return tags; } public void setTags(String tags) { this.tags = tags; }
    public Boolean getIsPublished() { return isPublished; } public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }
    public Long getCreatedBy() { return createdBy; } public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getDeletedAt() { return deletedAt; } public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
