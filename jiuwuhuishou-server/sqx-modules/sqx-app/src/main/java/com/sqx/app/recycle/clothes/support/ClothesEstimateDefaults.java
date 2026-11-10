package com.sqx.app.recycle.clothes.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.sqx.app.recycle.clothes.domain.vo.ClothesWeightOptionVo;
import com.sqx.common.redis.utils.ConfigUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 旧衣预估重量配置（可通过 sys_config.clothes_weight_options 覆盖）
 */
public final class ClothesEstimateDefaults {

    private static final String CONFIG_KEY = "clothes_weight_options";

    private ClothesEstimateDefaults() {
    }

    public static List<ClothesWeightOptionVo> resolveWeightOptions() {
        try {
            String configValue = ConfigUtil.getConfigValueOrDefault(CONFIG_KEY);
            if (StringUtils.isNotBlank(configValue)) {
                List<ClothesWeightOptionVo> options = parseOptions(configValue);
                if (!options.isEmpty()) {
                    return options;
                }
            }
        } catch (Exception ignored) {
            // 使用内置默认配置
        }
        return defaultWeightOptions();
    }

    private static List<ClothesWeightOptionVo> parseOptions(String configValue) {
        JSONArray array = JSON.parseArray(configValue);
        if (array == null || array.isEmpty()) {
            return Collections.emptyList();
        }
        List<ClothesWeightOptionVo> options = new ArrayList<>();
        for (int index = 0; index < array.size(); index++) {
            JSONObject item = array.getJSONObject(index);
            if (item == null) {
                continue;
            }
            String title = item.getString("title");
            if (StringUtils.isBlank(title)) {
                continue;
            }
            ClothesWeightOptionVo option = new ClothesWeightOptionVo();
            option.setTitle(title.trim());
            option.setDesc(StringUtils.defaultString(item.getString("desc")));
            BigDecimal estimateAmount = item.getBigDecimal("estimateAmount");
            option.setEstimateAmount(estimateAmount == null ? BigDecimal.ZERO : estimateAmount);
            Integer sort = item.getInteger("sort");
            option.setSort(sort == null ? index + 1 : sort);
            options.add(option);
        }
        options.sort(Comparator.comparing(ClothesWeightOptionVo::getSort, Comparator.nullsLast(Integer::compareTo)));
        return options;
    }

    private static List<ClothesWeightOptionVo> defaultWeightOptions() {
        List<ClothesWeightOptionVo> options = new ArrayList<>();
        options.add(buildOption("5-20kg", "日常清理", new BigDecimal("18.50"), 1));
        options.add(buildOption("20-50kg", "换季储物", new BigDecimal("36.00"), 2));
        options.add(buildOption(">50kg", "搬家回收", new BigDecimal("58.00"), 3));
        return options;
    }

    private static ClothesWeightOptionVo buildOption(String title, String desc, BigDecimal estimateAmount, int sort) {
        ClothesWeightOptionVo option = new ClothesWeightOptionVo();
        option.setTitle(title);
        option.setDesc(desc);
        option.setEstimateAmount(estimateAmount);
        option.setSort(sort);
        return option;
    }
}
