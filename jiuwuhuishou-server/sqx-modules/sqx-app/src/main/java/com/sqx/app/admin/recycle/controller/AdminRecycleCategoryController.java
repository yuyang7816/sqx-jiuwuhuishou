package com.sqx.app.admin.recycle.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.domain.bo.*;
import com.sqx.app.admin.recycle.service.AdminRecycleCategoryManageService;
import com.sqx.app.recycle.config.domain.entity.RecycleCategory;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-回收分类", description = "管理端-回收分类")
@RestController
@RequestMapping("/admin/recycle/category")
public class AdminRecycleCategoryController {

    @Resource
    private AdminRecycleCategoryManageService adminRecycleCategoryManageService;

    @Operation(summary = "保存")
    @PostMapping("save")
    public Result<Void> save(@RequestBody @Validated AdminRecycleCategorySaveBo saveBo) {
        adminRecycleCategoryManageService.saveInfo(saveBo);
        return Result.success();
    }

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<RecycleCategory>> page(@RequestBody @Validated AdminRecycleCategoryPageBo pageBo) {
        return Result.success(adminRecycleCategoryManageService.pageInfo(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<RecycleCategory> info(@RequestBody @Validated AdminRecycleCategoryInfoBo infoBo) {
        return Result.success(adminRecycleCategoryManageService.info(infoBo));
    }

    @Operation(summary = "更新")
    @PostMapping("update")
    public Result<Void> update(@RequestBody @Validated AdminRecycleCategoryUpdateBo updateBo) {
        adminRecycleCategoryManageService.updateInfo(updateBo);
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("delete")
    public Result<Void> delete(@RequestBody @Validated AdminRecycleCategoryInfoBo infoBo) {
        adminRecycleCategoryManageService.delete(infoBo);
        return Result.success();
    }
}
