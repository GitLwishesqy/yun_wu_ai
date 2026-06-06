package com.yunwu.skill.controller;

import com.yunwu.common.constant.Constants;
import com.yunwu.common.response.ApiResponse;
import com.yunwu.skill.dto.SkillDTO;
import com.yunwu.skill.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.API_PREFIX + "/skills")
@Tag(name = "技能训练", description = "听力/阅读/写作")
public class SkillController {

    private final SkillService service;
    public SkillController(SkillService service) { this.service = service; }

    // ==================== 听力 ====================
    @GetMapping("/listening")
    @Operation(summary = "听力材料列表")
    public ApiResponse<List<SkillDTO.ListeningItem>> listListening(
            @RequestParam(required = false) String gradeLevel, @RequestParam(required = false) Integer difficulty,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(service.listListening(gradeLevel, difficulty, page, size));
    }

    @GetMapping("/listening/{id}")
    @Operation(summary = "听力材料详情 (含转写+题目)")
    public ApiResponse<SkillDTO.ListeningDetail> getListening(@PathVariable Long id) {
        return ApiResponse.ok(service.getListening(id));
    }

    @PostMapping("/listening/{id}/submit")
    @Operation(summary = "提交听力答案")
    public ApiResponse<SkillDTO.AnswerResult> submitListening(@PathVariable Long id, @Valid @RequestBody SkillDTO.AnswerSubmit submit) {
        return ApiResponse.ok(service.submitListening(id, submit));
    }

    // ==================== 阅读 ====================
    @GetMapping("/reading")
    @Operation(summary = "阅读材料列表")
    public ApiResponse<List<SkillDTO.ReadingItem>> listReading(
            @RequestParam(required = false) String gradeLevel, @RequestParam(required = false) Integer difficulty,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(service.listReading(gradeLevel, difficulty, page, size));
    }

    @GetMapping("/reading/{id}")
    @Operation(summary = "阅读材料详情 (含文章+题目)")
    public ApiResponse<SkillDTO.ReadingDetail> getReading(@PathVariable Long id) {
        return ApiResponse.ok(service.getReading(id));
    }

    @PostMapping("/reading/{id}/submit")
    @Operation(summary = "提交阅读答案")
    public ApiResponse<SkillDTO.AnswerResult> submitReading(@PathVariable Long id, @Valid @RequestBody SkillDTO.AnswerSubmit submit) {
        return ApiResponse.ok(service.submitReading(id, submit));
    }

    // ==================== 写作 ====================
    @GetMapping("/writing")
    @Operation(summary = "写作题目列表")
    public ApiResponse<List<SkillDTO.WritingItem>> listWriting(
            @RequestParam(required = false) String gradeLevel, @RequestParam(required = false) Integer difficulty,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(service.listWriting(gradeLevel, difficulty, page, size));
    }

    @GetMapping("/writing/{id}")
    @Operation(summary = "写作题目详情")
    public ApiResponse<SkillDTO.WritingDetail> getWriting(@PathVariable Long id) {
        return ApiResponse.ok(service.getWriting(id));
    }

    @PostMapping("/writing/{id}/submit")
    @Operation(summary = "提交作文 (AI批改)")
    public ApiResponse<SkillDTO.WritingResult> submitWriting(@PathVariable Long id, @Valid @RequestBody SkillDTO.WritingSubmit submit) {
        return ApiResponse.ok(service.submitWriting(id, submit));
    }
}
