package com.sqx.app.recycle.appliance.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sqx.common.redis.utils.ConfigUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 家电规格估价规则（品类维度固定加减价，可通过 sys_config 覆盖）
 */
public final class ApplianceEstimateRuleSupport {

    private static final String CONFIG_PREFIX = "appliance_estimate_adjust_";
    private static final BigDecimal DEFAULT_MIN_PRICE = new BigDecimal("60");
    private static final int DEFAULT_RANGE_DELTA = 120;

    private ApplianceEstimateRuleSupport() {
    }

    public static JSONObject resolveCategoryRules(String categoryName) {
        if (StringUtils.isNotBlank(categoryName)) {
            try {
                String configValue = ConfigUtil.getConfigValueOrDefault(CONFIG_PREFIX + categoryName);
                if (StringUtils.isNotBlank(configValue)) {
                    return JSON.parseObject(configValue);
                }
            } catch (Exception ignored) {
                // 使用内置默认规则
            }
        }
        JSONObject defaults = DEFAULT_CATEGORY_RULES.get(categoryName);
        return defaults == null ? DEFAULT_CATEGORY_RULES.get("冰箱") : defaults;
    }

    public static BigDecimal resolveMinPrice(JSONObject categoryRules) {
        if (categoryRules == null) {
            return DEFAULT_MIN_PRICE;
        }
        BigDecimal minPrice = categoryRules.getBigDecimal("minPrice");
        return minPrice == null ? DEFAULT_MIN_PRICE : minPrice;
    }

    public static int resolveRangeDelta(JSONObject categoryRules) {
        if (categoryRules == null) {
            return DEFAULT_RANGE_DELTA;
        }
        Integer delta = categoryRules.getInteger("rangeDelta");
        return delta == null ? DEFAULT_RANGE_DELTA : delta;
    }

    public static int resolveReward(JSONObject categoryRules) {
        if (categoryRules == null) {
            return 0;
        }
        Integer reward = categoryRules.getInteger("reward");
        return reward == null ? 0 : reward;
    }

    public static BigDecimal resolveAdjustAmount(JSONObject categoryRules, String dimension, String value) {
        if (categoryRules == null || StringUtils.isAnyBlank(dimension, value)) {
            return BigDecimal.ZERO;
        }
        JSONObject dimensions = categoryRules.getJSONObject("dimensions");
        if (dimensions == null) {
            return BigDecimal.ZERO;
        }
        JSONObject dimensionRules = dimensions.getJSONObject(dimension);
        if (dimensionRules == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal amount = dimensionRules.getBigDecimal(value);
        if (amount != null) {
            return amount;
        }
        // 多选参数：逗号分隔多个取值时分别累加
        if (!value.contains(",")) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (String part : value.split(",")) {
            if (StringUtils.isBlank(part)) {
                continue;
            }
            BigDecimal partAmount = dimensionRules.getBigDecimal(part.trim());
            if (partAmount != null) {
                sum = sum.add(partAmount);
            }
        }
        return sum;
    }

    private static JSONObject buildRule(int reward, Map<String, Map<String, Number>> dimensions) {
        JSONObject rule = new JSONObject();
        rule.put("minPrice", 60);
        rule.put("rangeDelta", 120);
        rule.put("reward", reward);
        JSONObject dimensionJson = new JSONObject();
        dimensions.forEach((key, values) -> {
            JSONObject valueJson = new JSONObject();
            values.forEach(valueJson::put);
            dimensionJson.put(key, valueJson);
        });
        rule.put("dimensions", dimensionJson);
        return rule;
    }

    private static Map<String, Number> values(Object... pairs) {
        Map<String, Number> map = new LinkedHashMap<>();
        for (int index = 0; index + 1 < pairs.length; index += 2) {
            map.put(String.valueOf(pairs[index]), (Number) pairs[index + 1]);
        }
        return map;
    }

    private static Map<String, JSONObject> buildDefaultCategoryRules() {
        Map<String, JSONObject> rules = new HashMap<>();

        Map<String, Number> commonCondition = values(
                "九成新", 180, "良好", 110, "正常老化", 40, "成色一般", -40, "外观磨损明显", -80
        );
        Map<String, Number> commonAge = values(
                "1年内", 180, "1-3年", 110, "3-5年", 40, "5年以上", -90
        );

        Map<String, Map<String, Number>> airDimensions = new LinkedHashMap<>();
        airDimensions.put("condition", commonCondition);
        airDimensions.put("age", commonAge);
        airDimensions.put("damage", values("无明显故障", 120, "轻微磕碰", 30, "制冷效果一般", -60, "无法制冷", -180, "缺遥控 / 配件", -30));
        airDimensions.put("type", values("挂机", 40, "柜机", 120, "中央空调内机", 80, "中央空调外机", 150));
        airDimensions.put("capacity", values("1匹", 20, "1.5匹", 60, "2匹", 110, "3匹及以上", 180));
        rules.put("空调", buildRule(48, airDimensions));

        Map<String, Map<String, Number>> fridgeDimensions = new LinkedHashMap<>();
        fridgeDimensions.put("condition", commonCondition);
        fridgeDimensions.put("age", commonAge);
        fridgeDimensions.put("damage", values("无明显损伤", 120, "轻微划痕", 40, "门封老化", -40, "制冷异常", -120, "压缩机异响", -150));
        fridgeDimensions.put("doorType", values("双门", 30, "三门", 60, "十字对开门", 130, "多门法式", 160));
        fridgeDimensions.put("capacity", values("200L以下", 10, "200-300L", 50, "300-400L", 90, "400L以上", 150));
        rules.put("冰箱", buildRule(66, fridgeDimensions));

        Map<String, Map<String, Number>> washerDimensions = new LinkedHashMap<>();
        washerDimensions.put("condition", commonCondition);
        washerDimensions.put("age", commonAge);
        washerDimensions.put("damage", values("无明显问题", 120, "轻微磕碰", 30, "排水异常", -80, "电机噪音大", -90, "无法启动", -180));
        washerDimensions.put("type", values("滚筒", 90, "波轮", 30, "洗烘一体", 130, "迷你洗衣机", -40));
        washerDimensions.put("capacity", values("6kg以下", -20, "6-8kg", 20, "8-10kg", 70, "10kg以上", 120));
        rules.put("洗衣机", buildRule(42, washerDimensions));

        Map<String, Map<String, Number>> tvDimensions = new LinkedHashMap<>();
        tvDimensions.put("condition", commonCondition);
        tvDimensions.put("age", commonAge);
        tvDimensions.put("damage", values("无明显故障", 120, "边框磕碰", 10, "屏幕轻微老化", -60, "显示异常", -140, "碎屏 / 黑屏", -220));
        tvDimensions.put("screenType", values("液晶 LED", 40, "OLED", 180, "Mini LED", 160, "老式等离子", -50));
        tvDimensions.put("size", values("43寸以下", -20, "43-55寸", 40, "55-65寸", 90, "65寸以上", 150));
        rules.put("电视", buildRule(36, tvDimensions));

        return Collections.unmodifiableMap(rules);
    }

    private static final Map<String, JSONObject> DEFAULT_CATEGORY_RULES = buildDefaultCategoryRules();
}
