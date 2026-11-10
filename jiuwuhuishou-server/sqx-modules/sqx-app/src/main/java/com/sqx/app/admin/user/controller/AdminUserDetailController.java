package com.sqx.app.admin.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.address.domain.vo.UserAddressVo;
import com.sqx.app.admin.user.domain.bo.AdminUserAddressListBo;
import com.sqx.app.admin.user.domain.bo.AdminUserSignRecordPageBo;
import com.sqx.app.admin.user.domain.bo.AdminUserWelfareInfoBo;
import com.sqx.app.admin.user.domain.bo.AdminUserWelfareUpdateCoinBo;
import com.sqx.app.admin.user.service.AdminUserDetailService;
import com.sqx.app.welfare.domain.entity.UserSignRecord;
import com.sqx.app.welfare.domain.vo.WelfareSummaryVo;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "管理端-用户详情扩展", description = "管理端-用户详情扩展")
@RestController
@RequestMapping("/admin/base/user/detail")
public class AdminUserDetailController {

    @Resource
    private AdminUserDetailService adminUserDetailService;

    @Operation(summary = "用户地址列表")
    @PostMapping("address/list")
    public Result<List<UserAddressVo>> addressList(@RequestBody @Validated AdminUserAddressListBo listBo) {
        return Result.success(adminUserDetailService.listAddresses(listBo));
    }

    @Operation(summary = "用户积分信息")
    @PostMapping("welfare/info")
    public Result<WelfareSummaryVo> welfareInfo(@RequestBody @Validated AdminUserWelfareInfoBo infoBo) {
        return Result.success(adminUserDetailService.welfareInfo(infoBo));
    }

    @Operation(summary = "调整用户积分")
    @PostMapping("welfare/updateCoin")
    public Result<Void> updateWelfareCoin(@RequestBody @Validated AdminUserWelfareUpdateCoinBo updateBo) {
        adminUserDetailService.updateWelfareCoin(updateBo);
        return Result.success();
    }

    @Operation(summary = "用户积分明细")
    @PostMapping("signRecord/page")
    public Result<IPage<UserSignRecord>> signRecordPage(@RequestBody @Validated AdminUserSignRecordPageBo pageBo) {
        return Result.success(adminUserDetailService.signRecordPage(pageBo));
    }
}
