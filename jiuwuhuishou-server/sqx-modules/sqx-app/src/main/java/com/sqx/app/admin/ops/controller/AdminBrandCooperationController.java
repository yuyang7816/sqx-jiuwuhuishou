package com.sqx.app.admin.ops.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.ops.domain.bo.*;
import com.sqx.app.admin.ops.service.AdminBrandCooperationManageService;
import com.sqx.app.brand.domain.entity.BrandCooperation;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-品牌合作", description = "管理端-品牌合作")
@RestController
@RequestMapping("/admin/brand/cooperation")
public class AdminBrandCooperationController {

    @Resource
    private AdminBrandCooperationManageService adminBrandCooperationManageService;

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<BrandCooperation>> page(@RequestBody @Validated AdminBrandCooperationPageBo pageBo) {
        return Result.success(adminBrandCooperationManageService.pageInfo(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<BrandCooperation> info(@RequestBody @Validated AdminBrandCooperationInfoBo infoBo) {
        return Result.success(adminBrandCooperationManageService.info(infoBo));
    }

    @Operation(summary = "更新状态")
    @PostMapping("update")
    public Result<Void> update(@RequestBody @Validated AdminBrandCooperationUpdateStatusBo updateBo) {
        adminBrandCooperationManageService.updateStatus(updateBo);
        return Result.success();
    }
}
