package com.sqx.app.admin.datacenter.controller;

import com.sqx.app.admin.datacenter.domain.bo.AdminDatacenterOverviewBo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterBreakdownVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterGrowthVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterOverviewVo;
import com.sqx.app.admin.datacenter.domain.vo.AdminDatacenterTrendVo;
import com.sqx.app.admin.datacenter.service.AdminDatacenterService;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-数据中心", description = "管理端-数据中心（老板看板）")
@RestController
@RequestMapping("/admin/datacenter")
public class AdminDatacenterController {

    @Resource
    private AdminDatacenterService adminDatacenterService;

    @Operation(summary = "概览（P0：KPI + 漏斗 + 资金）")
    @PostMapping("overview")
    public Result<AdminDatacenterOverviewVo> overview(@RequestBody(required = false) AdminDatacenterOverviewBo bo) {
        return Result.success(adminDatacenterService.overview(bo == null ? new AdminDatacenterOverviewBo() : bo));
    }

    @Operation(summary = "趋势（P1：按日订单量/成交额）")
    @PostMapping("trend")
    public Result<AdminDatacenterTrendVo> trend(@RequestBody(required = false) AdminDatacenterOverviewBo bo) {
        return Result.success(adminDatacenterService.trend(bo == null ? new AdminDatacenterOverviewBo() : bo));
    }

    @Operation(summary = "拆解（P1：品类/履约/质量）")
    @PostMapping("breakdown")
    public Result<AdminDatacenterBreakdownVo> breakdown(@RequestBody(required = false) AdminDatacenterOverviewBo bo) {
        return Result.success(adminDatacenterService.breakdown(bo == null ? new AdminDatacenterOverviewBo() : bo));
    }

    @Operation(summary = "增长运营（P2：提现/分销/福利/客服/品牌/钱包）")
    @PostMapping("growth")
    public Result<AdminDatacenterGrowthVo> growth(@RequestBody(required = false) AdminDatacenterOverviewBo bo) {
        return Result.success(adminDatacenterService.growth(bo == null ? new AdminDatacenterOverviewBo() : bo));
    }
}
