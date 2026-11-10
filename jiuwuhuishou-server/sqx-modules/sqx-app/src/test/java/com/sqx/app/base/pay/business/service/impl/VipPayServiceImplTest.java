package com.sqx.app.base.pay.business.service.impl;

import com.sqx.app.base.pay.domain.BusinessParam;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.vip.plan.db.service.IVipPlanService;
import com.sqx.app.vip.plan.domain.entity.VipPlan;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.core.utils.SpringContextHolder;
import com.sqx.common.redis.utils.RedisUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.redisson.api.RedissonClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VipPayServiceImplTest {

    private static final Long USER_ID = 10L;
    private static final Long PLAN_ID = 5L;
    private static final String ORDER_NO = "VIP20260716001";
    private static final BigDecimal AMOUNT = new BigDecimal("99.00");

    private static MockedStatic<SpringContextHolder> springContextHolderMock;

    @Mock
    private IVipPlanService iVipPlanService;
    @Mock
    private IUserService iUserService;

    @InjectMocks
    private VipPayServiceImpl service;

    @BeforeAll
    static void initRedisUtilDependencies() {
        springContextHolderMock = mockStatic(SpringContextHolder.class);
        springContextHolderMock.when(() -> SpringContextHolder.getBean(RedissonClient.class))
                .thenReturn(mock(RedissonClient.class));
    }

    @AfterAll
    static void closeSpringContextMock() {
        if (springContextHolderMock != null) {
            springContextHolderMock.close();
        }
    }

    private BusinessParam validParam() {
        BusinessParam param = new BusinessParam();
        param.setUserId(USER_ID);
        param.setOpenId("wx-open-id");
        param.setOrderNo(ORDER_NO);
        param.setAmount(AMOUNT);
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("planId", PLAN_ID);
        param.setExtensions(extensions);
        return param;
    }

    private VipPlan validPlan() {
        VipPlan plan = new VipPlan();
        plan.setId(PLAN_ID);
        plan.setAmount(AMOUNT);
        return plan;
    }

    private UserEntity normalUser() {
        UserEntity user = new UserEntity();
        user.setUserId(USER_ID);
        user.setIsVip(0);
        return user;
    }

    private void assertPreparePayMessage(BusinessParam param, String expected) {
        BizException ex = assertThrows(BizException.class, () -> service.preparePay(param));
        assertEquals(expected, ex.getMessage());
    }

    @Test
    void preparePayShouldValidateAllFailureBranches() {
        BusinessParam noUser = validParam();
        noUser.setUserId(null);
        assertPreparePayMessage(noUser, "请先登录");

        BusinessParam noOpenId = validParam();
        noOpenId.setOpenId("");
        assertPreparePayMessage(noOpenId, "请使用微信登录后再开通会员");

        BusinessParam noOrderNo = validParam();
        noOrderNo.setOrderNo(null);
        assertPreparePayMessage(noOrderNo, "订单号生成失败");

        BusinessParam noExtensions = validParam();
        noExtensions.setExtensions(null);
        assertPreparePayMessage(noExtensions, "请选择会员套餐");

        BusinessParam emptyExtensions = validParam();
        emptyExtensions.setExtensions(Collections.emptyMap());
        assertPreparePayMessage(emptyExtensions, "请选择会员套餐");

        BusinessParam blankPlanId = validParam();
        blankPlanId.setExtensions(Collections.singletonMap("planId", ""));
        assertPreparePayMessage(blankPlanId, "请选择会员套餐");

        BusinessParam badPlanId = validParam();
        badPlanId.setExtensions(Collections.singletonMap("planId", "bad-id"));
        assertPreparePayMessage(badPlanId, "会员套餐参数无效");

        BusinessParam missingPlan = validParam();
        when(iVipPlanService.getById(PLAN_ID)).thenReturn(null);
        assertPreparePayMessage(missingPlan, "会员套餐不存在");

        BusinessParam wrongAmount = validParam();
        when(iVipPlanService.getById(PLAN_ID)).thenReturn(validPlan());
        wrongAmount.setAmount(new BigDecimal("1.00"));
        assertPreparePayMessage(wrongAmount, "会员套餐金额不匹配");

        BusinessParam nullAmount = validParam();
        nullAmount.setAmount(null);
        assertPreparePayMessage(nullAmount, "会员套餐金额不匹配");

        BusinessParam missingUser = validParam();
        when(iVipPlanService.getById(PLAN_ID)).thenReturn(validPlan());
        when(iUserService.getById(USER_ID)).thenReturn(null);
        assertPreparePayMessage(missingUser, "用户不存在");

        BusinessParam alreadyVip = validParam();
        when(iVipPlanService.getById(PLAN_ID)).thenReturn(validPlan());
        UserEntity vipUser = normalUser();
        vipUser.setIsVip(1);
        when(iUserService.getById(USER_ID)).thenReturn(vipUser);
        assertPreparePayMessage(alreadyVip, "您已是会员，无需重复开通");
    }

    @Test
    void preparePayShouldCacheContextOnSuccess() {
        BusinessParam param = validParam();
        when(iVipPlanService.getById(PLAN_ID)).thenReturn(validPlan());
        when(iUserService.getById(USER_ID)).thenReturn(normalUser());

        try (MockedStatic<RedisUtil> redis = mockStatic(RedisUtil.class)) {
            service.preparePay(param);
            redis.verify(() -> RedisUtil.setCacheObject(
                    eq("vip:pay:" + ORDER_NO),
                    eq(USER_ID + ":" + PLAN_ID),
                    any(Duration.class)));
        }
    }

    @Test
    void activateVipOnPaySuccessShouldHandleEarlyReturns() {
        try (MockedStatic<RedisUtil> redis = mockStatic(RedisUtil.class)) {
            service.activateVipOnPaySuccess("");
            service.activateVipOnPaySuccess(null);
            redis.verify(() -> RedisUtil.hasKey(anyString()), never());

            service.activateVipOnPaySuccess(ORDER_NO);
            redis.verify(() -> RedisUtil.hasKey("vip:pay:" + ORDER_NO));

            redis.when(() -> RedisUtil.hasKey("vip:pay:" + ORDER_NO)).thenReturn(true);
            redis.when(() -> RedisUtil.getCacheObject("vip:pay:" + ORDER_NO)).thenReturn("");
            service.activateVipOnPaySuccess(ORDER_NO);
            verify(iUserService, never()).updateById(any(UserEntity.class));

            redis.when(() -> RedisUtil.getCacheObject("vip:pay:" + ORDER_NO)).thenReturn("invalid");
            service.activateVipOnPaySuccess(ORDER_NO);

            redis.when(() -> RedisUtil.getCacheObject("vip:pay:" + ORDER_NO)).thenReturn("999:" + PLAN_ID);
            when(iUserService.getById(999L)).thenReturn(null);
            service.activateVipOnPaySuccess(ORDER_NO);
            verify(iUserService, never()).updateById(any(UserEntity.class));
        }
    }

    @Test
    void activateVipOnPaySuccessShouldUpgradeUser() {
        when(iUserService.getById(USER_ID)).thenReturn(normalUser());
        when(iUserService.updateById(any(UserEntity.class))).thenReturn(true);

        try (MockedStatic<RedisUtil> redis = mockStatic(RedisUtil.class)) {
            redis.when(() -> RedisUtil.hasKey("vip:pay:" + ORDER_NO)).thenReturn(true);
            redis.when(() -> RedisUtil.getCacheObject("vip:pay:" + ORDER_NO)).thenReturn(USER_ID + ":" + PLAN_ID);

            service.activateVipOnPaySuccess(ORDER_NO);

            redis.verify(() -> RedisUtil.deleteObject("vip:pay:" + ORDER_NO));
            verify(iUserService).updateById(argThat(u -> USER_ID.equals(u.getUserId())
                    && Integer.valueOf(1).equals(u.getIsVip())
                    && Integer.valueOf(2).equals(u.getUserType())));
        }
    }
}
