package com.sqx.app.admin.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.user.domain.bo.AdminUserAddressPageBo;
import com.sqx.app.admin.user.domain.vo.AdminUserAddressVo;
import com.sqx.app.admin.user.service.AdminUserAddressManageService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-用户地址", description = "管理端-用户地址")
@RestController
@RequestMapping("/admin/address")
public class AdminUserAddressController {

    @Resource
    private AdminUserAddressManageService adminUserAddressManageService;

    @Operation(summary = "地址分页")
    @PostMapping("page")
    public Result<IPage<AdminUserAddressVo>> page(@RequestBody @Validated AdminUserAddressPageBo pageBo) {
        return Result.success(adminUserAddressManageService.pageInfo(pageBo));
    }
}
