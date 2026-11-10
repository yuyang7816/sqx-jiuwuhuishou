package com.sqx.app.recycle.appliance.support;

import com.sqx.app.recycle.appliance.domain.vo.ApplianceHeroFlowVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 家电回收流程步骤默认值
 */
public final class ApplianceHeroFlowDefaults {

    private ApplianceHeroFlowDefaults() {
    }

    public static List<ApplianceHeroFlowVo> defaultFlows() {
        List<ApplianceHeroFlowVo> list = new ArrayList<>();
        list.add(build("线上预约", "edit-pen", 1));
        list.add(build("电话沟通", "phone", 2));
        list.add(build("上门回收", "car", 3));
        list.add(build("立即收钱", "rmb-circle", 4));
        return list;
    }

    private static ApplianceHeroFlowVo build(String name, String icon, int sort) {
        ApplianceHeroFlowVo vo = new ApplianceHeroFlowVo();
        vo.setName(name);
        vo.setIcon(icon);
        vo.setSort(sort);
        return vo;
    }
}
