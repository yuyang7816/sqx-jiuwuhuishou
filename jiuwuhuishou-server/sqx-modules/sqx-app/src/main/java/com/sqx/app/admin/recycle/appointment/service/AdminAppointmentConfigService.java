package com.sqx.app.admin.recycle.appointment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sqx.app.admin.recycle.appointment.domain.bo.AdminAppointmentSlotPageBo;
import com.sqx.app.admin.recycle.appointment.domain.bo.AdminAppointmentSlotSaveBo;
import com.sqx.app.admin.recycle.appointment.domain.bo.AdminAppointmentSlotUpdateBo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.order.domain.entity.RecycleAppointmentSlot;

public interface AdminAppointmentConfigService {

    void saveSlot(AdminAppointmentSlotSaveBo saveBo);

    IPage<RecycleAppointmentSlot> pageSlot(AdminAppointmentSlotPageBo pageBo);

    RecycleAppointmentSlot infoSlot(AdminRecycleIdBo infoBo);

    void updateSlot(AdminAppointmentSlotUpdateBo updateBo);

    void deleteSlot(AdminRecycleIdBo infoBo);
}
