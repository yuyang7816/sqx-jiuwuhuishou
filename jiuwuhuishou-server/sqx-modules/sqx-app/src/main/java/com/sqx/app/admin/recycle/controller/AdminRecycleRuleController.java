package com.sqx.app.admin.recycle.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.domain.bo.*;
import com.sqx.app.admin.recycle.service.AdminRecycleRuleManageService;
import com.sqx.app.recycle.config.domain.entity.RecycleRule;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-回收规则", description = "管理端-回收规则")
@RestController
@RequestMapping("/admin/recycle/rule")
public class AdminRecycleRuleController {

    @Resource
    private AdminRecycleRuleManageService adminRecycleRuleManageService;

    @Operation(summary = "保存")
    @PostMapping("save")
    public Result<Void> save(@RequestBody @Validated AdminRecycleRuleSaveBo saveBo) {
        adminRecycleRuleManageService.saveInfo(saveBo);
        return Result.success();
    }

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<RecycleRule>> page(@RequestBody @Validated AdminRecycleRulePageBo pageBo) {
        return Result.success(adminRecycleRuleManageService.pageInfo(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<RecycleRule> info(@RequestBody @Validated AdminRecycleRuleInfoBo infoBo) {
        return Result.success(adminRecycleRuleManageService.info(infoBo));
    }

    @Operation(summary = "更新")
    @PostMapping("update")
    public Result<Void> update(@RequestBody @Validated AdminRecycleRuleUpdateBo updateBo) {
        adminRecycleRuleManageService.updateInfo(updateBo);
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("delete")
    public Result<Void> delete(@RequestBody @Validated AdminRecycleRuleInfoBo infoBo) {
        adminRecycleRuleManageService.delete(infoBo);
        return Result.success();
    }
}
