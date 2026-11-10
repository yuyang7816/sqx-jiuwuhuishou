package com.sqx.app.admin.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.address.domain.vo.UserAddressVo;
import com.sqx.app.admin.user.domain.bo.AdminUserAddressListBo;
import com.sqx.app.admin.user.domain.bo.AdminUserSignRecordPageBo;
import com.sqx.app.admin.user.domain.bo.AdminUserWelfareInfoBo;
import com.sqx.app.admin.user.domain.bo.AdminUserWelfareUpdateCoinBo;
import com.sqx.app.welfare.domain.entity.UserSignRecord;
import com.sqx.app.welfare.domain.vo.WelfareSummaryVo;

import java.util.List;

public interface AdminUserDetailService {

    List<UserAddressVo> listAddresses(AdminUserAddressListBo listBo);

    WelfareSummaryVo welfareInfo(AdminUserWelfareInfoBo infoBo);

    void updateWelfareCoin(AdminUserWelfareUpdateCoinBo updateBo);

    IPage<UserSignRecord> signRecordPage(AdminUserSignRecordPageBo pageBo);
}
