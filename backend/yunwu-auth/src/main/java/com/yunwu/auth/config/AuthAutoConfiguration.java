package com.yunwu.auth.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 鉴权模块自动配置 — 扫描本模块所有组件
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.yunwu.auth")
public class AuthAutoConfiguration {
    // 通过 @ComponentScan 自动扫描本模块内的 @Component、@Service、@Configuration 等
}
