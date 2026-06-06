package com.yunwu.sms.service;

import com.yunwu.sms.dto.SmsTemplate;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 短信模板管理器 — 集中管理所有短信模板，按 purpose 路由
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Component
public class SmsTemplateManager {

    private static final Logger log = LoggerFactory.getLogger(SmsTemplateManager.class);

    /** purpose → SmsTemplate */
    private final Map<String, SmsTemplate> templateMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // ==================== 验证码类模板 ====================

        register(new SmsTemplate(
                "VERIFY_CODE_LOGIN",
                "登录验证码",
                "SMS_123456789",  // 替换为阿里云真实模板 ID
                List.of("code", "minutes"),
                "云悟英语"
        ));

        register(new SmsTemplate(
                "VERIFY_CODE_REGISTER",
                "注册验证码",
                "SMS_123456790",
                List.of("code", "minutes"),
                "云悟英语"
        ));

        register(new SmsTemplate(
                "VERIFY_CODE_RESET",
                "重置密码验证码",
                "SMS_123456791",
                List.of("code", "minutes"),
                "云悟英语"
        ));

        register(new SmsTemplate(
                "VERIFY_CODE_BIND",
                "绑定手机验证码",
                "SMS_123456792",
                List.of("code", "minutes"),
                "云悟英语"
        ));

        // 安全类模板: 禁止深夜发送
        SmsTemplate bindTemplate = templateMap.get("VERIFY_CODE_BIND");
        if (bindTemplate != null) {
            bindTemplate.setAllowedTimeRange("08:00-21:00");
        }

        log.info("[SMS] 模板管理器初始化完成，已注册 {} 个模板", templateMap.size());
    }

    /**
     * 注册模板
     */
    public void register(SmsTemplate template) {
        templateMap.put(template.getCode(), template);
        log.info("[SMS] 注册模板: code={}, name={}, providerId={}",
                template.getCode(), template.getName(), template.getProviderTemplateId());
    }

    /**
     * 根据 purpose 获取模板
     */
    public SmsTemplate getByPurpose(String purpose) {
        String templateCode = mapPurposeToTemplateCode(purpose);
        SmsTemplate template = templateMap.get(templateCode);
        if (template == null) {
            throw new IllegalArgumentException("未找到短信模板: purpose=" + purpose);
        }
        return template;
    }

    /**
     * 根据模板 CODE 获取
     */
    public SmsTemplate getByCode(String code) {
        return templateMap.get(code);
    }

    /**
     * 获取所有模板
     */
    public Map<String, SmsTemplate> getAll() {
        return Map.copyOf(templateMap);
    }

    // ==================== 私有方法 ====================

    private String mapPurposeToTemplateCode(String purpose) {
        return switch (purpose.toUpperCase()) {
            case "LOGIN"          -> "VERIFY_CODE_LOGIN";
            case "REGISTER"       -> "VERIFY_CODE_REGISTER";
            case "RESET_PASSWORD" -> "VERIFY_CODE_RESET";
            case "BIND"           -> "VERIFY_CODE_BIND";
            default -> throw new IllegalArgumentException("未知的验证码用途: " + purpose);
        };
    }
}
