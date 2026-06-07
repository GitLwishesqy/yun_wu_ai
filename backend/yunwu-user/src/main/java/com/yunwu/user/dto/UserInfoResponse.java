package com.yunwu.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户信息响应
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponse {

    private Long id;
    private String phone;
    private String email;
    private String nickname;
    private String avatarUrl;
    private String role;
    private String gradeLevel;
    private String gradeDetail;
    private String cefrLevel;
    private String status;
    private Boolean realNameVerified;
    private Integer dailyLimitMinutes;
    private String voicePreference;
    private BigDecimal speechRate;
    private BigDecimal uiFontScale;
    private String themeMode;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;

    // ==================== Getters & Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getGradeLevel() { return gradeLevel; }
    public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
    public String getGradeDetail() { return gradeDetail; }
    public void setGradeDetail(String gradeDetail) { this.gradeDetail = gradeDetail; }
    public String getCefrLevel() { return cefrLevel; }
    public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getRealNameVerified() { return realNameVerified; }
    public void setRealNameVerified(Boolean realNameVerified) { this.realNameVerified = realNameVerified; }
    public Integer getDailyLimitMinutes() { return dailyLimitMinutes; }
    public void setDailyLimitMinutes(Integer dailyLimitMinutes) { this.dailyLimitMinutes = dailyLimitMinutes; }
    public String getVoicePreference() { return voicePreference; }
    public void setVoicePreference(String voicePreference) { this.voicePreference = voicePreference; }
    public BigDecimal getSpeechRate() { return speechRate; }
    public void setSpeechRate(BigDecimal speechRate) { this.speechRate = speechRate; }
    public BigDecimal getUiFontScale() { return uiFontScale; }
    public void setUiFontScale(BigDecimal uiFontScale) { this.uiFontScale = uiFontScale; }
    public String getThemeMode() { return themeMode; }
    public void setThemeMode(String themeMode) { this.themeMode = themeMode; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
