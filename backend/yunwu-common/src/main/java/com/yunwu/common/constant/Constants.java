package com.yunwu.common.constant;

/**
 * 全局常量
 *
 * @author YunWu Team
 * @since 1.0.0
 */
public final class Constants {

    private Constants() {
    }

    /** 全局日期格式 */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMEZONE_DEFAULT = "Asia/Shanghai";

    /** API 版本 */
    public static final String API_VERSION = "v1";
    public static final String API_PREFIX = "/api/" + API_VERSION;

    /** Token */
    public static final String TOKEN_TYPE = "Bearer";
    public static final long ACCESS_TOKEN_EXPIRE_SECONDS = 7200L;      // 2 小时
    public static final long REFRESH_TOKEN_EXPIRE_SECONDS = 2592000L;  // 30 天
    public static final String TOKEN_CLAIM_USER_ID = "user_id";
    public static final String TOKEN_CLAIM_ROLE = "role";

    /** 陪练限制 */
    public static final int MAX_SESSION_ROUNDS = 50;           // 单次会话最大轮次
    public static final int MAX_DAILY_SESSIONS = 20;           // 单用户每日最大会话数
    public static final int MAX_INPUT_LENGTH = 2000;           // 用户输入最大字符数
    public static final int DEFAULT_DAILY_LIMIT_MINUTES = 60;  // 默认每日时长限制

    /** 积分 */
    public static final int POINTS_PER_CHECKIN = 5;            // 每日打卡积分
    public static final int POINTS_PER_SESSION = 10;           // 每次完成陪练积分
    public static final int POINTS_PER_PERFECT_SCORE = 20;     // 满分额外积分

    /** 分页 */
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    /** 缓存 Key 前缀 */
    public static final String CACHE_USER_PREFIX = "yunwu:user:";
    public static final String CACHE_SESSION_PREFIX = "yunwu:session:";
    public static final String CACHE_TOKEN_PREFIX = "yunwu:token:";
    public static final String CACHE_VERIFY_CODE_PREFIX = "yunwu:vcode:";

    /** 验证码 */
    public static final int VERIFY_CODE_LENGTH = 6;
    public static final int VERIFY_CODE_EXPIRE_SECONDS = 300;      // 5 分钟
    public static final int VERIFY_CODE_RETRY_SECONDS = 60;        // 重发间隔
    public static final int VERIFY_CODE_MAX_PER_HOUR = 5;          // 每小时最大发送次数
    public static final int VERIFY_CODE_MAX_PER_DAY = 10;          // 每天最大发送次数

    /** 音频 */
    public static final long MAX_AUDIO_UPLOAD_SIZE = 10 * 1024 * 1024;   // 10MB
    public static final String AUDIO_CONTENT_TYPE = "audio/mpeg";

    /** 难度自适应阈值 */
    public static final int CONSECUTIVE_CORRECT_TO_UPGRADE = 3;    // 连续 N 轮正确→升级
    public static final int CONSECUTIVE_SAME_ERROR_TO_DOWNGRADE = 2; // 连续 N 次同类错误→降级
}
