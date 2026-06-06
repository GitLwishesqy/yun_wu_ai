package com.yunwu.clazz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ClassDTO {
    public static class CreateReq { private String name; private String description; private String gradeLevel;
        public String getName() { return name; } public void setName(String name) { this.name = name; }
        public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
        public String getGradeLevel() { return gradeLevel; } public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
    }
    public static class AddStudentReq { private String phone;
        public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    }
    public static class AssignmentReq { private String title; private String description; private String assignmentType; private Long sceneId; private LocalDateTime dueDate;
        public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
        public String getAssignmentType() { return assignmentType; } public void setAssignmentType(String assignmentType) { this.assignmentType = assignmentType; }
        public Long getSceneId() { return sceneId; } public void setSceneId(Long sceneId) { this.sceneId = sceneId; }
        public LocalDateTime getDueDate() { return dueDate; } public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Dashboard {
        private String className; private int studentCount; private int activeThisWeek;
        private double avgWeeklySessions; private double avgWeeklyMinutes; private double avgScore; private double completionRate;
        private List<TopStudent> topStudents; private List<CommonWeakness> commonWeaknesses;

        public String getClassName() { return className; } public void setClassName(String className) { this.className = className; }
        public int getStudentCount() { return studentCount; } public void setStudentCount(int studentCount) { this.studentCount = studentCount; }
        public int getActiveThisWeek() { return activeThisWeek; } public void setActiveThisWeek(int activeThisWeek) { this.activeThisWeek = activeThisWeek; }
        public double getAvgWeeklySessions() { return avgWeeklySessions; } public void setAvgWeeklySessions(double avgWeeklySessions) { this.avgWeeklySessions = avgWeeklySessions; }
        public double getAvgWeeklyMinutes() { return avgWeeklyMinutes; } public void setAvgWeeklyMinutes(double avgWeeklyMinutes) { this.avgWeeklyMinutes = avgWeeklyMinutes; }
        public double getAvgScore() { return avgScore; } public void setAvgScore(double avgScore) { this.avgScore = avgScore; }
        public double getCompletionRate() { return completionRate; } public void setCompletionRate(double completionRate) { this.completionRate = completionRate; }
        public List<TopStudent> getTopStudents() { return topStudents; } public void setTopStudents(List<TopStudent> topStudents) { this.topStudents = topStudents; }
        public List<CommonWeakness> getCommonWeaknesses() { return commonWeaknesses; } public void setCommonWeaknesses(List<CommonWeakness> commonWeaknesses) { this.commonWeaknesses = commonWeaknesses; }
    }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TopStudent { private Long studentId; private String nickname; private int sessions; private double avgScore;
        public Long getStudentId() { return studentId; } public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getNickname() { return nickname; } public void setNickname(String nickname) { this.nickname = nickname; }
        public int getSessions() { return sessions; } public void setSessions(int sessions) { this.sessions = sessions; }
        public double getAvgScore() { return avgScore; } public void setAvgScore(double avgScore) { this.avgScore = avgScore; }
    }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CommonWeakness { private String type; private double pct;
        public String getType() { return type; } public void setType(String type) { this.type = type; }
        public double getPct() { return pct; } public void setPct(double pct) { this.pct = pct; }
    }
}
