package com.yunwu.common.exception;

import java.util.Map;

/**
 * 业务异常 — 统一异常类
 * <p>
 * 所有业务层异常抛出此异常，由 {@link GlobalExceptionHandler} 统一处理。
 *
 * @author YunWu Team
 * @since 1.0.0
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误码 */
    private final int code;

    /** 附加数据 (如剩余次数、限制值等) */
    private transient Object data;

    /** 扩展信息 (如字段校验错误) */
    private Map<String, String> errors;

    // ==================== 构造方法 ====================

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
    }

    // ==================== 链式方法 ====================

    public BusinessException data(Object data) {
        this.data = data;
        return this;
    }

    public BusinessException errors(Map<String, String> errors) {
        this.errors = errors;
        return this;
    }

    // ==================== 常用静态工厂 ====================

    /** 资源不存在 */
    public static BusinessException notFound(String resource) {
        return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, resource + " 不存在");
    }

    /** 参数错误 */
    public static BusinessException paramInvalid(String message) {
        return new BusinessException(ErrorCode.PARAM_INVALID, message);
    }

    /** 未授权 */
    public static BusinessException unauthorized() {
        return new BusinessException(ErrorCode.TOKEN_MISSING);
    }

    /** 权限不足 */
    public static BusinessException forbidden() {
        return new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    /** 系统错误 */
    public static BusinessException systemError(String message) {
        return new BusinessException(ErrorCode.INTERNAL_ERROR, message);
    }

    /** 第三方服务不可用 */
    public static BusinessException serviceUnavailable(String serviceName) {
        return new BusinessException(ErrorCode.LLM_SERVICE_UNAVAILABLE, serviceName + " 服务暂时不可用");
    }

    // ==================== Getters ====================

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
