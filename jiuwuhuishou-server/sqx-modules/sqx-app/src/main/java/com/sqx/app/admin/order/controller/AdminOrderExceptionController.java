package com.sqx.app.admin.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.order.domain.bo.AdminOrderExceptionHandleBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderExceptionInfoBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderExceptionPageBo;
import com.sqx.app.admin.order.domain.vo.AdminOrderExceptionVo;
import com.sqx.app.admin.order.service.AdminOrderExceptionManageService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-订单异常", description = "管理端-订单异常反馈")
@RestController
@RequestMapping("/admin/order/exception")
public class AdminOrderExceptionController {

    @Resource
    private AdminOrderExceptionManageService adminOrderExceptionManageService;

    @Operation(summary = "异常反馈分页")
    @PostMapping("page")
    public Result<IPage<AdminOrderExceptionVo>> page(@RequestBody @Validated AdminOrderExceptionPageBo pageBo) {
        return Result.success(adminOrderExceptionManageService.pageInfo(pageBo));
    }

    @Operation(summary = "异常反馈详情")
    @PostMapping("info")
    public Result<AdminOrderExceptionVo> info(@RequestBody @Validated AdminOrderExceptionInfoBo infoBo) {
        return Result.success(adminOrderExceptionManageService.info(infoBo));
    }

    @Operation(summary = "处理异常反馈")
    @PostMapping("handle")
    public Result<Void> handle(@RequestBody @Validated AdminOrderExceptionHandleBo handleBo) {
        adminOrderExceptionManageService.handle(handleBo);
        return Result.success();
    }
}
