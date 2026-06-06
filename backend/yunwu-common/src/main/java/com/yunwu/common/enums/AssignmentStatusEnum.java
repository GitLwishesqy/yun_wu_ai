package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 作业提交状态枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum AssignmentStatusEnum {

    PENDING("PENDING", "待提交"),
    SUBMITTED("SUBMITTED", "已提交"),
    GRADED("GRADED", "已批改"),
    LATE("LATE", "逾期提交");

    private final String code;
    private final String desc;

    AssignmentStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AssignmentStatusEnum fromCode(String code) {
        for (AssignmentStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
