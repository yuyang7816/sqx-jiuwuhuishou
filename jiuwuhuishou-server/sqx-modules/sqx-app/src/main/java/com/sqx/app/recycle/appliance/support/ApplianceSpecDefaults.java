package com.sqx.app.recycle.appliance.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceSpecFieldVo;

import java.util.Collections;
import java.util.List;

/**
 * 家电估价页默认配置
 */
public final class ApplianceSpecDefaults {

    private ApplianceSpecDefaults() {
    }

    public static JSONObject getTemplate(String categoryName) {
        String json = TEMPLATES.getOrDefault(categoryName, TEMPLATES.get("冰箱"));
        return JSON.parseObject(json);
    }

    private static final java.util.Map<String, String> TEMPLATES;

    static {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("空调", "{\"title\":\"空调估价参数\",\"subtitle\":\"选择品牌、型号和使用情况，系统将实时生成预估回收价。\",\"icon\":\"star\",\"cardLabel\":\"高频回收家电\",\"reward\":48,\"fields\":[{\"key\":\"type\",\"label\":\"空调类型\",\"options\":[\"挂机\",\"柜机\",\"中央空调内机\",\"中央空调外机\"]},{\"key\":\"capacity\",\"label\":\"匹数\",\"options\":[\"1匹\",\"1.5匹\",\"2匹\",\"3匹及以上\"]}],\"damageOptions\":[\"无明显故障\",\"轻微磕碰\",\"制冷效果一般\",\"无法制冷\",\"缺遥控 / 配件\"],\"conditionOptions\":[\"九成新\",\"良好\",\"正常老化\",\"成色一般\"],\"ageOptions\":[\"1年内\",\"1-3年\",\"3-5年\",\"5年以上\"]}");
        map.put("冰箱", "{\"title\":\"冰箱估价参数\",\"subtitle\":\"不同门型、容积和外观成色会直接影响最终回收报价。\",\"icon\":\"home\",\"cardLabel\":\"大家电专区\",\"reward\":66,\"fields\":[{\"key\":\"doorType\",\"label\":\"门型\",\"options\":[\"双门\",\"三门\",\"十字对开门\",\"多门法式\"]},{\"key\":\"capacity\",\"label\":\"容积\",\"options\":[\"200L以下\",\"200-300L\",\"300-400L\",\"400L以上\"]}],\"damageOptions\":[\"无明显损伤\",\"轻微划痕\",\"门封老化\",\"制冷异常\",\"压缩机异响\"],\"conditionOptions\":[\"九成新\",\"良好\",\"正常老化\",\"外观磨损明显\"],\"ageOptions\":[\"1年内\",\"1-3年\",\"3-5年\",\"5年以上\"]}");
        map.put("洗衣机", "{\"title\":\"洗衣机估价参数\",\"subtitle\":\"滚筒、波轮和容量不同，回收价格区间会有明显差异。\",\"icon\":\"grid\",\"cardLabel\":\"家电焕新\",\"reward\":42,\"fields\":[{\"key\":\"type\",\"label\":\"机型\",\"options\":[\"滚筒\",\"波轮\",\"洗烘一体\",\"迷你洗衣机\"]},{\"key\":\"capacity\",\"label\":\"容量\",\"options\":[\"6kg以下\",\"6-8kg\",\"8-10kg\",\"10kg以上\"]}],\"damageOptions\":[\"无明显问题\",\"轻微磕碰\",\"排水异常\",\"电机噪音大\",\"无法启动\"],\"conditionOptions\":[\"九成新\",\"良好\",\"正常老化\",\"成色一般\"],\"ageOptions\":[\"1年内\",\"1-3年\",\"3-5年\",\"5年以上\"]}");
        map.put("电视", "{\"title\":\"电视估价参数\",\"subtitle\":\"屏幕尺寸、显示状态和品牌溢价会影响回收价格。\",\"icon\":\"photo\",\"cardLabel\":\"智能家电\",\"reward\":36,\"fields\":[{\"key\":\"screenType\",\"label\":\"屏幕类型\",\"options\":[\"液晶 LED\",\"OLED\",\"Mini LED\",\"老式等离子\"]},{\"key\":\"size\",\"label\":\"尺寸\",\"options\":[\"43寸以下\",\"43-55寸\",\"55-65寸\",\"65寸以上\"]}],\"damageOptions\":[\"无明显故障\",\"边框磕碰\",\"屏幕轻微老化\",\"显示异常\",\"碎屏 / 黑屏\"],\"conditionOptions\":[\"九成新\",\"良好\",\"正常老化\",\"成色一般\"],\"ageOptions\":[\"1年内\",\"1-3年\",\"3-5年\",\"5年以上\"]}");
        TEMPLATES = Collections.unmodifiableMap(map);
    }

    public static List<ApplianceSpecFieldVo> parseFields(JSONObject template) {
        return template.getList("fields", ApplianceSpecFieldVo.class);
    }
}
