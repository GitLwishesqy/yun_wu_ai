package com.yunwu.notification.controller;

import com.yunwu.common.constant.Constants;
import com.yunwu.common.response.ApiResponse;
import com.yunwu.notification.entity.Notification;
import com.yunwu.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map;

@RestController @RequestMapping(Constants.API_PREFIX)
@Tag(name = "通知模块", description = "通知列表、已读、未读数")
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service) { this.service = service; }

    @GetMapping("/notifications")
    @Operation(summary = "通知列表")
    public ApiResponse<List<Notification>> list(@RequestParam(required = false) Boolean isRead,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(service.list(isRead, page, size));
    }

    @GetMapping("/notifications/unread-count")
    @Operation(summary = "未读数量")
    public ApiResponse<Map<String, Object>> unreadCount() { return ApiResponse.ok(service.unreadCount()); }

    @PatchMapping("/notifications/{id}/read")
    @Operation(summary = "标记已读") public ApiResponse<Void> markRead(@PathVariable Long id) { service.markRead(id); return ApiResponse.ok(); }

    @PatchMapping("/notifications/read-all")
    @Operation(summary = "全部已读") public ApiResponse<Void> markAllRead() { service.markAllRead(); return ApiResponse.ok(); }
}
