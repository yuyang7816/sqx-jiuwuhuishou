package com.sqx.app.recycle.appliance.controller;

import com.sqx.app.recycle.appliance.domain.bo.ApplianceEstimateBo;
import com.sqx.app.recycle.appliance.domain.bo.ApplianceModelListBo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceCategoryOverviewVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceCategoryVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceEstimateVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceHeroFlowVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceModelVo;
import com.sqx.app.recycle.appliance.domain.vo.ApplianceSpecPageVo;
import com.sqx.app.recycle.appliance.service.ApplianceRecycleService;
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
 * 家电回收 前端控制器
 */
@Tag(name = "用户端-家电回收", description = "用户端-家电回收")
@RestController
@RequestMapping("/app/recycle/appliance")
public class AppApplianceRecycleController {

    @Resource
    private ApplianceRecycleService applianceRecycleService;

    @Operation(summary = "家电估价页配置")
    @ApiResponse(content = @Content(schema = @Schema(implementation = ApplianceSpecPageVo.class)))
    @GetMapping("spec/page")
    public Result<ApplianceSpecPageVo> specPage(@RequestParam(required = false) String categoryName) {
        return Result.success(applianceRecycleService.getSpecPage(categoryName));
    }

    @Operation(summary = "家电品类概览（含价格卡片）")
    @ApiResponse(content = @Content(schema = @Schema(implementation = ApplianceCategoryOverviewVo.class)))
    @GetMapping("category/overview")
    public Result<List<ApplianceCategoryOverviewVo>> categoryOverview() {
        return Result.success(applianceRecycleService.listCategoryOverview());
    }

    @Operation(summary = "家电品类列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = ApplianceCategoryVo.class)))
    @GetMapping("category/list")
    public Result<List<ApplianceCategoryVo>> categoryList() {
        return Result.success(applianceRecycleService.listCategories());
    }

    @Operation(summary = "家电型号列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = ApplianceModelVo.class)))
    @PostMapping("model/list")
    public Result<List<ApplianceModelVo>> modelList(@RequestBody @Validated ApplianceModelListBo listBo) {
        return Result.success(applianceRecycleService.listModels(listBo.getCategoryId()));
    }

    @Operation(summary = "家电回收流程步骤列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = ApplianceHeroFlowVo.class)))
    @GetMapping("heroFlow/list")
    public Result<List<ApplianceHeroFlowVo>> heroFlowList() {
        return Result.success(applianceRecycleService.listHeroFlows());
    }

    @Operation(summary = "家电估价")
    @ApiResponse(content = @Content(schema = @Schema(implementation = ApplianceEstimateVo.class)))
    @PostMapping("estimate")
    public Result<ApplianceEstimateVo> estimate(@RequestBody @Validated ApplianceEstimateBo estimateBo) {
        return Result.success(applianceRecycleService.estimate(estimateBo));
    }
}
