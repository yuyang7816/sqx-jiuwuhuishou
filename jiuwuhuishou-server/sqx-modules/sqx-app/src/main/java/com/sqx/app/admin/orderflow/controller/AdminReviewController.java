package com.sqx.app.admin.orderflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.orderflow.domain.bo.AdminReviewInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminReviewPageBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminReviewReplyBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminReviewVo;
import com.sqx.app.admin.orderflow.service.AdminReviewManageService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-订单评价", description = "管理端-订单评价")
@RestController
@RequestMapping("/admin/review")
public class AdminReviewController {

    @Resource
    private AdminReviewManageService adminReviewManageService;

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<AdminReviewVo>> page(@RequestBody @Validated AdminReviewPageBo pageBo) {
        return Result.success(adminReviewManageService.pageInfo(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<AdminReviewVo> info(@RequestBody @Validated AdminReviewInfoBo infoBo) {
        return Result.success(adminReviewManageService.info(infoBo));
    }

    @Operation(summary = "回复评价")
    @PostMapping("reply")
    public Result<Void> reply(@RequestBody @Validated AdminReviewReplyBo replyBo) {
        adminReviewManageService.reply(replyBo);
        return Result.success();
    }
}
