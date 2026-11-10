package com.sqx.system.service.impl;


import com.google.code.kaptcha.Producer;
import com.sqx.common.core.exception.SqxException;
import com.sqx.common.redis.utils.RedisUtil;
import com.sqx.system.service.SysCaptchaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.time.Duration;

/**
 * 验证码
 *
 */
@Service("sysCaptchaService")
public class SysCaptchaServiceImpl implements SysCaptchaService {

    private static final String CAPTCHA_KEY_PREFIX = "sys:captcha:";
    private static final Duration CAPTCHA_TTL = Duration.ofMinutes(5);

    @Autowired
    private Producer producer;

    @Override
    public BufferedImage getCaptcha(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new SqxException("uuid不能为空");
        }
        String code = producer.createText();
        RedisUtil.setCacheObject(CAPTCHA_KEY_PREFIX + uuid, code, CAPTCHA_TTL);
        return producer.createImage(code);
    }

    @Override
    public boolean validate(String uuid, String code) {
        if (StringUtils.isBlank(uuid) || StringUtils.isBlank(code)) {
            return false;
        }
        String key = CAPTCHA_KEY_PREFIX + uuid;
        String cached = RedisUtil.getCacheObject(key);
        RedisUtil.deleteObject(key);
        return cached != null && cached.equalsIgnoreCase(code);
    }
}
