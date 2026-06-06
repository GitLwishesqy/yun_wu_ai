package com.yunwu.sms.dto;

/**
 * 短信发送结果
 *
 * @author YunWu Team
 * @since 1.0.0
 */
public class SmsSendResult {

    private boolean success;
    private String provider;
    private String providerMsgId;
    private String errorCode;
    private String errorMessage;
    private int feeCount;

    private SmsSendResult() {}

    public static SmsSendResult success(String provider, String msgId, int feeCount) {
        SmsSendResult r = new SmsSendResult();
        r.success = true;
        r.provider = provider;
        r.providerMsgId = msgId;
        r.feeCount = feeCount;
        return r;
    }

    public static SmsSendResult fail(String provider, String errorCode, String errorMessage) {
        SmsSendResult r = new SmsSendResult();
        r.success = false;
        r.provider = provider;
        r.errorCode = errorCode;
        r.errorMessage = errorMessage;
        return r;
    }

    public boolean isSuccess() { return success; }
    public String getProvider() { return provider; }
    public String getProviderMsgId() { return providerMsgId; }
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public int getFeeCount() { return feeCount; }
}
