package com.yunwu.vocabulary.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("vocabulary_library")
public class VocabularyLibrary implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO) private Long id;
    private String word;
    private String wordLower;
    private String pronunciation;
    private String pronunciationAudioUrl;
    private String translation;
    private String partOfSpeech;
    private String definitionEn;
    private String cefrLevel;
    private String chinaGrade;
    private Integer difficulty;
    private String exampleSentences;   // JSON
    private String commonCollocations; // JSON
    private String examTags;           // JSON

    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getWord() { return word; } public void setWord(String word) { this.word = word; }
    public String getWordLower() { return wordLower; } public void setWordLower(String wordLower) { this.wordLower = wordLower; }
    public String getPronunciation() { return pronunciation; } public void setPronunciation(String pronunciation) { this.pronunciation = pronunciation; }
    public String getPronunciationAudioUrl() { return pronunciationAudioUrl; } public void setPronunciationAudioUrl(String pronunciationAudioUrl) { this.pronunciationAudioUrl = pronunciationAudioUrl; }
    public String getTranslation() { return translation; } public void setTranslation(String translation) { this.translation = translation; }
    public String getPartOfSpeech() { return partOfSpeech; } public void setPartOfSpeech(String partOfSpeech) { this.partOfSpeech = partOfSpeech; }
    public String getDefinitionEn() { return definitionEn; } public void setDefinitionEn(String definitionEn) { this.definitionEn = definitionEn; }
    public String getCefrLevel() { return cefrLevel; } public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
    public String getChinaGrade() { return chinaGrade; } public void setChinaGrade(String chinaGrade) { this.chinaGrade = chinaGrade; }
    public Integer getDifficulty() { return difficulty; } public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    public String getExampleSentences() { return exampleSentences; } public void setExampleSentences(String exampleSentences) { this.exampleSentences = exampleSentences; }
    public String getCommonCollocations() { return commonCollocations; } public void setCommonCollocations(String commonCollocations) { this.commonCollocations = commonCollocations; }
    public String getExamTags() { return examTags; } public void setExamTags(String examTags) { this.examTags = examTags; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
