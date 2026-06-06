package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 错误严重程度枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum ErrorSeverityEnum {

    LOW("LOW", "轻微"),
    MEDIUM("MEDIUM", "中等"),
    HIGH("HIGH", "严重"),
    CRITICAL("CRITICAL", "关键");

    private final String code;
    private final String desc;

    ErrorSeverityEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ErrorSeverityEnum fromCode(String code) {
        for (ErrorSeverityEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
