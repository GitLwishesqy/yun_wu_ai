package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 消息角色枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum MessageRoleEnum {

    USER("USER", "用户"),
    AI("AI", "AI 助手"),
    SYSTEM("SYSTEM", "系统");

    private final String code;
    private final String desc;

    MessageRoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MessageRoleEnum fromCode(String code) {
        for (MessageRoleEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
