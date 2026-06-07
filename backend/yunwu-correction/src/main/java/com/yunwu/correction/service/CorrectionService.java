package com.yunwu.correction.service;

import cn.hutool.core.bean.BeanUtil;
import com.yunwu.common.context.UserContext;
import com.yunwu.correction.dto.CorrectionDTO;
import com.yunwu.correction.entity.Correction;
import com.yunwu.correction.entity.ErrorRecord;
import com.yunwu.correction.mapper.CorrectionMapper;
import com.yunwu.correction.mapper.ErrorRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CorrectionService {

    private static final Logger log = LoggerFactory.getLogger(CorrectionService.class);
    private final CorrectionMapper correctionMapper;
    private final ErrorRecordMapper errorRecordMapper;

    // 艾宾浩斯间隔 (天): 1, 2, 4, 7, 15, 30
    private static final int[] EBBINGHAUS_INTERVALS = {1, 2, 4, 7, 15, 30};

    public CorrectionService(CorrectionMapper correctionMapper,
                             ErrorRecordMapper errorRecordMapper) {
        this.correctionMapper = correctionMapper;
        this.errorRecordMapper = errorRecordMapper;
    }

    // ==================== 会话纠错 ====================

    public CorrectionDTO.SessionCorrections getSessionCorrections(Long sessionId) {
        List<Correction> list = correctionMapper.selectBySessionId(sessionId);

        CorrectionDTO.SessionCorrections result = new CorrectionDTO.SessionCorrections();
        result.setItems(list.stream().map(this::toItem).collect(Collectors.toList()));

        // 汇总
        CorrectionDTO.Summary summary = new CorrectionDTO.Summary();
        summary.setTotal(list.size());
        summary.setByType(list.stream()
                .collect(Collectors.groupingBy(Correction::getErrorType, Collectors.counting()))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue())));
        summary.setBySeverity(list.stream()
                .collect(Collectors.groupingBy(c -> c.getSeverity() != null ? c.getSeverity() : "MEDIUM",
                        Collectors.counting()))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue())));
        result.setSummary(summary);

        return result;
    }

    // ==================== 用户纠错历史 ====================

    public List<CorrectionDTO.Item> getUserHistory(String errorType, int page, int size) {
        Long userId = UserContext.getUserId();
        int offset = (page - 1) * size;
        return correctionMapper.selectByUserId(userId, errorType, size, offset)
                .stream().map(this::toItem).collect(Collectors.toList());
    }

    // ==================== 标记已查看 ====================

    public void markReviewed(Long id) {
        correctionMapper.markReviewed(id);
    }

    public void markSessionReviewed(Long sessionId) {
        correctionMapper.markReviewedBySession(sessionId);
    }

    // ==================== 错误分析 (核心) ====================

    public CorrectionDTO.ErrorAnalysis getErrorAnalysis() {
        Long userId = UserContext.getUserId();
        LocalDateTime since = LocalDateTime.now().minusDays(30);

        CorrectionDTO.ErrorAnalysis analysis = new CorrectionDTO.ErrorAnalysis();

        // 1. 五维雷达
        Map<String, Double> radar = buildRadar(userId);
        analysis.setRadar(radar);

        // 2. Top 错误
        analysis.setTopErrors(buildTopErrors(userId, since));

        // 3. 每日趋势
        analysis.setDailyTrend(buildDailyTrend(userId, since));

        // 4. 薄弱点
        analysis.setWeakPoints(buildWeakPoints(userId));

        // 5. 待复习
        analysis.setReviewDue(buildReviewDue(userId));

        return analysis;
    }

    // ==================== 艾宾浩斯复习 ====================

    public List<CorrectionDTO.ReviewItem> getReviewDue(int limit) {
        return buildReviewDue(UserContext.getUserId()).subList(0,
                Math.min(limit, 20));
    }

    public void completeReview(Long errorRecordId) {
        ErrorRecord er = errorRecordMapper.selectById(errorRecordId);
        if (er == null) return;

        int reviewCount = (er.getReviewCount() != null ? er.getReviewCount() : 0) + 1;
        int intervalIdx = Math.min(reviewCount, EBBINGHAUS_INTERVALS.length - 1);
        LocalDateTime next = LocalDateTime.now().plusDays(EBBINGHAUS_INTERVALS[intervalIdx]);

        // 复习 5 次以上 → 已掌握
        String status = reviewCount >= 5 ? "MASTERED" : "REVIEWING";
        errorRecordMapper.updateMasteryStatus(errorRecordId, status);
        errorRecordMapper.scheduleNextReview(errorRecordId, next);
        log.info("[Correction] 复习完成 id={}, status={}, nextReview={}", errorRecordId, status, next);
    }

    // ==================== 私有构建方法 ====================

    private Map<String, Double> buildRadar(Long userId) {
        long total = correctionMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Correction>()
                        .eq(Correction::getUserId, userId));
        if (total == 0) return Map.of("grammar", 0.0, "pronunciation", 0.0,
                "vocabulary", 0.0, "fluency", 0.0, "logic", 0.0);

        var stats = correctionMapper.countByType(userId, LocalDateTime.now().minusDays(30));
        Map<String, Double> radar = new LinkedHashMap<>();
        radar.put("grammar", 0.0); radar.put("pronunciation", 0.0);
        radar.put("vocabulary", 0.0); radar.put("fluency", 0.0); radar.put("logic", 0.0);

        for (var row : stats) {
            String type = String.valueOf(row.get("error_type")).toLowerCase();
            double pct = ((Number) row.get("cnt")).doubleValue() / total;
            radar.put(type, Math.min(pct, 1.0));
        }
        return radar;
    }

    private List<CorrectionDTO.TypeStat> buildTopErrors(Long userId, LocalDateTime since) {
        return correctionMapper.countByType(userId, since).stream()
                .map(row -> {
                    CorrectionDTO.TypeStat ts = new CorrectionDTO.TypeStat();
                    ts.setErrorType(String.valueOf(row.get("error_type")));
                    ts.setCount(((Number) row.get("cnt")).intValue());
                    return ts;
                }).collect(Collectors.toList());
    }

    private List<CorrectionDTO.TrendPoint> buildDailyTrend(Long userId, LocalDateTime since) {
        return correctionMapper.dailyTrend(userId, since).stream()
                .map(row -> {
                    CorrectionDTO.TrendPoint tp = new CorrectionDTO.TrendPoint();
                    tp.setDate(String.valueOf(row.get("date")));
                    tp.setCount(((Number) row.get("cnt")).intValue());
                    return tp;
                }).collect(Collectors.toList());
    }

    private List<CorrectionDTO.WeakPoint> buildWeakPoints(Long userId) {
        return errorRecordMapper.selectWeaknesses(userId).stream()
                .map(er -> {
                    CorrectionDTO.WeakPoint wp = new CorrectionDTO.WeakPoint();
                    BeanUtil.copyProperties(er, wp);
                    wp.setTotalCount(er.getTotalCount() != null ? er.getTotalCount() : 0);
                    return wp;
                }).collect(Collectors.toList());
    }

    private List<CorrectionDTO.ReviewItem> buildReviewDue(Long userId) {
        return errorRecordMapper.selectDueForReview(userId, 20).stream()
                .map(er -> {
                    CorrectionDTO.ReviewItem ri = new CorrectionDTO.ReviewItem();
                    BeanUtil.copyProperties(er, ri);
                    ri.setReviewCount(er.getReviewCount() != null ? er.getReviewCount() : 0);
                    return ri;
                }).collect(Collectors.toList());
    }

    private CorrectionDTO.Item toItem(Correction c) {
        CorrectionDTO.Item item = new CorrectionDTO.Item();
        BeanUtil.copyProperties(c, item);
        return item;
    }
}
