package com.yunwu.clazz.controller;

import com.yunwu.clazz.dto.ClassDTO;
import com.yunwu.clazz.entity.*;
import com.yunwu.clazz.service.ClassService;
import com.yunwu.common.constant.Constants;
import com.yunwu.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(Constants.API_PREFIX + "/classes")
@Tag(name = "班级模块", description = "班级管理、花名册、任务、看板")
public class ClassController {

    private final ClassService service;
    public ClassController(ClassService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "我的班级列表")
    public ApiResponse<List<ClassEntity>> list() { return ApiResponse.ok(service.listMyClasses()); }

    @PostMapping
    @Operation(summary = "创建班级")
    public ApiResponse<ClassEntity> create(@RequestBody ClassDTO.CreateReq req) { return ApiResponse.ok(service.create(req)); }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除班级")
    public ApiResponse<Void> delete(@PathVariable Long id) { service.delete(id); return ApiResponse.ok(); }

    @GetMapping("/{id}/roster")
    @Operation(summary = "花名册")
    public ApiResponse<List<ClassRoster>> roster(@PathVariable Long id) { return ApiResponse.ok(service.getRoster(id)); }

    @PostMapping("/{id}/roster")
    @Operation(summary = "添加学生")
    public ApiResponse<Void> addStudent(@PathVariable Long id, @RequestBody ClassDTO.AddStudentReq req) { service.addStudent(id, req); return ApiResponse.ok(); }

    @DeleteMapping("/{classId}/roster/{studentId}")
    @Operation(summary = "移除学生")
    public ApiResponse<Void> removeStudent(@PathVariable Long classId, @PathVariable Long studentId) { service.removeStudent(classId, studentId); return ApiResponse.ok(); }

    @GetMapping("/{id}/assignments")
    @Operation(summary = "任务列表")
    public ApiResponse<List<ClassAssignment>> assignments(@PathVariable Long id) { return ApiResponse.ok(service.getAssignments(id)); }

    @PostMapping("/{id}/assignments")
    @Operation(summary = "布置任务")
    public ApiResponse<ClassAssignment> createAssignment(@PathVariable Long id, @RequestBody ClassDTO.AssignmentReq req) { return ApiResponse.ok(service.createAssignment(id, req)); }

    @GetMapping("/assignments/{assignmentId}/submissions")
    @Operation(summary = "提交情况")
    public ApiResponse<List<AssignmentSubmission>> submissions(@PathVariable Long assignmentId) { return ApiResponse.ok(service.getSubmissions(assignmentId)); }

    @GetMapping("/{id}/dashboard")
    @Operation(summary = "班级数据看板")
    public ApiResponse<ClassDTO.Dashboard> dashboard(@PathVariable Long id) { return ApiResponse.ok(service.getDashboard(id)); }
}
