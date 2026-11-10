package com.sqx.app.admin.recycle.appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.appointment.domain.bo.AdminAppointmentSlotPageBo;
import com.sqx.app.admin.recycle.appointment.domain.bo.AdminAppointmentSlotSaveBo;
import com.sqx.app.admin.recycle.appointment.domain.bo.AdminAppointmentSlotUpdateBo;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.order.db.service.IRecycleAppointmentSlotService;
import com.sqx.app.order.domain.entity.RecycleAppointmentSlot;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminAppointmentConfigServiceImplTest {

    @Mock
    private IRecycleAppointmentSlotService iRecycleAppointmentSlotService;

    @InjectMocks
    private AdminAppointmentConfigServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(RecycleAppointmentSlot.class);
    }

    @Test
    void saveSlotShouldValidateTime() {
        AdminAppointmentSlotSaveBo saveBo = new AdminAppointmentSlotSaveBo();
        assertThrows(BizException.class, () -> service.saveSlot(saveBo));

        saveBo.setStartTime("10:00");
        assertThrows(BizException.class, () -> service.saveSlot(saveBo));

        saveBo.setEndTime("bad");
        assertThrows(BizException.class, () -> service.saveSlot(saveBo));

        saveBo.setEndTime("09:00");
        assertThrows(BizException.class, () -> service.saveSlot(saveBo));

        saveBo.setEndTime("10:00");
        assertThrows(BizException.class, () -> service.saveSlot(saveBo));

        saveBo.setStartTime("10：00");
        saveBo.setEndTime("12:00");
        service.saveSlot(saveBo);
        ArgumentCaptor<RecycleAppointmentSlot> captor = ArgumentCaptor.forClass(RecycleAppointmentSlot.class);
        verify(iRecycleAppointmentSlotService).save(captor.capture());
        assertEquals("10:00", captor.getValue().getStartTime());
        assertEquals(0, captor.getValue().getSort());
        assertEquals(1, captor.getValue().getState());

        saveBo.setStartTime("09:00");
        saveBo.setSort(2);
        saveBo.setState(2);
        service.saveSlot(saveBo);
        verify(iRecycleAppointmentSlotService, times(2)).save(any(RecycleAppointmentSlot.class));
    }

    @Test
    void pageInfoUpdateDelete() {
        AdminAppointmentSlotPageBo pageBo = new AdminAppointmentSlotPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setState(1);
        Page<RecycleAppointmentSlot> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new RecycleAppointmentSlot()));
        when(iRecycleAppointmentSlotService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        assertEquals(1, service.pageSlot(pageBo).getRecords().size());

        AdminRecycleIdBo infoBo = new AdminRecycleIdBo();
        infoBo.setId(1L);
        when(iRecycleAppointmentSlotService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.infoSlot(infoBo));

        RecycleAppointmentSlot slot = new RecycleAppointmentSlot();
        slot.setId(1L);
        when(iRecycleAppointmentSlotService.getById(1L)).thenReturn(slot);
        assertEquals(1L, service.infoSlot(infoBo).getId());

        AdminAppointmentSlotUpdateBo updateBo = new AdminAppointmentSlotUpdateBo();
        updateBo.setId(1L);
        updateBo.setStartTime("08:00");
        updateBo.setEndTime("09:30");
        service.updateSlot(updateBo);
        verify(iRecycleAppointmentSlotService).updateById(any(RecycleAppointmentSlot.class));

        service.deleteSlot(infoBo);
        verify(iRecycleAppointmentSlotService).removeById(1L);
    }
}
