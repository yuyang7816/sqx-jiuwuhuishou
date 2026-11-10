package com.sqx.app.admin.pickup.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskAssignBo;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskFinishBo;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskInfoBo;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskPageBo;
import com.sqx.app.admin.pickup.domain.vo.AdminPickupTaskDetailVo;
import com.sqx.app.admin.pickup.domain.vo.AdminPickupTaskVo;
import com.sqx.app.admin.pickup.service.AdminPickupManageService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-取件任务", description = "管理端-取件任务")
@RestController
@RequestMapping("/admin/pickup/task")
public class AdminPickupTaskController {

    @Resource
    private AdminPickupManageService adminPickupManageService;

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<AdminPickupTaskVo>> page(@RequestBody @Validated AdminPickupTaskPageBo pageBo) {
        return Result.success(adminPickupManageService.pageInfo(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<AdminPickupTaskDetailVo> info(@RequestBody @Validated AdminPickupTaskInfoBo infoBo) {
        return Result.success(adminPickupManageService.info(infoBo));
    }

    @Operation(summary = "分配取件员")
    @PostMapping("assign")
    public Result<Void> assign(@RequestBody @Validated AdminPickupTaskAssignBo assignBo) {
        adminPickupManageService.assign(assignBo);
        return Result.success();
    }

    @Operation(summary = "改派取件员")
    @PostMapping("reassign")
    public Result<Void> reassign(@RequestBody @Validated AdminPickupTaskAssignBo assignBo) {
        adminPickupManageService.reassign(assignBo);
        return Result.success();
    }

    @Operation(summary = "完成取件")
    @PostMapping("finish")
    public Result<Void> finish(@RequestBody @Validated AdminPickupTaskFinishBo finishBo) {
        adminPickupManageService.finish(finishBo);
        return Result.success();
    }
}
