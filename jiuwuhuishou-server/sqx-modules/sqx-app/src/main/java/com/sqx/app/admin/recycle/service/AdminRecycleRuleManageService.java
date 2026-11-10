package com.sqx.app.admin.recycle.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.domain.bo.*;
import com.sqx.app.recycle.config.domain.entity.RecycleRule;

public interface AdminRecycleRuleManageService {

    void saveInfo(AdminRecycleRuleSaveBo saveBo);

    IPage<RecycleRule> pageInfo(AdminRecycleRulePageBo pageBo);

    RecycleRule info(AdminRecycleRuleInfoBo infoBo);

    void updateInfo(AdminRecycleRuleUpdateBo updateBo);

    void delete(AdminRecycleRuleInfoBo infoBo);
}
