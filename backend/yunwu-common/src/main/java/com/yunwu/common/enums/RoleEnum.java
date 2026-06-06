package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum RoleEnum {

    STUDENT("STUDENT", "学生"),
    PARENT("PARENT", "家长"),
    TEACHER("TEACHER", "教师"),
    ADMIN("ADMIN", "管理员"),
    SUPER_ADMIN("SUPER_ADMIN", "超级管理员");

    private final String code;
    private final String desc;

    RoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RoleEnum fromCode(String code) {
        for (RoleEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    /** 是否为学生角色 */
    public boolean isStudent() {
        return this == STUDENT;
    }

    /** 是否为管理角色 */
    public boolean isAdmin() {
        return this == ADMIN || this == SUPER_ADMIN;
    }
}
