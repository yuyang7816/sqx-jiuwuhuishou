package com.sqx.app.recycle.digital.controller;

import com.sqx.app.recycle.digital.domain.bo.DigitalEstimateBo;
import com.sqx.app.recycle.digital.domain.bo.DigitalModelListBo;
import com.sqx.app.recycle.digital.domain.vo.DigitalBrandVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalEstimateVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalModelVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalSearchResultVo;
import com.sqx.app.recycle.digital.domain.vo.DigitalSpecPageVo;
import com.sqx.app.recycle.digital.service.DigitalRecycleService;
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
 * 数码回收 前端控制器
 */
@Tag(name = "用户端-数码回收", description = "用户端-数码回收")
@RestController
@RequestMapping("/app/recycle/digital")
public class AppDigitalRecycleController {

    @Resource
    private DigitalRecycleService digitalRecycleService;

    @Operation(summary = "数码品牌列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = DigitalBrandVo.class)))
    @GetMapping("brand/list")
    public Result<List<DigitalBrandVo>> brandList(@RequestParam(required = false, defaultValue = "digital") String scene) {
        return Result.success(digitalRecycleService.listBrands(scene));
    }

    @Operation(summary = "数码/奢侈品搜索")
    @ApiResponse(content = @Content(schema = @Schema(implementation = DigitalSearchResultVo.class)))
    @GetMapping("search")
    public Result<List<DigitalSearchResultVo>> search(@RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false, defaultValue = "digital") String scene,
                                                      @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return Result.success(digitalRecycleService.searchModels(keyword, scene, limit == null ? 20 : limit));
    }

    @Operation(summary = "热门搜索关键词")
    @GetMapping("search/hot")
    public Result<List<String>> hotKeywords(@RequestParam(required = false, defaultValue = "digital") String scene) {
        return Result.success(digitalRecycleService.hotKeywords(scene));
    }

    @Operation(summary = "数码型号列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = DigitalModelVo.class)))
    @PostMapping("model/list")
    public Result<List<DigitalModelVo>> modelList(@RequestBody @Validated DigitalModelListBo listBo) {
        return Result.success(digitalRecycleService.listModels(listBo.getBrandId(), listBo.getKeyword()));
    }

    @Operation(summary = "数码估价")
    @ApiResponse(content = @Content(schema = @Schema(implementation = DigitalEstimateVo.class)))
    @PostMapping("estimate")
    public Result<DigitalEstimateVo> estimate(@RequestBody @Validated DigitalEstimateBo estimateBo) {
        return Result.success(digitalRecycleService.estimate(estimateBo));
    }

    @Operation(summary = "数码商品规格页")
    @ApiResponse(content = @Content(schema = @Schema(implementation = DigitalSpecPageVo.class)))
    @GetMapping("spec/page")
    public Result<DigitalSpecPageVo> specPage(@RequestParam(required = false) Long modelId,
                                              @RequestParam(required = false, defaultValue = "digital") String scene,
                                              @RequestParam(required = false) String brandName,
                                              @RequestParam(required = false) String modelName) {
        return Result.success(digitalRecycleService.getSpecPage(modelId, scene, brandName, modelName));
    }
}
