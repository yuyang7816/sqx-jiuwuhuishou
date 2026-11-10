package com.sqx.app.admin.recycle.digital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.digital.domain.bo.*;
import com.sqx.app.admin.recycle.digital.domain.vo.AdminDigitalModelVo;
import com.sqx.app.admin.recycle.digital.domain.vo.AdminDigitalPricingVo;
import com.sqx.app.admin.recycle.digital.service.AdminDigitalManageService;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.digital.domain.entity.DigitalBrand;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-数码/奢侈品主数据", description = "品牌/型号/估价规则（用户端 /app/recycle/digital）")
@RestController
@RequestMapping("/admin/recycle/digital")
public class AdminDigitalController {

    @Resource
    private AdminDigitalManageService adminDigitalManageService;

    @Operation(summary = "品牌-保存")
    @PostMapping("brand/save")
    public Result<Void> saveBrand(@RequestBody @Validated AdminDigitalBrandSaveBo saveBo) {
        adminDigitalManageService.saveBrand(saveBo);
        return Result.success();
    }

    @Operation(summary = "品牌-分页")
    @PostMapping("brand/page")
    public Result<IPage<DigitalBrand>> pageBrand(@RequestBody @Validated AdminDigitalBrandPageBo pageBo) {
        return Result.success(adminDigitalManageService.pageBrand(pageBo));
    }

    @Operation(summary = "品牌-详情")
    @PostMapping("brand/info")
    public Result<DigitalBrand> infoBrand(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        return Result.success(adminDigitalManageService.infoBrand(infoBo));
    }

    @Operation(summary = "品牌-更新")
    @PostMapping("brand/update")
    public Result<Void> updateBrand(@RequestBody @Validated AdminDigitalBrandUpdateBo updateBo) {
        adminDigitalManageService.updateBrand(updateBo);
        return Result.success();
    }

    @Operation(summary = "品牌-删除")
    @PostMapping("brand/delete")
    public Result<Void> deleteBrand(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        adminDigitalManageService.deleteBrand(infoBo);
        return Result.success();
    }

    @Operation(summary = "型号-保存")
    @PostMapping("model/save")
    public Result<Void> saveModel(@RequestBody @Validated AdminDigitalModelSaveBo saveBo) {
        adminDigitalManageService.saveModel(saveBo);
        return Result.success();
    }

    @Operation(summary = "型号-分页")
    @PostMapping("model/page")
    public Result<IPage<AdminDigitalModelVo>> pageModel(@RequestBody @Validated AdminDigitalModelPageBo pageBo) {
        return Result.success(adminDigitalManageService.pageModel(pageBo));
    }

    @Operation(summary = "型号-详情")
    @PostMapping("model/info")
    public Result<AdminDigitalModelVo> infoModel(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        return Result.success(adminDigitalManageService.infoModel(infoBo));
    }

    @Operation(summary = "型号-更新")
    @PostMapping("model/update")
    public Result<Void> updateModel(@RequestBody @Validated AdminDigitalModelUpdateBo updateBo) {
        adminDigitalManageService.updateModel(updateBo);
        return Result.success();
    }

    @Operation(summary = "型号-删除")
    @PostMapping("model/delete")
    public Result<Void> deleteModel(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        adminDigitalManageService.deleteModel(infoBo);
        return Result.success();
    }

    @Operation(summary = "估价规则-保存")
    @PostMapping("pricing/save")
    public Result<Void> savePricing(@RequestBody @Validated AdminDigitalPricingSaveBo saveBo) {
        adminDigitalManageService.savePricing(saveBo);
        return Result.success();
    }

    @Operation(summary = "估价规则-分页")
    @PostMapping("pricing/page")
    public Result<IPage<AdminDigitalPricingVo>> pagePricing(@RequestBody @Validated AdminDigitalPricingPageBo pageBo) {
        return Result.success(adminDigitalManageService.pagePricing(pageBo));
    }

    @Operation(summary = "估价规则-详情")
    @PostMapping("pricing/info")
    public Result<AdminDigitalPricingVo> infoPricing(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        return Result.success(adminDigitalManageService.infoPricing(infoBo));
    }

    @Operation(summary = "估价规则-更新")
    @PostMapping("pricing/update")
    public Result<Void> updatePricing(@RequestBody @Validated AdminDigitalPricingUpdateBo updateBo) {
        adminDigitalManageService.updatePricing(updateBo);
        return Result.success();
    }

    @Operation(summary = "估价规则-删除")
    @PostMapping("pricing/delete")
    public Result<Void> deletePricing(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        adminDigitalManageService.deletePricing(infoBo);
        return Result.success();
    }
}
