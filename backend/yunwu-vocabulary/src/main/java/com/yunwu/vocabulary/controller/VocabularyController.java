package com.yunwu.vocabulary.controller;

import com.yunwu.common.constant.Constants;
import com.yunwu.common.response.ApiResponse;
import com.yunwu.vocabulary.dto.VocabularyDTO;
import com.yunwu.vocabulary.service.VocabularyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.API_PREFIX)
@Tag(name = "词汇模块", description = "个人词汇本、掌握状态、艾宾浩斯复习、公共词汇库")
public class VocabularyController {

    private final VocabularyService service;
    public VocabularyController(VocabularyService service) { this.service = service; }

    @GetMapping("/vocabulary/my")
    @Operation(summary = "我的词汇本 (含状态统计)")
    public ApiResponse<VocabularyDTO.MyVocabList> myVocab(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ApiResponse.ok(service.getMyVocab(status, q, page, size));
    }

    @PatchMapping("/vocabulary/{id}/status")
    @Operation(summary = "更新词汇掌握状态")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestBody VocabularyDTO.StatusUpdate req) {
        service.updateStatus(id, req.getStatus());
        return ApiResponse.ok();
    }

    @GetMapping("/vocabulary/review-due")
    @Operation(summary = "待复习词汇 (艾宾浩斯)")
    public ApiResponse<List<VocabularyDTO.MyWord>> reviewDue(@RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.ok(service.getReviewDue(limit));
    }

    @PostMapping("/vocabulary/review/{id}")
    @Operation(summary = "完成复习")
    public ApiResponse<Void> completeReview(@PathVariable Long id) {
        service.completeReview(id);
        return ApiResponse.ok();
    }

    @GetMapping("/vocabulary/library")
    @Operation(summary = "公共词汇库查询")
    public ApiResponse<List<VocabularyDTO.LibraryWord>> searchLibrary(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String cefrLevel,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ApiResponse.ok(service.searchLibrary(q, cefrLevel, page, size));
    }
}
