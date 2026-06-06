package com.yunwu.vocabulary.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("user_vocabulary")
public class UserVocabulary implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO) private Long id;
    private Long userId;
    private String word;
    private String wordLower;
    private String translation;
    private String status;          // NEW/LEARNING/REVIEWING/KNOWN/MASTERED
    private Integer seenCount;
    private Integer usedCount;
    private Integer errorCount;
    private Integer correctCount;
    private LocalDateTime firstSeenAt;
    private LocalDateTime lastSeenAt;
    private LocalDateTime lastReviewedAt;
    private LocalDateTime nextReviewAt;
    private Long sourceSessionId;
    private String sourceType;

    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getWord() { return word; } public void setWord(String word) { this.word = word; }
    public String getWordLower() { return wordLower; } public void setWordLower(String wordLower) { this.wordLower = wordLower; }
    public String getTranslation() { return translation; } public void setTranslation(String translation) { this.translation = translation; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public Integer getSeenCount() { return seenCount; } public void setSeenCount(Integer seenCount) { this.seenCount = seenCount; }
    public Integer getUsedCount() { return usedCount; } public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    public Integer getErrorCount() { return errorCount; } public void setErrorCount(Integer errorCount) { this.errorCount = errorCount; }
    public Integer getCorrectCount() { return correctCount; } public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }
    public LocalDateTime getFirstSeenAt() { return firstSeenAt; } public void setFirstSeenAt(LocalDateTime firstSeenAt) { this.firstSeenAt = firstSeenAt; }
    public LocalDateTime getLastSeenAt() { return lastSeenAt; } public void setLastSeenAt(LocalDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }
    public LocalDateTime getLastReviewedAt() { return lastReviewedAt; } public void setLastReviewedAt(LocalDateTime lastReviewedAt) { this.lastReviewedAt = lastReviewedAt; }
    public LocalDateTime getNextReviewAt() { return nextReviewAt; } public void setNextReviewAt(LocalDateTime nextReviewAt) { this.nextReviewAt = nextReviewAt; }
    public Long getSourceSessionId() { return sourceSessionId; } public void setSourceSessionId(Long sourceSessionId) { this.sourceSessionId = sourceSessionId; }
    public String getSourceType() { return sourceType; } public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
