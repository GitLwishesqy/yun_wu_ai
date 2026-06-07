package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 家长学生绑定状态枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum BindingStatusEnum {

    PENDING("PENDING", "待审批"),
    ACTIVE("ACTIVE", "已绑定"),
    REJECTED("REJECTED", "已拒绝"),
    UNBOUND("UNBOUND", "已解绑");

    private final String code;
    private final String desc;

    BindingStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BindingStatusEnum fromCode(String code) {
        for (BindingStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
