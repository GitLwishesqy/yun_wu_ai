package com.yunwu.plan.controller;

import com.yunwu.common.constant.Constants;
import com.yunwu.common.response.ApiResponse;
import com.yunwu.plan.dto.PlanDTO;
import com.yunwu.plan.entity.LearningPlan;
import com.yunwu.plan.service.PlanService;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping(Constants.API_PREFIX + "/plans")
@Tag(name = "学习计划", description = "AI生成计划、进度追踪")
public class PlanController {
    private final PlanService service;
    public PlanController(PlanService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "计划列表")
    public ApiResponse<List<LearningPlan>> list(@RequestParam(required = false) Boolean isActive) {
        return ApiResponse.ok(service.list(isActive));
    }

    @GetMapping("/active")
    @Operation(summary = "当前计划 (含今日任务)")
    public ApiResponse<PlanDTO.ActivePlan> active() { return ApiResponse.ok(service.getActive()); }

    @PostMapping("/generate")
    @Operation(summary = "AI 生成学习计划")
    public ApiResponse<PlanDTO.ActivePlan> generate(@RequestBody PlanDTO.GenerateReq req) {
        return ApiResponse.ok(service.generate(req));
    }
}
