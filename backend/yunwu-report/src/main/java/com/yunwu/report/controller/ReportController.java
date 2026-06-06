package com.yunwu.report.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yunwu.common.constant.Constants;
import com.yunwu.common.response.ApiResponse;
import com.yunwu.common.response.PageResult;
import com.yunwu.report.dto.ReportDTO;
import com.yunwu.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.API_PREFIX)
@Tag(name = "学习报告", description = "日/周/月/学期报告生成、查询、导出")
public class ReportController {

    private final ReportService service;
    public ReportController(ReportService service) { this.service = service; }

    @GetMapping("/reports")
    @Operation(summary = "报告列表")
    public ApiResponse<PageResult<ReportDTO.ListItem>> list(
            @RequestParam(required = false) String periodType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<ReportDTO.ListItem> result = service.list(periodType, page, size);
        return ApiResponse.ok(new PageResult<>(result.getRecords(), result.getTotal(), page, size));
    }

    @GetMapping("/reports/latest")
    @Operation(summary = "最新报告 (自动生成)")
    public ApiResponse<ReportDTO.Detail> latest(
            @RequestParam(defaultValue = "WEEKLY") String periodType) {
        return ApiResponse.ok(service.getLatest(periodType));
    }

    @GetMapping("/reports/{id}")
    @Operation(summary = "报告详情")
    public ApiResponse<ReportDTO.Detail> detail(@PathVariable Long id) {
        return ApiResponse.ok(service.getDetail(id));
    }

    @PatchMapping("/reports/{id}/read")
    @Operation(summary = "标记已读")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        service.markRead(id);
        return ApiResponse.ok();
    }

    @GetMapping("/reports/{id}/export")
    @Operation(summary = "导出报告 PDF (base64)")
    public ApiResponse<String> exportPdf(@PathVariable Long id) {
        return ApiResponse.ok(service.exportPdfBase64(id));
    }
}
