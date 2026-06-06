package com.yunwu.coach.speech.impl;

import com.yunwu.coach.speech.OssService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.time.Instant;
import java.util.UUID;

/**
 * 本地文件存储实现 — 开发环境使用 (生产环境可替换为 MinIO / 阿里云 OSS SDK)
 */
@Service
public class MinioOssServiceImpl implements OssService {

    private static final Logger log = LoggerFactory.getLogger(MinioOssServiceImpl.class);

    @Value("${yunwu.oss.upload-dir:./upload}")
    private String uploadDir;

    @Value("${yunwu.oss.base-url:http://localhost:8080/upload}")
    private String baseUrl;

    @Override
    public String upload(InputStream inputStream, String fileName, String contentType) {
        try {
            Path dir = Paths.get(uploadDir);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            // 按日期分目录
            String dateDir = java.time.LocalDate.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path datePath = dir.resolve(dateDir);
            if (!Files.exists(datePath)) {
                Files.createDirectories(datePath);
            }

            String uniqueName = UUID.randomUUID().toString().substring(0, 8) + "_" + fileName;
            Path filePath = datePath.resolve(uniqueName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            String url = baseUrl + "/" + dateDir + "/" + uniqueName;
            log.info("[OSS] 上传成功: {}", url);
            return url;
        } catch (IOException e) {
            log.error("[OSS] 上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public String generatePresignedUrl(String objectName, long expireSeconds) {
        return baseUrl + "/" + objectName + "?expires=" +
                (Instant.now().getEpochSecond() + expireSeconds);
    }

    @Override
    public InputStream download(String objectName) {
        try {
            Path path = Paths.get(uploadDir, objectName);
            return new FileInputStream(path.toFile());
        } catch (IOException e) {
            // 如果是完整 URL，尝试 HTTP 下载
            if (objectName.startsWith("http")) {
                try {
                    URL url = new URL(objectName);
                    URLConnection conn = url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(10000);
                    return conn.getInputStream();
                } catch (IOException ex) {
                    log.error("[OSS] HTTP下载失败: {}", ex.getMessage());
                }
            }
            log.error("[OSS] 下载失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void delete(String objectName) {
        try {
            Path path = Paths.get(uploadDir, objectName);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("[OSS] 删除失败: {}", e.getMessage());
        }
    }

    @Override
    public String extractObjectName(String url) {
        if (url == null) return "";
        // URL → objectName
        if (url.contains("/upload/")) {
            return url.substring(url.indexOf("/upload/") + 8);
        }
        return url;
    }
}
