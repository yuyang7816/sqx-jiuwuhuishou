package com.sqx.app.review.controller;

import com.sqx.app.base.auth.auth.annotation.Login;
import com.sqx.app.review.domain.bo.ReviewInfoBo;
import com.sqx.app.review.domain.bo.ReviewSaveBo;
import com.sqx.app.review.domain.vo.ReviewInfoVo;
import com.sqx.app.review.service.ReviewService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "用户端-订单评价", description = "评价与投诉")
@RestController
@RequestMapping("/app/review")
public class AppReviewController {

    @Resource
    private ReviewService reviewService;

    @Login
    @Operation(summary = "评价信息")
    @PostMapping("info")
    public Result<ReviewInfoVo> info(@RequestAttribute("userId") Long userId,
                                     @RequestBody @Validated ReviewInfoBo infoBo) {
        return Result.success(reviewService.info(userId, infoBo));
    }

    @Login
    @Operation(summary = "提交评价")
    @PostMapping("save")
    public Result<ReviewInfoVo> save(@RequestAttribute("userId") Long userId,
                                     @RequestBody @Validated ReviewSaveBo saveBo) {
        return Result.success(reviewService.save(userId, saveBo));
    }
}
