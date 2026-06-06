package com.yunwu.incentive.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("achievements")
public class Achievement implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id;
    private String code; private String name; private String nameEn; private String description;
    private String iconUrl; private String category; private Integer pointsReward;
    private Boolean isSecret; private String conditionJson; private LocalDateTime createdAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getCode() { return code; } public void setCode(String code) { this.code = code; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getNameEn() { return nameEn; } public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public String getIconUrl() { return iconUrl; } public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public String getCategory() { return category; } public void setCategory(String category) { this.category = category; }
    public Integer getPointsReward() { return pointsReward; } public void setPointsReward(Integer pointsReward) { this.pointsReward = pointsReward; }
    public Boolean getIsSecret() { return isSecret; } public void setIsSecret(Boolean isSecret) { this.isSecret = isSecret; }
    public String getConditionJson() { return conditionJson; } public void setConditionJson(String conditionJson) { this.conditionJson = conditionJson; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
