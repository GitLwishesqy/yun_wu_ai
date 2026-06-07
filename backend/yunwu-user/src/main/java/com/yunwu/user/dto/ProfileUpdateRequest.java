package com.yunwu.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 学习档案更新请求
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Schema(description = "学习档案更新请求")
public class ProfileUpdateRequest {

    @Size(max = 500, message = "学习目标最长500个字符")
    private String learningGoal;

    private List<String> preferredTopics;

    public String getLearningGoal() { return learningGoal; }
    public void setLearningGoal(String learningGoal) { this.learningGoal = learningGoal; }
    public List<String> getPreferredTopics() { return preferredTopics; }
    public void setPreferredTopics(List<String> preferredTopics) { this.preferredTopics = preferredTopics; }
}
