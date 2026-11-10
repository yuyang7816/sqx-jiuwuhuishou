package com.sqx.app.referral.user.controller;

import com.sqx.app.base.auth.auth.annotation.Login;
import com.sqx.app.referral.commission.domain.vo.ReferralLeaderboardVo;
import com.sqx.app.referral.user.service.ReferralUserCommissionService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 推荐排行榜
 */
@Tag(name = "用户端-推荐排行榜", description = "用户端-推荐排行榜")
@RestController
@RequestMapping("/app/referral")
public class AppReferralLeaderboardController {

    @Resource
    private ReferralUserCommissionService referralUserCommissionService;

    @Login
    @Operation(summary = "全局推荐排行榜")
    @GetMapping("leaderboard")
    public Result<List<ReferralLeaderboardVo>> leaderboard() {
        return Result.success(referralUserCommissionService.leaderboard());
    }
}
