package com.sqx.app.admin.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.order.domain.bo.AdminOrderRescheduleInfoBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderReschedulePageBo;
import com.sqx.app.admin.order.domain.vo.AdminOrderRescheduleVo;

import java.util.List;

public interface AdminOrderRescheduleManageService {

    IPage<AdminOrderRescheduleVo> pageInfo(AdminOrderReschedulePageBo pageBo);

    AdminOrderRescheduleVo info(AdminOrderRescheduleInfoBo infoBo);

    List<AdminOrderRescheduleVo> listByOrderId(Long orderId);
}
