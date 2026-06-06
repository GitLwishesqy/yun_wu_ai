package com.yunwu.common.util;

import java.util.UUID;

/**
 * 链路追踪 ID 生成器
 *
 * @author YunWu Team
 * @since 1.0.0
 */
public final class TraceIdGenerator {

    private TraceIdGenerator() {
    }

    /**
     * 生成带时间戳的短 TraceId
     * 格式: 8 位十六进制时间戳 + 4 位随机
     */
    public static String generate() {
        long timestamp = System.currentTimeMillis() / 1000;
        String timePart = String.format("%08x", timestamp);
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 4);
        return timePart + randomPart;
    }

    /**
     * 完整 UUID TraceId
     */
    public static String generateFull() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
