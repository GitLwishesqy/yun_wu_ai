package com.yunwu.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 发送验证码请求
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Schema(description = "发送验证码请求")
public class SendCodeRequest {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800000001")
    private String phone;

    @NotBlank(message = "验证码用途不能为空")
    @Schema(description = "用途: LOGIN/REGISTER/RESET_PASSWORD/BIND", example = "LOGIN")
    private String purpose;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}
