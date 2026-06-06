package com.yunwu.user.controller;

import com.yunwu.common.constant.Constants;
import com.yunwu.common.context.UserContext;
import com.yunwu.common.response.ApiResponse;
import com.yunwu.user.dto.*;
import com.yunwu.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@RestController
@RequestMapping(Constants.API_PREFIX + "/users")
@Tag(name = "用户模块", description = "用户信息查询与修改、学习档案管理")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==================== 当前用户 ====================

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public ApiResponse<UserInfoResponse> getCurrentUser() {
        Long userId = UserContext.getUserId();
        UserInfoResponse user = userService.getCurrentUser(userId);
        return ApiResponse.ok(user);
    }

    @PatchMapping("/me")
    @Operation(summary = "更新当前用户信息")
    public ApiResponse<UserInfoResponse> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request) {
        Long userId = UserContext.getUserId();
        UserInfoResponse user = userService.updateCurrentUser(userId, request);
        return ApiResponse.ok(user);
    }

    // ==================== 学习档案 ====================

    @GetMapping("/me/profile")
    @Operation(summary = "获取学习档案")
    public ApiResponse<ProfileResponse> getProfile() {
        Long userId = UserContext.getUserId();
        ProfileResponse profile = userService.getProfile(userId);
        return ApiResponse.ok(profile);
    }

    @PatchMapping("/me/profile")
    @Operation(summary = "更新学习档案")
    public ApiResponse<ProfileResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        Long userId = UserContext.getUserId();
        ProfileResponse profile = userService.updateProfile(userId, request);
        return ApiResponse.ok(profile);
    }
}
