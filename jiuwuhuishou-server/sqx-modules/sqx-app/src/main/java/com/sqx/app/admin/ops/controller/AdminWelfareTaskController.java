package com.sqx.app.admin.ops.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.ops.domain.bo.*;
import com.sqx.app.admin.ops.service.AdminWelfareTaskManageService;
import com.sqx.app.welfare.domain.entity.WelfareTask;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-福利任务", description = "管理端-福利任务")
@RestController
@RequestMapping("/admin/welfare/task")
public class AdminWelfareTaskController {

    @Resource
    private AdminWelfareTaskManageService adminWelfareTaskManageService;

    @Operation(summary = "保存")
    @PostMapping("save")
    public Result<Void> save(@RequestBody @Validated AdminWelfareTaskSaveBo saveBo) {
        adminWelfareTaskManageService.saveInfo(saveBo);
        return Result.success();
    }

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<WelfareTask>> page(@RequestBody @Validated AdminWelfareTaskPageBo pageBo) {
        return Result.success(adminWelfareTaskManageService.pageInfo(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<WelfareTask> info(@RequestBody @Validated AdminWelfareTaskInfoBo infoBo) {
        return Result.success(adminWelfareTaskManageService.info(infoBo));
    }

    @Operation(summary = "更新")
    @PostMapping("update")
    public Result<Void> update(@RequestBody @Validated AdminWelfareTaskUpdateBo updateBo) {
        adminWelfareTaskManageService.updateInfo(updateBo);
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("delete")
    public Result<Void> delete(@RequestBody @Validated AdminWelfareTaskInfoBo infoBo) {
        adminWelfareTaskManageService.delete(infoBo);
        return Result.success();
    }
}
