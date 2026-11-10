package com.sqx.app.recycle.book.controller;

import com.sqx.app.recycle.book.domain.vo.BookCategoryVo;
import com.sqx.app.recycle.book.domain.vo.BookEstimateConfigVo;
import com.sqx.app.recycle.book.service.BookRecycleService;
import com.sqx.app.recycle.clothes.domain.vo.ClothesWeightOptionVo;
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
 * 书籍回收 前端控制器
 */
@Tag(name = "用户端-书籍回收", description = "用户端-书籍回收")
@RestController
@RequestMapping("/app/recycle/book")
public class AppBookRecycleController {

    @Resource
    private BookRecycleService bookRecycleService;

    @Operation(summary = "书籍回收分类列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = BookCategoryVo.class)))
    @GetMapping("category/list")
    public Result<List<BookCategoryVo>> categoryList() {
        return Result.success(bookRecycleService.listCategories());
    }

    @Operation(summary = "书籍预约预估重量选项列表")
    @ApiResponse(content = @Content(schema = @Schema(implementation = ClothesWeightOptionVo.class)))
    @GetMapping("weightOption/list")
    public Result<List<ClothesWeightOptionVo>> weightOptionList() {
        return Result.success(bookRecycleService.listWeightOptions());
    }

    @Operation(summary = "书籍预估配置")
    @ApiResponse(content = @Content(schema = @Schema(implementation = BookEstimateConfigVo.class)))
    @GetMapping("estimate/config")
    public Result<BookEstimateConfigVo> estimateConfig() {
        return Result.success(bookRecycleService.getEstimateConfig());
    }
}
