package com.yunwu.sms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunwu.sms.dto.SmsSendResult;
import com.yunwu.sms.dto.SmsTemplate;
import com.yunwu.sms.service.SmsService;
import com.yunwu.sms.service.SmsTemplateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 腾讯云短信服务实现 — 纯 HTTP 调用 (备用/海外场景)
 * <p>
 * 通过腾讯云 API 3.0 签名机制直接调用。
 * 参考: https://cloud.tencent.com/document/product/382/55981
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Service
@ConditionalOnProperty(name = "yunwu.sms.provider", havingValue = "tencent")
public class TencentSmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(TencentSmsServiceImpl.class);

    private static final String ENDPOINT = "sms.tencentcloudapi.com";
    private static final String SERVICE = "sms";
    private static final String VERSION = "2021-01-11";
    private static final String ALGORITHM = "TC3-HMAC-SHA256";

    private final SmsTemplateManager templateManager;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${yunwu.sms.tencent.secret-id:}")
    private String secretId;

    @Value("${yunwu.sms.tencent.secret-key:}")
    private String secretKey;

    @Value("${yunwu.sms.tencent.app-id:}")
    private String appId;

    public TencentSmsServiceImpl(SmsTemplateManager templateManager) {
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
            return SmsSendResult.fail("TENCENT", "TEMPLATE_NOT_FOUND",
                    "模板不存在: " + templateCode);
        }

        if (secretId.isEmpty() || secretKey.isEmpty() || appId.isEmpty()) {
            log.error("[SMS-TENCENT] 配置不完整");
            return SmsSendResult.fail("TENCENT", "NOT_CONFIGURED",
                    "请配置 yunwu.sms.tencent.* 配置项");
        }

        try {
            // 构建请求体
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("SmsSdkAppId", appId);
            body.put("SignName", template.getSignName());
            body.put("TemplateId", template.getProviderTemplateId());
            body.put("TemplateParamSet",
                    new ArrayList<>(templateParams.values()));
            body.put("PhoneNumberSet", List.of("+86" + phone));

            String payload = objectMapper.writeValueAsString(body);
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

            // 签名
            String authorization = sign(payload, timestamp);

            // 发送请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-TC-Action", "SendSms");
            headers.set("X-TC-Version", VERSION);
            headers.set("X-TC-Timestamp", timestamp);
            headers.set("X-TC-Region", "ap-guangzhou");
            headers.set("Authorization", authorization);

            HttpEntity<String> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://" + ENDPOINT, HttpMethod.POST, entity, String.class);

            // 解析响应
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);

            @SuppressWarnings("unchecked")
            Map<String, Object> respData = (Map<String, Object>) result.get("Response");

            if (respData != null && respData.get("Error") == null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> statusSet =
                        (List<Map<String, Object>>) respData.get("SendStatusSet");
                String msgId = statusSet != null && !statusSet.isEmpty()
                        ? (String) statusSet.get(0).get("SerialNo") : "N/A";
                log.info("[SMS-TENCENT] 发送成功 phone={}, msgId={}", phone, msgId);
                return SmsSendResult.success("TENCENT", msgId, 1);
            } else {
                @SuppressWarnings("unchecked")
                Map<String, Object> error = respData != null
                        ? (Map<String, Object>) respData.get("Error") : Map.of();
                log.error("[SMS-TENCENT] 发送失败 phone={}, error={}", phone, error);
                return SmsSendResult.fail("TENCENT",
                        (String) error.get("Code"), (String) error.get("Message"));
            }

        } catch (Exception e) {
            log.error("[SMS-TENCENT] 发送异常 phone={}", phone, e);
            return SmsSendResult.fail("TENCENT", "EXCEPTION", e.getMessage());
        }
    }

    @Override
    public String getProvider() {
        return "TENCENT";
    }

    // ==================== 腾讯云 API 3.0 签名 ====================

    private String sign(String payload, String timestamp) throws Exception {
        // CanonicalRequest
        String httpMethod = "POST";
        String canonicalUri = "/";
        String canonicalQuery = "";
        String canonicalHeaders = "content-type:application/json\nhost:" + ENDPOINT + "\n";
        String signedHeaders = "content-type;host";
        String hashedPayload = sha256Hex(payload);

        String canonicalRequest = httpMethod + "\n" +
                canonicalUri + "\n" +
                canonicalQuery + "\n" +
                canonicalHeaders + "\n" +
                signedHeaders + "\n" +
                hashedPayload;

        // StringToSign
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String credentialScope = date + "/" + SERVICE + "/tc3_request";
        String hashedCanonical = sha256Hex(canonicalRequest);

        String stringToSign = ALGORITHM + "\n" +
                timestamp + "\n" +
                credentialScope + "\n" +
                hashedCanonical;

        // Signature
        byte[] secretDate = hmacSha256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
        byte[] secretService = hmacSha256(secretDate, SERVICE);
        byte[] secretSigning = hmacSha256(secretService, "tc3_request");
        byte[] signature = hmacSha256(secretSigning, stringToSign);

        String signatureHex = bytesToHex(signature);
        return ALGORITHM + " Credential=" + secretId + "/" + credentialScope +
                ", SignedHeaders=" + signedHeaders + ", Signature=" + signatureHex;
    }

    private String sha256Hex(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return bytesToHex(md.digest(data.getBytes(StandardCharsets.UTF_8)));
    }

    private byte[] hmacSha256(byte[] key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
