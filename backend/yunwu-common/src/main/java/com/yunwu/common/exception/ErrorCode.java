package com.yunwu.common.exception;

/**
 * 错误码枚举 — 遵循 API 文档 9 段制
 * <pre>
 * 1xxxx: 通用错误
 * 2xxxx: 鉴权错误
 * 3xxxx: 用户错误
 * 4xxxx: 陪练错误
 * 5xxxx: 评测错误
 * 6xxxx: 内容审核
 * 7xxxx: 家长/班级
 * 8xxxx: 系统/限流
 * 9xxxx: 第三方服务
 * </pre>
 *
 * @author YunWu Team
 * @since 1.0.0
 */
public enum ErrorCode {

    // ==================== 通用错误 (1xxxx) ====================
    SUCCESS(0, "success"),
    PARAM_INVALID(10001, "参数校验失败"),
    RESOURCE_NOT_FOUND(10002, "资源不存在"),
    RESOURCE_CONFLICT(10003, "资源冲突"),
    BUSINESS_ERROR(10004, "业务逻辑错误"),
    INTERNAL_ERROR(10005, "服务器内部错误"),
    METHOD_NOT_SUPPORTED(10006, "不支持的请求方法"),
    MEDIA_TYPE_NOT_SUPPORTED(10007, "不支持的媒体类型"),
    DATA_INTEGRITY_VIOLATION(10008, "数据完整性冲突"),

    // ==================== 鉴权错误 (2xxxx) ====================
    TOKEN_MISSING(20001, "Token 缺失"),
    TOKEN_EXPIRED(20002, "Token 已过期"),
    TOKEN_INVALID(20003, "Token 无效"),
    REFRESH_TOKEN_EXPIRED(20004, "Refresh Token 已过期"),
    ACCESS_DENIED(20005, "权限不足"),
    VERIFY_CODE_TOO_FREQUENT(20006, "验证码发送太频繁，请稍后再试"),
    VERIFY_CODE_ERROR(20007, "验证码错误"),
    VERIFY_CODE_EXPIRED(20008, "验证码已过期"),
    LOGIN_FAILED(20009, "登录失败"),

    // ==================== 用户错误 (3xxxx) ====================
    USER_NOT_FOUND(30001, "用户不存在"),
    USER_BANNED(30002, "用户已被封禁"),
    USER_SUSPENDED(30003, "用户已被停用"),
    PHONE_ALREADY_REGISTERED(30004, "该手机号已注册"),
    REAL_NAME_NOT_VERIFIED(30005, "实名认证未通过"),
    GRADE_LEVEL_NOT_SET(30006, "请先设置学段信息"),
    DAILY_LIMIT_REACHED(30007, "今日学习时长已用完"),
    PROFILE_NOT_FOUND(30008, "学习档案不存在"),

    // ==================== 陪练错误 (4xxxx) ====================
    SESSION_NOT_FOUND(40001, "会话不存在"),
    SESSION_ALREADY_ENDED(40002, "会话已结束"),
    SESSION_MAX_ROUNDS_REACHED(40003, "会话已达到最大轮次"),
    DAILY_SESSION_LIMIT_REACHED(40004, "每日会话次数已用完"),
    DAILY_TIME_LIMIT_REACHED(40005, "今日学习时长已用完"),
    INPUT_TOO_LONG(40006, "输入内容超长"),
    SCENE_NOT_FOUND(40007, "场景模板不存在"),
    SCENE_NOT_PUBLISHED(40008, "场景模板未发布"),
    SCENE_GRADE_MISMATCH(40009, "场景学段与用户不匹配"),
    SESSION_NOT_OWNER(40010, "无权访问此会话"),
    MESSAGE_NOT_FOUND(40011, "消息不存在"),
    AUDIO_UPLOAD_TOO_LARGE(40012, "音频文件过大"),

    // ==================== 评测错误 (5xxxx) ====================
    EVALUATION_NOT_FOUND(50001, "评测记录不存在"),
    EVAL_SERVICE_UNAVAILABLE(50002, "评测服务不可用"),
    NO_EVALUABLE_CONTENT(50003, "该会话无可评测内容"),
    EVAL_FAILED(50004, "评测失败，请重试"),

    // ==================== 内容审核 (6xxxx) ====================
    CONTENT_SENSITIVE(60001, "输入内容包含敏感词"),
    AI_OUTPUT_BLOCKED(60002, "AI 输出被审核拦截"),
    CONTENT_REVIEW_NOT_FOUND(60003, "审核记录不存在"),

    // ==================== 家长/班级 (7xxxx) ====================
    BINDING_ALREADY_EXISTS(70001, "已存在相同绑定关系"),
    BINDING_NOT_FOUND(70002, "绑定关系不存在"),
    BINDING_NOT_APPROVED(70003, "绑定尚未通过审批"),
    NOT_BOUND_STUDENT(70004, "未绑定该学生"),
    CLASS_NOT_FOUND(70005, "班级不存在"),
    NOT_CLASS_TEACHER(70006, "不是该班级的教师"),
    STUDENT_ALREADY_IN_CLASS(70007, "学生已在班级中"),
    ASSIGNMENT_NOT_FOUND(70008, "作业不存在"),

    // ==================== 系统/限流 (8xxxx) ====================
    RATE_LIMIT_GLOBAL(80001, "系统繁忙，请稍后再试"),
    RATE_LIMIT_USER(80002, "操作过于频繁，请稍后再试"),
    SYSTEM_MAINTENANCE(80003, "系统维护中"),
    FILE_UPLOAD_FAILED(80004, "文件上传失败"),
    FILE_TOO_LARGE(80005, "文件大小超出限制"),

    // ==================== 第三方服务 (9xxxx) ====================
    LLM_SERVICE_UNAVAILABLE(90001, "AI 服务暂时不可用"),
    LLM_SERVICE_TIMEOUT(90002, "AI 服务响应超时"),
    LLM_RESPONSE_INVALID(90003, "AI 服务返回无效响应"),
    ASR_SERVICE_UNAVAILABLE(90004, "语音识别服务不可用"),
    TTS_SERVICE_UNAVAILABLE(90005, "语音合成服务不可用"),
    CONTENT_REVIEW_SERVICE_ERROR(90006, "内容审核服务异常"),
    THIRD_PARTY_TIMEOUT(90007, "第三方服务超时");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据 code 查找错误码
     */
    public static ErrorCode fromCode(int code) {
        for (ErrorCode ec : values()) {
            if (ec.code == code) {
                return ec;
            }
        }
        return INTERNAL_ERROR;
    }

    /**
     * 获取 HTTP 状态码建议
     */
    public int getHttpStatus() {
        if (code == SUCCESS.code) return 200;
        if (code >= 20001 && code <= 29999) return 401;
        if (code == 20005) return 403;
        if (code >= 80001 && code <= 89999) return 429;
        if (code >= 90001 && code <= 99999) return 502;
        if (code == 10002 || code == 30001) return 404;
        if (code == 10003) return 409;
        return 400;
    }
}
