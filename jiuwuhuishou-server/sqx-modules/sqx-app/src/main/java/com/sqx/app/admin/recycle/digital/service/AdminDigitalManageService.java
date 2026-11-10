package com.sqx.app.admin.recycle.digital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.digital.domain.bo.*;
import com.sqx.app.admin.recycle.digital.domain.vo.AdminDigitalModelVo;
import com.sqx.app.admin.recycle.digital.domain.vo.AdminDigitalPricingVo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.digital.domain.entity.DigitalBrand;

public interface AdminDigitalManageService {

    void saveBrand(AdminDigitalBrandSaveBo saveBo);

    IPage<DigitalBrand> pageBrand(AdminDigitalBrandPageBo pageBo);

    DigitalBrand infoBrand(AdminRecycleIdBo infoBo);

    void updateBrand(AdminDigitalBrandUpdateBo updateBo);

    void deleteBrand(AdminRecycleIdBo infoBo);

    void saveModel(AdminDigitalModelSaveBo saveBo);

    IPage<AdminDigitalModelVo> pageModel(AdminDigitalModelPageBo pageBo);

    AdminDigitalModelVo infoModel(AdminRecycleIdBo infoBo);

    void updateModel(AdminDigitalModelUpdateBo updateBo);

    void deleteModel(AdminRecycleIdBo infoBo);

    void savePricing(AdminDigitalPricingSaveBo saveBo);

    IPage<AdminDigitalPricingVo> pagePricing(AdminDigitalPricingPageBo pageBo);

    AdminDigitalPricingVo infoPricing(AdminRecycleIdBo infoBo);

    void updatePricing(AdminDigitalPricingUpdateBo updateBo);

    void deletePricing(AdminRecycleIdBo infoBo);
}
