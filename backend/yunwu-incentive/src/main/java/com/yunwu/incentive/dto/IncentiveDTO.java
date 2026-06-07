package com.yunwu.incentive.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class IncentiveDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CheckInResult {
        private Long id; private LocalDate checkInDate; private int streakCount;
        private int rewardPoints; private int totalPoints;
        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public LocalDate getCheckInDate() { return checkInDate; } public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
        public int getStreakCount() { return streakCount; } public void setStreakCount(int streakCount) { this.streakCount = streakCount; }
        public int getRewardPoints() { return rewardPoints; } public void setRewardPoints(int rewardPoints) { this.rewardPoints = rewardPoints; }
        public int getTotalPoints() { return totalPoints; } public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CalendarInfo {
        private String month; private List<String> checkedDates;
        private int currentStreak; private int totalThisMonth;
        public String getMonth() { return month; } public void setMonth(String month) { this.month = month; }
        public List<String> getCheckedDates() { return checkedDates; } public void setCheckedDates(List<String> checkedDates) { this.checkedDates = checkedDates; }
        public int getCurrentStreak() { return currentStreak; } public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
        public int getTotalThisMonth() { return totalThisMonth; } public void setTotalThisMonth(int totalThisMonth) { this.totalThisMonth = totalThisMonth; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PointsInfo {
        private int totalPoints; private List<PointsRecordItem> records;
        public int getTotalPoints() { return totalPoints; } public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
        public List<PointsRecordItem> getRecords() { return records; } public void setRecords(List<PointsRecordItem> records) { this.records = records; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PointsRecordItem {
        private Long id; private int points; private int balanceAfter;
        private String actionType; private String actionDesc; private LocalDateTime createdAt;
        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public int getPoints() { return points; } public void setPoints(int points) { this.points = points; }
        public int getBalanceAfter() { return balanceAfter; } public void setBalanceAfter(int balanceAfter) { this.balanceAfter = balanceAfter; }
        public String getActionType() { return actionType; } public void setActionType(String actionType) { this.actionType = actionType; }
        public String getActionDesc() { return actionDesc; } public void setActionDesc(String actionDesc) { this.actionDesc = actionDesc; }
        public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AchievementBoard {
        private List<AchievementItem> completed;
        private List<AchievementItem> inProgress;
        private List<AchievementItem> locked;
        public List<AchievementItem> getCompleted() { return completed; } public void setCompleted(List<AchievementItem> completed) { this.completed = completed; }
        public List<AchievementItem> getInProgress() { return inProgress; } public void setInProgress(List<AchievementItem> inProgress) { this.inProgress = inProgress; }
        public List<AchievementItem> getLocked() { return locked; } public void setLocked(List<AchievementItem> locked) { this.locked = locked; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AchievementItem {
        private Long id; private String code; private String name; private String nameEn;
        private String iconUrl; private String category; private int pointsReward;
        private Map<String, Integer> progress;  // {current: 7, target: 10}
        private LocalDateTime completedAt; private boolean isSecret;
        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getCode() { return code; } public void setCode(String code) { this.code = code; }
        public String getName() { return name; } public void setName(String name) { this.name = name; }
        public String getNameEn() { return nameEn; } public void setNameEn(String nameEn) { this.nameEn = nameEn; }
        public String getIconUrl() { return iconUrl; } public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
        public String getCategory() { return category; } public void setCategory(String category) { this.category = category; }
        public int getPointsReward() { return pointsReward; } public void setPointsReward(int pointsReward) { this.pointsReward = pointsReward; }
        public Map<String, Integer> getProgress() { return progress; } public void setProgress(Map<String, Integer> progress) { this.progress = progress; }
        public LocalDateTime getCompletedAt() { return completedAt; } public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        public boolean getIsSecret() { return isSecret; } public void setIsSecret(boolean isSecret) { this.isSecret = isSecret; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Leaderboard {
        private List<LeaderboardEntry> entries;
        public List<LeaderboardEntry> getEntries() { return entries; } public void setEntries(List<LeaderboardEntry> entries) { this.entries = entries; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LeaderboardEntry {
        private Long userId; private String nickname; private String avatarUrl; private int totalPoints;
        public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
        public String getNickname() { return nickname; } public void setNickname(String nickname) { this.nickname = nickname; }
        public String getAvatarUrl() { return avatarUrl; } public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public int getTotalPoints() { return totalPoints; } public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    }
}
