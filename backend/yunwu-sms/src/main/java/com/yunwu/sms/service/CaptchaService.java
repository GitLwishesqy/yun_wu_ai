package com.yunwu.sms.service;

import cn.hutool.core.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * 图形验证码服务 — 防刷第一道防线
 * <p>
 * 流程:
 * 1. 前端请求 /api/v1/captcha → 返回图片 base64 + captchaKey
 * 2. 发送验证码时附带 captchaKey + captchaCode
 * 3. 服务端校验图形验证码 → 通过后才发送短信
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Service
public class CaptchaService {

    private static final Logger log = LoggerFactory.getLogger(CaptchaService.class);

    private static final String CAPTCHA_KEY_PREFIX = "yunwu:captcha:";
    private static final int CAPTCHA_EXPIRE_SECONDS = 120;     // 2分钟
    private static final int IMAGE_WIDTH = 130;
    private static final int IMAGE_HEIGHT = 48;
    private static final int CODE_LENGTH = 4;

    private final RedisTemplate<String, Object> redisTemplate;

    public CaptchaService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成图形验证码
     *
     * @return {captchaKey, captchaImage (base64)}
     */
    public java.util.Map<String, String> generate() {
        String captchaKey = java.util.UUID.randomUUID().toString().replace("-", "");
        String code = RandomUtil.randomNumbers(CODE_LENGTH);

        // 存入 Redis
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        redisTemplate.opsForValue().set(redisKey, code, CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);

        // 生成图片
        String base64Image = generateImage(code);

        log.debug("[Captcha] 生成验证码 key={}", captchaKey);
        return java.util.Map.of("captchaKey", captchaKey, "captchaImage", base64Image);
    }

    /**
     * 校验图形验证码
     *
     * @param captchaKey  验证码 Key
     * @param captchaCode 用户输入的验证码
     * @return true 表示通过
     */
    public boolean verify(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaCode == null) {
            return false;
        }

        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        String storedCode = (String) redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            log.debug("[Captcha] 验证码已过期或不存在 key={}", captchaKey);
            return false;
        }

        boolean matched = storedCode.equalsIgnoreCase(captchaCode);

        // 无论是否匹配，立即删除 (一次性使用)
        redisTemplate.delete(redisKey);

        if (!matched) {
            log.debug("[Captcha] 验证码不匹配 key={}", captchaKey);
        }
        return matched;
    }

    // ==================== 图片生成 ====================

    private String generateImage(String code) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 背景 (浅绿色 — 符合产品视觉风格)
        g.setColor(new Color(0xF0, 0xFA, 0xF4));
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        // 干扰线
        g.setColor(new Color(0xD4, 0xED, 0xDA));
        for (int i = 0; i < 6; i++) {
            int x1 = RandomUtil.randomInt(0, IMAGE_WIDTH);
            int y1 = RandomUtil.randomInt(0, IMAGE_HEIGHT);
            int x2 = RandomUtil.randomInt(0, IMAGE_WIDTH);
            int y2 = RandomUtil.randomInt(0, IMAGE_HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 干扰点
        for (int i = 0; i < 30; i++) {
            int x = RandomUtil.randomInt(0, IMAGE_WIDTH);
            int y = RandomUtil.randomInt(0, IMAGE_HEIGHT);
            image.setRGB(x, y, new Color(0xA8, 0xD8, 0xBA).getRGB());
        }

        // 文字
        g.setFont(new Font("Arial", Font.BOLD, 28));
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(
                    RandomUtil.randomInt(0x3A, 0x7D),
                    RandomUtil.randomInt(0x55, 0xA8),
                    RandomUtil.randomInt(0x7A, 0xBA)));
            // 每个字符旋转 ±15°
            double angle = Math.toRadians(RandomUtil.randomInt(-15, 15));
            g.rotate(angle, 25 + i * 25, 32);
            g.drawString(String.valueOf(code.charAt(i)), 18 + i * 26, 35);
            g.rotate(-angle, 25 + i * 25, 32);
        }
        g.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            log.error("[Captcha] 图片生成失败", e);
            return "";
        }
    }
}
