package com.yunwu.sms.service.impl;

import com.yunwu.sms.dto.SmsSendResult;
import com.yunwu.sms.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Mock 短信服务实现 — 开发/测试环境使用
 * <p>
 * 行为: 日志打印验证码，不调用真实短信 API，不产生费用。
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Service
@Profile({"dev", "test", "default"})
public class MockSmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(MockSmsServiceImpl.class);

    @Override
    public SmsSendResult sendTemplate(String phone, String templateCode,
                                       Map<String, String> templateParams,
                                       String purpose, String ipAddress) {
        String code = templateParams.getOrDefault("code", "******");

        // 安全: 开发模式下日志输出验证码方便调试
        log.info("[SMS-MOCK] 模拟发送短信 phone={}, template={}, purpose={}, code={}",
                phone, templateCode, purpose, code);

        return SmsSendResult.success("MOCK", UUID.randomUUID().toString(), 1);
    }

    @Override
    public String getProvider() {
        return "MOCK";
    }
}
