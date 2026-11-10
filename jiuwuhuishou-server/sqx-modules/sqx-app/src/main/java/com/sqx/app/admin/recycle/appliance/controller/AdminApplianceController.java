package com.sqx.app.admin.recycle.appliance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.appliance.domain.bo.*;
import com.sqx.app.admin.recycle.appliance.domain.vo.AdminApplianceModelVo;
import com.sqx.app.admin.recycle.appliance.service.AdminApplianceManageService;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceCategory;
import com.sqx.app.recycle.appliance.domain.entity.ApplianceHeroFlow;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-家电主数据", description = "家电品类/型号（用户端 /app/recycle/appliance）")
@RestController
@RequestMapping("/admin/recycle/appliance")
public class AdminApplianceController {

    @Resource
    private AdminApplianceManageService adminApplianceManageService;

    @Operation(summary = "品类-保存")
    @PostMapping("category/save")
    public Result<Void> saveCategory(@RequestBody @Validated AdminApplianceCategorySaveBo saveBo) {
        adminApplianceManageService.saveCategory(saveBo);
        return Result.success();
    }

    @Operation(summary = "品类-分页")
    @PostMapping("category/page")
    public Result<IPage<ApplianceCategory>> pageCategory(@RequestBody @Validated AdminApplianceCategoryPageBo pageBo) {
        return Result.success(adminApplianceManageService.pageCategory(pageBo));
    }

    @Operation(summary = "品类-详情")
    @PostMapping("category/info")
    public Result<ApplianceCategory> infoCategory(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        return Result.success(adminApplianceManageService.infoCategory(infoBo));
    }

    @Operation(summary = "品类-更新")
    @PostMapping("category/update")
    public Result<Void> updateCategory(@RequestBody @Validated AdminApplianceCategoryUpdateBo updateBo) {
        adminApplianceManageService.updateCategory(updateBo);
        return Result.success();
    }

    @Operation(summary = "品类-删除")
    @PostMapping("category/delete")
    public Result<Void> deleteCategory(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        adminApplianceManageService.deleteCategory(infoBo);
        return Result.success();
    }

    @Operation(summary = "型号-保存")
    @PostMapping("model/save")
    public Result<Void> saveModel(@RequestBody @Validated AdminApplianceModelSaveBo saveBo) {
        adminApplianceManageService.saveModel(saveBo);
        return Result.success();
    }

    @Operation(summary = "型号-分页")
    @PostMapping("model/page")
    public Result<IPage<AdminApplianceModelVo>> pageModel(@RequestBody @Validated AdminApplianceModelPageBo pageBo) {
        return Result.success(adminApplianceManageService.pageModel(pageBo));
    }

    @Operation(summary = "型号-详情")
    @PostMapping("model/info")
    public Result<AdminApplianceModelVo> infoModel(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        return Result.success(adminApplianceManageService.infoModel(infoBo));
    }

    @Operation(summary = "型号-更新")
    @PostMapping("model/update")
    public Result<Void> updateModel(@RequestBody @Validated AdminApplianceModelUpdateBo updateBo) {
        adminApplianceManageService.updateModel(updateBo);
        return Result.success();
    }

    @Operation(summary = "型号-删除")
    @PostMapping("model/delete")
    public Result<Void> deleteModel(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        adminApplianceManageService.deleteModel(infoBo);
        return Result.success();
    }

    @Operation(summary = "流程步骤-保存")
    @PostMapping("heroFlow/save")
    public Result<Void> saveHeroFlow(@RequestBody @Validated AdminApplianceHeroFlowSaveBo saveBo) {
        adminApplianceManageService.saveHeroFlow(saveBo);
        return Result.success();
    }

    @Operation(summary = "流程步骤-分页")
    @PostMapping("heroFlow/page")
    public Result<IPage<ApplianceHeroFlow>> pageHeroFlow(@RequestBody @Validated AdminApplianceHeroFlowPageBo pageBo) {
        return Result.success(adminApplianceManageService.pageHeroFlow(pageBo));
    }

    @Operation(summary = "流程步骤-详情")
    @PostMapping("heroFlow/info")
    public Result<ApplianceHeroFlow> infoHeroFlow(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        return Result.success(adminApplianceManageService.infoHeroFlow(infoBo));
    }

    @Operation(summary = "流程步骤-更新")
    @PostMapping("heroFlow/update")
    public Result<Void> updateHeroFlow(@RequestBody @Validated AdminApplianceHeroFlowUpdateBo updateBo) {
        adminApplianceManageService.updateHeroFlow(updateBo);
        return Result.success();
    }

    @Operation(summary = "流程步骤-删除")
    @PostMapping("heroFlow/delete")
    public Result<Void> deleteHeroFlow(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        adminApplianceManageService.deleteHeroFlow(infoBo);
        return Result.success();
    }
}
