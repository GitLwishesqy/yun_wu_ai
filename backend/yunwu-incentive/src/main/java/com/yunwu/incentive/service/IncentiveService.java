package com.yunwu.incentive.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunwu.common.context.UserContext;
import com.yunwu.common.exception.BusinessException;
import com.yunwu.common.exception.ErrorCode;
import com.yunwu.incentive.dto.IncentiveDTO;
import com.yunwu.incentive.entity.*;
import com.yunwu.incentive.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IncentiveService {

    private static final Logger log = LoggerFactory.getLogger(IncentiveService.class);
    private final CheckInMapper checkInMapper;
    private final PointsRecordMapper pointsMapper;
    private final UserAchievementMapper uaMapper;
    private final AchievementMapper achievementMapper;

    private static final int POINTS_PER_CHECKIN = 5;

    public IncentiveService(CheckInMapper checkInMapper, PointsRecordMapper pointsMapper,
                            UserAchievementMapper uaMapper,
                            AchievementMapper achievementMapper) {
        this.checkInMapper = checkInMapper; this.pointsMapper = pointsMapper;
        this.uaMapper = uaMapper; this.achievementMapper = achievementMapper;
    }

    // ==================== 打卡 ====================

    @Transactional
    public IncentiveDTO.CheckInResult checkIn() {
        Long userId = UserContext.getUserId();
        LocalDate today = LocalDate.now();

        if (checkInMapper.findByDate(userId, today) != null)
            throw new BusinessException(ErrorCode.RESOURCE_CONFLICT, "今日已打卡");

        // 计算连续天数
        LocalDate lastDate = checkInMapper.findLastDate(userId);
        int streak = 1;
        if (lastDate != null && lastDate.equals(today.minusDays(1))) {
            CheckIn last = checkInMapper.selectOne(
                    new LambdaQueryWrapper<CheckIn>().eq(CheckIn::getUserId, userId).orderByDesc(CheckIn::getCheckInDate).last("LIMIT 1"));
            streak = (last != null ? last.getStreakCount() : 0) + 1;
        }

        int reward = POINTS_PER_CHECKIN + (streak >= 7 ? 5 : 0) + (streak >= 30 ? 15 : 0);

        CheckIn ci = new CheckIn();
        ci.setUserId(userId); ci.setCheckInDate(today); ci.setCheckInTime(LocalDateTime.now());
        ci.setStreakCount(streak); ci.setRewardPoints(reward);
        checkInMapper.insert(ci);

        // 加积分
        int balance = addPoints(userId, reward, "CHECK_IN", "每日打卡(连续" + streak + "天)");
        log.info("[Incentive] 打卡成功 userId={}, streak={}, reward={}", userId, streak, reward);

        IncentiveDTO.CheckInResult r = new IncentiveDTO.CheckInResult();
        r.setId(ci.getId()); r.setCheckInDate(today); r.setStreakCount(streak);
        r.setRewardPoints(reward); r.setTotalPoints(balance);
        return r;
    }

    public IncentiveDTO.CalendarInfo calendar(String month) {
        Long userId = UserContext.getUserId();
        LocalDate start = LocalDate.parse(month + "-01");
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        var list = checkInMapper.selectByMonth(userId, start, end);

        // 计算当前连续天数
        int streak = 0;
        LocalDate today = LocalDate.now();
        LocalDate cursor = today;
        while (checkInMapper.findByDate(userId, cursor) != null) {
            streak++;
            cursor = cursor.minusDays(1);
        }

        IncentiveDTO.CalendarInfo info = new IncentiveDTO.CalendarInfo();
        info.setMonth(month);
        info.setCheckedDates(list.stream().map(c -> c.getCheckInDate().toString()).collect(Collectors.toList()));
        info.setCurrentStreak(streak);
        info.setTotalThisMonth(list.size());
        return info;
    }

    // ==================== 积分 ====================

    public IncentiveDTO.PointsInfo getPoints() {
        Long userId = UserContext.getUserId();
        IncentiveDTO.PointsInfo info = new IncentiveDTO.PointsInfo();
        info.setTotalPoints(pointsMapper.sumByUserId(userId));
        info.setRecords(pointsMapper.selectRecent(userId, 20).stream().map(pr -> {
            IncentiveDTO.PointsRecordItem i = new IncentiveDTO.PointsRecordItem();
            i.setId(pr.getId()); i.setPoints(pr.getPoints()); i.setBalanceAfter(pr.getBalanceAfter());
            i.setActionType(pr.getActionType()); i.setActionDesc(pr.getActionDesc());
            i.setCreatedAt(pr.getCreatedAt());
            return i;
        }).collect(Collectors.toList()));
        return info;
    }

    // ==================== 成就 ====================

    public IncentiveDTO.AchievementBoard getAchievements() {
        Long userId = UserContext.getUserId();
        List<Achievement> all = achievementMapper.selectList(new LambdaQueryWrapper<>());
        List<UserAchievement> userAchs = uaMapper.selectByUserId(userId);
        Map<Long, UserAchievement> uaMap = userAchs.stream()
                .collect(Collectors.toMap(UserAchievement::getAchievementId, ua -> ua));

        IncentiveDTO.AchievementBoard board = new IncentiveDTO.AchievementBoard();
        List<IncentiveDTO.AchievementItem> completed = new ArrayList<>();
        List<IncentiveDTO.AchievementItem> inProgress = new ArrayList<>();
        List<IncentiveDTO.AchievementItem> locked = new ArrayList<>();

        for (Achievement a : all) {
            IncentiveDTO.AchievementItem item = new IncentiveDTO.AchievementItem();
            item.setId(a.getId()); item.setCode(a.getCode()); item.setName(a.getName());
            item.setNameEn(a.getNameEn()); item.setIconUrl(a.getIconUrl()); item.setCategory(a.getCategory());
            item.setPointsReward(a.getPointsReward() != null ? a.getPointsReward() : 0);
            item.setIsSecret(a.getIsSecret() != null && a.getIsSecret());

            UserAchievement ua = uaMap.get(a.getId());
            if (ua != null) {
                try {
                    item.setProgress(JSONUtil.toBean(ua.getProgress(), Map.class));
                } catch (Exception e) { item.setProgress(Map.of()); }
                if (ua.getIsCompleted() != null && ua.getIsCompleted()) {
                    item.setCompletedAt(ua.getCompletedAt());
                    completed.add(item);
                } else {
                    inProgress.add(item);
                }
            } else {
                item.setProgress(Map.of());
                if (a.getIsSecret() != null && a.getIsSecret()) continue; // 隐藏成就
                locked.add(item);
            }
        }

        board.setCompleted(completed); board.setInProgress(inProgress); board.setLocked(locked);
        return board;
    }

    // ==================== 排行榜 ====================

    public IncentiveDTO.Leaderboard getLeaderboard(int limit) {
        var entries = uaMapper.selectLeaderboard(limit);
        IncentiveDTO.Leaderboard lb = new IncentiveDTO.Leaderboard();
        lb.setEntries(entries.stream().map(e -> {
            IncentiveDTO.LeaderboardEntry le = new IncentiveDTO.LeaderboardEntry();
            le.setUserId(((Number) e.get("user_id")).longValue());
            le.setNickname((String) e.get("nickname"));
            le.setAvatarUrl((String) e.get("avatar_url"));
            le.setTotalPoints(((Number) e.get("total_pts")).intValue());
            return le;
        }).collect(Collectors.toList()));
        return lb;
    }

    // ==================== 内部 ====================

    private int addPoints(Long userId, int points, String actionType, String desc) {
        int before = pointsMapper.sumByUserId(userId);
        int after = before + points;
        PointsRecord pr = new PointsRecord();
        pr.setUserId(userId); pr.setPoints(points); pr.setBalanceAfter(after);
        pr.setActionType(actionType); pr.setActionDesc(desc);
        pr.setCreatedAt(LocalDateTime.now());
        pointsMapper.insert(pr);
        return after;
    }
}
