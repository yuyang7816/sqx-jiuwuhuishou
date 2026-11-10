package com.sqx.app.admin.ops.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.ops.domain.bo.AdminWelfareUserPageBo;
import com.sqx.app.admin.ops.domain.vo.AdminWelfareUserVo;

public interface AdminWelfareUserManageService {

    IPage<AdminWelfareUserVo> pageInfo(AdminWelfareUserPageBo pageBo);
}
