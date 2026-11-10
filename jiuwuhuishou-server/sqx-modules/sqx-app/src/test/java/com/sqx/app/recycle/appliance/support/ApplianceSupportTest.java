package com.sqx.app.recycle.appliance.support;

import com.alibaba.fastjson2.JSONObject;
import com.sqx.common.redis.utils.ConfigUtil;
import com.sqx.testsupport.PrivateCtor;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class ApplianceSupportTest {

    @Test
    void heroFlowAndSpecDefaultsShouldLoad() {
        PrivateCtor.invoke(ApplianceHeroFlowDefaults.class);
        PrivateCtor.invoke(ApplianceSpecDefaults.class);
        assertEquals(4, ApplianceHeroFlowDefaults.defaultFlows().size());
        assertFalse(ApplianceSpecDefaults.parseFields(ApplianceSpecDefaults.getTemplate("空调")).isEmpty());
        assertEquals("冰箱估价参数", ApplianceSpecDefaults.getTemplate("未知").getString("title"));
        assertNotNull(ApplianceSpecDefaults.getTemplate("洗衣机"));
        assertNotNull(ApplianceSpecDefaults.getTemplate("电视"));
    }

    @Test
    void estimateRuleSupportShouldCoverConfigAndFallbacks() {
        PrivateCtor.invoke(ApplianceEstimateRuleSupport.class);

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("appliance_estimate_adjust_冰箱")))
                    .thenReturn("{\"minPrice\":80,\"rangeDelta\":50,\"reward\":9,\"dimensions\":{\"doorType\":{\"双门\":10}}}");
            JSONObject rules = ApplianceEstimateRuleSupport.resolveCategoryRules("冰箱");
            assertEquals(0, new BigDecimal("80").compareTo(ApplianceEstimateRuleSupport.resolveMinPrice(rules)));
            assertEquals(50, ApplianceEstimateRuleSupport.resolveRangeDelta(rules));
            assertEquals(9, ApplianceEstimateRuleSupport.resolveReward(rules));
            assertEquals(0, new BigDecimal("10").compareTo(
                    ApplianceEstimateRuleSupport.resolveAdjustAmount(rules, "doorType", "双门")));

            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenReturn(null);
            assertNotNull(ApplianceEstimateRuleSupport.resolveCategoryRules("空调"));
            assertNotNull(ApplianceEstimateRuleSupport.resolveCategoryRules("洗衣机"));
            assertNotNull(ApplianceEstimateRuleSupport.resolveCategoryRules("电视"));
            assertNotNull(ApplianceEstimateRuleSupport.resolveCategoryRules("未知品类"));
            assertNotNull(ApplianceEstimateRuleSupport.resolveCategoryRules(" "));

            config.when(() -> ConfigUtil.getConfigValueOrDefault(anyString())).thenThrow(new RuntimeException("x"));
            assertNotNull(ApplianceEstimateRuleSupport.resolveCategoryRules("冰箱"));
        }

        assertEquals(0, new BigDecimal("60").compareTo(ApplianceEstimateRuleSupport.resolveMinPrice(null)));
        assertEquals(120, ApplianceEstimateRuleSupport.resolveRangeDelta(null));
        assertEquals(0, ApplianceEstimateRuleSupport.resolveReward(null));
        assertEquals(0, BigDecimal.ZERO.compareTo(ApplianceEstimateRuleSupport.resolveAdjustAmount(null, "a", "b")));
        assertEquals(0, BigDecimal.ZERO.compareTo(
                ApplianceEstimateRuleSupport.resolveAdjustAmount(new JSONObject(), "a", "b")));
        JSONObject emptyDims = new JSONObject();
        emptyDims.put("dimensions", new JSONObject());
        assertEquals(0, BigDecimal.ZERO.compareTo(
                ApplianceEstimateRuleSupport.resolveAdjustAmount(emptyDims, "doorType", "双门")));
        JSONObject dimOnly = new JSONObject();
        JSONObject dims = new JSONObject();
        dims.put("doorType", new JSONObject());
        dimOnly.put("dimensions", dims);
        assertEquals(0, BigDecimal.ZERO.compareTo(
                ApplianceEstimateRuleSupport.resolveAdjustAmount(dimOnly, "doorType", "双门")));
        JSONObject noKeys = new JSONObject();
        assertEquals(0, new BigDecimal("60").compareTo(ApplianceEstimateRuleSupport.resolveMinPrice(noKeys)));
        assertEquals(120, ApplianceEstimateRuleSupport.resolveRangeDelta(noKeys));
        assertEquals(0, ApplianceEstimateRuleSupport.resolveReward(noKeys));
    }
}
