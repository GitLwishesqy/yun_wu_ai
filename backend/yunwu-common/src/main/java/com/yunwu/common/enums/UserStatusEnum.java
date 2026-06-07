package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum UserStatusEnum {

    ACTIVE("ACTIVE", "正常"),
    INACTIVE("INACTIVE", "未激活"),
    SUSPENDED("SUSPENDED", "已停用"),
    BANNED("BANNED", "已封禁");

    private final String code;
    private final String desc;

    UserStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserStatusEnum fromCode(String code) {
        for (UserStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    /** 是否允许登录 */
    public boolean canLogin() {
        return this == ACTIVE;
    }
}
