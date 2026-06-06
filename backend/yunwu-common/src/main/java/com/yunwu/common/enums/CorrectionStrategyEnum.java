package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 纠错策略枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum CorrectionStrategyEnum {

    IMMEDIATE("IMMEDIATE", "立即纠正"),
    DELAYED("DELAYED", "延后纠正"),
    SKIPPED("SKIPPED", "跳过不纠"),
    REVIEW_LATER("REVIEW_LATER", "稍后复习");

    private final String code;
    private final String desc;

    CorrectionStrategyEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CorrectionStrategyEnum fromCode(String code) {
        for (CorrectionStrategyEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
