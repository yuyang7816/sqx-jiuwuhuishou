package com.sqx.app.base.verifyCode.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sqx.app.base.template.service.SmsTemplateService;
import com.sqx.app.base.verifyCode.constans.VerifyCodeConstant;
import com.sqx.app.base.verifyCode.db.service.IVerifyCodeService;
import com.sqx.app.base.verifyCode.domain.entity.VerifyCode;
import com.sqx.app.base.verifyCode.service.VerifyCodeService;
import com.sqx.common.mail.service.IMailService;
import com.sqx.common.redis.utils.ConfigUtil;
import com.sqx.common.redis.utils.RedisUtil;
import com.sqx.common.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 验证码 服务实现类
 *
 * @author sqx
 * @since 2025-04-22 15:31:50
 */
@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {

    private static final ConcurrentHashMap<String, String> TEST_CODE_CACHE = new ConcurrentHashMap<>();

    @Value("${sqx.verify-code.test-mode:false}")
    private boolean testMode;

    @Value("${sqx.verify-code.test-code:888888}")
    private String testCode;

    @Autowired
    private IVerifyCodeService iVerifyCodeService;
    @Autowired
    private IMailService mailService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private SmsTemplateService smsTemplateService;

    @Override
    public Boolean verify(String phone, String code) {
        String cacheCode;
        if (testMode) {
            cacheCode = TEST_CODE_CACHE.get(phone);
            if (!Objects.equals(cacheCode, code)) {
                return false;
            }
            TEST_CODE_CACHE.remove(phone);
        } else {
            String key = VerifyCodeConstant.CACHE_KEY + phone;
            if (!RedisUtil.hasKey(key)) {
                return false;
            }

            cacheCode = RedisUtil.getCacheObject(key);
            if (!Objects.equals(cacheCode, code)) {
                return false;
            }

            RedisUtil.deleteObject(key);
        }

        iVerifyCodeService.update(new VerifyCode(), new LambdaUpdateWrapper<VerifyCode>()
                .eq(VerifyCode::getPhone, phone)
                .eq(VerifyCode::getCode, cacheCode)
                .set(VerifyCode::getStatus, 2)
        );

        return true;
    }

    @Override
    public VerifyCode sendPhoneCode(String phone, String bizCode) {
        String code = testMode ? testCode : createRandomCode();
        if (!testMode) {
            String content = getContent(code, bizCode);
            smsService.sendSms(phone, content);

            if (RedisUtil.hasKey(VerifyCodeConstant.CACHE_KEY + phone)) {
                RedisUtil.deleteObject(VerifyCodeConstant.CACHE_KEY + phone);
            }

            RedisUtil.setCacheObject(VerifyCodeConstant.CACHE_KEY + phone, code, Duration.ofMinutes(10));
        } else {
            TEST_CODE_CACHE.put(phone, code);
        }

        // 保存验证码
        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setPhone(phone);
        verifyCode.setCode(code);
        verifyCode.setStatus(1);
        verifyCode.setExpireTime(LocalDateTime.now().plusMinutes(10));
        iVerifyCodeService.save(verifyCode);
        return verifyCode;
    }

    @Override
    public VerifyCode sendMailCode(String email, String bizCode) {
        String subject = ConfigUtil.getConfigValue("sys_server_name");

        String code = createRandomCode();
        String content = getContent(code, bizCode);
        mailService.sendSimpleMail(email, subject, content);

        // 放入缓存
        if (RedisUtil.hasKey(VerifyCodeConstant.CACHE_KEY + email)) {
            RedisUtil.deleteObject(VerifyCodeConstant.CACHE_KEY + email);
        }

        RedisUtil.setCacheObject(VerifyCodeConstant.CACHE_KEY + email, code, Duration.ofMinutes(10));

        // 保存验证码
        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setPhone(email);
        verifyCode.setCode(code);
        verifyCode.setStatus(1);
        verifyCode.setExpireTime(LocalDateTime.now().plusMinutes(10));
        iVerifyCodeService.save(verifyCode);

        return verifyCode;
    }

    /**
     * 根据业务代码获取验证码内容
     * @param bizCode
     * @return
     */
    private String getContent(String code, String bizCode) {
        if (testMode) {
            return String.format("测试验证码：%s", code);
        }
        String serverName = ConfigUtil.getConfigValue("sys_server_name");
        String contentTemplate = smsTemplateService.getContentByBizCode(bizCode);
        return String.format(contentTemplate, serverName, code);
    }

    private String createRandomCode() {
        int anInt = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(anInt);
    }
}
