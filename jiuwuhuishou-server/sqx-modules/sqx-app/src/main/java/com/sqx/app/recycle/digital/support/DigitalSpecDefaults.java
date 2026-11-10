package com.sqx.app.recycle.digital.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sqx.app.recycle.digital.domain.vo.DigitalSpecSectionVo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 数码/奢侈品规格页默认配置
 */
public final class DigitalSpecDefaults {

    private static final String SCENE_DIGITAL = "digital";
    private static final String SCENE_LUXURY = "luxury";

    private static final Map<String, String> TEMPLATES;

    private DigitalSpecDefaults() {
    }

    static {
        Map<String, String> map = new java.util.HashMap<>();
        map.put(SCENE_DIGITAL, "{\"sections\":[{\"key\":\"storage\",\"label\":\"容量版本\",\"options\":[\"128G\",\"256G\",\"512G\",\"1TB\"],\"layout\":\"grid\"},{\"key\":\"color\",\"label\":\"机身颜色\",\"options\":[\"深空黑\",\"银色\",\"蓝色\",\"紫色\"],\"layout\":\"grid\"},{\"key\":\"damage\",\"label\":\"损伤情况\",\"options\":[\"无明显故障\",\"轻微磕碰\",\"屏幕细纹\",\"电池损耗明显\",\"无法开机\"],\"layout\":\"grid\"},{\"key\":\"condition\",\"label\":\"新旧程度\",\"options\":[\"九成新\",\"良好\",\"正常老化\",\"成色一般\"],\"layout\":\"column\"}]}");
        map.put(SCENE_LUXURY, "{\"sections\":[{\"key\":\"series\",\"label\":\"款式系列\",\"options\":[\"经典款\",\"热门款\",\"限定款\",\"常规款\"],\"layout\":\"grid\"},{\"key\":\"material\",\"label\":\"材质\",\"options\":[\"牛皮\",\"帆布\",\"羊皮\",\"拼接材质\"],\"layout\":\"grid\"},{\"key\":\"damage\",\"label\":\"损伤情况\",\"options\":[\"无明显瑕疵\",\"轻微磨损\",\"五金划痕\",\"边角磨损\",\"需专业修复\"],\"layout\":\"grid\"},{\"key\":\"condition\",\"label\":\"新旧程度\",\"options\":[\"成色优秀\",\"成色良好\",\"正常使用痕迹\",\"成色一般\"],\"layout\":\"column\"}]}");
        TEMPLATES = Collections.unmodifiableMap(map);
    }

    public static JSONObject getTemplate(String scene) {
        String normalizedScene = SCENE_LUXURY.equalsIgnoreCase(scene) ? SCENE_LUXURY : SCENE_DIGITAL;
        return JSON.parseObject(TEMPLATES.get(normalizedScene));
    }

    public static List<DigitalSpecSectionVo> parseSections(JSONObject template) {
        return template.getList("sections", DigitalSpecSectionVo.class);
    }

    public static String resolveSceneLabel(String scene) {
        return SCENE_LUXURY.equalsIgnoreCase(scene) ? "奢侈品回收" : "数码回收";
    }
}
