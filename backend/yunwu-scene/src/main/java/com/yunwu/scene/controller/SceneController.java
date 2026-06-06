package com.yunwu.scene.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yunwu.common.constant.Constants;
import com.yunwu.common.response.ApiResponse;
import com.yunwu.common.response.PageResult;
import com.yunwu.scene.dto.SceneDTO;
import com.yunwu.scene.service.SceneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "场景模板", description = "陪练场景浏览、管理端CRUD")
public class SceneController {

    private final SceneService service;

    public SceneController(SceneService service) { this.service = service; }

    // ==================== 学生端 ====================

    @GetMapping(Constants.API_PREFIX + "/scenes")
    @Operation(summary = "场景列表 (已发布)")
    public ApiResponse<PageResult<SceneDTO.ListItem>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String gradeLevel,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String cefrLevel) {

        IPage<SceneDTO.ListItem> result = service.listPublished(
                page, size, gradeLevel, category, difficulty, keyword, cefrLevel);
        return ApiResponse.ok(new PageResult<>(result.getRecords(),
                result.getTotal(), (int) result.getCurrent(), (int) result.getSize()));
    }

    @GetMapping(Constants.API_PREFIX + "/scenes/{id}")
    @Operation(summary = "场景详情")
    public ApiResponse<SceneDTO.Response> detail(@PathVariable Long id) {
        return ApiResponse.ok(service.getPublished(id));
    }

    @GetMapping(Constants.API_PREFIX + "/scenes/categories")
    @Operation(summary = "所有分类")
    public ApiResponse<List<String>> categories() {
        return ApiResponse.ok(service.getCategories());
    }

    // ==================== 管理端 ====================

    @GetMapping(Constants.API_PREFIX + "/admin/scenes")
    @Operation(summary = "场景管理列表")
    public ApiResponse<PageResult<SceneDTO.ListItem>> adminList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) String keyword) {

        IPage<SceneDTO.ListItem> result = service.adminList(page, size, category, published, keyword);
        return ApiResponse.ok(new PageResult<>(result.getRecords(),
                result.getTotal(), (int) result.getCurrent(), (int) result.getSize()));
    }

    @PostMapping(Constants.API_PREFIX + "/admin/scenes")
    @Operation(summary = "创建场景")
    public ApiResponse<SceneDTO.Response> create(@Valid @RequestBody SceneDTO.CreateRequest req) {
        return ApiResponse.ok(service.create(req));
    }

    @PutMapping(Constants.API_PREFIX + "/admin/scenes/{id}")
    @Operation(summary = "更新场景")
    public ApiResponse<SceneDTO.Response> update(@PathVariable Long id,
                                                  @Valid @RequestBody SceneDTO.UpdateRequest req) {
        return ApiResponse.ok(service.update(id, req));
    }

    @DeleteMapping(Constants.API_PREFIX + "/admin/scenes/{id}")
    @Operation(summary = "删除场景")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok();
    }

    @PatchMapping(Constants.API_PREFIX + "/admin/scenes/{id}/publish")
    @Operation(summary = "发布场景")
    public ApiResponse<Void> publish(@PathVariable Long id) {
        service.publish(id);
        return ApiResponse.ok();
    }

    @PatchMapping(Constants.API_PREFIX + "/admin/scenes/{id}/unpublish")
    @Operation(summary = "下架场景")
    public ApiResponse<Void> unpublish(@PathVariable Long id) {
        service.unpublish(id);
        return ApiResponse.ok();
    }
}
