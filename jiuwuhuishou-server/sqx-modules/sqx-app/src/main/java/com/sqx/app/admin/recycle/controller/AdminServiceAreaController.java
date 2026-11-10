package com.sqx.app.admin.recycle.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.domain.bo.*;
import com.sqx.app.admin.recycle.service.AdminServiceAreaManageService;
import com.sqx.app.recycle.config.domain.entity.ServiceArea;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-服务区域", description = "管理端-服务区域")
@RestController
@RequestMapping("/admin/recycle/serviceArea")
public class AdminServiceAreaController {

    @Resource
    private AdminServiceAreaManageService adminServiceAreaManageService;

    @Operation(summary = "保存")
    @PostMapping("save")
    public Result<Void> save(@RequestBody @Validated AdminServiceAreaSaveBo saveBo) {
        adminServiceAreaManageService.saveInfo(saveBo);
        return Result.success();
    }

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<ServiceArea>> page(@RequestBody @Validated AdminServiceAreaPageBo pageBo) {
        return Result.success(adminServiceAreaManageService.pageInfo(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<ServiceArea> info(@RequestBody @Validated AdminServiceAreaInfoBo infoBo) {
        return Result.success(adminServiceAreaManageService.info(infoBo));
    }

    @Operation(summary = "更新")
    @PostMapping("update")
    public Result<Void> update(@RequestBody @Validated AdminServiceAreaUpdateBo updateBo) {
        adminServiceAreaManageService.updateInfo(updateBo);
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("delete")
    public Result<Void> delete(@RequestBody @Validated AdminServiceAreaInfoBo infoBo) {
        adminServiceAreaManageService.delete(infoBo);
        return Result.success();
    }
}
