package com.yunwu.user.service.impl;

import com.yunwu.common.service.IUserAuthService;
import com.yunwu.user.entity.User;
import com.yunwu.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * IUserAuthService 实现 — 供 yunwu-auth 模块调用
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Service
public class UserAuthServiceImpl implements IUserAuthService {

    private static final Logger log = LoggerFactory.getLogger(UserAuthServiceImpl.class);

    private final UserService userService;

    public UserAuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserAuthInfo findOrCreateByPhone(String phone, String ipAddress) {
        // 检查是否已存在
        User existingUser = userService.findByPhone(phone);
        boolean isNewUser = (existingUser == null);

        // 查找或创建
        User user = userService.findOrCreateByPhone(phone, ipAddress);

        log.info("[UserAuth] phone={}, userId={}, isNewUser={}", phone, user.getId(), isNewUser);
        return new UserAuthInfo(user.getId(), user.getRole(), user.getStatus(), isNewUser);
    }

    @Override
    public boolean canLogin(Long userId) {
        return userService.isActive(userId);
    }
}
