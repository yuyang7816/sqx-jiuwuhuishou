package com.sqx.app.admin.recycle.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.domain.bo.*;
import com.sqx.app.recycle.config.domain.entity.RecycleCategory;

public interface AdminRecycleCategoryManageService {

    void saveInfo(AdminRecycleCategorySaveBo saveBo);

    IPage<RecycleCategory> pageInfo(AdminRecycleCategoryPageBo pageBo);

    RecycleCategory info(AdminRecycleCategoryInfoBo infoBo);

    void updateInfo(AdminRecycleCategoryUpdateBo updateBo);

    void delete(AdminRecycleCategoryInfoBo infoBo);
}
