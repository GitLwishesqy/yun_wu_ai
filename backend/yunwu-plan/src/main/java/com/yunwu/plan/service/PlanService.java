package com.yunwu.plan.service;

import com.yunwu.common.context.UserContext;
import com.yunwu.plan.dto.PlanDTO;
import com.yunwu.plan.entity.*;
import com.yunwu.plan.mapper.*;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate; import java.util.*; import java.util.stream.Collectors;

@Service
public class PlanService {
    private static final Logger log = LoggerFactory.getLogger(PlanService.class);
    private final LearningPlanMapper planMapper; private final PlanItemMapper itemMapper;

    public PlanService(LearningPlanMapper planMapper, PlanItemMapper itemMapper) {
        this.planMapper = planMapper; this.itemMapper = itemMapper;
    }

    public List<LearningPlan> list(Boolean isActive) {
        return planMapper.selectByUser(UserContext.getUserId(), isActive);
    }

    public PlanDTO.ActivePlan getActive() {
        LearningPlan p = planMapper.selectActive(UserContext.getUserId());
        if (p == null) return null;
        return toActivePlan(p);
    }

    @Transactional
    public PlanDTO.ActivePlan generate(PlanDTO.GenerateReq req) {
        LearningPlan p = new LearningPlan();
        p.setUserId(UserContext.getUserId()); p.setName(req.getName()); p.setPlanType("AI_GENERATED");
        p.setStartDate(req.getStartDate()); p.setEndDate(req.getEndDate()); p.setTargetLevel(req.getTargetLevel());
        p.setTotalItems(30); p.setCompletedItems(0); p.setIsActive(true);
        p.setCreatedAt(java.time.LocalDateTime.now()); p.setUpdatedAt(java.time.LocalDateTime.now());
        planMapper.insert(p);

        // 生成示例计划项
        for (int i = 0; i < 5; i++) {
            PlanItem item = new PlanItem();
            item.setPlanId(p.getId()); item.setUserId(UserContext.getUserId());
            item.setItemType(i == 0 ? "COACH_SESSION" : i == 1 ? "VOCAB_REVIEW" : "LISTENING");
            item.setItemName("Day " + (i+1) + " 任务");
            item.setScheduledDate(req.getStartDate().plusDays(i));
            item.setEstimatedMinutes(15); item.setIsCompleted(false); item.setPointsReward(10);
            item.setSortOrder(i); item.setCreatedAt(java.time.LocalDateTime.now()); item.setUpdatedAt(java.time.LocalDateTime.now());
            itemMapper.insert(item);
        }
        p.setTotalItems(5); planMapper.updateById(p);

        log.info("[Plan] AI生成计划 id={}, name={}", p.getId(), p.getName());
        return toActivePlan(p);
    }

    private PlanDTO.ActivePlan toActivePlan(LearningPlan p) {
        PlanDTO.ActivePlan ap = new PlanDTO.ActivePlan();
        ap.setId(p.getId()); ap.setName(p.getName()); ap.setPlanType(p.getPlanType());
        ap.setStartDate(p.getStartDate()); ap.setEndDate(p.getEndDate()); ap.setTargetLevel(p.getTargetLevel());
        PlanDTO.Progress pg = new PlanDTO.Progress();
        pg.setTotalItems(p.getTotalItems() != null ? p.getTotalItems() : 0);
        pg.setCompletedItems(p.getCompletedItems() != null ? p.getCompletedItems() : 0);
        pg.setCompletionPct(pg.getTotalItems() > 0 ? pg.getCompletedItems() * 100.0 / pg.getTotalItems() : 0);
        ap.setProgress(pg);

        List<PlanItem> items = itemMapper.selectByPlanAndDate(p.getId(), LocalDate.now());
        ap.setTodayItems(items.stream().map(i -> {
            PlanDTO.TodayItem ti = new PlanDTO.TodayItem();
            ti.setId(i.getId()); ti.setItemType(i.getItemType()); ti.setItemName(i.getItemName());
            ti.setSceneId(i.getItemRefId()); ti.setEstimatedMinutes(i.getEstimatedMinutes() != null ? i.getEstimatedMinutes() : 15);
            ti.setPointsReward(i.getPointsReward() != null ? i.getPointsReward() : 0);
            ti.setIsCompleted(i.getIsCompleted() != null && i.getIsCompleted());
            return ti;
        }).collect(Collectors.toList()));
        return ap;
    }
}
