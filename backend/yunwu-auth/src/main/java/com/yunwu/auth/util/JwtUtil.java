package com.yunwu.auth.util;

import com.yunwu.common.constant.Constants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JWT 工具类 — Access Token + Refresh Token 签发与校验
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private final SecretKey secretKey;

    /** Access Token 有效期 (秒) */
    @Value("${yunwu.auth.access-token-expire:7200}")
    private long accessTokenExpire;

    /** Refresh Token 有效期 (秒) */
    @Value("${yunwu.auth.refresh-token-expire:2592000}")
    private long refreshTokenExpire;

    public JwtUtil(@Value("${yunwu.auth.jwt-secret:yunwu-english-jwt-secret-key-2026-min-length-256-bits!!}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(secret.getBytes())));
    }

    // ==================== 生成 Token ====================

    /**
     * 生成 Access Token
     */
    public String generateAccessToken(Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.TOKEN_CLAIM_USER_ID, userId);
        claims.put(Constants.TOKEN_CLAIM_ROLE, role);
        claims.put("type", "access");

        return buildToken(claims, accessTokenExpire * 1000);
    }

    /**
     * 生成 Refresh Token
     */
    public String generateRefreshToken(Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.TOKEN_CLAIM_USER_ID, userId);
        claims.put(Constants.TOKEN_CLAIM_ROLE, role);
        claims.put("type", "refresh");

        return buildToken(claims, refreshTokenExpire * 1000);
    }

    /**
     * 同时生成 Access + Refresh Token
     */
    public Map<String, Object> generateTokenPair(Long userId, String role) {
        Map<String, Object> result = new HashMap<>();
        result.put("access_token", generateAccessToken(userId, role));
        result.put("refresh_token", generateRefreshToken(userId, role));
        result.put("token_type", Constants.TOKEN_TYPE);
        result.put("expires_in", accessTokenExpire);
        result.put("refresh_expires_in", refreshTokenExpire);
        return result;
    }

    // ==================== 解析 Token ====================

    /**
     * 解析 Access Token 中的 claims
     */
    public Claims parseAccessToken(String token) {
        return parseToken(token);
    }

    /**
     * 解析任意 Token 中的 claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("Token 已过期: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("Token 解析失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 从 Token 中提取用户 ID
     */
    public Long getUserId(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get(Constants.TOKEN_CLAIM_USER_ID, Long.class);
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * 从 Token 中提取角色
     */
    public String getRole(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get(Constants.TOKEN_CLAIM_ROLE, String.class);
        } catch (JwtException e) {
            return null;
        }
    }

    // ==================== 校验 Token ====================

    /**
     * 校验 Access Token 是否有效
     */
    public boolean validateAccessToken(String token) {
        return validateToken(token, "access");
    }

    /**
     * 校验 Refresh Token 是否有效
     */
    public boolean validateRefreshToken(String token) {
        return validateToken(token, "refresh");
    }

    private boolean validateToken(String token, String expectedType) {
        try {
            Claims claims = parseToken(token);
            String type = claims.get("type", String.class);
            return expectedType.equals(type) && !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 判断 Token 是否即将过期 (剩余时间 < threshold 秒)
     */
    public boolean isTokenExpiringSoon(String token, long thresholdSeconds) {
        try {
            Claims claims = parseToken(token);
            long remainingTime = claims.getExpiration().getTime() - System.currentTimeMillis();
            return remainingTime > 0 && remainingTime < thresholdSeconds * 1000;
        } catch (JwtException e) {
            return true;
        }
    }

    // ==================== 内部方法 ====================

    private String buildToken(Map<String, Object> claims, long expireMillis) {
        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expireMillis))
                .signWith(secretKey)
                .compact();
    }
}
