package com.yunwu.coach.speech;

/**
 * 语音识别 (ASR) 服务接口 — 语音→文本
 */
public interface AsrService {

    /**
     * 将音频文件转写为英文文本
     *
     * @param audioUrl  音频文件 URL
     * @param audioFormat 格式: mp3 / wav / pcm
     * @return 识别结果
     */
    AsrResult transcribe(String audioUrl, String audioFormat);

    String getProvider();

    record AsrResult(
            String text,            // 识别的文本
            float confidence,       // 置信度 0.0-1.0
            int durationMs,         // 音频时长(毫秒)
            String providerMsgId    // 服务商消息ID
    ) {}
}
