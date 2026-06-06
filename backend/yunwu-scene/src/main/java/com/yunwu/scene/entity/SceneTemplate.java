package com.yunwu.scene.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 场景模板实体 — 对应 scene_templates 表
 */
@TableName("scene_templates")
public class SceneTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String nameEn;
    private String description;
    private String descriptionEn;
    private String category;        // DAILY_LIFE/TRAVEL/ACADEMIC/BUSINESS/EXAM/SOCIAL
    private String gradeLevel;      // ELEMENTARY/JUNIOR/SENIOR/ADULT
    private Integer difficulty;     // 1-9
    private String cefrLevel;       // A1/A2/B1/B2/C1/C2
    private String roles;           // JSON
    private String keywords;        // JSON
    private String targetSentences; // JSON
    private String openingDialogue; // JSON
    private Integer maxRounds;
    private Integer estimatedDuration; // 分钟
    private Boolean isPublished;
    private Integer version;
    private String tags;            // JSON
    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public boolean isDeleted() { return deletedAt != null; }

    // ==================== Getters & Setters ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getGradeLevel() { return gradeLevel; }
    public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
    public Integer getDifficulty() { return difficulty; }
    public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    public String getCefrLevel() { return cefrLevel; }
    public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
    public String getTargetSentences() { return targetSentences; }
    public void setTargetSentences(String targetSentences) { this.targetSentences = targetSentences; }
    public String getOpeningDialogue() { return openingDialogue; }
    public void setOpeningDialogue(String openingDialogue) { this.openingDialogue = openingDialogue; }
    public Integer getMaxRounds() { return maxRounds; }
    public void setMaxRounds(Integer maxRounds) { this.maxRounds = maxRounds; }
    public Integer getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }
    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
