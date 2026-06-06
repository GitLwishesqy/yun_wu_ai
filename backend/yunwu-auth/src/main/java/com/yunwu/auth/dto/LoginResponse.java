package com.yunwu.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 登录响应
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Schema(description = "登录响应")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    @Schema(description = "用户信息")
    private UserInfo user;

    @Schema(description = "Token 信息")
    private TokenInfo tokens;

    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
    public TokenInfo getTokens() { return tokens; }
    public void setTokens(TokenInfo tokens) { this.tokens = tokens; }

    @Schema(description = "登录用户信息")
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String avatarUrl;
        private String role;
        private String gradeLevel;
        private String cefrLevel;
        private String status;
        private Boolean isNewUser;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getGradeLevel() { return gradeLevel; }
        public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
        public String getCefrLevel() { return cefrLevel; }
        public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Boolean getIsNewUser() { return isNewUser; }
        public void setIsNewUser(Boolean isNewUser) { this.isNewUser = isNewUser; }
    }

    @Schema(description = "Token 信息")
    public static class TokenInfo {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private Long expiresIn;
        private Long refreshExpiresIn;

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        public Long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
        public Long getRefreshExpiresIn() { return refreshExpiresIn; }
        public void setRefreshExpiresIn(Long refreshExpiresIn) { this.refreshExpiresIn = refreshExpiresIn; }
    }
}
