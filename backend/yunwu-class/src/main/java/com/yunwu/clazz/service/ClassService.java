package com.yunwu.clazz.service;

import cn.hutool.core.util.RandomUtil;
import com.yunwu.clazz.dto.ClassDTO;
import com.yunwu.clazz.entity.*;
import com.yunwu.clazz.mapper.*;
import com.yunwu.common.context.UserContext;
import com.yunwu.common.exception.BusinessException;
import com.yunwu.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ClassService {

    private static final Logger log = LoggerFactory.getLogger(ClassService.class);
    private final ClassMapper classMapper;
    private final ClassRosterMapper rosterMapper;
    private final ClassAssignmentMapper assignmentMapper;
    private final AssignmentSubmissionMapper submissionMapper;

    public ClassService(ClassMapper classMapper, ClassRosterMapper rosterMapper,
                        ClassAssignmentMapper assignmentMapper, AssignmentSubmissionMapper submissionMapper) {
        this.classMapper = classMapper; this.rosterMapper = rosterMapper;
        this.assignmentMapper = assignmentMapper; this.submissionMapper = submissionMapper;
    }

    // ==================== 班级 CRUD ====================

    public List<ClassEntity> listMyClasses() {
        return classMapper.selectByTeacher(UserContext.getUserId());
    }

    @Transactional
    public ClassEntity create(ClassDTO.CreateReq req) {
        ClassEntity c = new ClassEntity();
        c.setName(req.getName()); c.setTeacherId(UserContext.getUserId());
        c.setDescription(req.getDescription()); c.setGradeLevel(req.getGradeLevel());
        c.setInviteCode(RandomUtil.randomString(8).toUpperCase());
        c.setStudentCount(0); c.setIsArchived(false);
        c.setCreatedAt(LocalDateTime.now()); c.setUpdatedAt(LocalDateTime.now());
        classMapper.insert(c);
        log.info("[Class] 创建班级 id={}, name={}", c.getId(), c.getName());
        return c;
    }

    @Transactional
    public void delete(Long id) {
        ClassEntity c = classMapper.selectById(id);
        if (c == null) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        c.setDeletedAt(LocalDateTime.now()); classMapper.updateById(c);
    }

    // ==================== 花名册 ====================

    public List<ClassRoster> getRoster(Long classId) {
        return rosterMapper.selectActiveByClass(classId);
    }

    @Transactional
    public void addStudent(Long classId, ClassDTO.AddStudentReq req) {
        // TODO: 根据手机号查学生ID
        Long studentId = (long) Math.abs(req.getPhone().hashCode());
        ClassRoster r = new ClassRoster();
        r.setClassId(classId); r.setStudentId(studentId); r.setJoinedAt(LocalDateTime.now());
        rosterMapper.insert(r);
        ClassEntity c = classMapper.selectById(classId);
        c.setStudentCount(rosterMapper.countActive(classId));
        classMapper.updateById(c);
    }

    @Transactional
    public void removeStudent(Long classId, Long studentId) {
        ClassRoster r = rosterMapper.selectById(studentId);
        if (r != null) { r.setLeftAt(LocalDateTime.now()); rosterMapper.updateById(r); }
    }

    // ==================== 任务 ====================

    public List<ClassAssignment> getAssignments(Long classId) {
        return assignmentMapper.selectByClass(classId);
    }

    @Transactional
    public ClassAssignment createAssignment(Long classId, ClassDTO.AssignmentReq req) {
        ClassAssignment a = new ClassAssignment();
        a.setClassId(classId); a.setTeacherId(UserContext.getUserId());
        a.setTitle(req.getTitle()); a.setDescription(req.getDescription());
        a.setAssignmentType(req.getAssignmentType()); a.setSceneId(req.getSceneId());
        a.setDueDate(req.getDueDate()); a.setCreatedAt(LocalDateTime.now()); a.setUpdatedAt(LocalDateTime.now());
        assignmentMapper.insert(a);
        log.info("[Class] 布置任务 id={}, title={}", a.getId(), a.getTitle());
        return a;
    }

    public List<AssignmentSubmission> getSubmissions(Long assignmentId) {
        return submissionMapper.selectByAssignment(assignmentId);
    }

    // ==================== 看板 ====================

    public ClassDTO.Dashboard getDashboard(Long classId) {
        ClassEntity c = classMapper.selectById(classId);
        ClassDTO.Dashboard d = new ClassDTO.Dashboard();
        d.setClassName(c.getName()); d.setStudentCount(rosterMapper.countActive(classId));
        d.setActiveThisWeek(25); d.setAvgWeeklySessions(8.5);
        d.setAvgWeeklyMinutes(120); d.setAvgScore(78.5); d.setCompletionRate(85.0);
        List<ClassDTO.TopStudent> tops = new ArrayList<>();
        ClassDTO.TopStudent ts = new ClassDTO.TopStudent();
        ts.setStudentId(1001L); ts.setNickname("小明"); ts.setSessions(15); ts.setAvgScore(92);
        tops.add(ts); d.setTopStudents(tops);
        List<ClassDTO.CommonWeakness> cws = new ArrayList<>();
        ClassDTO.CommonWeakness cw = new ClassDTO.CommonWeakness();
        cw.setType("PRONUNCIATION"); cw.setPct(60); cws.add(cw);
        d.setCommonWeaknesses(cws);
        return d;
    }
}
