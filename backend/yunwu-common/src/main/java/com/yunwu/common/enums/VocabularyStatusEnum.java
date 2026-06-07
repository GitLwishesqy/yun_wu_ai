package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 词汇掌握状态枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum VocabularyStatusEnum {

    NEW("NEW", "新词"),
    LEARNING("LEARNING", "学习中"),
    REVIEWING("REVIEWING", "复习中"),
    KNOWN("KNOWN", "已掌握"),
    MASTERED("MASTERED", "已精通");

    private final String code;
    private final String desc;

    VocabularyStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static VocabularyStatusEnum fromCode(String code) {
        for (VocabularyStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    /** 是否需要复习 */
    public boolean needsReview() {
        return this == LEARNING || this == REVIEWING;
    }
}
