package com.sqx.app.admin.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.order.domain.bo.AdminOrderExceptionHandleBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderExceptionInfoBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderExceptionPageBo;
import com.sqx.app.admin.order.domain.vo.AdminOrderExceptionVo;

import java.util.List;

public interface AdminOrderExceptionManageService {

    IPage<AdminOrderExceptionVo> pageInfo(AdminOrderExceptionPageBo pageBo);

    AdminOrderExceptionVo info(AdminOrderExceptionInfoBo infoBo);

    void handle(AdminOrderExceptionHandleBo handleBo);

    List<AdminOrderExceptionVo> listByOrderId(Long orderId);
}
