package com.sqx.app.address.controller;

import com.sqx.app.address.domain.bo.UserAddressDeleteBo;
import com.sqx.app.address.domain.bo.UserAddressSaveBo;
import com.sqx.app.address.domain.bo.UserAddressSetDefaultBo;
import com.sqx.app.address.domain.bo.UserAddressUpdateBo;
import com.sqx.app.address.domain.vo.UserAddressVo;
import com.sqx.app.address.service.UserAddressService;
import com.sqx.app.base.auth.auth.annotation.Login;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户地址 前端控制器
 */
@Tag(name = "用户端-用户地址", description = "用户端-用户地址")
@RestController
@RequestMapping("/app/address")
public class AppUserAddressController {

    @Resource
    private UserAddressService userAddressService;

    @Login
    @Operation(summary = "地址列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = UserAddressVo.class)))
    @GetMapping("list")
    public Result<List<UserAddressVo>> list(@RequestAttribute("userId") Long userId) {
        return Result.success(userAddressService.list(userId));
    }

    @Login
    @Operation(summary = "保存地址")
    @PostMapping("save")
    public Result<Void> save(@RequestAttribute("userId") Long userId,
                             @RequestBody @Validated UserAddressSaveBo saveBo) {
        userAddressService.save(userId, saveBo);
        return Result.success();
    }

    @Login
    @Operation(summary = "更新地址")
    @PostMapping("update")
    public Result<Void> update(@RequestAttribute("userId") Long userId,
                               @RequestBody @Validated UserAddressUpdateBo updateBo) {
        userAddressService.update(userId, updateBo);
        return Result.success();
    }

    @Login
    @Operation(summary = "删除地址")
    @PostMapping("delete")
    public Result<Void> delete(@RequestAttribute("userId") Long userId,
                                 @RequestBody @Validated UserAddressDeleteBo deleteBo) {
        userAddressService.delete(userId, deleteBo);
        return Result.success();
    }

    @Login
    @Operation(summary = "设置默认地址")
    @PostMapping("setDefault")
    public Result<Void> setDefault(@RequestAttribute("userId") Long userId,
                                   @RequestBody @Validated UserAddressSetDefaultBo setDefaultBo) {
        userAddressService.setDefault(userId, setDefaultBo);
        return Result.success();
    }
}
