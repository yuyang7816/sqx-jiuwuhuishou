package com.sqx.app.admin.orderflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutConfirmBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutCreateBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminPayoutPageBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminPayoutVo;
import com.sqx.app.admin.orderflow.service.AdminPayoutManageService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-订单打款", description = "管理端-订单打款")
@RestController
@RequestMapping("/admin/payout")
public class AdminPayoutController {

    @Resource
    private AdminPayoutManageService adminPayoutManageService;

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<AdminPayoutVo>> page(@RequestBody @Validated AdminPayoutPageBo pageBo) {
        return Result.success(adminPayoutManageService.pageInfo(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<AdminPayoutVo> info(@RequestBody @Validated AdminPayoutInfoBo infoBo) {
        return Result.success(adminPayoutManageService.info(infoBo));
    }

    @Operation(summary = "创建打款单")
    @PostMapping("create")
    public Result<Void> create(@RequestBody @Validated AdminPayoutCreateBo createBo) {
        adminPayoutManageService.create(createBo);
        return Result.success();
    }

    @Operation(summary = "确认打款")
    @PostMapping("confirm")
    public Result<Void> confirm(@RequestBody @Validated AdminPayoutConfirmBo confirmBo) {
        adminPayoutManageService.confirm(confirmBo);
        return Result.success();
    }
}
