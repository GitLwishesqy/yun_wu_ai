package com.yunwu.parent.service;

import com.yunwu.common.context.UserContext;
import com.yunwu.common.exception.BusinessException;
import com.yunwu.common.exception.ErrorCode;
import com.yunwu.parent.dto.ParentDTO;
import com.yunwu.parent.entity.ParentStudentBinding;
import com.yunwu.parent.mapper.BindingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ParentService {

    private static final Logger log = LoggerFactory.getLogger(ParentService.class);
    private final BindingMapper mapper;

    public ParentService(BindingMapper mapper) { this.mapper = mapper; }

    public List<ParentStudentBinding> getBindings() {
        return mapper.selectActiveByParent(UserContext.getUserId());
    }

    @Transactional
    public void requestBind(ParentDTO.BindRequest req) {
        Long parentId = UserContext.getUserId();
        // TODO: 根据手机号查找学生ID
        Long studentId = (long) Math.abs(req.getStudentPhone().hashCode());
        if (mapper.findByPair(parentId, studentId) != null)
            throw new BusinessException(ErrorCode.BINDING_ALREADY_EXISTS);
        ParentStudentBinding b = new ParentStudentBinding();
        b.setParentId(parentId); b.setStudentId(studentId); b.setBindingStatus("PENDING");
        b.setRelationship(req.getRelationship()); b.setRequestedAt(LocalDateTime.now());
        b.setDailyTimeLimitMinutes(60);
        b.setCreatedAt(LocalDateTime.now()); b.setUpdatedAt(LocalDateTime.now());
        mapper.insert(b);
        log.info("[Parent] 发起绑定 parent={}, student={}", parentId, studentId);
    }

    @Transactional
    public void approveBind(Long bindingId) {
        ParentStudentBinding b = mapper.selectById(bindingId);
        if (b == null) throw new BusinessException(ErrorCode.BINDING_NOT_FOUND);
        b.setBindingStatus("ACTIVE"); b.setApprovedAt(LocalDateTime.now()); b.setUpdatedAt(LocalDateTime.now());
        mapper.updateById(b);
    }

    @Transactional
    public void rejectBind(Long bindingId) {
        ParentStudentBinding b = mapper.selectById(bindingId);
        if (b == null) throw new BusinessException(ErrorCode.BINDING_NOT_FOUND);
        mapper.deleteById(bindingId);
    }

    @Transactional
    public void updateSettings(Long bindingId, ParentDTO.SettingsUpdate req) {
        ParentStudentBinding b = mapper.selectById(bindingId);
        if (b == null) throw new BusinessException(ErrorCode.BINDING_NOT_FOUND);
        if (req.getDailyTimeLimitMinutes() != null) b.setDailyTimeLimitMinutes(req.getDailyTimeLimitMinutes());
        if (req.getMonthlyBudgetLimit() != null) b.setMonthlyBudgetLimit(req.getMonthlyBudgetLimit());
        b.setUpdatedAt(LocalDateTime.now()); mapper.updateById(b);
    }

    public ParentDTO.StudentOverview getStudentOverview(Long studentId) {
        ParentDTO.StudentOverview ov = new ParentDTO.StudentOverview();
        ov.setStudentId(studentId); ov.setNickname("学生" + studentId); ov.setGradeLevel("ELEMENTARY"); ov.setCefrLevel("A1");
        ParentDTO.TodayStats today = new ParentDTO.TodayStats();
        today.setSessions(2); today.setMinutes(30); today.setRemainingMinutes(30); today.setCheckedIn(true);
        ov.setToday(today);
        ParentDTO.WeeklyStats week = new ParentDTO.WeeklyStats();
        week.setSessions(12); week.setMinutes(180); week.setAvgScore(82.5); week.setScoreChange(3.2);
        ov.setWeek(week);
        ov.setWeaknesses("pronunciation:0.6, grammar:0.4");
        return ov;
    }
}
