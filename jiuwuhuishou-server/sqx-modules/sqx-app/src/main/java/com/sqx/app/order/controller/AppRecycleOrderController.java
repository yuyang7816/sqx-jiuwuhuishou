package com.sqx.app.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.base.auth.auth.annotation.Login;
import com.sqx.app.order.domain.bo.*;
import com.sqx.app.order.domain.vo.AppointmentDateOptionVo;
import com.sqx.app.order.domain.vo.ExpressTrackVo;
import com.sqx.app.order.domain.vo.OrderCourierVo;
import com.sqx.app.order.domain.vo.RecycleOrderDetailVo;
import com.sqx.app.order.domain.vo.RecycleOrderVo;
import com.sqx.app.order.service.AppointmentOptionService;
import com.sqx.app.order.service.RecycleOrderService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 回收订单 前端控制器
 */
@Tag(name = "用户端-回收订单", description = "用户端-回收订单")
@RestController
@RequestMapping("/app/order")
public class AppRecycleOrderController {

    @Resource
    private RecycleOrderService recycleOrderService;
    @Resource
    private AppointmentOptionService appointmentOptionService;

    @Operation(summary = "预约时段选项")
    @GetMapping("appointment/options")
    public Result<List<AppointmentDateOptionVo>> appointmentOptions() {
        return Result.success(appointmentOptionService.listOptions());
    }

    @Login
    @Operation(summary = "旧衣回收下单")
    @PostMapping("clothes/save")
    public Result<Long> createClothesOrder(@RequestAttribute("userId") Long userId,
                                           @RequestBody @Validated ClothesOrderSaveBo saveBo) {
        return Result.success(recycleOrderService.createClothesOrder(userId, saveBo));
    }

    @Login
    @Operation(summary = "家电回收下单")
    @PostMapping("appliance/save")
    public Result<Long> createApplianceOrder(@RequestAttribute("userId") Long userId,
                                             @RequestBody @Validated ApplianceOrderSaveBo saveBo) {
        return Result.success(recycleOrderService.createApplianceOrder(userId, saveBo));
    }

    @Login
    @Operation(summary = "数码回收下单")
    @PostMapping("digital/save")
    public Result<Long> createDigitalOrder(@RequestAttribute("userId") Long userId,
                                           @RequestBody @Validated DigitalOrderSaveBo saveBo) {
        return Result.success(recycleOrderService.createDigitalOrder(userId, saveBo));
    }

    @Login
    @Operation(summary = "订单列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = RecycleOrderVo.class)))
    @PostMapping("page")
    public Result<IPage<RecycleOrderVo>> page(@RequestAttribute("userId") Long userId,
                                              @RequestBody @Validated RecycleOrderPageBo pageBo) {
        pageBo.setUserId(userId);
        return Result.success(recycleOrderService.pageInfo(userId, pageBo));
    }

    @Login
    @Operation(summary = "订单详情")
    @ApiResponse(content = @Content(schema = @Schema(implementation = RecycleOrderDetailVo.class)))
    @PostMapping("info")
    public Result<RecycleOrderDetailVo> info(@RequestAttribute("userId") Long userId,
                                             @RequestBody @Validated RecycleOrderInfoBo infoBo) {
        return Result.success(recycleOrderService.info(userId, infoBo));
    }

    @Login
    @Operation(summary = "取消订单")
    @PostMapping("cancel")
    public Result<Void> cancel(@RequestAttribute("userId") Long userId,
                               @RequestBody @Validated CancelOrderBo cancelBo) {
        recycleOrderService.cancel(userId, cancelBo);
        return Result.success();
    }

    @Login
    @Operation(summary = "改约")
    @PostMapping("reschedule")
    public Result<Void> reschedule(@RequestAttribute("userId") Long userId,
                                   @RequestBody @Validated RescheduleBo rescheduleBo) {
        recycleOrderService.reschedule(userId, rescheduleBo);
        return Result.success();
    }

    @Login
    @Operation(summary = "回收员信息")
    @PostMapping("courier/info")
    public Result<OrderCourierVo> courierInfo(@RequestAttribute("userId") Long userId,
                                              @RequestBody @Validated RecycleOrderInfoBo infoBo) {
        return Result.success(recycleOrderService.getCourierInfo(userId, infoBo));
    }

    @Login
    @Operation(summary = "异常反馈")
    @PostMapping("exception")
    public Result<Void> submitException(@RequestAttribute("userId") Long userId,
                                        @RequestBody @Validated ExceptionBo exceptionBo) {
        recycleOrderService.submitException(userId, exceptionBo);
        return Result.success();
    }

    @Login
    @Operation(summary = "邮寄订单物流轨迹")
    @PostMapping("express/tracks")
    public Result<List<ExpressTrackVo>> expressTracks(@RequestAttribute("userId") Long userId,
                                                      @RequestBody @Validated RecycleOrderInfoBo infoBo) {
        return Result.success(recycleOrderService.listExpressTracks(userId, infoBo));
    }

    @Login
    @Operation(summary = "邮寄订单发货")
    @PostMapping("mail/ship")
    public Result<Void> shipMail(@RequestAttribute("userId") Long userId,
                                 @RequestBody @Validated ShipMailOrderBo shipBo) {
        recycleOrderService.shipMail(userId, shipBo);
        return Result.success();
    }
}
