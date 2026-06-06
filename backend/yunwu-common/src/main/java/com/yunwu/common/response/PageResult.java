package com.yunwu.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页结果封装
 *
 * @param <T> 数据类型
 * @author YunWu Team
 * @since 1.0.0
 */
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "数据列表")
    private List<T> items;

    @Schema(description = "分页信息")
    private Pagination pagination;

    public PageResult() {
        this.items = Collections.emptyList();
        this.pagination = new Pagination();
    }

    public PageResult(List<T> items, long total, int page, int size) {
        this.items = items != null ? items : Collections.emptyList();
        this.pagination = new Pagination(page, size, total);
    }

    /**
     * 空结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(Collections.emptyList(), 0, 1, 20);
    }

    /**
     * 转换数据类型
     */
    public <R> PageResult<R> map(Function<? super T, ? extends R> converter) {
        List<R> converted = this.items.stream()
                .map(converter)
                .collect(Collectors.toList());
        PageResult<R> result = new PageResult<>();
        result.items = converted;
        result.pagination = this.pagination;
        return result;
    }

    // ==================== Getters & Setters ====================

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    // ==================== 内部类: 分页信息 ====================

    @Schema(description = "分页信息")
    public static class Pagination implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "当前页码", example = "1")
        private int page;

        @Schema(description = "每页条数", example = "20")
        private int size;

        @Schema(description = "总记录数", example = "156")
        private long total;

        @Schema(description = "总页数", example = "8")
        private int totalPages;

        public Pagination() {
        }

        public Pagination(int page, int size, long total) {
            this.page = page;
            this.size = size;
            this.total = total;
            this.totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
        }

        /** 是否有下一页 */
        public boolean isHasNext() {
            return page < totalPages;
        }

        /** 是否有上一页 */
        public boolean isHasPrev() {
            return page > 1;
        }

        // ==================== Getters & Setters ====================

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        @Schema(description = "是否有下一页")
        public boolean getHasNext() {
            return isHasNext();
        }

        @Schema(description = "是否有上一页")
        public boolean getHasPrev() {
            return isHasPrev();
        }
    }
}
