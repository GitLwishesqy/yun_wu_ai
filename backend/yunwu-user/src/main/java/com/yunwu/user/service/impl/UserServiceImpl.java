package com.yunwu.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunwu.common.enums.RoleEnum;
import com.yunwu.common.exception.BusinessException;
import com.yunwu.common.exception.ErrorCode;
import com.yunwu.user.dto.*;
import com.yunwu.user.entity.LearnerProfile;
import com.yunwu.user.entity.User;
import com.yunwu.user.mapper.LearnerProfileMapper;
import com.yunwu.user.mapper.UserMapper;
import com.yunwu.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户服务实现
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final LearnerProfileMapper profileMapper;

    public UserServiceImpl(UserMapper userMapper, LearnerProfileMapper profileMapper) {
        this.userMapper = userMapper;
        this.profileMapper = profileMapper;
    }

    // ==================== 用户查询 ====================

    @Override
    public UserInfoResponse getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.isDeleted()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return toUserInfoResponse(user);
    }

    @Override
    public User findByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    // ==================== 用户更新 ====================

    @Override
    @Transactional
    public UserInfoResponse updateCurrentUser(Long userId, UserUpdateRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null || user.isDeleted()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getGradeLevel() != null) user.setGradeLevel(request.getGradeLevel());
        if (request.getGradeDetail() != null) user.setGradeDetail(request.getGradeDetail());
        if (request.getVoicePreference() != null) user.setVoicePreference(request.getVoicePreference());
        if (request.getSpeechRate() != null) user.setSpeechRate(request.getSpeechRate());
        if (request.getThemeMode() != null) user.setThemeMode(request.getThemeMode());
        if (request.getDailyLimitMinutes() != null) user.setDailyLimitMinutes(request.getDailyLimitMinutes());
        if (request.getUiFontScale() != null) user.setUiFontScale(request.getUiFontScale());

        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("[User] 用户信息更新成功 userId={}", userId);
        return toUserInfoResponse(user);
    }

    // ==================== 注册/登录 ====================

    @Override
    @Transactional
    public User findOrCreateByPhone(String phone, String ipAddress) {
        User user = userMapper.selectByPhone(phone);

        if (user == null) {
            user = createUser(phone);
            initProfile(user.getId());
            log.info("[User] 新用户注册成功 userId={}, phone={}", user.getId(), phone);
        }

        // 更新登录信息
        updateLastLogin(user.getId(), ipAddress);
        return user;
    }

    @Override
    public void updateLastLogin(Long userId, String ip) {
        userMapper.updateLastLogin(userId, ip);
    }

    @Override
    public boolean isActive(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null && "ACTIVE".equals(user.getStatus());
    }

    // ==================== 学习档案 ====================

    @Override
    public ProfileResponse getProfile(Long userId) {
        LearnerProfile profile = profileMapper.selectByUserId(userId);
        if (profile == null) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_FOUND);
        }
        return toProfileResponse(profile);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        LearnerProfile profile = profileMapper.selectByUserId(userId);
        if (profile == null) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_FOUND);
        }

        if (request.getLearningGoal() != null) {
            profile.setLearningGoal(request.getLearningGoal());
        }
        if (request.getPreferredTopics() != null) {
            profile.setPreferredTopics(toJsonString(request.getPreferredTopics()));
        }

        profile.setUpdatedAt(LocalDateTime.now());
        profileMapper.updateById(profile);

        log.info("[Profile] 学习档案更新成功 userId={}", userId);
        return toProfileResponse(profile);
    }

    @Override
    @Transactional
    public void initProfile(Long userId) {
        LearnerProfile profile = new LearnerProfile();
        profile.setUserId(userId);
        profile.setEstimatedVocabularySize(0);
        profile.setCefrLevel("A1");
        // JSONB columns: skip setting, let DB DEFAULT handle it
        // profile.setWeaknesses("{}");
        profile.setTotalLearningDays(0);
        profile.setTotalSessionCount(0);
        profile.setTotalLearningMinutes(0);
        profile.setTotalWordsSpoken(0);
        profile.setAvgAccuracyRate(BigDecimal.ZERO);
        profile.setStreakDays(0);
        profile.setMaxStreakDays(0);
        // JSONB column: skip, let DB DEFAULT handle it
        // profile.setPreferredTopics("[]");
        profile.setLearningGoal("");

        profileMapper.insert(profile);
        log.info("[Profile] 学习档案初始化成功 userId={}", userId);
    }

    // ==================== 私有方法 ====================

    private User createUser(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickname("用户" + phone.substring(7));
        user.setRole(RoleEnum.STUDENT.getCode());
        user.setStatus("ACTIVE");
        user.setRealNameVerified(false);
        user.setDailyLimitMinutes(60);
        user.setVoicePreference("FEMALE");
        user.setSpeechRate(BigDecimal.ONE);
        user.setUiFontScale(BigDecimal.ONE);
        user.setThemeMode("LIGHT");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);
        return user;
    }

    private UserInfoResponse toUserInfoResponse(User user) {
        UserInfoResponse resp = new UserInfoResponse();
        BeanUtil.copyProperties(user, resp);
        // 手机号脱敏
        String phone = user.getPhone();
        if (phone != null && phone.length() == 11) {
            resp.setPhone(phone.substring(0, 3) + "****" + phone.substring(7));
        }
        return resp;
    }

    private ProfileResponse toProfileResponse(LearnerProfile profile) {
        ProfileResponse resp = new ProfileResponse();
        BeanUtil.copyProperties(profile, resp);
        return resp;
    }

    private String toJsonString(java.util.List<String> list) {
        if (list == null) return "[]";
        return "[\"" + String.join("\",\"", list) + "\"]";
    }
}
