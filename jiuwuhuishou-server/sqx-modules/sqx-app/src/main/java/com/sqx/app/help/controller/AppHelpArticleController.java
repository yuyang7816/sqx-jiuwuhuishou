package com.sqx.app.help.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.help.domain.bo.HelpArticleInfoBo;
import com.sqx.app.help.domain.bo.HelpArticlePageBo;
import com.sqx.app.help.domain.vo.HelpArticleDetailVo;
import com.sqx.app.help.domain.vo.HelpArticleVo;
import com.sqx.app.help.service.HelpArticleService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "用户端-帮助中心", description = "帮助中心文章")
@RestController
@RequestMapping("/app/help/article")
public class AppHelpArticleController {

    @Resource
    private HelpArticleService helpArticleService;

    @Operation(summary = "文章列表")
    @PostMapping("page")
    public Result<IPage<HelpArticleVo>> page(@RequestBody @Validated HelpArticlePageBo pageBo) {
        return Result.success(helpArticleService.page(pageBo));
    }

    @Operation(summary = "文章详情")
    @PostMapping("info")
    public Result<HelpArticleDetailVo> info(@RequestBody @Validated HelpArticleInfoBo infoBo) {
        return Result.success(helpArticleService.info(infoBo));
    }
}
