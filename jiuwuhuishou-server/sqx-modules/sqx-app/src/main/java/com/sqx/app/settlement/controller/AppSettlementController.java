package com.sqx.app.settlement.controller;

import com.sqx.app.base.auth.auth.annotation.Login;
import com.sqx.app.settlement.domain.bo.SettlementConfirmBo;
import com.sqx.app.settlement.domain.bo.SettlementDisputeBo;
import com.sqx.app.settlement.domain.bo.SettlementInfoBo;
import com.sqx.app.settlement.domain.vo.SettlementConfirmVo;
import com.sqx.app.settlement.domain.vo.SettlementInfoVo;
import com.sqx.app.settlement.service.SettlementService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "用户端-结算确认", description = "结算确认与争议")
@RestController
@RequestMapping("/app/settlement")
public class AppSettlementController {

    @Resource
    private SettlementService settlementService;

    @Login
    @Operation(summary = "结算信息")
    @PostMapping("info")
    public Result<SettlementInfoVo> info(@RequestAttribute("userId") Long userId,
                                         @RequestBody @Validated SettlementInfoBo infoBo) {
        return Result.success(settlementService.info(userId, infoBo));
    }

    @Login
    @Operation(summary = "确认成交")
    @PostMapping("confirm")
    public Result<SettlementConfirmVo> confirm(@RequestAttribute("userId") Long userId,
                                               @RequestBody @Validated SettlementConfirmBo confirmBo) {
        return Result.success(settlementService.confirm(userId, confirmBo));
    }

    @Login
    @Operation(summary = "发起价格争议")
    @PostMapping("dispute")
    public Result<Void> dispute(@RequestAttribute("userId") Long userId,
                                @RequestBody @Validated SettlementDisputeBo disputeBo) {
        settlementService.dispute(userId, disputeBo);
        return Result.success();
    }
}
