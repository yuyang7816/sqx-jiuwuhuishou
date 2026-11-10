package com.sqx.app.admin.ops.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.ops.domain.bo.AdminWelfareUserPageBo;
import com.sqx.app.admin.ops.domain.vo.AdminWelfareUserVo;
import com.sqx.app.admin.ops.service.AdminWelfareUserManageService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-用户福利", description = "管理端-用户福利")
@RestController
@RequestMapping("/admin/welfare/user")
public class AdminWelfareUserController {

    @Resource
    private AdminWelfareUserManageService adminWelfareUserManageService;

    @Operation(summary = "用户福利分页")
    @PostMapping("page")
    public Result<IPage<AdminWelfareUserVo>> page(@RequestBody @Validated AdminWelfareUserPageBo pageBo) {
        return Result.success(adminWelfareUserManageService.pageInfo(pageBo));
    }
}
