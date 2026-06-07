package com.yunwu.skill.service;

import cn.hutool.json.JSONUtil;
import com.yunwu.common.exception.BusinessException;
import com.yunwu.common.exception.ErrorCode;
import com.yunwu.skill.dto.SkillDTO;
import com.yunwu.skill.entity.*;
import com.yunwu.skill.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private static final Logger log = LoggerFactory.getLogger(SkillService.class);
    private final ContentAssetMapper assetMapper;
    private final ReadingPassageMapper readingMapper;
    private final WritingPromptMapper writingMapper;

    public SkillService(ContentAssetMapper assetMapper, ReadingPassageMapper readingMapper,
                        WritingPromptMapper writingMapper) {
        this.assetMapper = assetMapper; this.readingMapper = readingMapper; this.writingMapper = writingMapper;
    }

    // ==================== 听力 ====================

    public List<SkillDTO.ListeningItem> listListening(String gradeLevel, Integer difficulty, int page, int size) {
        int offset = (page - 1) * size;
        return assetMapper.selectPublished("LISTENING", gradeLevel, difficulty, size, offset)
                .stream().map(a -> { SkillDTO.ListeningItem i = new SkillDTO.ListeningItem();
                    i.setId(a.getId()); i.setTitle(a.getTitle()); i.setTitleEn(a.getTitleEn());
                    i.setFileUrl(a.getFileUrl()); i.setDurationSeconds(a.getDurationSeconds());
                    i.setGradeLevel(a.getGradeLevel()); i.setDifficulty(a.getDifficulty()); i.setCefrLevel(a.getCefrLevel());
                    return i; }).collect(Collectors.toList());
    }

    public SkillDTO.ListeningDetail getListening(Long id) {
        ContentAsset a = assetMapper.selectById(id);
        if (a == null || !a.getIsPublished()) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        SkillDTO.ListeningDetail d = new SkillDTO.ListeningDetail();
        d.setId(a.getId()); d.setTitle(a.getTitle()); d.setFileUrl(a.getFileUrl());
        d.setDurationSeconds(a.getDurationSeconds()); d.setTranscript(a.getTranscript());
        d.setDifficulty(a.getDifficulty()); d.setCefrLevel(a.getCefrLevel());
        try { @SuppressWarnings("unchecked") List<Map<String, Object>> qs = (List<Map<String, Object>>)(List<?>)JSONUtil.toList(a.getContentJson(), Map.class); d.setQuestions(qs); } catch (Exception ignored) {}
        return d;
    }

    public SkillDTO.AnswerResult submitListening(Long id, SkillDTO.AnswerSubmit submit) {
        ContentAsset a = assetMapper.selectById(id);
        if (a == null) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        @SuppressWarnings("unchecked") List<Map<String, Object>> questions = (List<Map<String, Object>>)(List<?>)JSONUtil.toList(a.getContentJson(), Map.class);
        int correct = 0;
        List<SkillDTO.QuestionResult> results = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Map<String, Object> q = questions.get(i);
            String correctAnswer = String.valueOf(q.getOrDefault("answer", ""));
            String userAnswer = i < submit.getAnswers().size()
                    ? submit.getAnswers().get(i).getOrDefault("answer", "") : "";
            boolean ok = correctAnswer.equalsIgnoreCase(userAnswer.trim());
            if (ok) correct++;
            SkillDTO.QuestionResult r = new SkillDTO.QuestionResult();
            r.setIndex(i); r.setCorrect(ok);
            r.setCorrectAnswer(correctAnswer);
            r.setExplanation(String.valueOf(q.getOrDefault("explanation", "")));
            results.add(r);
        }
        SkillDTO.AnswerResult ar = new SkillDTO.AnswerResult();
        ar.setTotalQuestions(questions.size()); ar.setCorrectCount(correct);
        ar.setScore(questions.isEmpty() ? 0 : correct * 100 / questions.size());
        ar.setResults(results);
        log.info("[Skill] 听力提交 id={}, score={}", id, ar.getScore());
        return ar;
    }

    // ==================== 阅读 ====================

    public List<SkillDTO.ReadingItem> listReading(String gradeLevel, Integer difficulty, int page, int size) {
        int offset = (page - 1) * size;
        return readingMapper.selectPublished(gradeLevel, difficulty, size, offset).stream()
                .map(r -> { SkillDTO.ReadingItem i = new SkillDTO.ReadingItem();
                    i.setId(r.getId()); i.setTitle(r.getTitle()); i.setWordCount(r.getWordCount());
                    i.setDifficulty(r.getDifficulty()); i.setCefrLevel(r.getCefrLevel()); return i;
                }).collect(Collectors.toList());
    }

    public SkillDTO.ReadingDetail getReading(Long id) {
        ReadingPassage r = readingMapper.selectById(id);
        if (r == null || !r.getIsPublished()) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        SkillDTO.ReadingDetail d = new SkillDTO.ReadingDetail();
        d.setId(r.getId()); d.setTitle(r.getTitle()); d.setContent(r.getContent());
        d.setWordCount(r.getWordCount()); d.setDifficulty(r.getDifficulty()); d.setCefrLevel(r.getCefrLevel());
        try { @SuppressWarnings("unchecked") var qs = (List<Map<String, Object>>)(List<?>)JSONUtil.toList(r.getQuestions(), Map.class); d.setQuestions(qs); } catch (Exception ignored) {}
        return d;
    }

    public SkillDTO.AnswerResult submitReading(Long id, SkillDTO.AnswerSubmit submit) {
        ReadingPassage r = readingMapper.selectById(id);
        if (r == null) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        @SuppressWarnings("unchecked") List<Map<String, Object>> questions2 = (List<Map<String, Object>>)(List<?>)JSONUtil.toList(r.getQuestions(), Map.class);
        int correct = 0;
        List<SkillDTO.QuestionResult> results = new ArrayList<>();
        for (int i = 0; i < questions2.size(); i++) {
            Map<String, Object> q = questions2.get(i);
            String ca = String.valueOf(q.getOrDefault("answer", ""));
            String ua = i < submit.getAnswers().size() ? submit.getAnswers().get(i).getOrDefault("answer", "") : "";
            boolean ok = ca.equalsIgnoreCase(ua.trim());
            if (ok) correct++;
            SkillDTO.QuestionResult qr = new SkillDTO.QuestionResult();
            qr.setIndex(i); qr.setCorrect(ok); qr.setCorrectAnswer(ca);
            qr.setExplanation(String.valueOf(q.getOrDefault("explanation", "")));
            results.add(qr);
        }
        SkillDTO.AnswerResult ar = new SkillDTO.AnswerResult();
        ar.setTotalQuestions(questions2.size()); ar.setCorrectCount(correct);
        ar.setScore(questions2.isEmpty() ? 0 : correct * 100 / questions2.size());
        ar.setResults(results);
        return ar;
    }

    // ==================== 写作 ====================

    public List<SkillDTO.WritingItem> listWriting(String gradeLevel, Integer difficulty, int page, int size) {
        int offset = (page - 1) * size;
        return writingMapper.selectPublished(gradeLevel, difficulty, size, offset).stream()
                .map(w -> { SkillDTO.WritingItem i = new SkillDTO.WritingItem();
                    i.setId(w.getId()); i.setTitle(w.getTitle()); i.setDifficulty(w.getDifficulty());
                    i.setCefrLevel(w.getCefrLevel()); return i;
                }).collect(Collectors.toList());
    }

    public SkillDTO.WritingDetail getWriting(Long id) {
        WritingPrompt w = writingMapper.selectById(id);
        if (w == null || !w.getIsPublished()) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        SkillDTO.WritingDetail d = new SkillDTO.WritingDetail();
        d.setId(w.getId()); d.setTitle(w.getTitle()); d.setPrompt(w.getPrompt()); d.setPromptEn(w.getPromptEn());
        d.setWordLimitMin(w.getWordLimitMin()); d.setWordLimitMax(w.getWordLimitMax());
        d.setTimeLimitMinutes(w.getTimeLimitMinutes()); d.setDifficulty(w.getDifficulty()); d.setCefrLevel(w.getCefrLevel());
        return d;
    }

    public SkillDTO.WritingResult submitWriting(Long id, SkillDTO.WritingSubmit submit) {
        WritingPrompt w = writingMapper.selectById(id);
        if (w == null) throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);

        // AI 批改 — 占位 (后续对接 Python AI 智能体)
        SkillDTO.WritingResult result = new SkillDTO.WritingResult();
        result.setScore(78);
        result.setDimensionScores(Map.of("grammar", BigDecimal.valueOf(80), "structure", BigDecimal.valueOf(75),
                "content", BigDecimal.valueOf(82), "vocabulary", BigDecimal.valueOf(77)));
        result.setFeedbackSummary("文章结构清晰，词汇使用恰当。注意时态一致性。");

        log.info("[Skill] 写作提交 id={}, wordCount={}", id, submit.getContent().split("\\s+").length);
        return result;
    }
}
