package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 学习计划类型枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum PlanTypeEnum {

    AI_GENERATED("AI_GENERATED", "AI 生成"),
    MANUAL("MANUAL", "手动创建"),
    TEACHER_ASSIGNED("TEACHER_ASSIGNED", "教师布置");

    private final String code;
    private final String desc;

    PlanTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PlanTypeEnum fromCode(String code) {
        for (PlanTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
