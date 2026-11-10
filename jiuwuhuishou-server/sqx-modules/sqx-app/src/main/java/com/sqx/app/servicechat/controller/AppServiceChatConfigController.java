package com.sqx.app.servicechat.controller;

import com.sqx.app.servicechat.domain.vo.ServiceQuickQuestionVo;
import com.sqx.app.servicechat.domain.vo.ServiceShortcutVo;
import com.sqx.app.servicechat.service.ServiceChatConfigService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "用户端-客服配置", description = "快捷问题与快捷提示词")
@RestController
@RequestMapping("/app/service/config")
public class AppServiceChatConfigController {

    @Resource
    private ServiceChatConfigService serviceChatConfigService;

    @Operation(summary = "欢迎区快捷问题列表")
    @GetMapping("quickQuestion/list")
    public Result<List<ServiceQuickQuestionVo>> quickQuestionList() {
        return Result.success(serviceChatConfigService.listWelcomeQuestions());
    }

    @Operation(summary = "输入框上方快捷提示词列表")
    @GetMapping("shortcut/list")
    public Result<List<ServiceShortcutVo>> shortcutList() {
        return Result.success(serviceChatConfigService.listShortcuts());
    }
}
