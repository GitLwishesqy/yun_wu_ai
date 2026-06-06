package com.yunwu.correction.controller;

import com.yunwu.common.constant.Constants;
import com.yunwu.common.response.ApiResponse;
import com.yunwu.common.response.PageResult;
import com.yunwu.correction.dto.CorrectionDTO;
import com.yunwu.correction.service.CorrectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.API_PREFIX)
@Tag(name = "纠错与反馈", description = "纠错记录、薄弱点分析、艾宾浩斯复习")
public class CorrectionController {

    private final CorrectionService service;

    public CorrectionController(CorrectionService service) { this.service = service; }

    // ==================== 会话纠错 ====================

    @GetMapping("/sessions/{sessionId}/corrections")
    @Operation(summary = "会话纠错记录")
    public ApiResponse<CorrectionDTO.SessionCorrections> sessionCorrections(
            @PathVariable Long sessionId) {
        return ApiResponse.ok(service.getSessionCorrections(sessionId));
    }

    // ==================== 纠错历史 ====================

    @GetMapping("/corrections/history")
    @Operation(summary = "用户纠错历史")
    public ApiResponse<PageResult<CorrectionDTO.Item>> history(
            @RequestParam(required = false) String errorType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<CorrectionDTO.Item> items = service.getUserHistory(errorType, page, size);
        return ApiResponse.ok(new PageResult<>(items, items.size(), page, size));
    }

    // ==================== 标记已查看 ====================

    @PatchMapping("/corrections/{id}/read")
    @Operation(summary = "标记单条纠错已查看")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        service.markReviewed(id);
        return ApiResponse.ok();
    }

    @PatchMapping("/sessions/{sessionId}/corrections/read-all")
    @Operation(summary = "标记会话所有纠错已查看")
    public ApiResponse<Void> markSessionRead(@PathVariable Long sessionId) {
        service.markSessionReviewed(sessionId);
        return ApiResponse.ok();
    }

    // ==================== 错误分析 (核心) ====================

    @GetMapping("/users/me/error-analysis")
    @Operation(summary = "薄弱点分析", description = "五维雷达图 + Top错误 + 每日趋势 + 薄弱点 + 待复习")
    public ApiResponse<CorrectionDTO.ErrorAnalysis> errorAnalysis() {
        return ApiResponse.ok(service.getErrorAnalysis());
    }

    // ==================== 艾宾浩斯复习 ====================

    @GetMapping("/corrections/review-due")
    @Operation(summary = "待复习错误 (艾宾浩斯)")
    public ApiResponse<List<CorrectionDTO.ReviewItem>> reviewDue(
            @RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.ok(service.getReviewDue(limit));
    }

    @PostMapping("/corrections/review/{errorRecordId}")
    @Operation(summary = "完成复习", description = "标记复习完成，自动计算下次复习时间")
    public ApiResponse<Void> completeReview(@PathVariable Long errorRecordId) {
        service.completeReview(errorRecordId);
        return ApiResponse.ok();
    }
}
