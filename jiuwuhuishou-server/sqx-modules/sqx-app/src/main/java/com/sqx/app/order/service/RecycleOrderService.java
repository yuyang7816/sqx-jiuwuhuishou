package com.sqx.app.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.order.domain.bo.*;
import com.sqx.app.order.domain.vo.ExpressTrackVo;
import com.sqx.app.order.domain.vo.OrderCourierVo;
import com.sqx.app.order.domain.vo.RecycleOrderDetailVo;
import com.sqx.app.order.domain.vo.RecycleOrderVo;

import java.util.List;

/**
 * 回收订单业务服务
 */
public interface RecycleOrderService {

    Long createClothesOrder(Long userId, ClothesOrderSaveBo saveBo);

    Long createApplianceOrder(Long userId, ApplianceOrderSaveBo saveBo);

    Long createDigitalOrder(Long userId, DigitalOrderSaveBo saveBo);

    IPage<RecycleOrderVo> pageInfo(Long userId, RecycleOrderPageBo pageBo);

    RecycleOrderDetailVo info(Long userId, RecycleOrderInfoBo infoBo);

    void cancel(Long userId, CancelOrderBo cancelBo);

    void reschedule(Long userId, RescheduleBo rescheduleBo);

    void submitException(Long userId, ExceptionBo exceptionBo);

    OrderCourierVo getCourierInfo(Long userId, RecycleOrderInfoBo infoBo);

    /**
     * 查询邮寄订单顺丰物流轨迹
     */
    List<ExpressTrackVo> listExpressTracks(Long userId, RecycleOrderInfoBo infoBo);

    /**
     * 用户邮寄订单发货（填写快递单号，物流状态变为待收货）
     */
    void shipMail(Long userId, ShipMailOrderBo shipBo);
}
