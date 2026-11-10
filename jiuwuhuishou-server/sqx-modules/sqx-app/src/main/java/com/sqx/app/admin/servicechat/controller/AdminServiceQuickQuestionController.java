package com.sqx.app.admin.servicechat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.servicechat.domain.bo.*;
import com.sqx.app.admin.servicechat.service.AdminServiceChatConfigService;
import com.sqx.app.servicechat.domain.entity.CustomerServiceQuickQuestion;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-客服快捷问题", description = "客服快捷问题与自动回复配置")
@RestController
@RequestMapping("/admin/service/quickQuestion")
public class AdminServiceQuickQuestionController {

    @Resource
    private AdminServiceChatConfigService adminServiceChatConfigService;

    @Operation(summary = "保存")
    @PostMapping("save")
    public Result<Void> save(@RequestBody @Validated AdminServiceQuickQuestionSaveBo saveBo) {
        adminServiceChatConfigService.saveQuickQuestion(saveBo);
        return Result.success();
    }

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<CustomerServiceQuickQuestion>> page(@RequestBody @Validated AdminServiceQuickQuestionPageBo pageBo) {
        return Result.success(adminServiceChatConfigService.pageQuickQuestion(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<CustomerServiceQuickQuestion> info(@RequestBody @Validated AdminServiceConfigIdBo infoBo) {
        return Result.success(adminServiceChatConfigService.infoQuickQuestion(infoBo));
    }

    @Operation(summary = "更新")
    @PostMapping("update")
    public Result<Void> update(@RequestBody @Validated AdminServiceQuickQuestionUpdateBo updateBo) {
        adminServiceChatConfigService.updateQuickQuestion(updateBo);
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("delete")
    public Result<Void> delete(@RequestBody @Validated AdminServiceConfigIdBo infoBo) {
        adminServiceChatConfigService.deleteQuickQuestion(infoBo);
        return Result.success();
    }
}
