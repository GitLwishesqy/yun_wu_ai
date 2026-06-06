package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 陪练会话状态枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum SessionStatusEnum {

    ACTIVE("ACTIVE", "进行中"),
    PAUSED("PAUSED", "已暂停"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String desc;

    SessionStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SessionStatusEnum fromCode(String code) {
        for (SessionStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    /** 是否可发送消息 */
    public boolean canSendMessage() {
        return this == ACTIVE;
    }

    /** 是否为终态 */
    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED;
    }
}
