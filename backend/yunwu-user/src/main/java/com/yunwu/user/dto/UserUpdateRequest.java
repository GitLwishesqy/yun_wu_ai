package com.yunwu.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 用户信息更新请求
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Schema(description = "用户信息更新请求")
public class UserUpdateRequest {

    @Size(max = 100, message = "昵称最长100个字符")
    private String nickname;

    @Schema(description = "学段: ELEMENTARY/JUNIOR/SENIOR/ADULT")
    private String gradeLevel;

    @Schema(description = "具体年级")
    private String gradeDetail;

    @Schema(description = "语音偏好: FEMALE/MALE")
    private String voicePreference;

    @Schema(description = "语速 0.5-2.0")
    private BigDecimal speechRate;

    @Schema(description = "主题模式: LIGHT/DARK/AUTO")
    private String themeMode;

    @Schema(description = "每日时长限制 (分钟)")
    private Integer dailyLimitMinutes;

    @Schema(description = "UI 字体缩放")
    private BigDecimal uiFontScale;

    // ==================== Getters & Setters ====================

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getGradeLevel() { return gradeLevel; }
    public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
    public String getGradeDetail() { return gradeDetail; }
    public void setGradeDetail(String gradeDetail) { this.gradeDetail = gradeDetail; }
    public String getVoicePreference() { return voicePreference; }
    public void setVoicePreference(String voicePreference) { this.voicePreference = voicePreference; }
    public BigDecimal getSpeechRate() { return speechRate; }
    public void setSpeechRate(BigDecimal speechRate) { this.speechRate = speechRate; }
    public String getThemeMode() { return themeMode; }
    public void setThemeMode(String themeMode) { this.themeMode = themeMode; }
    public Integer getDailyLimitMinutes() { return dailyLimitMinutes; }
    public void setDailyLimitMinutes(Integer dailyLimitMinutes) { this.dailyLimitMinutes = dailyLimitMinutes; }
    public BigDecimal getUiFontScale() { return uiFontScale; }
    public void setUiFontScale(BigDecimal uiFontScale) { this.uiFontScale = uiFontScale; }
}
