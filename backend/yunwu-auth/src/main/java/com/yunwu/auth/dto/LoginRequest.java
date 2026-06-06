package com.yunwu.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 手机号验证码登录请求
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Schema(description = "登录请求")
public class LoginRequest {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800000001")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码", example = "123456")
    private String code;

    @Schema(description = "设备信息")
    private DeviceInfo deviceInfo;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public DeviceInfo getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(DeviceInfo deviceInfo) { this.deviceInfo = deviceInfo; }

    @Schema(description = "设备信息")
    public static class DeviceInfo {
        @Schema(description = "平台: WEB/IOS/ANDROID")
        private String platform;
        @Schema(description = "设备名称")
        private String deviceName;

        public String getPlatform() { return platform; }
        public void setPlatform(String platform) { this.platform = platform; }
        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    }
}
