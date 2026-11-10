package com.sqx.app.pickup.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.base.auth.auth.annotation.Login;
import com.sqx.app.pickup.domain.bo.PickupTaskCancelBo;
import com.sqx.app.pickup.domain.bo.PickupTaskFinishBo;
import com.sqx.app.pickup.domain.bo.PickupTaskInfoBo;
import com.sqx.app.pickup.domain.bo.PickupTaskPageBo;
import com.sqx.app.pickup.domain.bo.PickupTaskStartBo;
import com.sqx.app.pickup.domain.bo.PickupTaskUpdateActualBo;
import com.sqx.app.pickup.domain.vo.PickupTaskDetailVo;
import com.sqx.app.pickup.domain.vo.PickupTaskFinishVo;
import com.sqx.app.pickup.domain.vo.PickupTaskStatsVo;
import com.sqx.app.pickup.domain.vo.PickupTaskVo;
import com.sqx.app.pickup.service.PickupTaskService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "用户端-取件任务", description = "员工取件任务")
@RestController
@RequestMapping("/app/pickup/task")
public class AppPickupTaskController {

    @Resource
    private PickupTaskService pickupTaskService;

    @Login
    @Operation(summary = "取件任务统计")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PickupTaskStatsVo.class)))
    @GetMapping("stats")
    public Result<PickupTaskStatsVo> stats(@RequestAttribute("userId") Long userId) {
        return Result.success(pickupTaskService.stats(userId));
    }

    @Login
    @Operation(summary = "取件任务分页列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PickupTaskVo.class)))
    @PostMapping("page")
    public Result<IPage<PickupTaskVo>> page(@RequestAttribute("userId") Long userId,
                                            @RequestBody @Validated PickupTaskPageBo pageBo) {
        pageBo.setStaffId(userId);
        return Result.success(pickupTaskService.page(pageBo));
    }

    @Login
    @Operation(summary = "取件任务详情")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PickupTaskDetailVo.class)))
    @PostMapping("info")
    public Result<PickupTaskDetailVo> info(@RequestAttribute("userId") Long userId,
                                           @RequestBody @Validated PickupTaskInfoBo infoBo) {
        return Result.success(pickupTaskService.info(userId, infoBo));
    }

    @Login
    @Operation(summary = "开始上门取件")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PickupTaskDetailVo.class)))
    @PostMapping("start")
    public Result<PickupTaskDetailVo> start(@RequestAttribute("userId") Long userId,
                                            @RequestBody @Validated PickupTaskStartBo startBo) {
        return Result.success(pickupTaskService.start(userId, startBo));
    }

    @Login
    @Operation(summary = "完成取件")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PickupTaskFinishVo.class)))
    @PostMapping("finish")
    public Result<PickupTaskFinishVo> finish(@RequestAttribute("userId") Long userId,
                                             @RequestBody @Validated PickupTaskFinishBo finishBo) {
        return Result.success(pickupTaskService.finish(userId, finishBo));
    }

    @Login
    @Operation(summary = "修改实际打款金额")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PickupTaskDetailVo.class)))
    @PostMapping("updateActual")
    public Result<PickupTaskDetailVo> updateActual(@RequestAttribute("userId") Long userId,
                                                   @RequestBody @Validated PickupTaskUpdateActualBo updateBo) {
        return Result.success(pickupTaskService.updateActual(userId, updateBo));
    }

    @Login
    @Operation(summary = "取消订单")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PickupTaskDetailVo.class)))
    @PostMapping("cancel")
    public Result<PickupTaskDetailVo> cancel(@RequestAttribute("userId") Long userId,
                                             @RequestBody @Validated PickupTaskCancelBo cancelBo) {
        return Result.success(pickupTaskService.cancel(userId, cancelBo));
    }
}
