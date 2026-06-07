package com.yunwu.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.io.Serializable;

/**
 * 分页请求参数
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Schema(description = "分页请求")
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "页码 (从1开始)", example = "1")
    @Min(value = 1, message = "页码最小为1")
    private int page = 1;

    @Schema(description = "每页条数 (最大100)", example = "20")
    @Min(value = 1, message = "每页最少1条")
    @Max(value = 100, message = "每页最多100条")
    private int size = 20;

    @Schema(description = "排序 (格式: field,asc|desc)", example = "created_at,desc")
    private String sort;

    public PageRequest() {
    }

    public PageRequest(int page, int size) {
        this.page = Math.max(page, 1);
        this.size = Math.min(Math.max(size, 1), 100);
    }

    public PageRequest(int page, int size, String sort) {
        this(page, size);
        this.sort = sort;
    }

    /** 计算 MySQL/Oracle 的 OFFSET */
    public int getOffset() {
        return (page - 1) * size;
    }

    /** 获取排序字段 */
    public String getSortField() {
        if (sort == null || sort.isEmpty()) {
            return "created_at";
        }
        String[] parts = sort.split(",");
        return parts[0].trim();
    }

    /** 获取排序方向 */
    public String getSortDirection() {
        if (sort == null || sort.isEmpty()) {
            return "DESC";
        }
        String[] parts = sort.split(",");
        if (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())) {
            return "ASC";
        }
        return "DESC";
    }

    // ==================== Getters & Setters ====================

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(page, 1);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = Math.min(Math.max(size, 1), 100);
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
