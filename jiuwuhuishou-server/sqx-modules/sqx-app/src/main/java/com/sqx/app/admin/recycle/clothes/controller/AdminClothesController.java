package com.sqx.app.admin.recycle.clothes.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.clothes.domain.bo.AdminClothesCategoryPageBo;
import com.sqx.app.admin.recycle.clothes.domain.bo.AdminClothesCategorySaveBo;
import com.sqx.app.admin.recycle.clothes.domain.bo.AdminClothesCategoryUpdateBo;
import com.sqx.app.admin.recycle.clothes.service.AdminClothesManageService;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.clothes.domain.entity.ClothesCategory;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-旧衣主数据", description = "旧衣分类（用户端 /app/recycle/clothes）")
@RestController
@RequestMapping("/admin/recycle/clothes")
public class AdminClothesController {

    @Resource
    private AdminClothesManageService adminClothesManageService;

    @Operation(summary = "分类-保存")
    @PostMapping("category/save")
    public Result<Void> saveCategory(@RequestBody @Validated AdminClothesCategorySaveBo saveBo) {
        adminClothesManageService.saveCategory(saveBo);
        return Result.success();
    }

    @Operation(summary = "分类-分页")
    @PostMapping("category/page")
    public Result<IPage<ClothesCategory>> pageCategory(@RequestBody @Validated AdminClothesCategoryPageBo pageBo) {
        return Result.success(adminClothesManageService.pageCategory(pageBo));
    }

    @Operation(summary = "分类-详情")
    @PostMapping("category/info")
    public Result<ClothesCategory> infoCategory(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        return Result.success(adminClothesManageService.infoCategory(infoBo));
    }

    @Operation(summary = "分类-更新")
    @PostMapping("category/update")
    public Result<Void> updateCategory(@RequestBody @Validated AdminClothesCategoryUpdateBo updateBo) {
        adminClothesManageService.updateCategory(updateBo);
        return Result.success();
    }

    @Operation(summary = "分类-删除")
    @PostMapping("category/delete")
    public Result<Void> deleteCategory(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        adminClothesManageService.deleteCategory(infoBo);
        return Result.success();
    }
}
