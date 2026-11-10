package com.sqx.app.admin.recycle.appointment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.appointment.domain.bo.AdminAppointmentSlotPageBo;
import com.sqx.app.admin.recycle.appointment.domain.bo.AdminAppointmentSlotSaveBo;
import com.sqx.app.admin.recycle.appointment.domain.bo.AdminAppointmentSlotUpdateBo;
import com.sqx.app.admin.recycle.appointment.service.AdminAppointmentConfigService;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.order.domain.entity.RecycleAppointmentSlot;
import com.sqx.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "管理端-上门预约时间段", description = "时间段 CRUD（用户端 /app/order/appointment/options）")
@RestController
@RequestMapping("/admin/recycle")
public class AdminAppointmentConfigController {

    @Resource
    private AdminAppointmentConfigService adminAppointmentConfigService;

    @Operation(summary = "时间段-保存")
    @PostMapping("appointmentSlot/save")
    public Result<Void> saveSlot(@RequestBody @Validated AdminAppointmentSlotSaveBo saveBo) {
        adminAppointmentConfigService.saveSlot(saveBo);
        return Result.success();
    }

    @Operation(summary = "时间段-分页")
    @PostMapping("appointmentSlot/page")
    public Result<IPage<RecycleAppointmentSlot>> pageSlot(@RequestBody @Validated AdminAppointmentSlotPageBo pageBo) {
        return Result.success(adminAppointmentConfigService.pageSlot(pageBo));
    }

    @Operation(summary = "时间段-详情")
    @PostMapping("appointmentSlot/info")
    public Result<RecycleAppointmentSlot> infoSlot(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        return Result.success(adminAppointmentConfigService.infoSlot(infoBo));
    }

    @Operation(summary = "时间段-更新")
    @PostMapping("appointmentSlot/update")
    public Result<Void> updateSlot(@RequestBody @Validated AdminAppointmentSlotUpdateBo updateBo) {
        adminAppointmentConfigService.updateSlot(updateBo);
        return Result.success();
    }

    @Operation(summary = "时间段-删除")
    @PostMapping("appointmentSlot/delete")
    public Result<Void> deleteSlot(@RequestBody @Validated AdminRecycleIdBo infoBo) {
        adminAppointmentConfigService.deleteSlot(infoBo);
        return Result.success();
    }
}
