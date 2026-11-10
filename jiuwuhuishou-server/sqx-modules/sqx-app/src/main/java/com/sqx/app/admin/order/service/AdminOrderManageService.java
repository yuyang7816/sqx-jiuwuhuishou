package com.sqx.app.admin.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.order.domain.bo.AdminOrderAssignPickupBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderCancelBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderCompleteBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderConfirmMailReceiveBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderConfirmPayoutBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderHandleBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderInfoBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderPageBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderUpdateEstimateBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderUpdateExpressBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderUpdateStatusBo;
import com.sqx.app.admin.order.domain.vo.AdminOrderDetailVo;
import com.sqx.app.admin.order.domain.vo.AdminOrderVo;
import com.sqx.app.order.domain.vo.ExpressTrackVo;

import java.util.List;

public interface AdminOrderManageService {

    IPage<AdminOrderVo> pageInfo(AdminOrderPageBo pageBo);

    AdminOrderDetailVo info(AdminOrderInfoBo infoBo);

    void cancel(AdminOrderCancelBo cancelBo);

    void updateStatus(AdminOrderUpdateStatusBo updateBo);

    void assignPickup(AdminOrderAssignPickupBo assignBo);

    void updateEstimate(AdminOrderUpdateEstimateBo updateBo);

    void confirmPayout(AdminOrderConfirmPayoutBo confirmBo);

    void complete(AdminOrderCompleteBo completeBo);

    void handle(AdminOrderHandleBo handleBo);

    /**
     * 修改邮寄订单快递单号
     */
    void updateExpressNo(AdminOrderUpdateExpressBo updateBo);

    /**
     * 管理端确认邮寄订单收货，订单进入待确认
     */
    void confirmMailReceive(AdminOrderConfirmMailReceiveBo confirmBo);

    /**
     * 查询邮寄订单顺丰物流轨迹
     */
    List<ExpressTrackVo> listExpressTracks(AdminOrderInfoBo infoBo);
}
