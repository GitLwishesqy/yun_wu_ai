package com.yunwu.auth.security;

import com.yunwu.auth.service.AuthService;
import com.yunwu.auth.util.JwtUtil;
import com.yunwu.common.constant.Constants;
import com.yunwu.common.context.UserContext;
import com.yunwu.common.util.TraceIdGenerator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器 — 在 Spring Security 过滤器链中拦截每个请求，校验 Token
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        // 1. 设置 TraceId
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (!StringUtils.hasText(traceId)) {
            traceId = TraceIdGenerator.generate();
        }
        MDC.put("traceId", traceId);
        UserContext.setTraceId(traceId);
        UserContext.setIp(getClientIp(request));

        try {
            // 2. 提取 Token
            String token = extractToken(request);
            if (token != null) {
                authenticate(token, request);
            }
        } catch (ExpiredJwtException e) {
            log.debug("Token 已过期: {}", e.getMessage());
            // 不在这里返回 401，让 Spring Security 后续处理
        } catch (JwtException e) {
            log.debug("Token 无效: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT 认证异常: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);

        // 3. 清理 ThreadLocal
        UserContext.clear();
        MDC.clear();
    }

    // ==================== 私有方法 ====================

    /**
     * 从请求头提取 Bearer Token
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 认证 Token 并设置 SecurityContext
     */
    private void authenticate(String token, HttpServletRequest request) {
        // 1. 检查黑名单
        if (authService.isTokenBlacklisted(token)) {
            log.debug("Token 已在黑名单中");
            return;
        }

        // 2. 解析 Token
        var claims = jwtUtil.parseAccessToken(token);
        Long userId = claims.get(Constants.TOKEN_CLAIM_USER_ID, Long.class);
        String role = claims.get(Constants.TOKEN_CLAIM_ROLE, String.class);

        if (userId == null || role == null) {
            return;
        }

        // 3. 设置用户上下文
        UserContext.setUserId(userId);
        UserContext.setRole(role);

        // 4. 构建 Authentication 对象
        var authority = new SimpleGrantedAuthority("ROLE_" + role);
        var authentication = new UsernamePasswordAuthenticationToken(
                userId, token, Collections.singleton(authority));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
