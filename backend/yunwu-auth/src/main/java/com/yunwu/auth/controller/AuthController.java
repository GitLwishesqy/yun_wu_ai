package com.yunwu.auth.controller;

import com.yunwu.auth.dto.*;
import com.yunwu.auth.service.AuthService;
import com.yunwu.common.constant.Constants;
import com.yunwu.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 鉴权控制器
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@RestController
@RequestMapping(Constants.API_PREFIX + "/auth")
@Tag(name = "鉴权模块", description = "验证码发送、登录/注册、Token 刷新、登出")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ==================== 发送验证码 ====================

    @PostMapping("/send-code")
    @Operation(summary = "发送验证码", description = "向指定手机号发送短信验证码")
    public ResponseEntity<ApiResponse<Void>> sendCode(@Valid @RequestBody SendCodeRequest request,
                                                       HttpServletRequest httpRequest) {
        authService.sendVerifyCode(request, getClientIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.ok());
    }

    // ==================== 登录 ====================

    @PostMapping("/login")
    @Operation(summary = "手机号登录/注册", description = "使用手机号和验证码登录，未注册用户自动创建账号")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request,
                                                              HttpServletRequest httpRequest) {
        LoginResponse response = authService.login(request, getClientIp(httpRequest));
        HttpStatus status = response.getUser().getIsNewUser() != null
                && response.getUser().getIsNewUser() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(ApiResponse.ok(response));
    }

    // ==================== 刷新 Token ====================

    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "使用 Refresh Token 获取新的 Access Token 和 Refresh Token")
    public ResponseEntity<ApiResponse<LoginResponse.TokenInfo>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse.TokenInfo tokens = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.ok(tokens));
    }

    // ==================== 登出 ====================

    @PostMapping("/logout")
    @Operation(summary = "登出", description = "将 Refresh Token 加入黑名单，使其失效")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok());
    }

    // ==================== 辅助方法 ====================

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
