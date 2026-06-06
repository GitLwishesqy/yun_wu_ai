package com.yunwu.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户模块自动配置
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.yunwu.user")
@MapperScan("com.yunwu.user.mapper")
public class UserAutoConfiguration {
}
