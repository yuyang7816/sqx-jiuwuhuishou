package com.sqx.app.admin.servicechat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.servicechat.domain.bo.AdminServiceMessagePageBo;
import com.sqx.app.admin.servicechat.domain.bo.AdminServiceMessageSaveBo;
import com.sqx.app.admin.servicechat.domain.vo.AdminServiceMessageVo;
import com.sqx.app.admin.servicechat.service.AdminServiceChatManageService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-客服消息", description = "管理端-客服消息")
@RestController
@RequestMapping("/admin/service/message")
public class AdminServiceMessageController {

    @Resource
    private AdminServiceChatManageService adminServiceChatManageService;

    @Operation(summary = "消息分页")
    @PostMapping("page")
    public Result<IPage<AdminServiceMessageVo>> page(@RequestBody @Validated AdminServiceMessagePageBo pageBo) {
        return Result.success(adminServiceChatManageService.pageMessage(pageBo));
    }

    @Operation(summary = "客服回复")
    @PostMapping("save")
    public Result<AdminServiceMessageVo> save(@RequestBody @Validated AdminServiceMessageSaveBo saveBo) {
        return Result.success(adminServiceChatManageService.saveMessage(saveBo));
    }
}
