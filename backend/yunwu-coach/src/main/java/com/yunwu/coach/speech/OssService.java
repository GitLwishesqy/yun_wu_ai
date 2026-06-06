package com.yunwu.coach.speech;

import java.io.InputStream;

/**
 * 对象存储 (OSS) 服务 — 音频文件上传/下载/删除
 */
public interface OssService {

    /**
     * 上传文件到 OSS
     *
     * @param inputStream 文件流
     * @param fileName    文件名 (如 audio_20260606_103000.mp3)
     * @param contentType MIME 类型
     * @return 文件访问 URL
     */
    String upload(InputStream inputStream, String fileName, String contentType);

    /**
     * 生成带签名的临时下载 URL
     *
     * @param objectName 对象名
     * @param expireSeconds 有效期(秒)
     * @return 签名 URL
     */
    String generatePresignedUrl(String objectName, long expireSeconds);

    /**
     * 下载文件流
     */
    InputStream download(String objectName);

    /**
     * 删除文件
     */
    void delete(String objectName);

    /**
     * 根据 URL 提取 objectName
     */
    String extractObjectName(String url);
}
