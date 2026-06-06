package com.yunwu.plan.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.util.List;

public class PlanDTO {

    public static class GenerateReq {
        private String name; private LocalDate startDate; private LocalDate endDate;
        private String targetLevel; private List<String> focusAreas; private String weeklyEffort;
        public String getName() { return name; } public void setName(String name) { this.name = name; }
        public LocalDate getStartDate() { return startDate; } public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; } public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public String getTargetLevel() { return targetLevel; } public void setTargetLevel(String targetLevel) { this.targetLevel = targetLevel; }
        public List<String> getFocusAreas() { return focusAreas; } public void setFocusAreas(List<String> focusAreas) { this.focusAreas = focusAreas; }
        public String getWeeklyEffort() { return weeklyEffort; } public void setWeeklyEffort(String weeklyEffort) { this.weeklyEffort = weeklyEffort; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ActivePlan {
        private Long id; private String name; private String planType; private LocalDate startDate; private LocalDate endDate;
        private String targetLevel; private Progress progress; private List<TodayItem> todayItems;

        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getName() { return name; } public void setName(String name) { this.name = name; }
        public String getPlanType() { return planType; } public void setPlanType(String planType) { this.planType = planType; }
        public LocalDate getStartDate() { return startDate; } public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; } public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public String getTargetLevel() { return targetLevel; } public void setTargetLevel(String targetLevel) { this.targetLevel = targetLevel; }
        public Progress getProgress() { return progress; } public void setProgress(Progress progress) { this.progress = progress; }
        public List<TodayItem> getTodayItems() { return todayItems; } public void setTodayItems(List<TodayItem> todayItems) { this.todayItems = todayItems; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Progress { private int totalItems; private int completedItems; private double completionPct;
        public int getTotalItems() { return totalItems; } public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
        public int getCompletedItems() { return completedItems; } public void setCompletedItems(int completedItems) { this.completedItems = completedItems; }
        public double getCompletionPct() { return completionPct; } public void setCompletionPct(double completionPct) { this.completionPct = completionPct; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TodayItem {
        private Long id; private String itemType; private String itemName; private Long sceneId;
        private boolean isCompleted; private int estimatedMinutes; private int pointsReward;
        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getItemType() { return itemType; } public void setItemType(String itemType) { this.itemType = itemType; }
        public String getItemName() { return itemName; } public void setItemName(String itemName) { this.itemName = itemName; }
        public Long getSceneId() { return sceneId; } public void setSceneId(Long sceneId) { this.sceneId = sceneId; }
        public boolean getIsCompleted() { return isCompleted; } public void setIsCompleted(boolean isCompleted) { this.isCompleted = isCompleted; }
        public int getEstimatedMinutes() { return estimatedMinutes; } public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }
        public int getPointsReward() { return pointsReward; } public void setPointsReward(int pointsReward) { this.pointsReward = pointsReward; }
    }
}
