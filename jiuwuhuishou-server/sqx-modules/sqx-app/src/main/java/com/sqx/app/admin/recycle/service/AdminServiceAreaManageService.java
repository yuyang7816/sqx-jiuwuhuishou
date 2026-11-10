package com.sqx.app.admin.recycle.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.domain.bo.*;
import com.sqx.app.recycle.config.domain.entity.ServiceArea;

public interface AdminServiceAreaManageService {

    void saveInfo(AdminServiceAreaSaveBo saveBo);

    IPage<ServiceArea> pageInfo(AdminServiceAreaPageBo pageBo);

    ServiceArea info(AdminServiceAreaInfoBo infoBo);

    void updateInfo(AdminServiceAreaUpdateBo updateBo);

    void delete(AdminServiceAreaInfoBo infoBo);
}
