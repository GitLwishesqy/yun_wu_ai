package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 学习报告周期枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum ReportPeriodEnum {

    DAILY("DAILY", "日报"),
    WEEKLY("WEEKLY", "周报"),
    MONTHLY("MONTHLY", "月报"),
    SEMESTER("SEMESTER", "学期报");

    private final String code;
    private final String desc;

    ReportPeriodEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReportPeriodEnum fromCode(String code) {
        for (ReportPeriodEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
