package com.yunwu.coach.speech.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunwu.coach.speech.AsrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 阿里云语音识别 (ASR) — 一句话识别
 * 纯 HTTP 签名调用，不依赖 SDK
 */
@Service
public class AliyunAsrServiceImpl implements AsrService {

    private static final Logger log = LoggerFactory.getLogger(AliyunAsrServiceImpl.class);
    private static final String ENDPOINT = "nls-meta.cn-shanghai.aliyuncs.com";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${yunwu.speech.aliyun.access-key-id:}")
    private String accessKeyId;

    @Value("${yunwu.speech.aliyun.access-key-secret:}")
    private String accessKeySecret;

    @Value("${yunwu.speech.aliyun.app-key:}")
    private String appKey;

    @Override
    public AsrResult transcribe(String audioUrl, String audioFormat) {
        if (accessKeyId.isEmpty()) {
            return new AsrResult("", 0f, 0, "NOT_CONFIGURED");
        }
        try {
            Map<String, String> params = new TreeMap<>();
            params.put("AccessKeyId", accessKeyId);
            params.put("Action", "CreateTask");
            params.put("Format", "JSON");
            params.put("Version", "2019-08-15");
            params.put("SignatureMethod", "HMAC-SHA1");
            params.put("Timestamp", formatTimestamp(new Date()));
            params.put("SignatureVersion", "1.0");
            params.put("SignatureNonce", UUID.randomUUID().toString());
            params.put("AppKey", appKey);
            params.put("FileLink", audioUrl);

            String signature = sign(params, accessKeySecret);
            params.put("Signature", signature);

            String queryString = buildQuery(params);
            String url = "https://" + ENDPOINT + "/?" + queryString;

            HttpHeaders headers = new HttpHeaders();
            ResponseEntity<String> resp = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(resp.getBody(), Map.class);

            if ("20000000".equals(String.valueOf(result.get("Status")))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) result.get("Result");
                String text = data != null ? (String) data.get("Sentences") : "";
                return new AsrResult(text, 0.95f, 0,
                        (String) result.get("TaskId"));
            }
            log.warn("[ASR-ALIYUN] 识别失败: {}", result);
            return new AsrResult("", 0f, 0, "FAILED");
        } catch (Exception e) {
            log.error("[ASR-ALIYUN] 异常: {}", e.getMessage());
            return new AsrResult("", 0f, 0, "ERROR");
        }
    }

    @Override public String getProvider() { return "ALIYUN"; }

    private String sign(Map<String, String> params, String secret) throws Exception {
        String query = buildQuery(params);
        String toSign = "GET&" + encode("/") + "&" + encode(query);
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec((secret + "&").getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        return Base64.getEncoder().encodeToString(mac.doFinal(toSign.getBytes(StandardCharsets.UTF_8)));
    }

    private String buildQuery(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            if (sb.length() > 0) sb.append("&");
            sb.append(encode(k)).append("=").append(encode(v));
        });
        return sb.toString();
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8)
                .replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    private String formatTimestamp(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(d);
    }
}
