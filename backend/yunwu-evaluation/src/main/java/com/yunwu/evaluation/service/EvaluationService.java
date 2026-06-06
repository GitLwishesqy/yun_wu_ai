package com.yunwu.evaluation.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunwu.common.context.UserContext;
import com.yunwu.common.exception.BusinessException;
import com.yunwu.common.exception.ErrorCode;
import com.yunwu.evaluation.dto.EvaluationDTO;
import com.yunwu.evaluation.entity.Evaluation;
import com.yunwu.evaluation.mapper.EvaluationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EvaluationService {

    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);
    private final EvaluationMapper mapper;

    public EvaluationService(EvaluationMapper mapper) { this.mapper = mapper; }

    // ==================== 列表 ====================

    public IPage<EvaluationDTO.ListItem> list(String evalType, int page, int size) {
        Long userId = UserContext.getUserId();
        int offset = (page - 1) * size;
        long total = mapper.countByUserId(userId);
        List<Evaluation> list = mapper.selectByUserId(userId, evalType, size, offset);
        IPage<EvaluationDTO.ListItem> result = new Page<>(page, size, total);
        result.setRecords(list.stream().map(e -> {
            EvaluationDTO.ListItem item = new EvaluationDTO.ListItem();
            item.setId(e.getId());
            item.setSessionId(e.getSessionId());
            item.setEvalType(e.getEvalType());
            item.setOverallScore(e.getOverallScore());
            item.setImprovement(e.getImprovement());
            item.setCreatedAt(e.getCreatedAt());
            return item;
        }).collect(Collectors.toList()));
        return result;
    }

    // ==================== 详情 ====================

    public EvaluationDTO.Detail getDetail(Long id) {
        Evaluation e = mapper.selectById(id);
        if (e == null) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        return toDetail(e);
    }

    // ==================== 概览 (仪表盘) ====================

    public EvaluationDTO.Overview getOverview() {
        Long userId = UserContext.getUserId();
        EvaluationDTO.Overview ov = new EvaluationDTO.Overview();

        ov.setAvgScore(mapper.selectAvgScore(userId));
        ov.setMaxScore(mapper.selectMaxScore(userId));
        ov.setThisMonthCount(mapper.countThisMonth(userId));
        ov.setStatsByType(mapper.statsByType(userId));

        // 最近 10 次趋势
        var recent = mapper.selectRecentScores(userId, 10);
        List<EvaluationDTO.ScoreTrend> trend = new ArrayList<>();
        BigDecimal prev = null;
        for (int i = recent.size() - 1; i >= 0; i--) {
            var row = recent.get(i);
            EvaluationDTO.ScoreTrend st = new EvaluationDTO.ScoreTrend();
            st.setOverallScore((BigDecimal) row.get("overall_score"));
            st.setCreatedAt((java.time.LocalDateTime) row.get("created_at"));
            trend.add(st);
        }
        ov.setRecentTrend(trend);
        ov.setTotalEvaluations(recent.size() < 10 ? recent.size() : (int) mapper.countByUserId(userId));

        if (!recent.isEmpty()) {
            ov.setLatestScore((BigDecimal) recent.get(0).get("overall_score"));
            if (recent.size() >= 2) {
                BigDecimal b = (BigDecimal) recent.get(1).get("overall_score");
                ov.setScoreChange(ov.getLatestScore().subtract(b));
            }
        }

        // 维度平均
        Map<String, BigDecimal> dimAvgs = new LinkedHashMap<>();
        recent.forEach(r -> {
            try {
                Map<String, BigDecimal> scores = JSONUtil.toBean(
                        r.get("dimension_scores") != null ? r.get("dimension_scores").toString() : "{}",
                        new cn.hutool.core.lang.TypeReference<Map<String, BigDecimal>>() {}, false);
                scores.forEach((k, v) -> dimAvgs.merge(k, v, BigDecimal::add));
            } catch (Exception ignored) {}
        });
        if (!recent.isEmpty() && !dimAvgs.isEmpty()) {
            int cnt = recent.size();
            dimAvgs.replaceAll((k, v) -> v.divide(BigDecimal.valueOf(cnt), 1, RoundingMode.HALF_UP));
        }
        ov.setDimensionAverages(dimAvgs);

        return ov;
    }

    // ==================== 私有 ====================

    private EvaluationDTO.Detail toDetail(Evaluation e) {
        EvaluationDTO.Detail d = new EvaluationDTO.Detail();
        BeanUtil.copyProperties(e, d);
        try {
            Map<String, BigDecimal> scores = JSONUtil.toBean(e.getDimensionScores(),
                    new cn.hutool.core.lang.TypeReference<Map<String, BigDecimal>>() {}, false);
            d.setDimensionScores(scores);
            d.setStrengths(JSONUtil.toList(e.getStrengths(), String.class));
            d.setWeaknesses(JSONUtil.toList(e.getWeaknesses(), String.class));
            @SuppressWarnings("unchecked")
            List<Map<String, String>> sug =
                    (List<Map<String, String>>) (List<?>) JSONUtil.toList(e.getSuggestions(), Map.class);
            d.setSuggestions(sug);
        } catch (Exception ignored) {}
        return d;
    }
}
