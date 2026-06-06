package com.yunwu.coach.speech.impl;

import com.yunwu.coach.speech.AsrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 讯飞语音识别 — ASR 备用方案
 */
@Service
@ConditionalOnProperty(name = "yunwu.speech.provider", havingValue = "xunfei")
public class XunfeiAsrServiceImpl implements AsrService {

    private static final Logger log = LoggerFactory.getLogger(XunfeiAsrServiceImpl.class);

    @Value("${yunwu.speech.xunfei.app-id:}")
    private String appId;

    @Value("${yunwu.speech.xunfei.api-key:}")
    private String apiKey;

    @Value("${yunwu.speech.xunfei.api-secret:}")
    private String apiSecret;

    @Override
    public AsrResult transcribe(String audioUrl, String audioFormat) {
        if (appId.isEmpty()) {
            return new AsrResult("", 0f, 0, "NOT_CONFIGURED");
        }
        // 讯飞 WebSocket 实时流式识别 — 需要建立 WebSocket 连接
        // 此处为示意结构，完整实现需要引入讯飞 SDK 或 WebSocket 客户端
        log.warn("[ASR-XUNFEI] 讯飞 ASR 需要 WebSocket 连接，请配置后启用");
        return new AsrResult("", 0f, 0, "NEED_WEBSOCKET_CONFIG");
    }

    @Override public String getProvider() { return "XUNFEI"; }
}
