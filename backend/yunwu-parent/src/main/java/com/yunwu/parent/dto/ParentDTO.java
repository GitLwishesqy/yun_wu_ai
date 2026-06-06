package com.yunwu.parent.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParentDTO {

    public static class BindRequest { private String studentPhone; private String relationship;
        public String getStudentPhone() { return studentPhone; } public void setStudentPhone(String studentPhone) { this.studentPhone = studentPhone; }
        public String getRelationship() { return relationship; } public void setRelationship(String relationship) { this.relationship = relationship; }
    }

    public static class SettingsUpdate { private Integer dailyTimeLimitMinutes; private BigDecimal monthlyBudgetLimit;
        public Integer getDailyTimeLimitMinutes() { return dailyTimeLimitMinutes; } public void setDailyTimeLimitMinutes(Integer dailyTimeLimitMinutes) { this.dailyTimeLimitMinutes = dailyTimeLimitMinutes; }
        public BigDecimal getMonthlyBudgetLimit() { return monthlyBudgetLimit; } public void setMonthlyBudgetLimit(BigDecimal monthlyBudgetLimit) { this.monthlyBudgetLimit = monthlyBudgetLimit; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StudentOverview {
        private Long studentId; private String nickname; private String gradeLevel; private String cefrLevel;
        private TodayStats today; private WeeklyStats week; private String weaknesses;
        private java.util.List<RecentSession> recentSessions;

        public Long getStudentId() { return studentId; } public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getNickname() { return nickname; } public void setNickname(String nickname) { this.nickname = nickname; }
        public String getGradeLevel() { return gradeLevel; } public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
        public String getCefrLevel() { return cefrLevel; } public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
        public TodayStats getToday() { return today; } public void setToday(TodayStats today) { this.today = today; }
        public WeeklyStats getWeek() { return week; } public void setWeek(WeeklyStats week) { this.week = week; }
        public String getWeaknesses() { return weaknesses; } public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }
        public java.util.List<RecentSession> getRecentSessions() { return recentSessions; } public void setRecentSessions(java.util.List<RecentSession> recentSessions) { this.recentSessions = recentSessions; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TodayStats { private int sessions; private int minutes; private int remainingMinutes; private boolean checkedIn;
        public int getSessions() { return sessions; } public void setSessions(int sessions) { this.sessions = sessions; }
        public int getMinutes() { return minutes; } public void setMinutes(int minutes) { this.minutes = minutes; }
        public int getRemainingMinutes() { return remainingMinutes; } public void setRemainingMinutes(int remainingMinutes) { this.remainingMinutes = remainingMinutes; }
        public boolean isCheckedIn() { return checkedIn; } public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WeeklyStats { private int sessions; private int minutes; private double avgScore; private double scoreChange;
        public int getSessions() { return sessions; } public void setSessions(int sessions) { this.sessions = sessions; }
        public int getMinutes() { return minutes; } public void setMinutes(int minutes) { this.minutes = minutes; }
        public double getAvgScore() { return avgScore; } public void setAvgScore(double avgScore) { this.avgScore = avgScore; }
        public double getScoreChange() { return scoreChange; } public void setScoreChange(double scoreChange) { this.scoreChange = scoreChange; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RecentSession { private Long id; private String sceneName; private double score; private int durationMinutes; private String date;
        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getSceneName() { return sceneName; } public void setSceneName(String sceneName) { this.sceneName = sceneName; }
        public double getScore() { return score; } public void setScore(double score) { this.score = score; }
        public int getDurationMinutes() { return durationMinutes; } public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
        public String getDate() { return date; } public void setDate(String date) { this.date = date; }
    }
}
