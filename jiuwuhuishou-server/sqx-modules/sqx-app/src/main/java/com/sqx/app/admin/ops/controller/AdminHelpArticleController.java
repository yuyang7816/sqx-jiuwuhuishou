package com.sqx.app.admin.ops.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.ops.domain.bo.*;
import com.sqx.app.admin.ops.service.AdminHelpArticleManageService;
import com.sqx.app.help.domain.entity.HelpArticle;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-帮助文章", description = "管理端-帮助文章")
@RestController
@RequestMapping("/admin/help/article")
public class AdminHelpArticleController {

    @Resource
    private AdminHelpArticleManageService adminHelpArticleManageService;

    @Operation(summary = "保存")
    @PostMapping("save")
    public Result<Void> save(@RequestBody @Validated AdminHelpArticleSaveBo saveBo) {
        adminHelpArticleManageService.saveInfo(saveBo);
        return Result.success();
    }

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<HelpArticle>> page(@RequestBody @Validated AdminHelpArticlePageBo pageBo) {
        return Result.success(adminHelpArticleManageService.pageInfo(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<HelpArticle> info(@RequestBody @Validated AdminHelpArticleInfoBo infoBo) {
        return Result.success(adminHelpArticleManageService.info(infoBo));
    }

    @Operation(summary = "更新")
    @PostMapping("update")
    public Result<Void> update(@RequestBody @Validated AdminHelpArticleUpdateBo updateBo) {
        adminHelpArticleManageService.updateInfo(updateBo);
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("delete")
    public Result<Void> delete(@RequestBody @Validated AdminHelpArticleInfoBo infoBo) {
        adminHelpArticleManageService.delete(infoBo);
        return Result.success();
    }
}
