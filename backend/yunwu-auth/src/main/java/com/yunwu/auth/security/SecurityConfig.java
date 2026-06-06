package com.yunwu.auth.security;

import com.yunwu.common.constant.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF (REST API 不需要)
                .csrf(AbstractHttpConfigurer::disable)
                // 无状态会话
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 异常处理
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(new JwtAccessDeniedHandler()))
                // 请求授权
                .authorizeHttpRequests(auth -> auth
                        // 公开接口
                        .requestMatchers(Constants.API_PREFIX + "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Swagger/Knife4j
                        .requestMatchers(
                                "/doc.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()
                        // 健康检查
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        // 管理员接口
                        .requestMatchers(Constants.API_PREFIX + "/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        // 教师接口
                        .requestMatchers(Constants.API_PREFIX + "/classes/**").hasAnyRole("TEACHER", "ADMIN", "SUPER_ADMIN")
                        // 家长接口
                        .requestMatchers(Constants.API_PREFIX + "/parent/**").hasRole("PARENT")
                        // 其余接口需要登录
                        .anyRequest().authenticated()
                )
                // 添加 JWT 过滤器 (在 UsernamePasswordAuthenticationFilter 之前)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
