package com.sqx.app.recycle.config.controller;

import com.sqx.app.recycle.config.domain.bo.ServiceAreaCheckBo;
import com.sqx.app.recycle.config.domain.vo.RecycleCategoryVo;
import com.sqx.app.recycle.config.domain.vo.RecycleRuleVo;
import com.sqx.app.recycle.config.domain.vo.ServiceAreaCheckVo;
import com.sqx.app.recycle.config.service.RecycleConfigService;
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
 * 回收配置 前端控制器
 */
@Tag(name = "用户端-回收配置", description = "用户端-回收配置")
@RestController
@RequestMapping("/app/recycle")
public class AppRecycleConfigController {

    @Resource
    private RecycleConfigService recycleConfigService;

    @Operation(summary = "回收分类列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = RecycleCategoryVo.class)))
    @GetMapping("category/list")
    public Result<List<RecycleCategoryVo>> categoryList() {
        return Result.success(recycleConfigService.listCategories());
    }

    @Operation(summary = "回收规则列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = RecycleRuleVo.class)))
    @GetMapping("rule/list")
    public Result<List<RecycleRuleVo>> ruleList() {
        return Result.success(recycleConfigService.listRules());
    }

    @Operation(summary = "服务区域校验")
    @ApiResponse(content = @Content(schema = @Schema(implementation = ServiceAreaCheckVo.class)))
    @PostMapping("serviceArea/check")
    public Result<ServiceAreaCheckVo> serviceAreaCheck(@RequestBody @Validated ServiceAreaCheckBo checkBo) {
        return Result.success(recycleConfigService.checkServiceArea(checkBo));
    }
}
