package com.sqx.app.admin.recycle.appliance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.appliance.domain.bo.*;
import com.sqx.app.admin.recycle.appliance.domain.vo.AdminApplianceModelVo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceCategory;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceHeroFlow;

public interface AdminApplianceManageService {

    void saveCategory(AdminApplianceCategorySaveBo saveBo);

    IPage<ApplianceCategory> pageCategory(AdminApplianceCategoryPageBo pageBo);

    ApplianceCategory infoCategory(AdminRecycleIdBo infoBo);

    void updateCategory(AdminApplianceCategoryUpdateBo updateBo);

    void deleteCategory(AdminRecycleIdBo infoBo);

    void saveModel(AdminApplianceModelSaveBo saveBo);

    IPage<AdminApplianceModelVo> pageModel(AdminApplianceModelPageBo pageBo);

    AdminApplianceModelVo infoModel(AdminRecycleIdBo infoBo);

    void updateModel(AdminApplianceModelUpdateBo updateBo);

    void deleteModel(AdminRecycleIdBo infoBo);

    void saveHeroFlow(AdminApplianceHeroFlowSaveBo saveBo);

    IPage<ApplianceHeroFlow> pageHeroFlow(AdminApplianceHeroFlowPageBo pageBo);

    ApplianceHeroFlow infoHeroFlow(AdminRecycleIdBo infoBo);

    void updateHeroFlow(AdminApplianceHeroFlowUpdateBo updateBo);

    void deleteHeroFlow(AdminRecycleIdBo infoBo);
}
