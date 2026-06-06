package com.yunwu.coach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 发送消息请求
 */
public class SendMessageRequest {

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容最长2000个字符")
    private String content;

    private String contentType = "TEXT";    // TEXT / AUDIO

    private String audioUrl;                // 语音消息时的音频URL

    private Integer audioDuration;          // 音频时长(秒)

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public Integer getAudioDuration() { return audioDuration; }
    public void setAudioDuration(Integer audioDuration) { this.audioDuration = audioDuration; }
}
