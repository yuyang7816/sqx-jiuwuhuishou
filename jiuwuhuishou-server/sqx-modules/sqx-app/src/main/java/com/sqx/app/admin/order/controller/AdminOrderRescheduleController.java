package com.sqx.app.admin.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.order.domain.bo.AdminOrderRescheduleInfoBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderReschedulePageBo;
import com.sqx.app.admin.order.domain.vo.AdminOrderRescheduleVo;
import com.sqx.app.admin.order.service.AdminOrderRescheduleManageService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-订单改约", description = "管理端-订单改约记录")
@RestController
@RequestMapping("/admin/order/reschedule")
public class AdminOrderRescheduleController {

    @Resource
    private AdminOrderRescheduleManageService adminOrderRescheduleManageService;

    @Operation(summary = "改约记录分页")
    @PostMapping("page")
    public Result<IPage<AdminOrderRescheduleVo>> page(@RequestBody @Validated AdminOrderReschedulePageBo pageBo) {
        return Result.success(adminOrderRescheduleManageService.pageInfo(pageBo));
    }

    @Operation(summary = "改约记录详情")
    @PostMapping("info")
    public Result<AdminOrderRescheduleVo> info(@RequestBody @Validated AdminOrderRescheduleInfoBo infoBo) {
        return Result.success(adminOrderRescheduleManageService.info(infoBo));
    }
}
