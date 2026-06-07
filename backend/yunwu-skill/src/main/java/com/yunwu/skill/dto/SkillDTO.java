package com.yunwu.skill.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SkillDTO {

    // ==================== 听力 ====================
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListeningItem {
        private Long id; private String title; private String titleEn;
        private String fileUrl; private Integer durationSeconds;
        private String gradeLevel; private Integer difficulty; private String cefrLevel;

        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
        public String getTitleEn() { return titleEn; } public void setTitleEn(String titleEn) { this.titleEn = titleEn; }
        public String getFileUrl() { return fileUrl; } public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
        public Integer getDurationSeconds() { return durationSeconds; } public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
        public String getGradeLevel() { return gradeLevel; } public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
        public Integer getDifficulty() { return difficulty; } public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
        public String getCefrLevel() { return cefrLevel; } public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListeningDetail {
        private Long id; private String title; private String fileUrl; private Integer durationSeconds;
        private String transcript; private List<Map<String, Object>> questions;
        private Integer difficulty; private String cefrLevel;

        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
        public String getFileUrl() { return fileUrl; } public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
        public Integer getDurationSeconds() { return durationSeconds; } public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
        public String getTranscript() { return transcript; } public void setTranscript(String transcript) { this.transcript = transcript; }
        public List<Map<String, Object>> getQuestions() { return questions; } public void setQuestions(List<Map<String, Object>> questions) { this.questions = questions; }
        public Integer getDifficulty() { return difficulty; } public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
        public String getCefrLevel() { return cefrLevel; } public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AnswerSubmit {
        private List<Map<String, String>> answers;
        public List<Map<String, String>> getAnswers() { return answers; } public void setAnswers(List<Map<String, String>> answers) { this.answers = answers; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AnswerResult {
        private int score; private int totalQuestions; private int correctCount;
        private List<QuestionResult> results;
        public int getScore() { return score; } public void setScore(int score) { this.score = score; }
        public int getTotalQuestions() { return totalQuestions; } public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
        public int getCorrectCount() { return correctCount; } public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
        public List<QuestionResult> getResults() { return results; } public void setResults(List<QuestionResult> results) { this.results = results; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class QuestionResult {
        private int index; private boolean correct; private String correctAnswer; private String explanation;
        public int getIndex() { return index; } public void setIndex(int index) { this.index = index; }
        public boolean isCorrect() { return correct; } public void setCorrect(boolean correct) { this.correct = correct; }
        public String getCorrectAnswer() { return correctAnswer; } public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
        public String getExplanation() { return explanation; } public void setExplanation(String explanation) { this.explanation = explanation; }
    }

    // ==================== 阅读 ====================
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReadingItem {
        private Long id; private String title; private Integer wordCount; private Integer difficulty; private String cefrLevel;
        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
        public Integer getWordCount() { return wordCount; } public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }
        public Integer getDifficulty() { return difficulty; } public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
        public String getCefrLevel() { return cefrLevel; } public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReadingDetail {
        private Long id; private String title; private String content; private Integer wordCount;
        private List<Map<String, Object>> questions; private Integer difficulty; private String cefrLevel;
        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; } public void setContent(String content) { this.content = content; }
        public Integer getWordCount() { return wordCount; } public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }
        public List<Map<String, Object>> getQuestions() { return questions; } public void setQuestions(List<Map<String, Object>> questions) { this.questions = questions; }
        public Integer getDifficulty() { return difficulty; } public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
        public String getCefrLevel() { return cefrLevel; } public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
    }

    // ==================== 写作 ====================
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WritingItem {
        private Long id; private String title; private Integer difficulty; private String cefrLevel;
        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
        public Integer getDifficulty() { return difficulty; } public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
        public String getCefrLevel() { return cefrLevel; } public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WritingDetail {
        private Long id; private String title; private String prompt; private String promptEn;
        private Integer wordLimitMin; private Integer wordLimitMax; private Integer timeLimitMinutes;
        private Integer difficulty; private String cefrLevel;
        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
        public String getPrompt() { return prompt; } public void setPrompt(String prompt) { this.prompt = prompt; }
        public String getPromptEn() { return promptEn; } public void setPromptEn(String promptEn) { this.promptEn = promptEn; }
        public Integer getWordLimitMin() { return wordLimitMin; } public void setWordLimitMin(Integer wordLimitMin) { this.wordLimitMin = wordLimitMin; }
        public Integer getWordLimitMax() { return wordLimitMax; } public void setWordLimitMax(Integer wordLimitMax) { this.wordLimitMax = wordLimitMax; }
        public Integer getTimeLimitMinutes() { return timeLimitMinutes; } public void setTimeLimitMinutes(Integer timeLimitMinutes) { this.timeLimitMinutes = timeLimitMinutes; }
        public Integer getDifficulty() { return difficulty; } public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
        public String getCefrLevel() { return cefrLevel; } public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WritingSubmit {
        private String content; private Integer timeTakenSeconds;
        public String getContent() { return content; } public void setContent(String content) { this.content = content; }
        public Integer getTimeTakenSeconds() { return timeTakenSeconds; } public void setTimeTakenSeconds(Integer timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WritingResult {
        private int score; private Map<String, BigDecimal> dimensionScores;
        private List<Map<String, String>> corrections; private String polishedVersion; private String feedbackSummary;
        public int getScore() { return score; } public void setScore(int score) { this.score = score; }
        public Map<String, BigDecimal> getDimensionScores() { return dimensionScores; } public void setDimensionScores(Map<String, BigDecimal> dimensionScores) { this.dimensionScores = dimensionScores; }
        public List<Map<String, String>> getCorrections() { return corrections; } public void setCorrections(List<Map<String, String>> corrections) { this.corrections = corrections; }
        public String getPolishedVersion() { return polishedVersion; } public void setPolishedVersion(String polishedVersion) { this.polishedVersion = polishedVersion; }
        public String getFeedbackSummary() { return feedbackSummary; } public void setFeedbackSummary(String feedbackSummary) { this.feedbackSummary = feedbackSummary; }
    }
}
