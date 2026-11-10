package com.sqx.app.base.pay.business.service.impl;

import com.sqx.app.base.pay.business.service.VipPayService;
import com.sqx.app.base.pay.domain.BusinessParam;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.vip.plan.db.service.IVipPlanService;
import com.sqx.app.vip.plan.domain.entity.VipPlan;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.redis.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * VIP 会员支付业务实现
 */
@Slf4j
@Service
public class VipPayServiceImpl implements VipPayService {

    private static final String VIP_PAY_CACHE_PREFIX = "vip:pay:";
    private static final Duration VIP_PAY_CACHE_TTL = Duration.ofHours(2);

    @Resource
    private IVipPlanService iVipPlanService;
    @Resource
    private IUserService iUserService;

    @Override
    public void preparePay(BusinessParam param) {
        if (Objects.isNull(param.getUserId())) {
            throw new BizException("请先登录");
        }
        if (StringUtils.isBlank(param.getOpenId())) {
            throw new BizException("请使用微信登录后再开通会员");
        }
        if (StringUtils.isBlank(param.getOrderNo())) {
            throw new BizException("订单号生成失败");
        }

        Long planId = resolvePlanId(param.getExtensions());
        VipPlan vipPlan = iVipPlanService.getById(planId);
        if (Objects.isNull(vipPlan)) {
            throw new BizException("会员套餐不存在");
        }

        BigDecimal requestAmount = param.getAmount();
        if (Objects.isNull(requestAmount) || vipPlan.getAmount().compareTo(requestAmount) != 0) {
            throw new BizException("会员套餐金额不匹配");
        }

        UserEntity user = iUserService.getById(param.getUserId());
        if (Objects.isNull(user)) {
            throw new BizException("用户不存在");
        }
        if (Objects.equals(user.getIsVip(), 1)) {
            throw new BizException("您已是会员，无需重复开通");
        }

        String cacheKey = buildCacheKey(param.getOrderNo());
        RedisUtil.setCacheObject(cacheKey, param.getUserId() + ":" + planId, VIP_PAY_CACHE_TTL);
        log.info("VIP 支付上下文已缓存，orderNo={}, userId={}, planId={}", param.getOrderNo(), param.getUserId(), planId);
    }

    @Override
    public void activateVipOnPaySuccess(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return;
        }

        String cacheKey = buildCacheKey(orderNo);
        if (!RedisUtil.hasKey(cacheKey)) {
            log.warn("VIP 支付回调未找到订单上下文，orderNo={}", orderNo);
            return;
        }

        String cacheValue = RedisUtil.getCacheObject(cacheKey);
        RedisUtil.deleteObject(cacheKey);
        if (StringUtils.isBlank(cacheValue) || !cacheValue.contains(":")) {
            log.warn("VIP 支付回调上下文无效，orderNo={}, value={}", orderNo, cacheValue);
            return;
        }

        String[] parts = cacheValue.split(":");
        Long userId = Long.valueOf(parts[0]);
        Long planId = Long.valueOf(parts[1]);

        UserEntity user = iUserService.getById(userId);
        if (Objects.isNull(user)) {
            log.warn("VIP 支付回调用户不存在，userId={}, orderNo={}", userId, orderNo);
            return;
        }

        UserEntity update = new UserEntity();
        update.setUserId(userId);
        update.setIsVip(1);
        update.setUserType(2);
        iUserService.updateById(update);
        log.info("VIP 会员开通成功，userId={}, planId={}, orderNo={}", userId, planId, orderNo);
    }

    private Long resolvePlanId(Map<String, Object> extensions) {
        if (Objects.isNull(extensions) || extensions.isEmpty()) {
            throw new BizException("请选择会员套餐");
        }

        Object planIdValue = extensions.get("planId");
        if (Objects.isNull(planIdValue) || StringUtils.isBlank(String.valueOf(planIdValue))) {
            throw new BizException("请选择会员套餐");
        }

        try {
            return Long.valueOf(String.valueOf(planIdValue));
        } catch (NumberFormatException error) {
            throw new BizException("会员套餐参数无效");
        }
    }

    private String buildCacheKey(String orderNo) {
        return VIP_PAY_CACHE_PREFIX + orderNo;
    }
}
