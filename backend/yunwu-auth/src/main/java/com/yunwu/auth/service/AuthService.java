package com.yunwu.auth.service;

import com.yunwu.auth.dto.*;

/**
 * 鉴权服务接口
 *
 * @author YunWu Team
 * @since 1.0.0
 */
public interface AuthService {

    /**
     * 发送短信验证码
     */
    void sendVerifyCode(SendCodeRequest request, String ipAddress);

    /**
     * 手机号验证码登录 (自动注册)
     */
    LoginResponse login(LoginRequest request, String ipAddress);

    /**
     * 刷新 Token
     */
    LoginResponse.TokenInfo refreshToken(RefreshTokenRequest request);

    /**
     * 登出 (将 Refresh Token 加入黑名单)
     */
    void logout(String refreshToken);

    /**
     * 检查 Token 是否在黑名单中 (已登出)
     */
    boolean isTokenBlacklisted(String token);
}
