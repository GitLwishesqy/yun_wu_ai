package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 内容审核结果枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum ReviewResultEnum {

    PASS("PASS", "通过"),
    BLOCK("BLOCK", "屏蔽"),
    REVIEW("REVIEW", "人工复审"),
    MODIFIED("MODIFIED", "修改后放行");

    private final String code;
    private final String desc;

    ReviewResultEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReviewResultEnum fromCode(String code) {
        for (ReviewResultEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    /** 是否需要人工介入 */
    public boolean needsManualReview() {
        return this == REVIEW || this == BLOCK;
    }
}
