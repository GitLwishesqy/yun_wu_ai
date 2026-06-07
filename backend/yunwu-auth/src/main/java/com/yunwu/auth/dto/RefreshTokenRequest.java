package com.yunwu.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 刷新 Token 请求
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Schema(description = "刷新 Token 请求")
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh Token 不能为空")
    @Schema(description = "Refresh Token")
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
