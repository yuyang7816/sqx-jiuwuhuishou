package com.sqx.app.brand.controller;

import com.sqx.app.base.auth.auth.annotation.Login;
import com.sqx.app.brand.domain.bo.BrandCooperationSaveBo;
import com.sqx.app.brand.service.BrandCooperationService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Tag(name = "用户端-品牌合作", description = "品牌合作意向提交")
@RestController
@RequestMapping("/app/brand/cooperation")
public class AppBrandCooperationController {

    @Resource
    private BrandCooperationService brandCooperationService;

    @Login
    @Operation(summary = "提交品牌合作意向")
    @PostMapping("save")
    public Result<Void> save(@RequestAttribute Long userId,
                             @RequestBody @Validated BrandCooperationSaveBo saveBo) {
        brandCooperationService.save(userId, saveBo);
        return Result.success();
    }
}
