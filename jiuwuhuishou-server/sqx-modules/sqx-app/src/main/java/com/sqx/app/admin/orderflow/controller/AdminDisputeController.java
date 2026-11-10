package com.sqx.app.admin.orderflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.orderflow.domain.bo.AdminDisputeInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminDisputePageBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminDisputeResolveBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminDisputeVo;
import com.sqx.app.admin.orderflow.service.AdminDisputeManageService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-价格争议", description = "管理端-价格争议")
@RestController
@RequestMapping("/admin/dispute")
public class AdminDisputeController {

    @Resource
    private AdminDisputeManageService adminDisputeManageService;

    @Operation(summary = "列表")
    @PostMapping("page")
    public Result<IPage<AdminDisputeVo>> page(@RequestBody @Validated AdminDisputePageBo pageBo) {
        return Result.success(adminDisputeManageService.pageInfo(pageBo));
    }

    @Operation(summary = "详情")
    @PostMapping("info")
    public Result<AdminDisputeVo> info(@RequestBody @Validated AdminDisputeInfoBo infoBo) {
        return Result.success(adminDisputeManageService.info(infoBo));
    }

    @Operation(summary = "处理争议")
    @PostMapping("resolve")
    public Result<Void> resolve(@RequestBody @Validated AdminDisputeResolveBo resolveBo) {
        adminDisputeManageService.resolve(resolveBo);
        return Result.success();
    }
}
