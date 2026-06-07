package com.yunwu.vocabulary.service;

import cn.hutool.json.JSONUtil;
import com.yunwu.common.context.UserContext;
import com.yunwu.vocabulary.dto.VocabularyDTO;
import com.yunwu.vocabulary.entity.UserVocabulary;
import com.yunwu.vocabulary.entity.VocabularyLibrary;
import com.yunwu.vocabulary.mapper.UserVocabularyMapper;
import com.yunwu.vocabulary.mapper.VocabularyLibraryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VocabularyService {

    private static final Logger log = LoggerFactory.getLogger(VocabularyService.class);
    private final UserVocabularyMapper uvMapper;
    private final VocabularyLibraryMapper libMapper;

    // 艾宾浩斯间隔: 1, 2, 4, 7, 15, 30 天
    private static final int[] INTERVALS = {1, 2, 4, 7, 15, 30};

    public VocabularyService(UserVocabularyMapper uvMapper, VocabularyLibraryMapper libMapper) {
        this.uvMapper = uvMapper; this.libMapper = libMapper;
    }

    // ==================== 我的词汇本 ====================

    public VocabularyDTO.MyVocabList getMyVocab(String status, String q, int page, int size) {
        Long userId = UserContext.getUserId();
        long total;
        List<UserVocabulary> list;
        int offset = (page - 1) * size;

        if (q != null && !q.isEmpty()) {
            list = uvMapper.search(userId, q, size, offset);
            total = list.size();
        } else if (status != null && !status.isEmpty()) {
            list = uvMapper.selectByStatus(userId, status, size, offset);
            total = uvMapper.countByUserId(userId);
        } else {
            list = uvMapper.selectByStatus(userId, null, size, offset);
            total = uvMapper.countByUserId(userId);
        }

        VocabularyDTO.MyVocabList result = new VocabularyDTO.MyVocabList();
        result.setItems(list.stream().map(this::toMyWord).collect(Collectors.toList()));

        // 统计
        var stats = uvMapper.statsByStatus(userId);
        Map<String, Integer> statsMap = new LinkedHashMap<>();
        for (String s : List.of("NEW", "LEARNING", "REVIEWING", "KNOWN", "MASTERED")) statsMap.put(s, 0);
        for (var row : stats) statsMap.put(String.valueOf(row.get("status")), ((Number) row.get("cnt")).intValue());
        result.setStats(statsMap);

        VocabularyDTO.Pagination pag = new VocabularyDTO.Pagination();
        pag.setPage(page); pag.setSize(size); pag.setTotal(total);
        pag.setTotalPages((int) Math.ceil((double) total / size));
        result.setPagination(pag);

        return result;
    }

    // ==================== 更新词汇状态 ====================

    public void updateStatus(Long id, String status) {
        uvMapper.updateStatus(id, status);
        log.info("[Vocab] 更新词汇状态 id={}, status={}", id, status);
    }

    // ==================== 待复习 ====================

    public List<VocabularyDTO.MyWord> getReviewDue(int limit) {
        return uvMapper.selectDueForReview(UserContext.getUserId(), limit)
                .stream().map(this::toMyWord).collect(Collectors.toList());
    }

    // ==================== 完成复习 ====================

    public void completeReview(Long id) {
        UserVocabulary uv = uvMapper.selectById(id);
        if (uv == null) return;
        int reviewCount = (uv.getSeenCount() != null ? uv.getSeenCount() : 0) + 1;
        int idx = Math.min(reviewCount, INTERVALS.length - 1);
        LocalDateTime next = LocalDateTime.now().plusDays(INTERVALS[idx]);
        String status = reviewCount >= 5 ? "MASTERED" : "REVIEWING";
        uvMapper.updateStatus(id, status);
        uvMapper.scheduleNextReview(id, next);
        log.info("[Vocab] 复习完成 id={}, status={}, next={}", id, status, next);
    }

    // ==================== 公共词汇库 ====================

    public List<VocabularyDTO.LibraryWord> searchLibrary(String q, String cefrLevel, int page, int size) {
        int offset = (page - 1) * size;
        return libMapper.search(q, cefrLevel, size, offset).stream()
                .map(this::toLibraryWord).collect(Collectors.toList());
    }

    // ==================== 辅助 ====================

    private VocabularyDTO.MyWord toMyWord(UserVocabulary uv) {
        VocabularyDTO.MyWord w = new VocabularyDTO.MyWord();
        w.setId(uv.getId()); w.setWord(uv.getWord()); w.setTranslation(uv.getTranslation());
        w.setStatus(uv.getStatus()); w.setSeenCount(uv.getSeenCount());
        w.setUsedCount(uv.getUsedCount()); w.setErrorCount(uv.getErrorCount());
        w.setCorrectCount(uv.getCorrectCount()); w.setLastSeenAt(uv.getLastSeenAt());
        w.setNextReviewAt(uv.getNextReviewAt());
        return w;
    }

    @SuppressWarnings("unchecked")
    private VocabularyDTO.LibraryWord toLibraryWord(VocabularyLibrary lib) {
        VocabularyDTO.LibraryWord w = new VocabularyDTO.LibraryWord();
        w.setId(lib.getId()); w.setWord(lib.getWord()); w.setPronunciation(lib.getPronunciation());
        w.setPronunciationAudioUrl(lib.getPronunciationAudioUrl()); w.setTranslation(lib.getTranslation());
        w.setPartOfSpeech(lib.getPartOfSpeech()); w.setDefinitionEn(lib.getDefinitionEn());
        w.setCefrLevel(lib.getCefrLevel()); w.setDifficulty(lib.getDifficulty());
        try {
            @SuppressWarnings("unchecked")
            List<Map<String,String>> es = (List<Map<String,String>>)(List<?>)JSONUtil.toList(lib.getExampleSentences(), Map.class);
            @SuppressWarnings("unchecked")
            List<Map<String,String>> cc = (List<Map<String,String>>)(List<?>)JSONUtil.toList(lib.getCommonCollocations(), Map.class);
            w.setExampleSentences(es);
            w.setCommonCollocations(cc);
            w.setExamTags(JSONUtil.toList(lib.getExamTags(), String.class));
        } catch (Exception ignored) {}
        return w;
    }
}
