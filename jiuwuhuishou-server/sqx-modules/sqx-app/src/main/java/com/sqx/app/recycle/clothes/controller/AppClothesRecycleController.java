package com.sqx.app.recycle.clothes.controller;

import com.sqx.app.recycle.clothes.domain.vo.ClothesCategoryVo;
import com.sqx.app.recycle.clothes.domain.vo.ClothesEstimateConfigVo;
import com.sqx.app.recycle.clothes.service.ClothesRecycleService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 旧衣回收 前端控制器
 */
@Tag(name = "用户端-旧衣回收", description = "用户端-旧衣回收")
@RestController
@RequestMapping("/app/recycle/clothes")
public class AppClothesRecycleController {

    @Resource
    private ClothesRecycleService clothesRecycleService;

    @Operation(summary = "旧衣回收分类列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = ClothesCategoryVo.class)))
    @GetMapping("category/list")
    public Result<List<ClothesCategoryVo>> categoryList() {
        return Result.success(clothesRecycleService.listCategories());
    }

    @Operation(summary = "旧衣预估配置")
    @ApiResponse(content = @Content(schema = @Schema(implementation = ClothesEstimateConfigVo.class)))
    @GetMapping("estimate/config")
    public Result<ClothesEstimateConfigVo> estimateConfig() {
        return Result.success(clothesRecycleService.getEstimateConfig());
    }
}
