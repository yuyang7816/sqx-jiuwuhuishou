package com.sqx.app.admin.servicechat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.servicechat.domain.bo.AdminServiceSessionCloseBo;
import com.sqx.app.admin.servicechat.domain.bo.AdminServiceSessionInfoBo;
import com.sqx.app.admin.servicechat.domain.bo.AdminServiceSessionPageBo;
import com.sqx.app.admin.servicechat.domain.vo.AdminServiceSessionVo;
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

@Tag(name = "管理端-客服会话", description = "管理端-客服会话")
@RestController
@RequestMapping("/admin/service/session")
public class AdminServiceSessionController {

    @Resource
    private AdminServiceChatManageService adminServiceChatManageService;

    @Operation(summary = "会话分页")
    @PostMapping("page")
    public Result<IPage<AdminServiceSessionVo>> page(@RequestBody @Validated AdminServiceSessionPageBo pageBo) {
        return Result.success(adminServiceChatManageService.pageSession(pageBo));
    }

    @Operation(summary = "会话详情")
    @PostMapping("info")
    public Result<AdminServiceSessionVo> info(@RequestBody @Validated AdminServiceSessionInfoBo infoBo) {
        return Result.success(adminServiceChatManageService.sessionInfo(infoBo));
    }

    @Operation(summary = "关闭会话")
    @PostMapping("close")
    public Result<Void> close(@RequestBody @Validated AdminServiceSessionCloseBo closeBo) {
        adminServiceChatManageService.closeSession(closeBo);
        return Result.success();
    }
}
