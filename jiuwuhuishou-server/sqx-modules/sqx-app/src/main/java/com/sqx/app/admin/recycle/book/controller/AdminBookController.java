package com.sqx.app.admin.recycle.book.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookCategoryPageBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookCategorySaveBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookCategoryUpdateBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookWeightOptionPageBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookWeightOptionSaveBo;
import com.sqx.app.admin.recycle.book.domain.bo.AdminBookWeightOptionUpdateBo;
import com.sqx.app.admin.recycle.book.service.AdminBookManageService;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.recycle.book.domain.entity.BookCategory;
import com.sqx.app.recycle.book.domain.entity.BookWeightOption;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-书籍主数据", description = "书籍分类/预约重量配置（用户端 /app/recycle/book）")
@RestController
@RequestMapping("/admin/recycle/book")
public class AdminBookController {

    @Resource
    private AdminBookManageService adminBookManageService;

    @Operation(summary = "分类-保存")
    @PostMapping("category/save")
    public Result<Void> saveCategory(@RequestBody @Validated AdminBookCategorySaveBo saveBo) {
        adminBookManageService.saveCategory(saveBo);
        return Result.success();
    }

    @Operation(summary = "分类-分页")
    @PostMapping("category/page")
    public Result<IPage<BookCategory>> pageCategory(@RequestBody @Validated AdminBookCategoryPageBo pageBo) {
        return Result.success(adminBookManageService.pageCategory(pageBo));
    }

    @Operation(summary = "分类-详情")
    @PostMapping("category/info")
    public Result<BookCategory> infoCategory(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        return Result.success(adminBookManageService.infoCategory(infoBo));
    }

    @Operation(summary = "分类-更新")
    @PostMapping("category/update")
    public Result<Void> updateCategory(@RequestBody @Validated AdminBookCategoryUpdateBo updateBo) {
        adminBookManageService.updateCategory(updateBo);
        return Result.success();
    }

    @Operation(summary = "分类-删除")
    @PostMapping("category/delete")
    public Result<Void> deleteCategory(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        adminBookManageService.deleteCategory(infoBo);
        return Result.success();
    }

    @Operation(summary = "预约重量配置-保存")
    @PostMapping("weightOption/save")
    public Result<Void> saveWeightOption(@RequestBody @Validated AdminBookWeightOptionSaveBo saveBo) {
        adminBookManageService.saveWeightOption(saveBo);
        return Result.success();
    }

    @Operation(summary = "预约重量配置-分页")
    @PostMapping("weightOption/page")
    public Result<IPage<BookWeightOption>> pageWeightOption(@RequestBody @Validated AdminBookWeightOptionPageBo pageBo) {
        return Result.success(adminBookManageService.pageWeightOption(pageBo));
    }

    @Operation(summary = "预约重量配置-详情")
    @PostMapping("weightOption/info")
    public Result<BookWeightOption> infoWeightOption(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        return Result.success(adminBookManageService.infoWeightOption(infoBo));
    }

    @Operation(summary = "预约重量配置-更新")
    @PostMapping("weightOption/update")
    public Result<Void> updateWeightOption(@RequestBody @Validated AdminBookWeightOptionUpdateBo updateBo) {
        adminBookManageService.updateWeightOption(updateBo);
        return Result.success();
    }

    @Operation(summary = "预约重量配置-删除")
    @PostMapping("weightOption/delete")
    public Result<Void> deleteWeightOption(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        adminBookManageService.deleteWeightOption(infoBo);
        return Result.success();
    }
}
