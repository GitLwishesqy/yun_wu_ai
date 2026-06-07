package com.yunwu.scene.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunwu.common.context.UserContext;
import com.yunwu.common.exception.BusinessException;
import com.yunwu.common.exception.ErrorCode;
import com.yunwu.scene.dto.SceneDTO;
import com.yunwu.scene.entity.SceneTemplate;
import com.yunwu.scene.mapper.SceneTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SceneService {

    private static final Logger log = LoggerFactory.getLogger(SceneService.class);
    private final SceneTemplateMapper mapper;

    public SceneService(SceneTemplateMapper mapper) { this.mapper = mapper; }

    // ==================== 学生端 ====================

    public IPage<SceneDTO.ListItem> listPublished(int page, int size,
                                                   String gradeLevel, String category,
                                                   Integer difficulty, String keyword,
                                                   String cefrLevel) {
        LambdaQueryWrapper<SceneTemplate> q = new LambdaQueryWrapper<>();
        q.eq(SceneTemplate::getIsPublished, true)
         .isNull(SceneTemplate::getDeletedAt);

        if (gradeLevel != null) q.eq(SceneTemplate::getGradeLevel, gradeLevel);
        if (category != null) q.eq(SceneTemplate::getCategory, category);
        if (difficulty != null) q.eq(SceneTemplate::getDifficulty, difficulty);
        if (cefrLevel != null) q.eq(SceneTemplate::getCefrLevel, cefrLevel);
        if (keyword != null && !keyword.isEmpty())
            q.and(w -> w.like(SceneTemplate::getName, keyword)
                        .or().like(SceneTemplate::getNameEn, keyword));

        q.orderByAsc(SceneTemplate::getDifficulty);

        // Simplified: skip pagination for now
        List<SceneTemplate> records = mapper.selectList(q);
        IPage<SceneTemplate> result = new Page<>(page, size, records.size());
        result.setRecords(records);
        return result.convert(this::toListItem);
    }

    public SceneDTO.Response getPublished(Long id) {
        SceneTemplate s = mapper.selectById(id);
        if (s == null || !s.getIsPublished() || s.isDeleted())
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "场景不存在");
        return toResponse(s);
    }

    public List<String> getCategories() {
        return mapper.selectAllCategories();
    }

    // ==================== 管理端 CRUD ====================

    public IPage<SceneDTO.ListItem> adminList(int page, int size, String category,
                                               Boolean published, String keyword) {
        LambdaQueryWrapper<SceneTemplate> q = new LambdaQueryWrapper<>();
        q.isNull(SceneTemplate::getDeletedAt);
        if (category != null) q.eq(SceneTemplate::getCategory, category);
        if (published != null) q.eq(SceneTemplate::getIsPublished, published);
        if (keyword != null && !keyword.isEmpty())
            q.and(w -> w.like(SceneTemplate::getName, keyword)
                        .or().like(SceneTemplate::getDescription, keyword));
        q.orderByDesc(SceneTemplate::getCreatedAt);

        // Simplified: skip pagination for now
        List<SceneTemplate> records = mapper.selectList(q);
        IPage<SceneTemplate> result = new Page<>(page, size, records.size());
        result.setRecords(records);
        return result.convert(this::toListItem);
    }

    @Transactional
    public SceneDTO.Response create(SceneDTO.CreateRequest req) {
        SceneTemplate s = new SceneTemplate();
        BeanUtil.copyProperties(req, s);
        s.setRoles(JSONUtil.toJsonStr(req.getRoles()));
        s.setKeywords(JSONUtil.toJsonStr(req.getKeywords()));
        s.setTargetSentences(JSONUtil.toJsonStr(req.getTargetSentences()));
        s.setOpeningDialogue(JSONUtil.toJsonStr(req.getOpeningDialogue()));
        s.setTags(JSONUtil.toJsonStr(req.getTags()));
        s.setIsPublished(false);
        s.setVersion(1);
        s.setCreatedBy(UserContext.getUserId());
        s.setCreatedAt(LocalDateTime.now());
        s.setUpdatedAt(LocalDateTime.now());
        mapper.insert(s);

        log.info("[Scene] 创建场景 id={}, name={}, category={}",
                s.getId(), s.getName(), s.getCategory());
        return toResponse(s);
    }

    @Transactional
    public SceneDTO.Response update(Long id, SceneDTO.UpdateRequest req) {
        SceneTemplate s = mapper.selectById(id);
        if (s == null || s.isDeleted()) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);

        if (req.getName() != null) s.setName(req.getName());
        if (req.getNameEn() != null) s.setNameEn(req.getNameEn());
        if (req.getDescription() != null) s.setDescription(req.getDescription());
        if (req.getCategory() != null) s.setCategory(req.getCategory());
        if (req.getGradeLevel() != null) s.setGradeLevel(req.getGradeLevel());
        if (req.getDifficulty() != null) s.setDifficulty(req.getDifficulty());
        if (req.getCefrLevel() != null) s.setCefrLevel(req.getCefrLevel());
        if (req.getRoles() != null) s.setRoles(JSONUtil.toJsonStr(req.getRoles()));
        if (req.getKeywords() != null) s.setKeywords(JSONUtil.toJsonStr(req.getKeywords()));
        if (req.getTargetSentences() != null) s.setTargetSentences(JSONUtil.toJsonStr(req.getTargetSentences()));
        if (req.getOpeningDialogue() != null) s.setOpeningDialogue(JSONUtil.toJsonStr(req.getOpeningDialogue()));
        if (req.getMaxRounds() != null) s.setMaxRounds(req.getMaxRounds());
        if (req.getEstimatedDuration() != null) s.setEstimatedDuration(req.getEstimatedDuration());
        if (req.getTags() != null) s.setTags(JSONUtil.toJsonStr(req.getTags()));

        s.setVersion(s.getVersion() + 1);
        s.setUpdatedAt(LocalDateTime.now());
        mapper.updateById(s);

        log.info("[Scene] 更新场景 id={}", id);
        return toResponse(s);
    }

    @Transactional
    public void delete(Long id) {
        SceneTemplate s = mapper.selectById(id);
        if (s == null) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        s.setDeletedAt(LocalDateTime.now());
        mapper.updateById(s);
        log.info("[Scene] 删除场景 id={}", id);
    }

    @Transactional
    public void publish(Long id) {
        SceneTemplate s = mapper.selectById(id);
        if (s == null) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        s.setIsPublished(true);
        s.setUpdatedAt(LocalDateTime.now());
        mapper.updateById(s);
        log.info("[Scene] 发布场景 id={}", id);
    }

    @Transactional
    public void unpublish(Long id) {
        SceneTemplate s = mapper.selectById(id);
        if (s == null) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        s.setIsPublished(false);
        s.setUpdatedAt(LocalDateTime.now());
        mapper.updateById(s);
    }

    // ==================== 辅助 ====================

    private SceneDTO.Response toResponse(SceneTemplate s) {
        SceneDTO.Response r = new SceneDTO.Response();
        // Manually copy compatible fields (skip JSON String fields to avoid type mismatch)
        r.setId(s.getId()); r.setName(s.getName()); r.setNameEn(s.getNameEn());
        r.setDescription(s.getDescription()); r.setCategory(s.getCategory());
        r.setGradeLevel(s.getGradeLevel()); r.setDifficulty(s.getDifficulty());
        r.setCefrLevel(s.getCefrLevel()); r.setMaxRounds(s.getMaxRounds());
        r.setEstimatedDuration(s.getEstimatedDuration()); r.setIsPublished(s.getIsPublished());
        r.setVersion(s.getVersion()); r.setCreatedBy(s.getCreatedBy());
        r.setCreatedAt(s.getCreatedAt()); r.setUpdatedAt(s.getUpdatedAt());
        // Parse JSON fields
        r.setRoles(safeJsonList(s.getRoles()));
        r.setKeywords(safeJsonList(s.getKeywords()));
        r.setTargetSentences(safeJsonList(s.getTargetSentences()));
        r.setOpeningDialogue(safeJsonMap(s.getOpeningDialogue()));
        r.setTags(safeJsonStrList(s.getTags()));
        return r;
    }
    @SuppressWarnings("unchecked")
    private List<Map<String, String>> safeJsonList(String json) {
        try { return (List<Map<String, String>>)(List<?>)JSONUtil.toList(json, Map.class); } catch (Exception e) { return new ArrayList<>(); }
    }
    @SuppressWarnings("unchecked")
    private Map<String, String> safeJsonMap(String json) {
        try { return (Map<String, String>)(Map<?,?>)JSONUtil.toBean(json, Map.class); } catch (Exception e) { return new HashMap<>(); }
    }
    private List<String> safeJsonStrList(String json) {
        try { return JSONUtil.toList(json, String.class); } catch (Exception e) { return new ArrayList<>(); }
    }

    private SceneDTO.ListItem toListItem(SceneTemplate s) {
        SceneDTO.ListItem r = new SceneDTO.ListItem();
        r.setId(s.getId()); r.setName(s.getName()); r.setNameEn(s.getNameEn());
        r.setCategory(s.getCategory()); r.setGradeLevel(s.getGradeLevel());
        r.setDifficulty(s.getDifficulty()); r.setCefrLevel(s.getCefrLevel());
        r.setEstimatedDuration(s.getEstimatedDuration()); r.setIsPublished(s.getIsPublished());
        r.setVersion(s.getVersion());
        r.setTags(safeJsonStrList(s.getTags()));
        return r;
    }
}
