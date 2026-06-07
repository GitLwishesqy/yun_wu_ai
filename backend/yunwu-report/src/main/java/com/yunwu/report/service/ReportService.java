package com.yunwu.report.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunwu.common.context.UserContext;
import com.yunwu.common.exception.BusinessException;
import com.yunwu.common.exception.ErrorCode;
import com.yunwu.report.dto.ReportDTO;
import com.yunwu.report.entity.LearningReport;
import com.yunwu.report.mapper.LearningReportMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);
    private final LearningReportMapper mapper;

    // 周期起止日: 周/月/学期
    private static final Map<String, Period> PERIODS = Map.of(
            "WEEKLY", new Period(1, 7),
            "MONTHLY", new Period(1, 30),
            "SEMESTER", new Period(1, 180)
    );

    public ReportService(LearningReportMapper mapper) { this.mapper = mapper; }

    // ==================== 列表 ====================

    public IPage<ReportDTO.ListItem> list(String periodType, int page, int size) {
        Long userId = UserContext.getUserId();
        long total = mapper.countByUserId(userId);
        int offset = (page - 1) * size;
        List<LearningReport> list = mapper.selectByUserId(userId, periodType, size, offset);
        IPage<ReportDTO.ListItem> result = new Page<>(page, size, total);
        result.setRecords(list.stream().map(r -> {
            ReportDTO.ListItem item = new ReportDTO.ListItem();
            BeanUtil.copyProperties(r, item);
            return item;
        }).collect(Collectors.toList()));
        return result;
    }

    // ==================== 最新报告 ====================

    public ReportDTO.Detail getLatest(String periodType) {
        Long userId = UserContext.getUserId();
        LearningReport r = mapper.selectLatest(userId, periodType);
        if (r == null) {
            // 自动生成一份新报告
            r = generateReport(userId, periodType != null ? periodType : "WEEKLY");
        }
        return toDetail(r);
    }

    // ==================== 报告详情 ====================

    public ReportDTO.Detail getDetail(Long id) {
        LearningReport r = mapper.selectById(id);
        if (r == null) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        return toDetail(r);
    }

    // ==================== 标记已读 ====================

    public void markRead(Long id) { mapper.markRead(id); }

    // ==================== PDF 导出 ====================

    public String exportPdfBase64(Long id) {
        // 占位 — 实际实现需要引入 iText/POI 等 PDF 库
        // 此处返回报告 JSON 的 base64 作为示意
        LearningReport r = mapper.selectById(id);
        if (r == null) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        return Base64.getEncoder().encodeToString(r.getReportData().getBytes());
    }

    // ==================== 报告生成 (占位 — 后续接入定时任务) ====================

    private LearningReport generateReport(Long userId, String periodType) {
        Period p = PERIODS.getOrDefault(periodType, new Period(1, 7));
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusDays(p.offset);
        LocalDate end = now;

        // 检查是否已存在
        if (mapper.exists(userId, periodType, start) > 0) {
            return mapper.selectLatest(userId, periodType);
        }

        // 构建报告数据 (模拟 — 后续接入真实统计数据)
        ReportDTO.ReportData data = new ReportDTO.ReportData();
        data.setSummary("本周你完成了学习任务，口语能力稳步提升！");
        ReportDTO.StatsInfo stats = new ReportDTO.StatsInfo();
        stats.setTotalSessions(12); stats.setTotalMinutes(180); stats.setTotalMessages(245);
        stats.setTotalCorrections(28); stats.setAvgScore(82.5); stats.setScoreChange(3.2);
        stats.setNewVocabulary(34);
        data.setStats(stats);

        Map<String, ReportDTO.DimensionChange> dimTrend = new LinkedHashMap<>();
        dimTrend.put("grammar", dc(85.0, 2.0));
        dimTrend.put("pronunciation", dc(72.0, 5.0));
        dimTrend.put("vocabulary", dc(80.0, 4.0));
        dimTrend.put("fluency", dc(76.0, 1.0));
        dimTrend.put("logic", dc(82.0, 3.0));
        data.setDimensionTrend(dimTrend);

        data.setTopErrors(List.of(er("TH_SOUND", 8, "DOWN"), er("PAST_TENSE", 6, "STABLE")));
        data.setNextPeriodSuggestions(List.of(
                "重点练习 'th' 发音，每天5分钟发音训练",
                "尝试挑战难度 2 的场景",
                "这周学会的34个新单词记得复习哦"));

        LearningReport r = new LearningReport();
        r.setUserId(userId);
        r.setPeriodType(periodType != null ? periodType : "WEEKLY");
        r.setPeriodStart(start);
        r.setPeriodEnd(end);
        r.setReportData(JSONUtil.toJsonStr(data));
        r.setIsRead(false);
        r.setGeneratedAt(java.time.LocalDateTime.now());
        mapper.insert(r);
        log.info("[Report] 生成报告 userId={}, period={}, start={}", userId, periodType, start);
        return r;
    }

    // ==================== 辅助 ====================

    private ReportDTO.Detail toDetail(LearningReport r) {
        ReportDTO.Detail d = new ReportDTO.Detail();
        BeanUtil.copyProperties(r, d);
        try {
            d.setReportData(JSONUtil.toBean(r.getReportData(), ReportDTO.ReportData.class));
        } catch (Exception ignored) {}
        return d;
    }

    private static ReportDTO.DimensionChange dc(double current, double change) {
        ReportDTO.DimensionChange d = new ReportDTO.DimensionChange();
        d.setCurrent(current); d.setChange(change); return d;
    }

    private static ReportDTO.ErrorItem er(String type, int count, String trend) {
        ReportDTO.ErrorItem e = new ReportDTO.ErrorItem();
        e.setType(type); e.setCount(count); e.setTrend(trend); return e;
    }

    private record Period(int offset, int length) {}
}
