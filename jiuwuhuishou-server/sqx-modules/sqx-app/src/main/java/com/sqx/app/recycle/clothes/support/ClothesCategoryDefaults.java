package com.sqx.app.recycle.clothes.support;

import com.sqx.app.recycle.clothes.domain.vo.ClothesCategoryVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 旧衣回收分类默认数据
 */
public final class ClothesCategoryDefaults {

    private ClothesCategoryDefaults() {
    }

    public static List<ClothesCategoryVo> defaultCategories() {
        List<ClothesCategoryVo> list = new ArrayList<>();
        list.add(build(1L, "上衣/裤子", "integral", 1));
        list.add(build(2L, "床单/毛毯", "home", 2));
        list.add(build(3L, "鞋帽/包包", "bag", 3));
        list.add(build(4L, "儿童旧衣", "gift", 4));
        return list;
    }

    private static ClothesCategoryVo build(Long id, String name, String icon, int sort) {
        ClothesCategoryVo vo = new ClothesCategoryVo();
        vo.setId(id);
        vo.setName(name);
        vo.setIcon(icon);
        vo.setSort(sort);
        vo.setState(1);
        return vo;
    }
}
