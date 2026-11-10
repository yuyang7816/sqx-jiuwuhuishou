package com.sqx.app.admin.ops.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.ops.domain.bo.*;
import com.sqx.app.admin.ops.service.AdminHelpCategoryManageService;
import com.sqx.app.help.domain.entity.HelpCategory;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-帮助分类", description = "管理端-帮助分类")
@RestController
@RequestMapping("/admin/help/category")
public class AdminHelpCategoryController {

    @Resource
    private AdminHelpCategoryManageService adminHelpCategoryManageService;

    @Operation(summary = "保存")
    @PostMapping("save")
    public Result<Void> save(@RequestBody @Validated AdminHelpCategorySaveBo saveBo) {
        adminHelpCategoryManageService.saveInfo(saveBo);
        return Result.success();
    }

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<HelpCategory>> page(@RequestBody @Validated AdminHelpCategoryPageBo pageBo) {
        return Result.success(adminHelpCategoryManageService.pageInfo(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<HelpCategory> info(@RequestBody @Validated AdminHelpCategoryInfoBo infoBo) {
        return Result.success(adminHelpCategoryManageService.info(infoBo));
    }

    @Operation(summary = "更新")
    @PostMapping("update")
    public Result<Void> update(@RequestBody @Validated AdminHelpCategoryUpdateBo updateBo) {
        adminHelpCategoryManageService.updateInfo(updateBo);
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("delete")
    public Result<Void> delete(@RequestBody @Validated AdminHelpCategoryInfoBo infoBo) {
        adminHelpCategoryManageService.delete(infoBo);
        return Result.success();
    }
}
