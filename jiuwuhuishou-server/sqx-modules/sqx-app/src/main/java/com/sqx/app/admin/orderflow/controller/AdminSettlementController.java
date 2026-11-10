package com.sqx.app.admin.orderflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementConfirmBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementPageBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminSettlementSaveBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminSettlementVo;
import com.sqx.app.admin.orderflow.service.AdminSettlementManageService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-订单结算", description = "管理端-订单结算")
@RestController
@RequestMapping("/admin/settlement")
public class AdminSettlementController {

    @Resource
    private AdminSettlementManageService adminSettlementManageService;

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<AdminSettlementVo>> page(@RequestBody @Validated AdminSettlementPageBo pageBo) {
        return Result.success(adminSettlementManageService.pageInfo(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<AdminSettlementVo> info(@RequestBody @Validated AdminSettlementInfoBo infoBo) {
        return Result.success(adminSettlementManageService.info(infoBo));
    }

    @Operation(summary = "录入实际金额")
    @PostMapping("save")
    public Result<Void> save(@RequestBody @Validated AdminSettlementSaveBo saveBo) {
        adminSettlementManageService.save(saveBo);
        return Result.success();
    }

    @Operation(summary = "确认结算")
    @PostMapping("confirm")
    public Result<Void> confirm(@RequestBody @Validated AdminSettlementConfirmBo confirmBo) {
        adminSettlementManageService.confirm(confirmBo);
        return Result.success();
    }
}
