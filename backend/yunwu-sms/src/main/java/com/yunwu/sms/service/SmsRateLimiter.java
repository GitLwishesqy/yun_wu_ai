package com.yunwu.sms.service;

import com.yunwu.sms.mapper.SmsSendLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 短信风控限流器 — 多层次防护
 * <p>
 * 防护层级:
 * 1. 单号码重试间隔 (60s)
 * 2. 单号码每小时上限 (5次)
 * 3. 单号码每日上限 (10次)
 * 4. 单 IP 每日上限 (20次)
 * 5. 全平台每日总量熔断
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Service
public class SmsRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(SmsRateLimiter.class);

    private static final String RATE_RETRY_KEY = "yunwu:sms:retry:";     // 60s 重试间隔
    private static final String RATE_HOUR_KEY = "yunwu:sms:hour:";       // 每小时次数
    private static final String RATE_IP_KEY = "yunwu:sms:ip:";           // IP 每日次数
    private static final String RATE_GLOBAL_KEY = "yunwu:sms:global:daily"; // 全平台熔断

    private final RedisTemplate<String, Object> redisTemplate;
    private final SmsSendLogMapper smsSendLogMapper;

    /** 单号码每日上限 */
    @Value("${yunwu.sms.limit.phone-daily:10}")
    private int phoneDailyLimit;

    /** 单号码每小时上限 */
    @Value("${yunwu.sms.limit.phone-hourly:5}")
    private int phoneHourlyLimit;

    /** 单号码重试间隔 (秒) */
    @Value("${yunwu.sms.limit.retry-seconds:60}")
    private int retrySeconds;

    /** 单 IP 每日上限 */
    @Value("${yunwu.sms.limit.ip-daily:20}")
    private int ipDailyLimit;

    /** 全平台每日总量熔断阈值 */
    @Value("${yunwu.sms.limit.global-daily:100000}")
    private int globalDailyLimit;

    public SmsRateLimiter(RedisTemplate<String, Object> redisTemplate,
                          SmsSendLogMapper smsSendLogMapper) {
        this.redisTemplate = redisTemplate;
        this.smsSendLogMapper = smsSendLogMapper;
    }

    /**
     * 全量风控检查 — 返回 null 表示通过，否则返回错误信息
     */
    public RateLimitResult check(String phone, String ipAddress) {
        // 1. 60s 重试间隔
        String retryKey = RATE_RETRY_KEY + phone;
        Long retryTtl = redisTemplate.getExpire(retryKey);
        if (retryTtl != null && retryTtl > 0) {
            return RateLimitResult.deny("请 " + retryTtl + " 秒后再试");
        }

        // 2. 每小时限额
        String hourKey = RATE_HOUR_KEY + phone;
        String hourCount = (String) redisTemplate.opsForValue().get(hourKey);
        int hourlyCount = hourCount != null ? Integer.parseInt(hourCount) : 0;
        if (hourlyCount >= phoneHourlyLimit) {
            return RateLimitResult.deny("该手机号发送过于频繁，请 1 小时后再试");
        }

        // 3. 每日限额 (手机号) — 查数据库确保权威
        int dailyCount = smsSendLogMapper.countTodayByPhone(phone);
        if (dailyCount >= phoneDailyLimit) {
            return RateLimitResult.deny("该手机号今日发送次数已达上限");
        }

        // 4. 每日限额 (IP)
        if (ipAddress != null && !ipAddress.isEmpty()) {
            String ipKey = RATE_IP_KEY + ipAddress;
            int ipCount = smsSendLogMapper.countTodayByIp(ipAddress);
            if (ipCount >= ipDailyLimit) {
                return RateLimitResult.deny("该 IP 今日发送次数已达上限");
            }
        }

        // 5. 全平台熔断
        String globalCountStr = (String) redisTemplate.opsForValue().get(RATE_GLOBAL_KEY);
        int globalCount = globalCountStr != null ? Integer.parseInt(globalCountStr) : 0;
        if (globalCount >= globalDailyLimit) {
            log.error("[SMS-FUSE] 全平台短信发送量已达到熔断阈值 {}，暂停发送", globalDailyLimit);
            return RateLimitResult.deny("系统繁忙，请稍后再试");
        }

        // 通过 — 记录计数器
        redisTemplate.opsForValue().set(retryKey, "1", retrySeconds, TimeUnit.SECONDS);
        redisTemplate.opsForValue().increment(hourKey);
        redisTemplate.expire(hourKey, 1, TimeUnit.HOURS);
        redisTemplate.opsForValue().increment(RATE_GLOBAL_KEY);
        redisTemplate.expire(RATE_GLOBAL_KEY, 1, TimeUnit.DAYS);

        return RateLimitResult.allow();
    }

    /**
     * 风控检查结果
     */
    public static class RateLimitResult {
        private final boolean allowed;
        private final String message;

        private RateLimitResult(boolean allowed, String message) {
            this.allowed = allowed;
            this.message = message;
        }

        public static RateLimitResult allow() {
            return new RateLimitResult(true, null);
        }

        public static RateLimitResult deny(String message) {
            return new RateLimitResult(false, message);
        }

        public boolean isAllowed() { return allowed; }
        public String getMessage() { return message; }
    }
}
