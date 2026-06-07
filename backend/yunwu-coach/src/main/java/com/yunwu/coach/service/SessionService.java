package com.yunwu.coach.service;

import com.yunwu.coach.client.AiAgentClient;
import com.yunwu.coach.dto.*;
import com.yunwu.coach.entity.*;
import com.yunwu.coach.mapper.*;
import com.yunwu.coach.speech.AsrService;
import com.yunwu.coach.speech.TtsService;
import com.yunwu.common.context.UserContext;
import com.yunwu.common.exception.BusinessException;
import com.yunwu.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 会话服务 — 支持文本 + 语音消息
 */
@Service
public class SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private final CoachSessionMapper sessionMapper;
    private final CoachMessageMapper messageMapper;
    private final CoachCorrectionMapper correctionMapper;
    private final AiAgentClient aiAgentClient;
    private final AsrService asrService;
    private final TtsService ttsService;

    public SessionService(CoachSessionMapper sessionMapper,
                          CoachMessageMapper messageMapper,
                          CoachCorrectionMapper correctionMapper,
                          AiAgentClient aiAgentClient,
                          @Autowired(required = false) AsrService asrService,
                          @Autowired(required = false) TtsService ttsService) {
        this.sessionMapper = sessionMapper;
        this.messageMapper = messageMapper;
        this.correctionMapper = correctionMapper;
        this.aiAgentClient = aiAgentClient;
        this.asrService = asrService;
        this.ttsService = ttsService;
    }

    // ==================== 创建会话 ====================

    @Transactional
    public SessionResponse createSession(CreateSessionRequest request) {
        Long userId = UserContext.getUserId();

        CoachSession session = new CoachSession();
        session.setUserId(userId);
        session.setSceneId(request.getSceneId());
        session.setSessionType(request.getSessionType());
        session.setStatus("ACTIVE");
        session.setMessageCount(0);
        session.setUserMessageCount(0);
        session.setAiMessageCount(0);
        session.setCorrectionCount(0);
        session.setTotalTokensUsed(0L);
        session.setStartedAt(LocalDateTime.now());
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        sessionMapper.insert(session);
        log.info("[Session] 会话创建成功 id={}, userId={}, sceneId={}",
                session.getId(), userId, request.getSceneId());

        return toSessionResponse(session);
    }

    // ==================== 发送消息 (核心 — 文本 + 语音) ====================

    @Transactional
    public MessageResponse sendMessage(Long sessionId, SendMessageRequest request) {
        Long userId = UserContext.getUserId();
        CoachSession session = sessionMapper.selectById(sessionId);

        if (session == null) throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        if (!session.getUserId().equals(userId)) throw new BusinessException(ErrorCode.SESSION_NOT_OWNER);
        if ("COMPLETED".equals(session.getStatus())) throw new BusinessException(ErrorCode.SESSION_ALREADY_ENDED);

        int seq = session.getMessageCount() != null ? session.getMessageCount() : 0;
        String userText = request.getContent();

        // === 语音管道: ASR (语音→文本) ===
        if ("AUDIO".equalsIgnoreCase(request.getContentType()) && asrService != null) {
            String audioFormat = extractAudioFormat(request.getAudioUrl());
            AsrService.AsrResult asrResult = asrService.transcribe(
                    request.getAudioUrl(), audioFormat);
            if (asrResult.text() != null && !asrResult.text().isEmpty()) {
                userText = asrResult.text();
                log.info("[Voice] ASR识别: {} (confidence={})",
                        userText, asrResult.confidence());
            }
        }

        // 1. 保存用户消息
        CoachMessage userMsg = new CoachMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setRole("USER");
        userMsg.setContent(userText);
        userMsg.setContentType(request.getContentType());
        userMsg.setAudioUrl(request.getAudioUrl());
        userMsg.setAudioDuration(request.getAudioDuration());
        userMsg.setHasCorrection(false);
        userMsg.setSequenceNum(seq + 1);
        userMsg.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(userMsg);

        // 2. 构建 AI 请求
        AiAgentClient.AiAgentRequest aiRequest = buildAiRequest(session, userText);

        // 3. 调用 Python AI 智能体
        AiAgentClient.AiAgentResponse aiResponse = aiAgentClient.chat(aiRequest);

        // 4. 保存 AI 回复 (文本)  +  TTS 语音合成
        String aiText = aiResponse.getAiMessage();
        String aiAudioUrl = null;
        int aiAudioDuration = 0;

        // === TTS 管道: AI文本→语音 ===
        if (ttsService != null && aiText != null && !aiText.isEmpty()) {
            try {
                TtsService.TtsResult ttsResult = ttsService.synthesize(
                        aiText, "FEMALE", 1.0);
                if (ttsResult.audioUrl() != null && !ttsResult.audioUrl().isEmpty()) {
                    aiAudioUrl = ttsResult.audioUrl();
                    aiAudioDuration = ttsResult.durationMs();
                    log.info("[Voice] TTS合成成功 duration={}ms", aiAudioDuration);
                }
            } catch (Exception e) {
                log.warn("[Voice] TTS合成失败(降级为纯文本): {}", e.getMessage());
            }
        }

        CoachMessage aiMsg = new CoachMessage();
        aiMsg.setSessionId(sessionId);
        aiMsg.setRole("AI");
        aiMsg.setContent(aiText);
        aiMsg.setContentType(aiAudioUrl != null ? "MIXED" : "TEXT");
        aiMsg.setAudioUrl(aiAudioUrl);
        aiMsg.setAudioDuration(aiAudioDuration);
        aiMsg.setModelName(aiResponse.getModelName());
        aiMsg.setPromptTokens(aiResponse.getPromptTokens());
        aiMsg.setCompletionTokens(aiResponse.getCompletionTokens());
        aiMsg.setLatencyMs((int) aiResponse.getLatencyMs());
        aiMsg.setSequenceNum(seq + 2);
        aiMsg.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(aiMsg);

        // 5. 保存纠错记录
        if (aiResponse.getCorrections() != null) {
            for (var corr : aiResponse.getCorrections()) {
                Correction correction = new Correction();
                correction.setMessageId(userMsg.getId());
                correction.setSessionId(sessionId);
                correction.setUserId(userId);
                correction.setErrorType(corr.getErrorType());
                correction.setErrorSubtype(corr.getErrorSubtype());
                correction.setSeverity(corr.getSeverity());
                correction.setOriginalText(corr.getOriginalText());
                correction.setErrorSpan(corr.getErrorSpan());
                correction.setCorrectedText(corr.getCorrectedText());
                correction.setExplanation(corr.getExplanation());
                correction.setImprovementTip(corr.getImprovementTip());
                correction.setRelatedRule(corr.getRelatedRule());
                correction.setCorrectionStrategy(corr.getCorrectionStrategy());
                correction.setCreatedAt(LocalDateTime.now());
                correctionMapper.insert(correction);
            }
            userMsg.setHasCorrection(true);
            messageMapper.updateById(userMsg);
        }

        // 6. 更新会话统计
        session.setMessageCount(session.getMessageCount() + 2);
        session.setUserMessageCount(session.getUserMessageCount() + 1);
        session.setAiMessageCount(session.getAiMessageCount() + 1);
        session.setCorrectionCount(session.getCorrectionCount() +
                (aiResponse.getCorrections() != null ? aiResponse.getCorrections().size() : 0));
        session.setTotalTokensUsed(session.getTotalTokensUsed() +
                (aiResponse.getTotalTokens() != null ? aiResponse.getTotalTokens() : 0));
        session.setUpdatedAt(LocalDateTime.now());
        sessionMapper.updateById(session);

        log.info("[Session] 消息处理完成 sessionId={}, tokens={}",
                sessionId, aiResponse.getTotalTokens());

        // 7. 构建响应
        return toMessageResponse(aiMsg, userMsg);
    }

    // ==================== 获取消息列表 ====================

    public List<MessageResponse> getMessages(Long sessionId) {
        List<CoachMessage> messages;
        try { messages = messageMapper.selectBySessionId(sessionId); } catch (Exception e) { messages = new ArrayList<>(); }
        return messages.stream()
                .map(msg -> {
                    MessageResponse resp = toMessageResponse(msg, null);
                    if (msg.getHasCorrection() != null && msg.getHasCorrection()) {
                        List<Correction> corrections = correctionMapper.selectByMessageId(msg.getId());
                        if (!corrections.isEmpty()) {
                            resp.setCorrection(toCorrectionInfo(corrections.get(0)));
                        }
                    }
                    return resp;
                })
                .collect(Collectors.toList());
    }

    // ==================== 完成会话 ====================

    @Transactional
    public SessionResponse completeSession(Long sessionId) {
        CoachSession session = sessionMapper.selectById(sessionId);
        if (session == null) throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);

        session.setStatus("COMPLETED");
        session.setEndedAt(LocalDateTime.now());
        if (session.getStartedAt() != null) {
            session.setDurationSeconds((int) java.time.Duration.between(
                    session.getStartedAt(), session.getEndedAt()).getSeconds());
        }
        session.setUpdatedAt(LocalDateTime.now());
        sessionMapper.updateById(session);

        log.info("[Session] 会话完成 id={}, duration={}s", sessionId, session.getDurationSeconds());
        return toSessionResponse(session);
    }

    // ==================== 私有辅助方法 ====================

    private AiAgentClient.AiAgentRequest buildAiRequest(
            CoachSession session, String userMessage) {
        AiAgentClient.AiAgentRequest req = new AiAgentClient.AiAgentRequest();
        req.setSessionId(String.valueOf(session.getId()));
        req.setUserId(session.getUserId());
        req.setUserMessage(userMessage);

        // 对话历史
        try {
            List<CoachMessage> history = messageMapper.selectBySessionId(session.getId());
            List<AiAgentClient.AiAgentRequest.HistoryMessage> histMsgs = new ArrayList<>();
            for (CoachMessage m : history) {
                histMsgs.add(new AiAgentClient.AiAgentRequest.HistoryMessage(m.getRole(), m.getContent()));
            }
            if (histMsgs.size() > 40) histMsgs = histMsgs.subList(histMsgs.size() - 40, histMsgs.size());
            req.setConversationHistory(histMsgs);
        } catch (Exception e) {
            req.setConversationHistory(new ArrayList<>());
        }

        // 场景上下文 (注入 Prompt)
        if (session.getSceneId() != null) {
            try {
                AiAgentClient.AiAgentRequest.SceneContext sc = new AiAgentClient.AiAgentRequest.SceneContext();
                sc.setId(session.getSceneId());
                // TODO: 从 SceneService 加载场景详情填充 keywords, roles, targetSentences
                req.setScene(sc);
            } catch (Exception ignored) {}
        }

        return req;
    }

    private SessionResponse toSessionResponse(CoachSession s) {
        SessionResponse resp = new SessionResponse();
        resp.setId(s.getId());
        resp.setSceneId(s.getSceneId());
        resp.setSessionType(s.getSessionType());
        resp.setTitle(s.getTitle());
        resp.setStatus(s.getStatus());
        resp.setMessageCount(s.getMessageCount());
        resp.setCorrectionCount(s.getCorrectionCount());
        resp.setDurationSeconds(s.getDurationSeconds());
        resp.setTotalTokensUsed(s.getTotalTokensUsed());
        resp.setStartedAt(s.getStartedAt());
        resp.setEndedAt(s.getEndedAt());
        return resp;
    }

    /** 从 URL 提取音频格式 */
    private String extractAudioFormat(String audioUrl) {
        if (audioUrl == null) return "mp3";
        String lower = audioUrl.toLowerCase();
        if (lower.contains(".wav")) return "wav";
        if (lower.contains(".pcm")) return "pcm";
        if (lower.contains(".ogg")) return "ogg";
        return "mp3";
    }

    private MessageResponse toMessageResponse(CoachMessage msg, CoachMessage userMsg) {
        MessageResponse resp = new MessageResponse();
        resp.setId(msg.getId());
        resp.setSessionId(msg.getSessionId());
        resp.setRole(msg.getRole());
        resp.setContent(msg.getContent());
        resp.setContentType(msg.getContentType());
        resp.setAudioUrl(msg.getAudioUrl());
        resp.setAudioDuration(msg.getAudioDuration());
        resp.setSequenceNum(msg.getSequenceNum());
        resp.setCreatedAt(msg.getCreatedAt());
        resp.setModelName(msg.getModelName());
        resp.setTokensUsed(msg.getPromptTokens() != null && msg.getCompletionTokens() != null
                ? msg.getPromptTokens() + msg.getCompletionTokens() : null);
        return resp;
    }

    private MessageResponse.CorrectionInfo toCorrectionInfo(Correction c) {
        MessageResponse.CorrectionInfo info = new MessageResponse.CorrectionInfo();
        info.setId(c.getId());
        info.setErrorType(c.getErrorType());
        info.setErrorSubtype(c.getErrorSubtype());
        info.setSeverity(c.getSeverity());
        info.setOriginalText(c.getOriginalText());
        info.setErrorSpan(c.getErrorSpan());
        info.setCorrectedText(c.getCorrectedText());
        info.setExplanation(c.getExplanation());
        info.setImprovementTip(c.getImprovementTip());
        info.setRelatedRule(c.getRelatedRule());
        info.setCorrectionStrategy(c.getCorrectionStrategy());
        return info;
    }
}
