package com.sqx.app.wallet.wallet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.base.auth.auth.annotation.Login;
import com.sqx.app.wallet.wallet.domain.bo.UserWalletInfoBo;
import com.sqx.app.wallet.wallet.domain.bo.UserWalletPageBo;
import com.sqx.app.wallet.wallet.domain.bo.UserWalletSaveBo;
import com.sqx.app.wallet.wallet.domain.bo.UserWalletUpdateBo;
import com.sqx.app.wallet.wallet.domain.vo.UserWalletVo;
import com.sqx.app.wallet.wallet.service.UserWalletService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户钱包主表 前端控制器
 *
 * @author 西安省钱兄网络科技有限公司
 * @since 2025-08-13 15:09:58
 */
@Tag(name = "用户端-用户钱包", description = "用户端-用户钱包")
@RestController
@RequestMapping("/app/userWallet")
public class AppUserWalletController {

    @Resource
    private UserWalletService userWalletService;

    /**
     * 保存
     */
    @Operation(summary = "保存")
    @PostMapping("save")
    public Result<Void> save(@RequestBody @Validated UserWalletSaveBo userWalletSaveBo) {
        userWalletService.saveInfo(userWalletSaveBo);
        return Result.success();
    }

    /**
     * 列表
     */
    @Operation(summary = "列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = UserWalletVo.class)))
    @PostMapping("page")
    public Result<IPage<UserWalletVo>> page(@RequestBody @Validated UserWalletPageBo userWalletPageBo) {
        IPage<UserWalletVo> voIPage = userWalletService.pageInfo(userWalletPageBo);
        return Result.success(voIPage);
    }

    /**
     * 信息
     */
    @Operation(summary = "信息")
    @ApiResponse(content = @Content(schema = @Schema(implementation = UserWalletVo.class)))
    @PostMapping("info")
    public Result<UserWalletVo> info(@RequestBody @Validated UserWalletInfoBo userWalletInfoBo) {
        UserWalletVo vo = userWalletService.info(userWalletInfoBo);
        return Result.success(vo);
    }

    /**
     * 更新
     */
    @Operation(summary = "更新")
    @PostMapping("update")
    public Result<Void> update(@RequestBody @Validated UserWalletUpdateBo userWalletUpdateBo) {
        userWalletService.updateInfo(userWalletUpdateBo);
        return Result.success();
    }

    /**
     * 删除
     */
    @Operation(summary = "删除")
    @PostMapping("delete")
    public Result<Void> delete(@RequestBody @Validated UserWalletInfoBo userWalletInfoBo) {
        userWalletService.delete(userWalletInfoBo);
        return Result.success();
    }

    @Login
    @Operation(summary = "我的钱包")
    @ApiResponse(content = @Content(schema = @Schema(implementation = UserWalletVo.class)))
    @GetMapping("wallet")
    public Result<UserWalletVo> wallet() {
        UserWalletVo vo = userWalletService.wallet();
        return Result.success(vo);
    }
}
