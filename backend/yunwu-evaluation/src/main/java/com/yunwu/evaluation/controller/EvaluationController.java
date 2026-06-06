package com.yunwu.evaluation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yunwu.common.constant.Constants;
import com.yunwu.common.response.ApiResponse;
import com.yunwu.common.response.PageResult;
import com.yunwu.evaluation.dto.EvaluationDTO;
import com.yunwu.evaluation.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.API_PREFIX)
@Tag(name = "测评模块", description = "多维评分、评测历史、分数趋势")
public class EvaluationController {

    private final EvaluationService service;
    public EvaluationController(EvaluationService service) { this.service = service; }

    @GetMapping("/evaluations")
    @Operation(summary = "评测记录列表")
    public ApiResponse<PageResult<EvaluationDTO.ListItem>> list(
            @RequestParam(required = false) String evalType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        IPage<EvaluationDTO.ListItem> result = service.list(evalType, page, size);
        return ApiResponse.ok(new PageResult<>(result.getRecords(), result.getTotal(), page, size));
    }

    @GetMapping("/evaluations/{id}")
    @Operation(summary = "评测详情 (含多维度评分)")
    public ApiResponse<EvaluationDTO.Detail> detail(@PathVariable Long id) {
        return ApiResponse.ok(service.getDetail(id));
    }

    @GetMapping("/evaluations/overview")
    @Operation(summary = "评测概览", description = "仪表盘: 平均分/最高分/趋势/维度均值/本月统计")
    public ApiResponse<EvaluationDTO.Overview> overview() {
        return ApiResponse.ok(service.getOverview());
    }
}
