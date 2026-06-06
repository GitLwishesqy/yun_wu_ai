package com.yunwu.user.service;

import com.yunwu.user.dto.*;
import com.yunwu.user.entity.User;

/**
 * 用户服务接口
 *
 * @author YunWu Team
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 获取当前登录用户信息
     */
    UserInfoResponse getCurrentUser(Long userId);

    /**
     * 更新当前用户信息
     */
    UserInfoResponse updateCurrentUser(Long userId, UserUpdateRequest request);

    /**
     * 根据手机号查找用户 (不创建)
     */
    User findByPhone(String phone);

    /**
     * 根据手机号查找或创建用户
     *
     * @return [user, isNewUser]
     */
    User findOrCreateByPhone(String phone, String ipAddress);

    /**
     * 更新最后登录信息
     */
    void updateLastLogin(Long userId, String ip);

    /**
     * 获取学习档案
     */
    ProfileResponse getProfile(Long userId);

    /**
     * 更新学习档案
     */
    ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request);

    /**
     * 初始化学习档案 (新用户注册后调用)
     */
    void initProfile(Long userId);

    /**
     * 检查用户是否活跃
     */
    boolean isActive(Long userId);
}
