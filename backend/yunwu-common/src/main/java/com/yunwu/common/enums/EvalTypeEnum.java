package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 评测类型枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum EvalTypeEnum {

    SESSION("SESSION", "会话评测"),
    DAILY("DAILY", "每日评测"),
    WEEKLY("WEEKLY", "每周评测"),
    DIAGNOSTIC("DIAGNOSTIC", "诊断评测"),
    EXAM_MOCK("EXAM_MOCK", "模拟考试");

    private final String code;
    private final String desc;

    EvalTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EvalTypeEnum fromCode(String code) {
        for (EvalTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
