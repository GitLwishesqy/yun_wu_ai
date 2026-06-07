package com.yunwu.coach.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 创建会话请求
 */
public class CreateSessionRequest {

    @NotNull(message = "场景ID不能为空")
    private Long sceneId;

    private String sessionType = "SCENE";

    public Long getSceneId() { return sceneId; }
    public void setSceneId(Long sceneId) { this.sceneId = sceneId; }
    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
}
