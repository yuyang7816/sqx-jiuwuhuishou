package com.sqx.app.admin.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.product.domain.bo.*;
import com.sqx.app.admin.product.domain.vo.AdminProductBrandVo;
import com.sqx.app.admin.product.domain.vo.AdminProductCategoryVo;
import com.sqx.app.admin.product.domain.vo.AdminProductModelVo;
import com.sqx.app.admin.product.domain.vo.AdminProductSpecTemplateVo;
import com.sqx.app.admin.product.domain.vo.AdminProductSpecVo;
import com.sqx.app.admin.product.service.AdminProductManageService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-商品分类", description = "管理端-品类/品牌/型号/规格估值")
@RestController
@RequestMapping("/admin/product")
public class AdminProductController {

    @Resource
    private AdminProductManageService adminProductManageService;

    @Operation(summary = "商品分类-保存")
    @PostMapping("category/save")
    public Result<Void> saveCategory(@RequestBody @Validated AdminProductCategorySaveBo saveBo) {
        adminProductManageService.saveCategory(saveBo);
        return Result.success();
    }

    @Operation(summary = "商品分类-列表")
    @PostMapping("category/page")
    public Result<IPage<AdminProductCategoryVo>> pageCategory(@RequestBody @Validated AdminProductCategoryPageBo pageBo) {
        return Result.success(adminProductManageService.pageCategory(pageBo));
    }

    @Operation(summary = "商品分类-详情")
    @PostMapping("category/info")
    public Result<AdminProductCategoryVo> infoCategory(@RequestBody @Validated AdminProductIdBo infoBo) {
        return Result.success(adminProductManageService.infoCategory(infoBo));
    }

    @Operation(summary = "商品分类-更新")
    @PostMapping("category/update")
    public Result<Void> updateCategory(@RequestBody @Validated AdminProductCategoryUpdateBo updateBo) {
        adminProductManageService.updateCategory(updateBo);
        return Result.success();
    }

    @Operation(summary = "商品分类-删除")
    @PostMapping("category/delete")
    public Result<Void> deleteCategory(@RequestBody @Validated AdminProductIdBo infoBo) {
        adminProductManageService.deleteCategory(infoBo);
        return Result.success();
    }

    @Operation(summary = "品牌-保存")
    @PostMapping("brand/save")
    public Result<Void> saveBrand(@RequestBody @Validated AdminProductBrandSaveBo saveBo) {
        adminProductManageService.saveBrand(saveBo);
        return Result.success();
    }

    @Operation(summary = "品牌-列表")
    @PostMapping("brand/page")
    public Result<IPage<AdminProductBrandVo>> pageBrand(@RequestBody @Validated AdminProductBrandPageBo pageBo) {
        return Result.success(adminProductManageService.pageBrand(pageBo));
    }

    @Operation(summary = "品牌-详情")
    @PostMapping("brand/info")
    public Result<AdminProductBrandVo> infoBrand(@RequestBody @Validated AdminProductIdBo infoBo) {
        return Result.success(adminProductManageService.infoBrand(infoBo));
    }

    @Operation(summary = "品牌-更新")
    @PostMapping("brand/update")
    public Result<Void> updateBrand(@RequestBody @Validated AdminProductBrandUpdateBo updateBo) {
        adminProductManageService.updateBrand(updateBo);
        return Result.success();
    }

    @Operation(summary = "品牌-删除")
    @PostMapping("brand/delete")
    public Result<Void> deleteBrand(@RequestBody @Validated AdminProductIdBo infoBo) {
        adminProductManageService.deleteBrand(infoBo);
        return Result.success();
    }

    @Operation(summary = "型号-保存")
    @PostMapping("model/save")
    public Result<Void> saveModel(@RequestBody @Validated AdminProductModelSaveBo saveBo) {
        adminProductManageService.saveModel(saveBo);
        return Result.success();
    }

    @Operation(summary = "型号-列表")
    @PostMapping("model/page")
    public Result<IPage<AdminProductModelVo>> pageModel(@RequestBody @Validated AdminProductModelPageBo pageBo) {
        return Result.success(adminProductManageService.pageModel(pageBo));
    }

    @Operation(summary = "型号-详情")
    @PostMapping("model/info")
    public Result<AdminProductModelVo> infoModel(@RequestBody @Validated AdminProductIdBo infoBo) {
        return Result.success(adminProductManageService.infoModel(infoBo));
    }

    @Operation(summary = "型号-更新")
    @PostMapping("model/update")
    public Result<Void> updateModel(@RequestBody @Validated AdminProductModelUpdateBo updateBo) {
        adminProductManageService.updateModel(updateBo);
        return Result.success();
    }

    @Operation(summary = "型号-删除")
    @PostMapping("model/delete")
    public Result<Void> deleteModel(@RequestBody @Validated AdminProductIdBo infoBo) {
        adminProductManageService.deleteModel(infoBo);
        return Result.success();
    }

    @Operation(summary = "规格-保存")
    @PostMapping("spec/save")
    public Result<Void> saveSpec(@RequestBody @Validated AdminProductSpecSaveBo saveBo) {
        adminProductManageService.saveSpec(saveBo);
        return Result.success();
    }

    @Operation(summary = "规格-列表")
    @PostMapping("spec/page")
    public Result<IPage<AdminProductSpecVo>> pageSpec(@RequestBody @Validated AdminProductSpecPageBo pageBo) {
        return Result.success(adminProductManageService.pageSpec(pageBo));
    }

    @Operation(summary = "规格-详情")
    @PostMapping("spec/info")
    public Result<AdminProductSpecVo> infoSpec(@RequestBody @Validated AdminProductIdBo infoBo) {
        return Result.success(adminProductManageService.infoSpec(infoBo));
    }

    @Operation(summary = "规格-更新")
    @PostMapping("spec/update")
    public Result<Void> updateSpec(@RequestBody @Validated AdminProductSpecUpdateBo updateBo) {
        adminProductManageService.updateSpec(updateBo);
        return Result.success();
    }

    @Operation(summary = "规格-删除")
    @PostMapping("spec/delete")
    public Result<Void> deleteSpec(@RequestBody @Validated AdminProductIdBo infoBo) {
        adminProductManageService.deleteSpec(infoBo);
        return Result.success();
    }

    @Operation(summary = "规格估值-文案配置模板")
    @PostMapping("spec/template")
    public Result<AdminProductSpecTemplateVo> loadSpecTemplate(@RequestBody @Validated AdminProductSpecTemplateBo templateBo) {
        return Result.success(adminProductManageService.loadSpecTemplate(templateBo));
    }
}
