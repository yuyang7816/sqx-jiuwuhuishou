package com.sqx.app.admin.datacenter.service;

import com.sqx.app.admin.datacenter.domain.bo.AdminDatacenterOverviewBo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterBreakdownVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterGrowthVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterOverviewVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterTrendVo;

public interface AdminDatacenterService {

    AdminDatacenterOverviewVo overview(AdminDatacenterOverviewBo bo);

    AdminDatacenterTrendVo trend(AdminDatacenterOverviewBo bo);

    AdminDatacenterBreakdownVo breakdown(AdminDatacenterOverviewBo bo);

    AdminDatacenterGrowthVo growth(AdminDatacenterOverviewBo bo);
}
