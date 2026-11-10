package com.sqx.app.referral.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.base.auth.auth.annotation.Login;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.referral.commission.domain.vo.ReferralListVo;
import com.sqx.app.referral.user.domain.bo.ReferralCommissionWithdrawBo;
import com.sqx.app.referral.user.domain.vo.InvitePosterVo;
import com.sqx.app.referral.user.domain.vo.ReferralCommissionWithdrawResultVo;
import com.sqx.app.referral.user.domain.vo.ReferralDataVo;
import com.sqx.app.referral.user.domain.vo.ReferralLawyerVo;
import com.sqx.app.referral.user.domain.vo.ReferralUserCommissionVo;
import com.sqx.app.referral.user.service.ReferralUserCommissionService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户佣金 前端控制器
 *
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-08-04 14:44:53
 */
@Tag(name = "用户端-用户佣金", description = "用户端-用户佣金")
@RestController
@RequestMapping("/app/referral/userCommission")
public class AppReferralUserCommissionController {

    @Resource
    private ReferralUserCommissionService referralUserCommissionService;

    /**
     * 信息
     */
    @Login
    @Operation(summary = "信息")
    @GetMapping("info")
    public Result<ReferralUserCommissionVo> info(@RequestAttribute("userId") Long userId) {
        ReferralUserCommissionVo vo = referralUserCommissionService.info(userId);
        return Result.success(vo);
    }

    @Login
    @Operation(summary = "分销数据")
    @GetMapping("referralData")
    public Result<ReferralDataVo> referralData(@RequestAttribute("userId") Long userId) {
        ReferralDataVo vo = referralUserCommissionService.referralData(userId);
        return Result.success(vo);
    }

    @Login
    @Operation(summary = "邀请用户列表")
    @GetMapping("referralUserList")
    public Result<IPage<UserEntity>> referralUserList(@RequestAttribute("userId") Long userId) {
        IPage<UserEntity> userList = referralUserCommissionService.referralUserList(userId);
        return Result.success(userList);
    }

    @Login
    @Operation(summary = "个人收益奖励列表")
    @GetMapping("referralList")
    public Result<IPage<ReferralListVo>> referralList(@RequestAttribute("userId") Long userId) {
        IPage<ReferralListVo> voIPage = referralUserCommissionService.referralList(userId);
        return Result.success(voIPage);
    }

    @Login
    @Operation(summary = "律师查看资产")
    @GetMapping("lawyer")
    public Result<ReferralLawyerVo> lawyer() {
        ReferralLawyerVo vo = referralUserCommissionService.lawyer();
        return Result.success(vo);
    }

    @Login
    @Operation(summary = "生成邀请海报")
    @GetMapping("poster")
    public Result<InvitePosterVo> poster(@RequestAttribute("userId") Long userId) {
        InvitePosterVo vo = referralUserCommissionService.invitePoster(userId);
        return Result.success(vo);
    }

    @Login
    @Operation(summary = "佣金提现到钱包")
    @PostMapping("withdraw")
    public Result<ReferralCommissionWithdrawResultVo> withdraw(@RequestAttribute("userId") Long userId,
                                                               @RequestBody @Validated ReferralCommissionWithdrawBo withdrawBo) {
        return Result.success(referralUserCommissionService.withdrawToWallet(userId, withdrawBo));
    }
}
