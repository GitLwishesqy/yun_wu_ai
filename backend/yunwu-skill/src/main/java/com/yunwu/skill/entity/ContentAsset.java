package com.yunwu.skill.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("content_assets")
public class ContentAsset implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id;
    private String assetType; private String title; private String titleEn; private String description;
    private String fileUrl; private Long fileSize; private Integer durationSeconds;
    private String transcript; private String contentJson; private String gradeLevel;
    private Integer difficulty; private String cefrLevel; private Long sceneId;
    private String tags; private Long viewCount; private BigDecimal avgRating;
    private Boolean isPublished; private Long createdBy; private LocalDateTime deletedAt;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getAssetType() { return assetType; } public void setAssetType(String assetType) { this.assetType = assetType; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getTitleEn() { return titleEn; } public void setTitleEn(String titleEn) { this.titleEn = titleEn; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public String getFileUrl() { return fileUrl; } public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public Long getFileSize() { return fileSize; } public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public Integer getDurationSeconds() { return durationSeconds; } public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
    public String getTranscript() { return transcript; } public void setTranscript(String transcript) { this.transcript = transcript; }
    public String getContentJson() { return contentJson; } public void setContentJson(String contentJson) { this.contentJson = contentJson; }
    public String getGradeLevel() { return gradeLevel; } public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
    public Integer getDifficulty() { return difficulty; } public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    public String getCefrLevel() { return cefrLevel; } public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
    public Long getSceneId() { return sceneId; } public void setSceneId(Long sceneId) { this.sceneId = sceneId; }
    public String getTags() { return tags; } public void setTags(String tags) { this.tags = tags; }
    public Long getViewCount() { return viewCount; } public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
    public BigDecimal getAvgRating() { return avgRating; } public void setAvgRating(BigDecimal avgRating) { this.avgRating = avgRating; }
    public Boolean getIsPublished() { return isPublished; } public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }
    public Long getCreatedBy() { return createdBy; } public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getDeletedAt() { return deletedAt; } public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public boolean isDeleted() { return deletedAt != null; }
}
