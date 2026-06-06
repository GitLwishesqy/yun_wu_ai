package com.yunwu.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 统一 API 响应包装类
 * <pre>
 * 格式:
 * {
 *   "code": 0,
 *   "message": "success",
 *   "data": {...},
 *   "timestamp": 1717632000000,
 *   "trace_id": "uuid"
 * }
 * </pre>
 *
 * @param <T> 数据类型
 * @author YunWu Team
 * @since 1.0.0
 */
@Schema(description = "统一响应")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int SUCCESS_CODE = 0;
    private static final String SUCCESS_MESSAGE = "success";

    @Schema(description = "状态码，0 表示成功")
    private int code;

    @Schema(description = "响应消息")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "时间戳 (毫秒)")
    private long timestamp;

    @Schema(description = "链路追踪 ID")
    private String traceId;

    private ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // ==================== 成功响应 ====================

    /** 成功 (无数据) */
    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    /** 成功 (有数据) */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    /** 成功 (自定义消息) */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(SUCCESS_CODE, message, data);
    }

    // ==================== 失败响应 ====================

    /** 失败 */
    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /** 失败 (带数据) */
    public static <T> ApiResponse<T> fail(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    // ==================== 构建器 ====================

    public ApiResponse<T> traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    // ==================== Getters & Setters ====================

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    /** 是否成功 */
    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }
}
