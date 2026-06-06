package com.yunwu.user.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体 — 对应 users 表
 * <p>
 * 注意: 不使用基类继承，因为 MyBatis-Plus 注解在子类重写时存在兼容问题。
 * 所有字段直接定义在本类中。
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@TableName("users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 手机号 */
    private String phone;

    /** 手机号加密存储 */
    private String phoneEncrypted;

    /** 邮箱 */
    private String email;

    /** 密码哈希 (bcrypt) */
    private String passwordHash;

    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String avatarUrl;

    /** 角色: STUDENT/PARENT/TEACHER/ADMIN/SUPER_ADMIN */
    private String role;

    /** 学段: ELEMENTARY/JUNIOR/SENIOR/ADULT */
    private String gradeLevel;

    /** 具体年级: GRADE_1~12 / UNIVERSITY / WORKING */
    private String gradeDetail;

    /** CEFR 等级: A1/A2/B1/B2/C1/C2 */
    private String cefrLevel;

    /** 状态: ACTIVE/INACTIVE/SUSPENDED/BANNED */
    private String status;

    /** 是否实名认证 */
    private Boolean realNameVerified;

    /** 真实姓名 */
    private String realName;

    /** 身份证号加密 */
    private String idCardEncrypted;

    /** 每日时长限制 (分钟) */
    private Integer dailyLimitMinutes;

    /** 语音偏好: FEMALE/MALE */
    private String voicePreference;

    /** 语速: 0.5-2.0 */
    private BigDecimal speechRate;

    /** UI 字体缩放 */
    private BigDecimal uiFontScale;

    /** 主题模式: LIGHT/DARK/AUTO */
    private String themeMode;

    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;

    /** 最后登录 IP */
    private String lastLoginIp;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 删除时间 (软删除) */
    private LocalDateTime deletedAt;

    /** 创建者 ID */
    private Long createdBy;

    /** 更新者 ID */
    private Long updatedBy;

    // ==================== 便捷方法 ====================

    public boolean isDeleted() {
        return deletedAt != null;
    }

    // ==================== Getters & Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhoneEncrypted() { return phoneEncrypted; }
    public void setPhoneEncrypted(String phoneEncrypted) { this.phoneEncrypted = phoneEncrypted; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
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
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getIdCardEncrypted() { return idCardEncrypted; }
    public void setIdCardEncrypted(String idCardEncrypted) { this.idCardEncrypted = idCardEncrypted; }
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
    public String getLastLoginIp() { return lastLoginIp; }
    public void setLastLoginIp(String lastLoginIp) { this.lastLoginIp = lastLoginIp; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}
