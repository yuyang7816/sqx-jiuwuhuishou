package com.sqx.app.base.verifyCode.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sqx.app.base.template.service.SmsTemplateService;
import com.sqx.app.base.verifyCode.constans.VerifyCodeConstant;
import com.sqx.app.base.verifyCode.db.service.IVerifyCodeService;
import com.sqx.app.base.verifyCode.domain.entity.VerifyCode;
import com.sqx.common.core.utils.SpringContextHolder;
import com.sqx.common.mail.service.IMailService;
import com.sqx.common.redis.utils.ConfigUtil;
import com.sqx.common.redis.utils.RedisUtil;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VerifyCodeServiceImplTest {

    @Mock
    private IVerifyCodeService iVerifyCodeService;
    @Mock
    private IMailService mailService;
    @Mock
    private com.sqx.common.sms.SmsService smsService;
    @Mock
    private SmsTemplateService smsTemplateService;

    @InjectMocks
    private VerifyCodeServiceImpl service;

    @BeforeAll
    static void initLambdaAndRedisContext() {
        MybatisPlusLambdaInit.init(VerifyCode.class);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        RedissonClient redissonClient = mock(RedissonClient.class);
        when(applicationContext.getBean(RedissonClient.class)).thenReturn(redissonClient);
        ReflectionTestUtils.setField(SpringContextHolder.class, "applicationContext", applicationContext);
    }

    @BeforeEach
    void clearTestCache() throws Exception {
        Field cacheField = VerifyCodeServiceImpl.class.getDeclaredField("TEST_CODE_CACHE");
        cacheField.setAccessible(true);
        ((ConcurrentHashMap<?, ?>) cacheField.get(null)).clear();
        ReflectionTestUtils.setField(service, "testMode", false);
        ReflectionTestUtils.setField(service, "testCode", "888888");
    }

    @Test
    void verifyInTestModeShouldCheckCacheAndUpdateStatus() {
        ReflectionTestUtils.setField(service, "testMode", true);

        assertFalse(service.verify("13800138001", "888888"));

        VerifyCode sent = service.sendPhoneCode("13800138001", "register");
        assertEquals("888888", sent.getCode());
        assertTrue(service.verify("13800138001", "888888"));
        verify(iVerifyCodeService).update(any(VerifyCode.class), any(LambdaUpdateWrapper.class));

        assertFalse(service.verify("13800138001", "888888"));
    }

    @Test
    void verifyInRedisModeShouldHandleMissingWrongAndMatchingCode() {
        String phone = "13800138002";
        String key = VerifyCodeConstant.CACHE_KEY + phone;

        try (MockedStatic<RedisUtil> redis = mockStatic(RedisUtil.class)) {
            redis.when(() -> RedisUtil.hasKey(key)).thenReturn(false);
            assertFalse(service.verify(phone, "123456"));

            redis.when(() -> RedisUtil.hasKey(key)).thenReturn(true);
            redis.when(() -> RedisUtil.getCacheObject(key)).thenReturn("123456");
            assertFalse(service.verify(phone, "654321"));

            redis.when(() -> RedisUtil.hasKey(key)).thenReturn(true);
            redis.when(() -> RedisUtil.getCacheObject(key)).thenReturn("123456");
            assertTrue(service.verify(phone, "123456"));
            redis.verify(() -> RedisUtil.deleteObject(key));
            verify(iVerifyCodeService).update(any(VerifyCode.class), any(LambdaUpdateWrapper.class));
        }
    }

    @Test
    void sendPhoneCodeInTestModeShouldUseTestCodeAndSkipRedis() {
        ReflectionTestUtils.setField(service, "testMode", true);
        ReflectionTestUtils.setField(service, "testCode", "666666");

        VerifyCode verifyCode = service.sendPhoneCode("13800138003", "register");

        assertEquals("666666", verifyCode.getCode());
        assertEquals("13800138003", verifyCode.getPhone());
        assertEquals(Integer.valueOf(1), verifyCode.getStatus());
        assertNotNull(verifyCode.getExpireTime());
        verify(iVerifyCodeService).save(any(VerifyCode.class));
        verifyNoInteractions(smsService);
    }

    @Test
    void sendPhoneCodeInRedisModeShouldSendSmsAndCacheCode() {
        String phone = "13800138004";
        String key = VerifyCodeConstant.CACHE_KEY + phone;

        when(smsTemplateService.getContentByBizCode("register"))
                .thenReturn("【%s】验证码: %s，注册");

        try (MockedStatic<RedisUtil> redis = mockStatic(RedisUtil.class);
             MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValue("sys_server_name")).thenReturn("回收平台");
            redis.when(() -> RedisUtil.hasKey(key)).thenReturn(true, false);

            VerifyCode verifyCode = service.sendPhoneCode(phone, "register");

            assertNotNull(verifyCode.getCode());
            assertEquals(6, verifyCode.getCode().length());
            verify(smsService).sendSms(eq(phone), contains(verifyCode.getCode()));
            redis.verify(() -> RedisUtil.deleteObject(key));
            redis.verify(() -> RedisUtil.setCacheObject(eq(key), eq(verifyCode.getCode()), any(Duration.class)));
            verify(iVerifyCodeService).save(any(VerifyCode.class));
        }
    }

    @Test
    void sendMailCodeShouldSendMailAndCacheCode() {
        String email = "user@example.com";
        String key = VerifyCodeConstant.CACHE_KEY + email;

        when(smsTemplateService.getContentByBizCode("forget"))
                .thenReturn("【%s】验证码: %s，找回密码");

        try (MockedStatic<RedisUtil> redis = mockStatic(RedisUtil.class);
             MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValue("sys_server_name")).thenReturn("回收平台");
            redis.when(() -> RedisUtil.hasKey(key)).thenReturn(false);

            VerifyCode verifyCode = service.sendMailCode(email, "forget");

            assertEquals(email, verifyCode.getPhone());
            assertNotNull(verifyCode.getCode());
            verify(mailService).sendSimpleMail(eq(email), eq("回收平台"), contains(verifyCode.getCode()));
            redis.verify(() -> RedisUtil.setCacheObject(eq(key), eq(verifyCode.getCode()), any(Duration.class)));
            verify(iVerifyCodeService).save(any(VerifyCode.class));
        }
    }

    @Test
    void sendMailCodeInTestModeShouldUseTestContentTemplate() {
        ReflectionTestUtils.setField(service, "testMode", true);
        String email = "test@example.com";
        String key = VerifyCodeConstant.CACHE_KEY + email;

        try (MockedStatic<RedisUtil> redis = mockStatic(RedisUtil.class);
             MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValue("sys_server_name")).thenReturn("回收平台");
            redis.when(() -> RedisUtil.hasKey(key)).thenReturn(true);

            VerifyCode verifyCode = service.sendMailCode(email, "bind");

            assertNotNull(verifyCode.getCode());
            verify(mailService).sendSimpleMail(eq(email), eq("回收平台"), startsWith("测试验证码："));
            redis.verify(() -> RedisUtil.deleteObject(key));
            verifyNoInteractions(smsTemplateService);
        }
    }
}
