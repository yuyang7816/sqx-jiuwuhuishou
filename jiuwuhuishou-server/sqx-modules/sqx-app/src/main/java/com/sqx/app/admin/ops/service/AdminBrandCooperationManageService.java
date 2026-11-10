package com.sqx.app.admin.ops.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.ops.domain.bo.*;
import com.sqx.app.brand.domain.entity.BrandCooperation;

public interface AdminBrandCooperationManageService {

    IPage<BrandCooperation> pageInfo(AdminBrandCooperationPageBo pageBo);

    BrandCooperation info(AdminBrandCooperationInfoBo infoBo);

    void updateStatus(AdminBrandCooperationUpdateStatusBo updateBo);
}
