package com.sqx.app.servicechat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.base.auth.auth.annotation.Login;
import com.sqx.app.servicechat.domain.bo.ServiceMessagePageBo;
import com.sqx.app.servicechat.domain.bo.ServiceMessageQuickBo;
import com.sqx.app.servicechat.domain.bo.ServiceMessageSaveBo;
import com.sqx.app.servicechat.domain.vo.ServiceMessageQuickVo;
import com.sqx.app.servicechat.domain.vo.ServiceMessageVo;
import com.sqx.app.servicechat.service.ServiceMessageService;
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

@Tag(name = "用户端-客服消息", description = "客服会话消息")
@RestController
@RequestMapping("/app/service/message")
public class AppServiceMessageController {

    @Resource
    private ServiceMessageService serviceMessageService;

    @Login
    @Operation(summary = "消息列表")
    @PostMapping("page")
    public Result<IPage<ServiceMessageVo>> page(@RequestAttribute("userId") Long userId,
                                                @RequestBody @Validated ServiceMessagePageBo pageBo) {
        return Result.success(serviceMessageService.page(userId, pageBo));
    }

    @Login
    @Operation(summary = "发送消息")
    @PostMapping("save")
    public Result<ServiceMessageVo> save(@RequestAttribute("userId") Long userId,
                                       @RequestBody @Validated ServiceMessageSaveBo saveBo) {
        return Result.success(serviceMessageService.save(userId, saveBo));
    }

    @Login
    @Operation(summary = "快捷询问")
    @PostMapping("quick")
    public Result<ServiceMessageQuickVo> quick(@RequestAttribute("userId") Long userId,
                                               @RequestBody @Validated ServiceMessageQuickBo quickBo) {
        return Result.success(serviceMessageService.quickAsk(userId, quickBo));
    }
}
