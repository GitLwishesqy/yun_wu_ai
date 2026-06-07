package com.yunwu.vocabulary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class VocabularyDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MyWord {
        private Long id; private String word; private String translation;
        private String status; private Integer seenCount; private Integer usedCount;
        private Integer errorCount; private Integer correctCount;
        private LocalDateTime lastSeenAt; private LocalDateTime nextReviewAt;

        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getWord() { return word; } public void setWord(String word) { this.word = word; }
        public String getTranslation() { return translation; } public void setTranslation(String translation) { this.translation = translation; }
        public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
        public Integer getSeenCount() { return seenCount; } public void setSeenCount(Integer seenCount) { this.seenCount = seenCount; }
        public Integer getUsedCount() { return usedCount; } public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
        public Integer getErrorCount() { return errorCount; } public void setErrorCount(Integer errorCount) { this.errorCount = errorCount; }
        public Integer getCorrectCount() { return correctCount; } public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }
        public LocalDateTime getLastSeenAt() { return lastSeenAt; } public void setLastSeenAt(LocalDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }
        public LocalDateTime getNextReviewAt() { return nextReviewAt; } public void setNextReviewAt(LocalDateTime nextReviewAt) { this.nextReviewAt = nextReviewAt; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LibraryWord {
        private Long id; private String word; private String pronunciation;
        private String pronunciationAudioUrl; private String translation;
        private String partOfSpeech; private String definitionEn;
        private String cefrLevel; private Integer difficulty;
        private List<Map<String,String>> exampleSentences;
        private List<Map<String,String>> commonCollocations;
        private List<String> examTags;

        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getWord() { return word; } public void setWord(String word) { this.word = word; }
        public String getPronunciation() { return pronunciation; } public void setPronunciation(String pronunciation) { this.pronunciation = pronunciation; }
        public String getPronunciationAudioUrl() { return pronunciationAudioUrl; } public void setPronunciationAudioUrl(String pronunciationAudioUrl) { this.pronunciationAudioUrl = pronunciationAudioUrl; }
        public String getTranslation() { return translation; } public void setTranslation(String translation) { this.translation = translation; }
        public String getPartOfSpeech() { return partOfSpeech; } public void setPartOfSpeech(String partOfSpeech) { this.partOfSpeech = partOfSpeech; }
        public String getDefinitionEn() { return definitionEn; } public void setDefinitionEn(String definitionEn) { this.definitionEn = definitionEn; }
        public String getCefrLevel() { return cefrLevel; } public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
        public Integer getDifficulty() { return difficulty; } public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
        public List<Map<String,String>> getExampleSentences() { return exampleSentences; } public void setExampleSentences(List<Map<String,String>> exampleSentences) { this.exampleSentences = exampleSentences; }
        public List<Map<String,String>> getCommonCollocations() { return commonCollocations; } public void setCommonCollocations(List<Map<String,String>> commonCollocations) { this.commonCollocations = commonCollocations; }
        public List<String> getExamTags() { return examTags; } public void setExamTags(List<String> examTags) { this.examTags = examTags; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MyVocabList {
        private List<MyWord> items;
        private Map<String, Integer> stats;  // {NEW:50, LEARNING:80, ...}
        private Pagination pagination;

        public List<MyWord> getItems() { return items; } public void setItems(List<MyWord> items) { this.items = items; }
        public Map<String, Integer> getStats() { return stats; } public void setStats(Map<String, Integer> stats) { this.stats = stats; }
        public Pagination getPagination() { return pagination; } public void setPagination(Pagination pagination) { this.pagination = pagination; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Pagination { private int page; private int size; private long total; private int totalPages;
        public int getPage() { return page; } public void setPage(int page) { this.page = page; }
        public int getSize() { return size; } public void setSize(int size) { this.size = size; }
        public long getTotal() { return total; } public void setTotal(long total) { this.total = total; }
        public int getTotalPages() { return totalPages; } public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }

    /** 更新状态请求 */
    public static class StatusUpdate { private String status;
        public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    }
}
