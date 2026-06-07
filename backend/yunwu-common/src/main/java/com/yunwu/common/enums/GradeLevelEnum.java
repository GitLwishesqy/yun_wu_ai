package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 学段枚举 — 对标 PRD 四大用户群体
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum GradeLevelEnum {

    ELEMENTARY("ELEMENTARY", "小学", 6, 12),
    JUNIOR("JUNIOR", "初中", 12, 15),
    SENIOR("SENIOR", "高中", 15, 18),
    ADULT("ADULT", "成人", 18, 99);

    private final String code;
    private final String desc;
    private final int minAge;
    private final int maxAge;

    GradeLevelEnum(String code, String desc, int minAge, int maxAge) {
        this.code = code;
        this.desc = desc;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public static GradeLevelEnum fromCode(String code) {
        for (GradeLevelEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    /** 是否为未成年人学段 */
    public boolean isMinor() {
        return this == ELEMENTARY || this == JUNIOR || this == SENIOR;
    }
}
