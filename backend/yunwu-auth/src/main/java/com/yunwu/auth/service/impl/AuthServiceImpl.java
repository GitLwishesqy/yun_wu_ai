package com.yunwu.auth.service.impl;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.yunwu.auth.dto.LoginRequest;
import com.yunwu.auth.dto.LoginResponse;
import com.yunwu.auth.dto.RefreshTokenRequest;
import com.yunwu.auth.dto.SendCodeRequest;
import com.yunwu.auth.service.AuthService;
import com.yunwu.auth.util.JwtUtil;
import com.yunwu.common.constant.Constants;
import com.yunwu.common.exception.BusinessException;
import com.yunwu.common.exception.ErrorCode;
import com.yunwu.common.service.IUserAuthService;

import cn.hutool.core.util.RandomUtil;

/**
 * 鉴权服务实现 — 验证码、登录/注册、Token 管理
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final IUserAuthService userAuthService;

    /** 是否为开发模式 (开发模式下验证码固定为 123456) */
    @Value("${yunwu.auth.dev-mode:false}")
    private boolean devMode;

    public AuthServiceImpl(JwtUtil jwtUtil,
                           RedisTemplate<String, Object> redisTemplate,
                           @Autowired(required = false) IUserAuthService userAuthService) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.userAuthService = userAuthService;
    }

    // ==================== 发送验证码 ====================

    @Override
    public void sendVerifyCode(SendCodeRequest request, String ipAddress) {
        String phone = request.getPhone();
        String purpose = request.getPurpose();

        // 1. 频率限制检查
        checkSendRateLimit(phone);

        // 2. 生成验证码
        String code = generateCode();

        // 3. 存储到 Redis (有效期 5 分钟)
        String cacheKey = Constants.CACHE_VERIFY_CODE_PREFIX + purpose + ":" + phone;
        redisTemplate.opsForValue().set(cacheKey, code,
                Constants.VERIFY_CODE_EXPIRE_SECONDS, TimeUnit.SECONDS);

        // 4. 发送短信 (TODO: 接入阿里云/腾讯云短信服务)
        // 生产：smsService.sendTemplate(phone, templateCode, Map.of("code", code, "minutes", "5"), purpose, ipAddress);
        log.info("[SMS] 验证码已发送 phone={}, purpose={}, code={}", phone, purpose,
                devMode ? code : "******");

        // 5. 记录发送频率计数
        String rateKey = Constants.CACHE_VERIFY_CODE_PREFIX + "rate:" + phone;
        redisTemplate.opsForValue().increment(rateKey);
        redisTemplate.expire(rateKey, 1, TimeUnit.HOURS);
    }

    // ==================== 登录 ====================

    @Override
    public LoginResponse login(LoginRequest request, String ipAddress) {
        String phone = request.getPhone();
        String code = request.getCode();

        // 1. 校验验证码
        validateCode(phone, "LOGIN", code);

        // 2. 查找或创建用户
        IUserAuthService.UserAuthInfo authInfo;
        if (userAuthService != null) {
            authInfo = userAuthService.findOrCreateByPhone(phone, ipAddress);
        } else {
            // Fallback: 未接入用户模块时的 Mock 数据
            authInfo = new IUserAuthService.UserAuthInfo(
                    (long) Math.abs(phone.hashCode()), "STUDENT", "ACTIVE", false);
        }

        // 3. 检查用户状态
        if (!"ACTIVE".equals(authInfo.status())) {
            throw new BusinessException(ErrorCode.USER_BANNED);
        }

        // 4. 生成 Token
        Map<String, Object> tokenPair = jwtUtil.generateTokenPair(authInfo.userId(), authInfo.role());

        // 5. 删除已使用的验证码
        String cacheKey = Constants.CACHE_VERIFY_CODE_PREFIX + "LOGIN:" + phone;
        redisTemplate.delete(cacheKey);

        // 6. 构建响应
        LoginResponse response = new LoginResponse();

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(authInfo.userId());
        userInfo.setNickname("用户" + phone.substring(7));
        userInfo.setRole(authInfo.role());
        userInfo.setStatus(authInfo.status());
        userInfo.setIsNewUser(authInfo.isNewUser());
        response.setUser(userInfo);

        LoginResponse.TokenInfo tokenInfo = new LoginResponse.TokenInfo();
        tokenInfo.setAccessToken((String) tokenPair.get("access_token"));
        tokenInfo.setRefreshToken((String) tokenPair.get("refresh_token"));
        tokenInfo.setTokenType((String) tokenPair.get("token_type"));
        tokenInfo.setExpiresIn((Long) tokenPair.get("expires_in"));
        tokenInfo.setRefreshExpiresIn((Long) tokenPair.get("refresh_expires_in"));
        response.setTokens(tokenInfo);

        log.info("[Login] 用户登录成功 userId={}, phone={}, isNewUser={}",
                authInfo.userId(), phone, authInfo.isNewUser());
        return response;
    }

    // ==================== 刷新 Token ====================

    @Override
    public LoginResponse.TokenInfo refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 1. 校验 Refresh Token 是否有效
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 2. 检查是否已登出 (黑名单)
        if (isTokenBlacklisted(refreshToken)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Token 已失效，请重新登录");
        }

        // 3. 提取用户信息
        Long userId = jwtUtil.getUserId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        if (userId == null || role == null) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        // 4. 将旧 Refresh Token 加入黑名单 (防止重用)
        blacklistToken(refreshToken, Constants.REFRESH_TOKEN_EXPIRE_SECONDS);

        // 5. 生成新 Token Pair
        Map<String, Object> tokenPair = jwtUtil.generateTokenPair(userId, role);

        LoginResponse.TokenInfo tokenInfo = new LoginResponse.TokenInfo();
        tokenInfo.setAccessToken((String) tokenPair.get("access_token"));
        tokenInfo.setRefreshToken((String) tokenPair.get("refresh_token"));
        tokenInfo.setTokenType((String) tokenPair.get("token_type"));
        tokenInfo.setExpiresIn((Long) tokenPair.get("expires_in"));
        tokenInfo.setRefreshExpiresIn((Long) tokenPair.get("refresh_expires_in"));

        log.info("[Token] Token 刷新成功 userId={}", userId);
        return tokenInfo;
    }

    // ==================== 登出 ====================

    @Override
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return;
        }

        // 将 Refresh Token 和 Access Token 加入黑名单
        // 黑名单时间 = Token 剩余有效期
        try {
            long remainingTime = Constants.REFRESH_TOKEN_EXPIRE_SECONDS;
            blacklistToken(refreshToken, remainingTime);
            log.info("[Logout] 用户已登出");
        } catch (Exception e) {
            log.warn("[Logout] 登出处理异常: {}", e.getMessage());
        }
    }

    // ==================== 黑名单 ====================

    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = Constants.CACHE_TOKEN_PREFIX + "blacklist:" + hashToken(token);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private void blacklistToken(String token, long expireSeconds) {
        String key = Constants.CACHE_TOKEN_PREFIX + "blacklist:" + hashToken(token);
        redisTemplate.opsForValue().set(key, "1", expireSeconds, TimeUnit.SECONDS);
    }

    // ==================== 私有方法 ====================

    /**
     * 校验验证码
     */
    private void validateCode(String phone, String purpose, String code) {
        // 开发模式: 固定验证码 123456
        if (devMode && "123456".equals(code)) {
            return;
        }

        String cacheKey = Constants.CACHE_VERIFY_CODE_PREFIX + purpose + ":" + phone;
        String cachedCode = (String) redisTemplate.opsForValue().get(cacheKey);

        if (cachedCode == null) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_EXPIRED);
        }
        if (!cachedCode.equals(code)) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }
    }

    /**
     * 频率限制检查
     */
    private void checkSendRateLimit(String phone) {
        String rateKey = Constants.CACHE_VERIFY_CODE_PREFIX + "rate:" + phone;
        String retryKey = Constants.CACHE_VERIFY_CODE_PREFIX + "retry:" + phone;

        // 60 秒内不允许重复发送
        if (Boolean.TRUE.equals(redisTemplate.hasKey(retryKey))) {
            Long ttl = redisTemplate.getExpire(retryKey);
            throw new BusinessException(ErrorCode.VERIFY_CODE_TOO_FREQUENT,
                    "请 " + (ttl != null ? ttl : 60) + " 秒后再试");
        }

        // 每小时限制 5 次
        String countStr = (String) redisTemplate.opsForValue().get(rateKey);
        int count = countStr != null ? Integer.parseInt(countStr) : 0;
        if (count >= Constants.VERIFY_CODE_MAX_PER_HOUR) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_TOO_FREQUENT,
                    "该手机号发送验证码过于频繁，请 1 小时后再试");
        }

        // 设置重试间隔
        redisTemplate.opsForValue().set(retryKey, "1",
                Constants.VERIFY_CODE_RETRY_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 生成 6 位数字验证码
     */
    private String generateCode() {
        if (devMode) {
            return "123456";
        }
        return RandomUtil.randomNumbers(6);
    }

    /**
     * Token 哈希 (用于黑名单 key)
     */
    private String hashToken(String token) {
        return Integer.toHexString(token.hashCode());
    }
}
