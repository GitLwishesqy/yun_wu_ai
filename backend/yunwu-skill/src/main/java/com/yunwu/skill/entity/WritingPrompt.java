package com.yunwu.skill.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("writing_prompts")
public class WritingPrompt implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id;
    private String title; private String prompt; private String promptEn;
    private Integer wordLimitMin; private Integer wordLimitMax; private Integer timeLimitMinutes;
    private String gradeLevel; private Integer difficulty; private String cefrLevel;
    private String scoringRubric; private String tags; private Boolean isPublished; private Long createdBy; private LocalDateTime deletedAt;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getPrompt() { return prompt; } public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getPromptEn() { return promptEn; } public void setPromptEn(String promptEn) { this.promptEn = promptEn; }
    public Integer getWordLimitMin() { return wordLimitMin; } public void setWordLimitMin(Integer wordLimitMin) { this.wordLimitMin = wordLimitMin; }
    public Integer getWordLimitMax() { return wordLimitMax; } public void setWordLimitMax(Integer wordLimitMax) { this.wordLimitMax = wordLimitMax; }
    public Integer getTimeLimitMinutes() { return timeLimitMinutes; } public void setTimeLimitMinutes(Integer timeLimitMinutes) { this.timeLimitMinutes = timeLimitMinutes; }
    public String getGradeLevel() { return gradeLevel; } public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
    public Integer getDifficulty() { return difficulty; } public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    public String getCefrLevel() { return cefrLevel; } public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
    public String getScoringRubric() { return scoringRubric; } public void setScoringRubric(String scoringRubric) { this.scoringRubric = scoringRubric; }
    public String getTags() { return tags; } public void setTags(String tags) { this.tags = tags; }
    public Boolean getIsPublished() { return isPublished; } public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }
    public Long getCreatedBy() { return createdBy; } public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getDeletedAt() { return deletedAt; } public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
