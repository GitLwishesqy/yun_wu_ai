package com.yunwu.common.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户上下文 — 基于 ThreadLocal 的当前请求用户信息持有者
 * <p>
 * 在 Filter/Interceptor 中设置，在业务代码中读取。
 * 请求结束后必须调用 {@link #clear()} 防止内存泄漏。
 *
 * @author YunWu Team
 * @since 1.0.0
 */
public final class UserContext {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_GRADE_LEVEL = "gradeLevel";
    private static final String KEY_CEFR_LEVEL = "cefrLevel";
    private static final String KEY_TRACE_ID = "traceId";
    private static final String KEY_IP = "ip";

    private UserContext() {
    }

    // ==================== 设置方法 ====================

    public static void setUserId(Long userId) {
        CONTEXT.get().put(KEY_USER_ID, userId);
    }

    public static void setUsername(String username) {
        CONTEXT.get().put(KEY_USERNAME, username);
    }

    public static void setRole(String role) {
        CONTEXT.get().put(KEY_ROLE, role);
    }

    public static void setGradeLevel(String gradeLevel) {
        CONTEXT.get().put(KEY_GRADE_LEVEL, gradeLevel);
    }

    public static void setCefrLevel(String cefrLevel) {
        CONTEXT.get().put(KEY_CEFR_LEVEL, cefrLevel);
    }

    public static void setTraceId(String traceId) {
        CONTEXT.get().put(KEY_TRACE_ID, traceId);
    }

    public static void setIp(String ip) {
        CONTEXT.get().put(KEY_IP, ip);
    }

    // ==================== 获取方法 ====================

    public static Long getUserId() {
        return (Long) CONTEXT.get().get(KEY_USER_ID);
    }

    public static String getUsername() {
        return (String) CONTEXT.get().get(KEY_USERNAME);
    }

    public static String getRole() {
        return (String) CONTEXT.get().get(KEY_ROLE);
    }

    public static String getGradeLevel() {
        return (String) CONTEXT.get().get(KEY_GRADE_LEVEL);
    }

    public static String getCefrLevel() {
        return (String) CONTEXT.get().get(KEY_CEFR_LEVEL);
    }

    public static String getTraceId() {
        return (String) CONTEXT.get().get(KEY_TRACE_ID);
    }

    public static String getIp() {
        return (String) CONTEXT.get().get(KEY_IP);
    }

    /**
     * 获取所有上下文信息 (用于日志 MDC)
     */
    public static Map<String, Object> getAll() {
        return new HashMap<>(CONTEXT.get());
    }

    // ==================== 清理 ====================

    /**
     * 清除 ThreadLocal — 必须在下游处理完后调用，防止内存泄漏
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
