package com.yunwu.sms.service;

import com.yunwu.sms.dto.SmsSendResult;

/**
 * 短信服务接口 — 统一抽象，支持多服务商切换
 *
 * @author YunWu Team
 * @since 1.0.0
 */
public interface SmsService {

    /**
     * 发送模板短信
     *
     * @param phone          手机号
     * @param templateCode   模板编号 (内部 CODE，非服务商 ID)
     * @param templateParams 模板参数 (key-value)
     * @param purpose        用途: LOGIN/REGISTER/RESET_PASSWORD/BIND
     * @param ipAddress      客户端 IP
     * @return 发送结果
     */
    SmsSendResult sendTemplate(String phone, String templateCode,
                                java.util.Map<String, String> templateParams,
                                String purpose, String ipAddress);

    /**
     * 服务商标识
     */
    String getProvider();
}
