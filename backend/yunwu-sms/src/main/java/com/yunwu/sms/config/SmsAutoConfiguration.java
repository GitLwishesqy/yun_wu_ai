package com.yunwu.sms.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 短信模块自动配置
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@AutoConfiguration @MapperScan("com.yunwu.sms.mapper")
public class SmsAutoConfiguration {
}
