package com.sqx.app.admin.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.user.domain.bo.AdminUserAddressPageBo;
import com.sqx.app.admin.user.domain.vo.AdminUserAddressVo;

public interface AdminUserAddressManageService {

    IPage<AdminUserAddressVo> pageInfo(AdminUserAddressPageBo pageBo);
}
