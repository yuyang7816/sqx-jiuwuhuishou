package com.sqx.app.admin.orderflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementConfirmBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementPageBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementSaveBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminSettlementVo;

public interface AdminSettlementManageService {

    IPage<AdminSettlementVo> pageInfo(AdminSettlementPageBo pageBo);

    AdminSettlementVo info(AdminSettlementInfoBo infoBo);

    void save(AdminSettlementSaveBo saveBo);

    void confirm(AdminSettlementConfirmBo confirmBo);
}
