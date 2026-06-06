package com.yunwu.common.service;

/**
 * 用户鉴权服务接口 — 由 yunwu-user 模块实现，供 yunwu-auth 模块调用
 * <p>
 * 用于解耦 auth 和 user 模块之间的依赖。
 *
 * @author YunWu Team
 * @since 1.0.0
 */
public interface IUserAuthService {

    /**
     * 根据手机号查找用户，不存在则自动创建
     *
     * @param phone     手机号
     * @param ipAddress 注册 IP
     * @return UserAuthInfo 包含 userId、role、isNewUser 等信息
     */
    UserAuthInfo findOrCreateByPhone(String phone, String ipAddress);

    /**
     * 检查用户状态是否可以登录
     *
     * @param userId 用户 ID
     * @return true 表示可以登录
     */
    boolean canLogin(Long userId);

    /**
     * 用户鉴权信息 (内部类)
     */
    record UserAuthInfo(
            Long userId,
            String role,
            String status,
            boolean isNewUser
    ) {}
}
