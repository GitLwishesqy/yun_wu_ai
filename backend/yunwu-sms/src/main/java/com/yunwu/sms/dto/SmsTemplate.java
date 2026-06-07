package com.yunwu.sms.dto;

import java.util.Map;

/**
 * 短信模板
 *
 * @author YunWu Team
 * @since 1.0.0
 */
public class SmsTemplate {

    /** 模板编号 */
    private String code;

    /** 模板名称 (内部标识) */
    private String name;

    /** 服务商模板 ID */
    private String providerTemplateId;

    /** 模板参数 key 列表 (如 ["code", "minutes"]) */
    private java.util.List<String> paramKeys;

    /** 签名 (如 "云悟英语") */
    private String signName;

    /** 每日最大发送次数 (此模板) */
    private int dailyMaxPerPhone;

    /** 允许的发送时段 (如 07:00-22:00) */
    private String allowedTimeRange;

    public SmsTemplate() {}

    public SmsTemplate(String code, String name, String providerTemplateId,
                       java.util.List<String> paramKeys, String signName) {
        this.code = code;
        this.name = name;
        this.providerTemplateId = providerTemplateId;
        this.paramKeys = paramKeys;
        this.signName = signName;
        this.dailyMaxPerPhone = 10;
        this.allowedTimeRange = "00:00-23:59";
    }

    /**
     * 构建模板参数
     */
    public String buildParams(Map<String, String> values) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < paramKeys.size(); i++) {
            if (i > 0) sb.append(", ");
            String key = paramKeys.get(i);
            sb.append("\"").append(key).append("\":\"")
              .append(values.getOrDefault(key, "")).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    // ==================== Getters & Setters ====================

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProviderTemplateId() { return providerTemplateId; }
    public void setProviderTemplateId(String providerTemplateId) { this.providerTemplateId = providerTemplateId; }
    public java.util.List<String> getParamKeys() { return paramKeys; }
    public void setParamKeys(java.util.List<String> paramKeys) { this.paramKeys = paramKeys; }
    public String getSignName() { return signName; }
    public void setSignName(String signName) { this.signName = signName; }
    public int getDailyMaxPerPhone() { return dailyMaxPerPhone; }
    public void setDailyMaxPerPhone(int dailyMaxPerPhone) { this.dailyMaxPerPhone = dailyMaxPerPhone; }
    public String getAllowedTimeRange() { return allowedTimeRange; }
    public void setAllowedTimeRange(String allowedTimeRange) { this.allowedTimeRange = allowedTimeRange; }
}
