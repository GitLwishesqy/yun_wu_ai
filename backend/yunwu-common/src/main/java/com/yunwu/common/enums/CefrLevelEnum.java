package com.yunwu.common.enums;

import lombok.Getter;

/**
 * CEFR 欧洲语言共同参考框架等级
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Getter
public enum CefrLevelEnum {

    A1("A1", "入门级", 1),
    A2("A2", "初级", 2),
    B1("B1", "中级", 3),
    B2("B2", "中高级", 4),
    C1("C1", "高级", 5),
    C2("C2", "精通级", 6);

    private final String code;
    private final String desc;
    private final int level;

    CefrLevelEnum(String code, String desc, int level) {
        this.code = code;
        this.desc = desc;
        this.level = level;
    }

    public static CefrLevelEnum fromCode(String code) {
        for (CefrLevelEnum e : values()) {
            if (e.code.equalsIgnoreCase(code)) {
                return e;
            }
        }
        return null;
    }

    /** 获取下一个等级 (用于自动升级) */
    public CefrLevelEnum nextLevel() {
        int nextOrdinal = this.ordinal() + 1;
        if (nextOrdinal < values().length) {
            return values()[nextOrdinal];
        }
        return this;
    }

    /** 获取上一个等级 (用于自动降级) */
    public CefrLevelEnum previousLevel() {
        int prevOrdinal = this.ordinal() - 1;
        if (prevOrdinal >= 0) {
            return values()[prevOrdinal];
        }
        return this;
    }
}
