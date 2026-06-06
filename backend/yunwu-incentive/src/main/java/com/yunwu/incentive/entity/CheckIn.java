package com.yunwu.incentive.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("check_ins")
public class CheckIn implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId; private LocalDate checkInDate; private LocalDateTime checkInTime;
    private Integer streakCount; private Integer rewardPoints; private String note;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getCheckInDate() { return checkInDate; } public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDateTime getCheckInTime() { return checkInTime; } public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }
    public Integer getStreakCount() { return streakCount; } public void setStreakCount(Integer streakCount) { this.streakCount = streakCount; }
    public Integer getRewardPoints() { return rewardPoints; } public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }
    public String getNote() { return note; } public void setNote(String note) { this.note = note; }
}
