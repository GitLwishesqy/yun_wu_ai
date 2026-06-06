package com.yunwu.incentive.controller;

import com.yunwu.common.constant.Constants;
import com.yunwu.common.response.ApiResponse;
import com.yunwu.incentive.dto.IncentiveDTO;
import com.yunwu.incentive.service.IncentiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.API_PREFIX)
@Tag(name = "激励模块", description = "打卡、积分、成就、排行榜")
public class IncentiveController {

    private final IncentiveService service;
    public IncentiveController(IncentiveService service) { this.service = service; }

    @PostMapping("/check-ins")
    @Operation(summary = "每日打卡")
    public ApiResponse<IncentiveDTO.CheckInResult> checkIn() {
        return ApiResponse.ok(service.checkIn());
    }

    @GetMapping("/check-ins/calendar")
    @Operation(summary = "打卡日历")
    public ApiResponse<IncentiveDTO.CalendarInfo> calendar(@RequestParam(defaultValue = "2026-06") String month) {
        return ApiResponse.ok(service.calendar(month));
    }

    @GetMapping("/points/records")
    @Operation(summary = "积分记录")
    public ApiResponse<IncentiveDTO.PointsInfo> points() {
        return ApiResponse.ok(service.getPoints());
    }

    @GetMapping("/achievements")
    @Operation(summary = "成就面板 (已完成/进行中/未解锁)")
    public ApiResponse<IncentiveDTO.AchievementBoard> achievements() {
        return ApiResponse.ok(service.getAchievements());
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "积分排行榜")
    public ApiResponse<IncentiveDTO.Leaderboard> leaderboard(@RequestParam(defaultValue = "50") int limit) {
        return ApiResponse.ok(service.getLeaderboard(limit));
    }
}
