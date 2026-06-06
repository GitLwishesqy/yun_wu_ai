package com.yunwu.coach.speech;

/**
 * 语音合成 (TTS) 服务接口 — 文本→语音
 */
public interface TtsService {

    /**
     * 将英文文本合成为语音文件
     *
     * @param text        待合成文本
     * @param voiceType   音色: FEMALE / MALE / CHILD
     * @param speechRate  语速: 0.5-2.0
     * @return 合成结果
     */
    TtsResult synthesize(String text, String voiceType, double speechRate);

    String getProvider();

    record TtsResult(
            String audioUrl,        // 合成后的音频 URL
            int durationMs,         // 音频时长(毫秒)
            String providerMsgId    // 服务商消息ID
    ) {}
}
