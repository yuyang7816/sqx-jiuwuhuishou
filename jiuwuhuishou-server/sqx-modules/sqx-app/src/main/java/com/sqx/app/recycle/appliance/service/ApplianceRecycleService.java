package com.sqx.app.recycle.appliance.service;

import com.sqx.app.recycle.appliance.domain.bo.ApplianceEstimateBo;
import com.sqx.app.recycle.appliance.domain.bo.ApplianceModelListBo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceCategoryOverviewVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceCategoryVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceEstimateVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceHeroFlowVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceModelVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceSpecPageVo;

import java.util.List;

/**
 * 家电回收业务服务
 */
public interface ApplianceRecycleService {

    ApplianceSpecPageVo getSpecPage(String categoryName);

    List<ApplianceCategoryOverviewVo> listCategoryOverview();

    List<ApplianceCategoryVo> listCategories();

    List<ApplianceModelVo> listModels(Long categoryId);

    List<ApplianceHeroFlowVo> listHeroFlows();

    ApplianceEstimateVo estimate(Long modelId, String condition);

    ApplianceEstimateVo estimate(ApplianceEstimateBo estimateBo);
}
