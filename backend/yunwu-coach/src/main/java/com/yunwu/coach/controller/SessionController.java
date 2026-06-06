package com.yunwu.coach.controller;

import com.yunwu.common.constant.Constants;
import com.yunwu.common.response.ApiResponse;
import com.yunwu.coach.dto.*;
import com.yunwu.coach.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 陪练会话控制器
 */
@RestController
@RequestMapping(Constants.API_PREFIX + "/sessions")
@Tag(name = "陪练核心", description = "会话管理、消息收发")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    @Operation(summary = "创建陪练会话")
    public ApiResponse<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        return ApiResponse.ok(sessionService.createSession(request));
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "发送消息 (核心)", description = "发送消息并获取 AI 回复")
    public ApiResponse<MessageResponse> sendMessage(
            @PathVariable Long id,
            @Valid @RequestBody SendMessageRequest request) {
        return ApiResponse.ok(sessionService.sendMessage(id, request));
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "获取消息列表")
    public ApiResponse<List<MessageResponse>> getMessages(@PathVariable Long id) {
        return ApiResponse.ok(sessionService.getMessages(id));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "完成会话")
    public ApiResponse<SessionResponse> completeSession(@PathVariable Long id) {
        return ApiResponse.ok(sessionService.completeSession(id));
    }
}
