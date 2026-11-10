package com.sqx.app.payout.controller;

import com.sqx.app.base.auth.auth.annotation.Login;
import com.sqx.app.payout.domain.bo.PayoutInfoBo;
import com.sqx.app.payout.domain.vo.PayoutInfoVo;
import com.sqx.app.payout.service.PayoutService;
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

@Tag(name = "用户端-订单打款", description = "打款进度")
@RestController
@RequestMapping("/app/payout")
public class AppPayoutController {

    @Resource
    private PayoutService payoutService;

    @Login
    @Operation(summary = "打款信息")
    @PostMapping("info")
    public Result<PayoutInfoVo> info(@RequestAttribute("userId") Long userId,
                                     @RequestBody @Validated PayoutInfoBo infoBo) {
        return Result.success(payoutService.info(userId, infoBo));
    }
}
