package com.sqx.app.admin.orderflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.orderflow.domain.bo.AdminDisputeInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminDisputePageBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminDisputeResolveBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminDisputeVo;

public interface AdminDisputeManageService {

    IPage<AdminDisputeVo> pageInfo(AdminDisputePageBo pageBo);

    AdminDisputeVo info(AdminDisputeInfoBo infoBo);

    void resolve(AdminDisputeResolveBo resolveBo);
}
