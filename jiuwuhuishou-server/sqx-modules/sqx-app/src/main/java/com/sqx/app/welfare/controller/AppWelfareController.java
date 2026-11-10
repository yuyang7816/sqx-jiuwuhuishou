package com.sqx.app.welfare.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.base.auth.auth.annotation.Login;
import com.sqx.app.welfare.domain.bo.WelfareCoinLogPageBo;
import com.sqx.app.welfare.domain.bo.WelfareContinuousSignBo;
import com.sqx.app.welfare.domain.bo.WelfareWithdrawBo;
import com.sqx.app.welfare.domain.vo.WelfareCoinLogVo;
import com.sqx.app.welfare.domain.vo.WelfareSignResultVo;
import com.sqx.app.welfare.domain.vo.WelfareSummaryVo;
import com.sqx.app.welfare.domain.vo.WelfareTaskVo;
import com.sqx.app.welfare.domain.vo.WelfareWithdrawResultVo;
import com.sqx.app.welfare.service.WelfareService;
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
import java.util.List;

@Tag(name = "用户端-福利中心", description = "用户端-福利中心")
@RestController
@RequestMapping("/app/welfare")
public class AppWelfareController {

    @Resource
    private WelfareService welfareService;

    @Login
    @Operation(summary = "福利中心概览")
    @ApiResponse(content = @Content(schema = @Schema(implementation = WelfareSummaryVo.class)))
    @GetMapping("summary")
    public Result<WelfareSummaryVo> summary(@RequestAttribute("userId") Long userId) {
        return Result.success(welfareService.summary(userId));
    }

    @Operation(summary = "福利任务列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = WelfareTaskVo.class)))
    @GetMapping("tasks")
    public Result<List<WelfareTaskVo>> tasks() {
        return Result.success(welfareService.listTasks());
    }

    @Login
    @Operation(summary = "签到领金币")
    @ApiResponse(content = @Content(schema = @Schema(implementation = WelfareSignResultVo.class)))
    @PostMapping("sign")
    public Result<WelfareSignResultVo> sign(@RequestAttribute("userId") Long userId) {
        return Result.success(welfareService.sign(userId));
    }

    @Login
    @Operation(summary = "设置连续签到开关")
    @ApiResponse(content = @Content(schema = @Schema(implementation = WelfareSummaryVo.class)))
    @PostMapping("continuous-sign")
    public Result<WelfareSummaryVo> updateContinuousSignEnabled(@RequestAttribute("userId") Long userId,
                                                                @RequestBody @Validated WelfareContinuousSignBo continuousSignBo) {
        return Result.success(welfareService.updateContinuousSignEnabled(userId, continuousSignBo));
    }

    @Login
    @Operation(summary = "金币提现到钱包")
    @ApiResponse(content = @Content(schema = @Schema(implementation = WelfareWithdrawResultVo.class)))
    @PostMapping("withdraw")
    public Result<WelfareWithdrawResultVo> withdraw(@RequestAttribute("userId") Long userId,
                                                    @RequestBody @Validated WelfareWithdrawBo withdrawBo) {
        return Result.success(welfareService.withdraw(userId, withdrawBo));
    }

    @Login
    @Operation(summary = "积分明细")
    @ApiResponse(content = @Content(schema = @Schema(implementation = WelfareCoinLogVo.class)))
    @PostMapping("coin/page")
    public Result<IPage<WelfareCoinLogVo>> coinPage(@RequestAttribute("userId") Long userId,
                                                    @RequestBody @Validated WelfareCoinLogPageBo pageBo) {
        return Result.success(welfareService.coinLogPage(userId, pageBo));
    }
}
