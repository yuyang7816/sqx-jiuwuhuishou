package com.sqx.app.admin.order.controller;

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
import com.sqx.app.admin.order.service.AdminOrderManageService;
import com.sqx.app.order.domain.vo.ExpressTrackVo;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "管理端-回收订单", description = "管理端-回收订单")
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Resource
    private AdminOrderManageService adminOrderManageService;

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<AdminOrderVo>> page(@RequestBody @Validated AdminOrderPageBo pageBo) {
        return Result.success(adminOrderManageService.pageInfo(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<AdminOrderDetailVo> info(@RequestBody @Validated AdminOrderInfoBo infoBo) {
        return Result.success(adminOrderManageService.info(infoBo));
    }

    @Operation(summary = "取消订单")
    @PostMapping("cancel")
    public Result<Void> cancel(@RequestBody @Validated AdminOrderCancelBo cancelBo) {
        adminOrderManageService.cancel(cancelBo);
        return Result.success();
    }

    @Operation(summary = "更新状态")
    @PostMapping("updateStatus")
    public Result<Void> updateStatus(@RequestBody @Validated AdminOrderUpdateStatusBo updateBo) {
        adminOrderManageService.updateStatus(updateBo);
        return Result.success();
    }

    @Operation(summary = "分配取件员")
    @PostMapping("assignPickup")
    public Result<Void> assignPickup(@RequestBody @Validated AdminOrderAssignPickupBo assignBo) {
        adminOrderManageService.assignPickup(assignBo);
        return Result.success();
    }

    @Operation(summary = "修改实际金额")
    @PostMapping("updateEstimate")
    public Result<Void> updateEstimate(@RequestBody @Validated AdminOrderUpdateEstimateBo updateBo) {
        adminOrderManageService.updateEstimate(updateBo);
        return Result.success();
    }

    @Operation(summary = "确认打款")
    @PostMapping("confirmPayout")
    public Result<Void> confirmPayout(@RequestBody @Validated AdminOrderConfirmPayoutBo confirmBo) {
        adminOrderManageService.confirmPayout(confirmBo);
        return Result.success();
    }

    @Operation(summary = "完成订单")
    @PostMapping("complete")
    public Result<Void> complete(@RequestBody @Validated AdminOrderCompleteBo completeBo) {
        adminOrderManageService.complete(completeBo);
        return Result.success();
    }

    @Operation(summary = "处理异常/争议")
    @PostMapping("handle")
    public Result<Void> handle(@RequestBody @Validated AdminOrderHandleBo handleBo) {
        adminOrderManageService.handle(handleBo);
        return Result.success();
    }

    @Operation(summary = "修改快递单号")
    @PostMapping("updateExpressNo")
    public Result<Void> updateExpressNo(@RequestBody @Validated AdminOrderUpdateExpressBo updateBo) {
        adminOrderManageService.updateExpressNo(updateBo);
        return Result.success();
    }

    @Operation(summary = "确认邮寄收货")
    @PostMapping("confirmMailReceive")
    public Result<Void> confirmMailReceive(@RequestBody @Validated AdminOrderConfirmMailReceiveBo confirmBo) {
        adminOrderManageService.confirmMailReceive(confirmBo);
        return Result.success();
    }

    @Operation(summary = "邮寄订单物流轨迹")
    @PostMapping("express/tracks")
    public Result<List<ExpressTrackVo>> expressTracks(@RequestBody @Validated AdminOrderInfoBo infoBo) {
        return Result.success(adminOrderManageService.listExpressTracks(infoBo));
    }
}
