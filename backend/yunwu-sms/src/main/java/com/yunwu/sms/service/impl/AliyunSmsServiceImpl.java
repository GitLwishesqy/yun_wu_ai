package com.yunwu.sms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunwu.sms.dto.SmsSendResult;
import com.yunwu.sms.dto.SmsTemplate;
import com.yunwu.sms.service.SmsService;
import com.yunwu.sms.service.SmsTemplateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 阿里云短信服务实现 — 生产环境使用 (纯 HTTP，不依赖 SDK)
 * <p>
 * 通过阿里云 POP API 签名机制直接调用，无需引入外部 SDK。
 * 参考文档: https://help.aliyun.com/zh/sms/developer-reference/api-send-sms
 * <p>
 * 配置项 (application-prod.yml):
 * <pre>
 * yunwu:
 *   sms:
 *     aliyun:
 *       access-key-id: LTAI5t...
 *       access-key-secret: your-secret
 * </pre>
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Service
@Profile("prod")
public class AliyunSmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(AliyunSmsServiceImpl.class);

    private static final String ENDPOINT = "dysmsapi.aliyuncs.com";
    private static final String API_VERSION = "2017-05-25";
    private static final String SIGN_METHOD = "HMAC-SHA1";
    private static final String SIGN_VERSION = "1.0";

    private final SmsTemplateManager templateManager;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${yunwu.sms.aliyun.access-key-id:}")
    private String accessKeyId;

    @Value("${yunwu.sms.aliyun.access-key-secret:}")
    private String accessKeySecret;

    public AliyunSmsServiceImpl(SmsTemplateManager templateManager) {
        this.templateManager = templateManager;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public SmsSendResult sendTemplate(String phone, String templateCode,
                                       Map<String, String> templateParams,
                                       String purpose, String ipAddress) {
        SmsTemplate template = templateManager.getByCode(templateCode);
        if (template == null) {
            return SmsSendResult.fail("ALIYUN", "TEMPLATE_NOT_FOUND",
                    "模板不存在: " + templateCode);
        }

        if (accessKeyId.isEmpty() || accessKeySecret.isEmpty()) {
            log.error("[SMS-ALIYUN] AccessKey 未配置");
            return SmsSendResult.fail("ALIYUN", "NOT_CONFIGURED",
                    "请配置 yunwu.sms.aliyun.access-key-id 和 access-key-secret");
        }

        try {
            // 构建公共参数
            Map<String, String> params = new TreeMap<>();
            params.put("AccessKeyId", accessKeyId);
            params.put("Action", "SendSms");
            params.put("Format", "JSON");
            params.put("PhoneNumbers", phone);
            params.put("SignName", template.getSignName());
            params.put("TemplateCode", template.getProviderTemplateId());
            params.put("TemplateParam", template.buildParams(templateParams));
            params.put("Version", API_VERSION);
            params.put("SignatureMethod", SIGN_METHOD);
            params.put("SignatureVersion", SIGN_VERSION);
            params.put("SignatureNonce", UUID.randomUUID().toString());
            params.put("Timestamp", formatTimestamp(new Date()));
            params.put("RegionId", "cn-hangzhou");

            // 签名
            String signature = sign(params, accessKeySecret);
            params.put("Signature", signature);

            // 构造请求 URL
            String queryString = buildQuery(params);
            URI uri = new URI("https", ENDPOINT, "/", queryString, null);

            // 发送请求
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    uri, HttpMethod.GET, entity, String.class);

            // 解析响应
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);

            if ("OK".equals(result.get("Code"))) {
                String bizId = (String) result.get("BizId");
                log.info("[SMS-ALIYUN] 发送成功 phone={}, template={}, bizId={}",
                        phone, templateCode, bizId);
                return SmsSendResult.success("ALIYUN", bizId, 1);
            } else {
                log.error("[SMS-ALIYUN] 发送失败 phone={}, code={}, message={}",
                        phone, result.get("Code"), result.get("Message"));
                return SmsSendResult.fail("ALIYUN",
                        (String) result.get("Code"),
                        (String) result.get("Message"));
            }

        } catch (Exception e) {
            log.error("[SMS-ALIYUN] 发送异常 phone={}", phone, e);
            return SmsSendResult.fail("ALIYUN", "EXCEPTION", e.getMessage());
        }
    }

    @Override
    public String getProvider() {
        return "ALIYUN";
    }

    // ==================== 签名算法 (阿里云 POP API V1) ====================

    private String sign(Map<String, String> params, String secret) throws Exception {
        String sortedQuery = buildQuery(params);
        String stringToSign = "GET" + "&" +
                URLEncoder.encode("/", StandardCharsets.UTF_8) + "&" +
                URLEncoder.encode(sortedQuery, StandardCharsets.UTF_8);

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec keySpec = new SecretKeySpec(
                (secret + "&").getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        mac.init(keySpec);
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signData);
    }

    private String buildQuery(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) sb.append("&");
            sb.append(encode(entry.getKey())).append("=").append(encode(entry.getValue()));
        }
        return sb.toString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
    }

    private String formatTimestamp(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }
}
