package com.yunwu.common.enums;

import lombok.Getter;

/**
 * 陪练会话类型枚举
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum SessionTypeEnum {

    SCENE("SCENE", "场景陪练"),
    FREE("FREE", "自由对话"),
    CUSTOM("CUSTOM", "自定主题"),
    EXAM("EXAM", "考试模拟");

    private final String code;
    private final String desc;

    SessionTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SessionTypeEnum fromCode(String code) {
        for (SessionTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
