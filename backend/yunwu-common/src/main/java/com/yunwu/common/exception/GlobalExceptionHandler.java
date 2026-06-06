package com.yunwu.common.exception;

import com.yunwu.common.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一拦截所有异常，返回 {@link ApiResponse} 格式。
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ==================== 业务异常 ====================

    /**
     * 业务异常 — 预期内的错误
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        log.warn("[BusinessException] code={}, message={}", ex.getCode(), ex.getMessage());
        ApiResponse<Void> response = ApiResponse.fail(ex.getCode(), ex.getMessage());
        if (ex.getData() != null) {
            response.setData(null);  // data field holds the extra info
        }
        return ResponseEntity
                .status(ErrorCode.fromCode(ex.getCode()).getHttpStatus())
                .body(response);
    }

    // ==================== 参数校验异常 ====================

    /**
     * @Valid 请求体校验失败 (POST/PUT JSON Body)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.warn("[Validation] {}", errors);
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ErrorCode.PARAM_INVALID.getCode(),
                        "参数校验失败", errors));
    }

    /**
     * 方法参数校验失败 (@RequestParam / @PathVariable)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Set<String>>> handleConstraintViolation(
            ConstraintViolationException ex) {
        Set<String> errors = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        log.warn("[ConstraintViolation] {}", errors);
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ErrorCode.PARAM_INVALID.getCode(),
                        "参数校验失败", errors));
    }

    /**
     * 缺少必填参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ErrorCode.PARAM_INVALID.getCode(),
                        "缺少必要参数: " + ex.getParameterName()));
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ErrorCode.PARAM_INVALID.getCode(),
                        "参数类型错误: " + ex.getName()));
    }

    /**
     * 请求体不可读
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(
            HttpMessageNotReadableException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ErrorCode.PARAM_INVALID.getCode(), "请求体格式错误"));
    }

    // ==================== HTTP 方法/媒体类型异常 ====================

    /**
     * 不支持的 HTTP 方法
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.fail(ErrorCode.METHOD_NOT_SUPPORTED.getCode(),
                        "不支持的请求方法: " + ex.getMethod()));
    }

    /**
     * 不支持的媒体类型
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.fail(ErrorCode.MEDIA_TYPE_NOT_SUPPORTED.getCode(),
                        "不支持的 Content-Type"));
    }

    // ==================== 文件上传异常 ====================

    /**
     * 文件超出大小限制
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSize(
            MaxUploadSizeExceededException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ErrorCode.FILE_TOO_LARGE.getCode(),
                        "文件大小超出限制 (最大 " + ex.getMaxUploadSize() / 1024 / 1024 + "MB)"));
    }

    // ==================== 兜底异常 ====================

    /**
     * 未知异常 — 兜底处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknownException(Exception ex) {
        log.error("[UnhandledException] type={}, message={}", ex.getClass().getName(), ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ErrorCode.INTERNAL_ERROR.getCode(),
                        "服务器内部错误，请稍后再试"));
    }
}
