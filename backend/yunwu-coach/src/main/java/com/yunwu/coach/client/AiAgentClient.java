package com.yunwu.coach.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * AI 智能体 HTTP 客户端 — 调用 Python LangGraph 微服务
 * <p>
 * 调用链: Java后端 → HTTP → Python FastAPI → LangGraph Agent → LLM → 返回
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Component
public class AiAgentClient {

    private static final Logger log = LoggerFactory.getLogger(AiAgentClient.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${yunwu.ai-agent.url:http://localhost:8000}")
    private String agentBaseUrl;

    public AiAgentClient(ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = objectMapper;
    }

    /**
     * 发送消息到 AI 智能体，获取回复
     *
     * @param request AI 请求
     * @return AI 响应
     */
    public AiAgentResponse chat(AiAgentRequest request) {
        try {
            String body = objectMapper.writeValueAsString(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(agentBaseUrl + "/api/v1/chat"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            long startMs = System.currentTimeMillis();
            HttpResponse<String> response = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());
            long latencyMs = System.currentTimeMillis() - startMs;

            if (response.statusCode() == 200) {
                AiAgentResponse agentResponse = objectMapper.readValue(
                        response.body(), AiAgentResponse.class);
                agentResponse.setLatencyMs(latencyMs);
                log.info("[AI-Agent] 响应成功 latency={}ms tokens={}",
                        latencyMs, agentResponse.getTotalTokens());
                return agentResponse;
            } else {
                log.error("[AI-Agent] 响应异常 status={} body={}",
                        response.statusCode(), response.body());
                return AiAgentResponse.error("AI 服务返回错误: " + response.statusCode());
            }
        } catch (Exception e) {
            log.error("[AI-Agent] 调用失败", e);
            return AiAgentResponse.error("AI 服务暂时不可用");
        }
    }

    // ==================== 内部类: 请求/响应 ====================

    /**
     * 发送给 AI 智能体的请求
     */
    public static class AiAgentRequest {
        @JsonProperty("session_id")
        private String sessionId;

        @JsonProperty("user_id")
        private Long userId;

        @JsonProperty("user_message")
        private String userMessage;

        @JsonProperty("scene")
        private SceneContext scene;

        @JsonProperty("learner_profile")
        private LearnerContext learnerProfile;

        @JsonProperty("conversation_history")
        private List<HistoryMessage> conversationHistory;

        // Builder 模式
        public AiAgentRequest() {}

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserMessage() { return userMessage; }
        public void setUserMessage(String userMessage) { this.userMessage = userMessage; }
        public SceneContext getScene() { return scene; }
        public void setScene(SceneContext scene) { this.scene = scene; }
        public LearnerContext getLearnerProfile() { return learnerProfile; }
        public void setLearnerProfile(LearnerContext learnerProfile) { this.learnerProfile = learnerProfile; }
        public List<HistoryMessage> getConversationHistory() { return conversationHistory; }
        public void setConversationHistory(List<HistoryMessage> conversationHistory) { this.conversationHistory = conversationHistory; }

        public static class SceneContext {
            private Long id;
            private String name;
            private String nameEn;
            private Integer difficulty;
            private String cefrLevel;
            private List<Map<String, String>> roles;
            private List<Map<String, String>> keywords;
            private List<Map<String, String>> targetSentences;

            public Long getId() { return id; }
            public void setId(Long id) { this.id = id; }
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public String getNameEn() { return nameEn; }
            public void setNameEn(String nameEn) { this.nameEn = nameEn; }
            public Integer getDifficulty() { return difficulty; }
            public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
            public String getCefrLevel() { return cefrLevel; }
            public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
            public List<Map<String, String>> getRoles() { return roles; }
            public void setRoles(List<Map<String, String>> roles) { this.roles = roles; }
            public List<Map<String, String>> getKeywords() { return keywords; }
            public void setKeywords(List<Map<String, String>> keywords) { this.keywords = keywords; }
            public List<Map<String, String>> getTargetSentences() { return targetSentences; }
            public void setTargetSentences(List<Map<String, String>> targetSentences) { this.targetSentences = targetSentences; }
        }

        public static class LearnerContext {
            private String cefrLevel;
            private Integer vocabularySize;
            private Map<String, Double> weaknesses;
            private String learningGoal;

            public String getCefrLevel() { return cefrLevel; }
            public void setCefrLevel(String cefrLevel) { this.cefrLevel = cefrLevel; }
            public Integer getVocabularySize() { return vocabularySize; }
            public void setVocabularySize(Integer vocabularySize) { this.vocabularySize = vocabularySize; }
            public Map<String, Double> getWeaknesses() { return weaknesses; }
            public void setWeaknesses(Map<String, Double> weaknesses) { this.weaknesses = weaknesses; }
            public String getLearningGoal() { return learningGoal; }
            public void setLearningGoal(String learningGoal) { this.learningGoal = learningGoal; }
        }

        public static class HistoryMessage {
            private String role;
            private String content;

            public HistoryMessage() {}
            public HistoryMessage(String role, String content) {
                this.role = role;
                this.content = content;
            }
            public String getRole() { return role; }
            public void setRole(String role) { this.role = role; }
            public String getContent() { return content; }
            public void setContent(String content) { this.content = content; }
        }
    }

    /**
     * AI 智能体返回的响应
     */
    public static class AiAgentResponse {
        @JsonProperty("ai_message")
        private String aiMessage;

        @JsonProperty("corrections")
        private List<CorrectionItem> corrections;

        @JsonProperty("model_name")
        private String modelName;

        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        @JsonProperty("total_tokens")
        private Integer totalTokens;

        private long latencyMs;

        // 由 AiAgentClient 填充
        public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }
        public long getLatencyMs() { return latencyMs; }

        public static AiAgentResponse error(String message) {
            AiAgentResponse r = new AiAgentResponse();
            r.aiMessage = message;
            r.modelName = "ERROR";
            return r;
        }

        public String getAiMessage() { return aiMessage; }
        public void setAiMessage(String aiMessage) { this.aiMessage = aiMessage; }
        public List<CorrectionItem> getCorrections() { return corrections; }
        public void setCorrections(List<CorrectionItem> corrections) { this.corrections = corrections; }
        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        public Integer getPromptTokens() { return promptTokens; }
        public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }
        public Integer getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }
        public Integer getTotalTokens() { return totalTokens; }
        public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }

        public static class CorrectionItem {
            @JsonProperty("error_type")
            private String errorType;
            @JsonProperty("error_subtype")
            private String errorSubtype;
            private String severity;
            @JsonProperty("original_text")
            private String originalText;
            @JsonProperty("error_span")
            private String errorSpan;
            @JsonProperty("corrected_text")
            private String correctedText;
            private String explanation;
            @JsonProperty("improvement_tip")
            private String improvementTip;
            @JsonProperty("related_rule")
            private String relatedRule;
            @JsonProperty("correction_strategy")
            private String correctionStrategy;

            public String getErrorType() { return errorType; }
            public void setErrorType(String errorType) { this.errorType = errorType; }
            public String getErrorSubtype() { return errorSubtype; }
            public void setErrorSubtype(String errorSubtype) { this.errorSubtype = errorSubtype; }
            public String getSeverity() { return severity; }
            public void setSeverity(String severity) { this.severity = severity; }
            public String getOriginalText() { return originalText; }
            public void setOriginalText(String originalText) { this.originalText = originalText; }
            public String getErrorSpan() { return errorSpan; }
            public void setErrorSpan(String errorSpan) { this.errorSpan = errorSpan; }
            public String getCorrectedText() { return correctedText; }
            public void setCorrectedText(String correctedText) { this.correctedText = correctedText; }
            public String getExplanation() { return explanation; }
            public void setExplanation(String explanation) { this.explanation = explanation; }
            public String getImprovementTip() { return improvementTip; }
            public void setImprovementTip(String improvementTip) { this.improvementTip = improvementTip; }
            public String getRelatedRule() { return relatedRule; }
            public void setRelatedRule(String relatedRule) { this.relatedRule = relatedRule; }
            public String getCorrectionStrategy() { return correctionStrategy; }
            public void setCorrectionStrategy(String correctionStrategy) { this.correctionStrategy = correctionStrategy; }
        }
    }
}
