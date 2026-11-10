package com.sqx.app.admin.ops.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.ops.domain.bo.*;
import com.sqx.app.help.domain.entity.HelpCategory;

public interface AdminHelpCategoryManageService {

    void saveInfo(AdminHelpCategorySaveBo saveBo);

    IPage<HelpCategory> pageInfo(AdminHelpCategoryPageBo pageBo);

    HelpCategory info(AdminHelpCategoryInfoBo infoBo);

    void updateInfo(AdminHelpCategoryUpdateBo updateBo);

    void delete(AdminHelpCategoryInfoBo infoBo);
}
