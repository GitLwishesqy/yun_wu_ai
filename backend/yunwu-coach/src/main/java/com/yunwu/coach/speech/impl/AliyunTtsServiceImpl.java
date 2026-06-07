package com.yunwu.coach.speech.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunwu.coach.speech.OssService;
import com.yunwu.coach.speech.TtsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 阿里云语音合成 (TTS) — 文本→语音
 * 纯 HTTP 签名调用，不依赖 SDK
 */
@Service
public class AliyunTtsServiceImpl implements TtsService {

    private static final Logger log = LoggerFactory.getLogger(AliyunTtsServiceImpl.class);
    private static final String ENDPOINT = "nls-meta.cn-shanghai.aliyuncs.com";

    private final RestTemplate restTemplate = new RestTemplate();
    private final OssService ossService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${yunwu.speech.aliyun.access-key-id:}")
    private String accessKeyId;

    @Value("${yunwu.speech.aliyun.access-key-secret:}")
    private String accessKeySecret;

    @Value("${yunwu.speech.aliyun.tts-voice-female:}")
    private String femaleVoice;

    @Value("${yunwu.speech.aliyun.tts-voice-male:}")
    private String maleVoice;

    public AliyunTtsServiceImpl(OssService ossService) {
        this.ossService = ossService;
    }

    @Override
    public TtsResult synthesize(String text, String voiceType, double speechRate) {
        if (accessKeyId.isEmpty()) {
            return new TtsResult("", 0, "NOT_CONFIGURED");
        }
        try {
            String voice = "FEMALE".equalsIgnoreCase(voiceType)
                    ? (femaleVoice.isEmpty() ? "eva" : femaleVoice)
                    : (maleVoice.isEmpty() ? "jack" : maleVoice);

            // 阿里云 TTS 使用 RESTful API (POST)
            String token = generateToken();
            String url = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/tts";

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("appkey", appKey());
            body.put("text", text);
            body.put("token", token);
            body.put("format", "mp3");
            body.put("voice", voice);
            body.put("speech_rate", (int) (speechRate * 100));
            body.put("volume", 50);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<byte[]> resp = restTemplate.exchange(
                    url, HttpMethod.POST, entity, byte[].class);

            if (resp.getBody() != null && resp.getBody().length > 100) {
                String fileName = "tts/tts_" +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                        "_" + UUID.randomUUID().toString().substring(0, 8) + ".mp3";
                String audioUrl = ossService.upload(
                        new ByteArrayInputStream(resp.getBody()), fileName, "audio/mpeg");
                int durationMs = estimateDuration(text);
                log.info("[TTS-ALIYUN] 合成成功 text={}..., url={}", text.substring(0, Math.min(30, text.length())), audioUrl);
                return new TtsResult(audioUrl, durationMs, UUID.randomUUID().toString());
            }
            return new TtsResult("", 0, "EMPTY_RESPONSE");
        } catch (Exception e) {
            log.error("[TTS-ALIYUN] 异常: {}", e.getMessage());
            return new TtsResult("", 0, "ERROR");
        }
    }

    @Override public String getProvider() { return "ALIYUN"; }

    private String generateToken() throws Exception {
        // 阿里云令牌服务 (使用 AK/SK 换取临时 token)
        return "token_placeholder";  // TODO: 实现完整的 Token 换取逻辑
    }

    private String appKey() {
        return ""; // TODO: 从配置读取
    }

    /** 估算语音时长 (英文约 150 词/分钟) */
    private int estimateDuration(String text) {
        int words = text.split("\\s+").length;
        return (int) (words / 150.0 * 60 * 1000);
    }
}
