package com.sqx.app.admin.servicechat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.servicechat.domain.bo.*;
import com.sqx.app.admin.servicechat.service.AdminServiceChatConfigService;
import com.sqx.app.servicechat.domain.entity.CustomerServiceShortcut;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-客服快捷提示词", description = "客服输入框上方快捷提示词配置")
@RestController
@RequestMapping("/admin/service/shortcut")
public class AdminServiceShortcutController {

    @Resource
    private AdminServiceChatConfigService adminServiceChatConfigService;

    @Operation(summary = "保存")
    @PostMapping("save")
    public Result<Void> save(@RequestBody @Validated AdminServiceShortcutSaveBo saveBo) {
        adminServiceChatConfigService.saveShortcut(saveBo);
        return Result.success();
    }

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<CustomerServiceShortcut>> page(@RequestBody @Validated AdminServiceShortcutPageBo pageBo) {
        return Result.success(adminServiceChatConfigService.pageShortcut(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<CustomerServiceShortcut> info(@RequestBody @Validated AdminServiceConfigIdBo infoBo) {
        return Result.success(adminServiceChatConfigService.infoShortcut(infoBo));
    }

    @Operation(summary = "更新")
    @PostMapping("update")
    public Result<Void> update(@RequestBody @Validated AdminServiceShortcutUpdateBo updateBo) {
        adminServiceChatConfigService.updateShortcut(updateBo);
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("delete")
    public Result<Void> delete(@RequestBody @Validated AdminServiceConfigIdBo infoBo) {
        adminServiceChatConfigService.deleteShortcut(infoBo);
        return Result.success();
    }
}
