package com.sqx.app.admin.orderflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutConfirmBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutCreateBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutPageBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminPayoutVo;

public interface AdminPayoutManageService {

    IPage<AdminPayoutVo> pageInfo(AdminPayoutPageBo pageBo);

    AdminPayoutVo info(AdminPayoutInfoBo infoBo);

    void create(AdminPayoutCreateBo createBo);

    void confirm(AdminPayoutConfirmBo confirmBo);
}
