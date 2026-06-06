package com.yunwu.parent.controller;

import com.yunwu.common.constant.Constants;
import com.yunwu.common.response.ApiResponse;
import com.yunwu.parent.dto.ParentDTO;
import com.yunwu.parent.entity.ParentStudentBinding;
import com.yunwu.parent.service.ParentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(Constants.API_PREFIX + "/parent")
@Tag(name = "家长模块", description = "绑定管理、学生监控")
public class ParentController {

    private final ParentService service;
    public ParentController(ParentService service) { this.service = service; }

    @GetMapping("/bindings")
    @Operation(summary = "绑定列表")
    public ApiResponse<List<ParentStudentBinding>> bindings() { return ApiResponse.ok(service.getBindings()); }

    @PostMapping("/bindings")
    @Operation(summary = "发起绑定请求")
    public ApiResponse<Void> requestBind(@RequestBody ParentDTO.BindRequest req) { service.requestBind(req); return ApiResponse.ok(); }

    @PostMapping("/bindings/{id}/approve")
    @Operation(summary = "审批通过 (学生端)")
    public ApiResponse<Void> approve(@PathVariable Long id) { service.approveBind(id); return ApiResponse.ok(); }

    @PostMapping("/bindings/{id}/reject")
    @Operation(summary = "拒绝绑定")
    public ApiResponse<Void> reject(@PathVariable Long id) { service.rejectBind(id); return ApiResponse.ok(); }

    @PatchMapping("/bindings/{id}/settings")
    @Operation(summary = "更新设置 (时长/预算)")
    public ApiResponse<Void> settings(@PathVariable Long id, @RequestBody ParentDTO.SettingsUpdate req) { service.updateSettings(id, req); return ApiResponse.ok(); }

    @GetMapping("/students/{studentId}/overview")
    @Operation(summary = "学生总览仪表盘")
    public ApiResponse<ParentDTO.StudentOverview> overview(@PathVariable Long studentId) { return ApiResponse.ok(service.getStudentOverview(studentId)); }
}
