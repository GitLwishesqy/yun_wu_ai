package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 纠错类型枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum ErrorTypeEnum {

    GRAMMAR("GRAMMAR", "语法错误"),
    PRONUNCIATION("PRONUNCIATION", "发音错误"),
    VOCABULARY("VOCABULARY", "词汇错误"),
    LOGIC("LOGIC", "逻辑错误"),
    COLLOCATION("COLLOCATION", "搭配错误");

    private final String code;
    private final String desc;

    ErrorTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ErrorTypeEnum fromCode(String code) {
        for (ErrorTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
