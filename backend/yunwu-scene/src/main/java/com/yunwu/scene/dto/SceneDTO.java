package com.yunwu.scene.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 场景 DTO — 请求/响应共用
 */
public class SceneDTO {

    // ==================== 请求 ====================

    public static class CreateRequest {
        @NotBlank(message = "场景名称不能为空")
        @Size(max = 200)
        private String name;

        private String nameEn;
        private String description;
        private String descriptionEn;

        @NotBlank(message = "场景分类不能为空")
        private String category;

        private String gradeLevel;
        private Integer difficulty = 1;
        private String cefrLevel;
        private List<Map<String, String>> roles;
        private List<Map<String, String>> keywords;
        private List<Map<String, String>> targetSentences;
        private Map<String, String> openingDialogue;
        private Integer maxRounds = 30;
        private Integer estimatedDuration = 15;
        private List<String> tags;

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
        public List<Map<String, String>> getRoles() { return roles; }
        public void setRoles(List<Map<String, String>> roles) { this.roles = roles; }
        public List<Map<String, String>> getKeywords() { return keywords; }
        public void setKeywords(List<Map<String, String>> keywords) { this.keywords = keywords; }
        public List<Map<String, String>> getTargetSentences() { return targetSentences; }
        public void setTargetSentences(List<Map<String, String>> targetSentences) { this.targetSentences = targetSentences; }
        public Map<String, String> getOpeningDialogue() { return openingDialogue; }
        public void setOpeningDialogue(Map<String, String> openingDialogue) { this.openingDialogue = openingDialogue; }
        public Integer getMaxRounds() { return maxRounds; }
        public void setMaxRounds(Integer maxRounds) { this.maxRounds = maxRounds; }
        public Integer getEstimatedDuration() { return estimatedDuration; }
        public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }

    public static class UpdateRequest {
        private String name;
        private String nameEn;
        private String description;
        private String descriptionEn;
        private String category;
        private String gradeLevel;
        private Integer difficulty;
        private String cefrLevel;
        private List<Map<String, String>> roles;
        private List<Map<String, String>> keywords;
        private List<Map<String, String>> targetSentences;
        private Map<String, String> openingDialogue;
        private Integer maxRounds;
        private Integer estimatedDuration;
        private List<String> tags;

        // getters & setters (same pattern)
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
        public List<Map<String, String>> getRoles() { return roles; }
        public void setRoles(List<Map<String, String>> roles) { this.roles = roles; }
        public List<Map<String, String>> getKeywords() { return keywords; }
        public void setKeywords(List<Map<String, String>> keywords) { this.keywords = keywords; }
        public List<Map<String, String>> getTargetSentences() { return targetSentences; }
        public void setTargetSentences(List<Map<String, String>> targetSentences) { this.targetSentences = targetSentences; }
        public Map<String, String> getOpeningDialogue() { return openingDialogue; }
        public void setOpeningDialogue(Map<String, String> openingDialogue) { this.openingDialogue = openingDialogue; }
        public Integer getMaxRounds() { return maxRounds; }
        public void setMaxRounds(Integer maxRounds) { this.maxRounds = maxRounds; }
        public Integer getEstimatedDuration() { return estimatedDuration; }
        public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }

    // ==================== 响应 ====================

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        private Long id;
        private String name;
        private String nameEn;
        private String description;
        private String category;
        private String gradeLevel;
        private Integer difficulty;
        private String cefrLevel;
        private List<Map<String, String>> roles;
        private List<Map<String, String>> keywords;
        private List<Map<String, String>> targetSentences;
        private Map<String, String> openingDialogue;
        private Integer maxRounds;
        private Integer estimatedDuration;
        private Boolean isPublished;
        private Integer version;
        private List<String> tags;
        private Long createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getNameEn() { return nameEn; }
        public void setNameEn(String nameEn) { this.nameEn = nameEn; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getGradeLevel() { return gradeLevel; }
        public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
        public Integer getDifficulty() { return difficulty; }
        public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
        public String getCefrLevel() { return cefrLevel; }
        public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
        public List<Map<String, String>> getRoles() { return roles; }
        public void setRoles(List<Map<String, String>> roles) { this.roles = roles; }
        public List<Map<String, String>> getKeywords() { return keywords; }
        public void setKeywords(List<Map<String, String>> keywords) { this.keywords = keywords; }
        public List<Map<String, String>> getTargetSentences() { return targetSentences; }
        public void setTargetSentences(List<Map<String, String>> targetSentences) { this.targetSentences = targetSentences; }
        public Map<String, String> getOpeningDialogue() { return openingDialogue; }
        public void setOpeningDialogue(Map<String, String> openingDialogue) { this.openingDialogue = openingDialogue; }
        public Integer getMaxRounds() { return maxRounds; }
        public void setMaxRounds(Integer maxRounds) { this.maxRounds = maxRounds; }
        public Integer getEstimatedDuration() { return estimatedDuration; }
        public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }
        public Boolean getIsPublished() { return isPublished; }
        public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }
        public Integer getVersion() { return version; }
        public void setVersion(Integer version) { this.version = version; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public Long getCreatedBy() { return createdBy; }
        public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListItem {
        private Long id;
        private String name;
        private String nameEn;
        private String category;
        private String gradeLevel;
        private Integer difficulty;
        private String cefrLevel;
        private Integer estimatedDuration;
        private List<String> tags;
        private Boolean isPublished;
        private Integer version;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getNameEn() { return nameEn; }
        public void setNameEn(String nameEn) { this.nameEn = nameEn; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getGradeLevel() { return gradeLevel; }
        public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
        public Integer getDifficulty() { return difficulty; }
        public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
        public String getCefrLevel() { return cefrLevel; }
        public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
        public Integer getEstimatedDuration() { return estimatedDuration; }
        public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public Boolean getIsPublished() { return isPublished; }
        public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }
        public Integer getVersion() { return version; }
        public void setVersion(Integer version) { this.version = version; }
    }
}
