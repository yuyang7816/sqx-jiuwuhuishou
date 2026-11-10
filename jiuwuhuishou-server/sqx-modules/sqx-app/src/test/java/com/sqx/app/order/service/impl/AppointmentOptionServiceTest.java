package com.sqx.app.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.order.db.service.IRecycleAppointmentSlotService;
import com.sqx.app.order.domain.entity.RecycleAppointmentSlot;
import com.sqx.app.order.domain.vo.AppointmentDateOptionVo;
import com.sqx.app.order.domain.vo.AppointmentSlotOptionVo;
import com.sqx.common.redis.utils.ConfigUtil;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentOptionServiceTest {

    @Mock
    private IRecycleAppointmentSlotService iRecycleAppointmentSlotService;

    @InjectMocks
    private AppointmentOptionServiceImpl appointmentOptionService;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(RecycleAppointmentSlot.class);
    }

    @Test
    void listOptionsShouldUseDbSlotsAndNumericDays() {
        RecycleAppointmentSlot slot = new RecycleAppointmentSlot();
        slot.setStartTime("10:00");
        slot.setEndTime("12:00");
        when(iRecycleAppointmentSlotService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(slot));

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("recycle_appointment_slots")))
                    .thenReturn("3");
            List<AppointmentDateOptionVo> options = appointmentOptionService.listOptions();
            assertEquals(3, options.size());
            assertEquals("今日", options.get(0).getLabel());
            assertEquals("明日", options.get(1).getLabel());
            assertEquals("后日", options.get(2).getLabel());
            assertFalse(options.get(0).getSlots().isEmpty());
        }
    }

    @Test
    void listOptionsShouldFallbackDefaultSlotsAndJsonDays() {
        when(iRecycleAppointmentSlotService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("recycle_appointment_slots")))
                    .thenReturn("{\"days\":4}");
            List<AppointmentDateOptionVo> options = appointmentOptionService.listOptions();
            assertEquals(4, options.size());
            assertEquals(3, options.get(0).getSlots().size());
        }
    }

    @Test
    void listOptionsShouldUseDefaultDaysWhenConfigBlankOrInvalid() {
        when(iRecycleAppointmentSlotService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("recycle_appointment_slots")))
                    .thenReturn(" ");
            assertEquals(5, appointmentOptionService.listOptions().size());

            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("recycle_appointment_slots")))
                    .thenReturn("{\"days\":0}");
            assertEquals(5, appointmentOptionService.listOptions().size());

            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("recycle_appointment_slots")))
                    .thenReturn("not-a-number");
            assertEquals(5, appointmentOptionService.listOptions().size());

            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("recycle_appointment_slots")))
                    .thenThrow(new RuntimeException("boom"));
            assertEquals(5, appointmentOptionService.listOptions().size());
        }
    }

    @Test
    void listOptionsShouldSkipInvalidSlotTemplatesAndMarkExpired() {
        RecycleAppointmentSlot bad = new RecycleAppointmentSlot();
        bad.setStartTime(" ");
        bad.setEndTime("12:00");
        RecycleAppointmentSlot weird = new RecycleAppointmentSlot();
        weird.setStartTime("xx");
        weird.setEndTime("12:00");
        RecycleAppointmentSlot badNumber = new RecycleAppointmentSlot();
        badNumber.setStartTime("aa:bb");
        badNumber.setEndTime("12:00");
        RecycleAppointmentSlot past = new RecycleAppointmentSlot();
        past.setStartTime("00:00");
        past.setEndTime("01:00");
        when(iRecycleAppointmentSlotService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(null, bad, weird, badNumber, past));

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("recycle_appointment_slots")))
                    .thenReturn("1");
            List<AppointmentDateOptionVo> options = appointmentOptionService.listOptions();
            assertEquals(1, options.size());
            List<AppointmentSlotOptionVo> slots = options.get(0).getSlots();
            // null/空白 start 被跳过；xx、aa:bb、00:00 均进入列表（前两者 parse 失败不算过期）
            assertEquals(3, slots.size());
            AppointmentSlotOptionVo expired = slots.stream()
                    .filter(s -> Boolean.TRUE.equals(s.getDisabled()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(expired);
            assertEquals("expired", expired.getDisabledReason());
        }
    }

    @Test
    void listOptionsShouldUseWeekdayLabelAfterDay3() {
        when(iRecycleAppointmentSlotService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault(eq("recycle_appointment_slots")))
                    .thenReturn("7");
            List<AppointmentDateOptionVo> options = appointmentOptionService.listOptions();
            assertEquals(7, options.size());
            assertTrue(options.get(3).getLabel().startsWith("周"));
        }
    }

    @Test
    void resolveSlotEndTimeShouldMatchConfiguredSlot() {
        RecycleAppointmentSlot slot = new RecycleAppointmentSlot();
        slot.setStartTime("14:00");
        slot.setEndTime("16:00");
        when(iRecycleAppointmentSlotService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(slot));

        java.time.LocalDateTime start = java.time.LocalDateTime.of(2026, 9, 24, 14, 0, 0);
        java.time.LocalDateTime end = appointmentOptionService.resolveSlotEndTime(start);
        assertNotNull(end);
        assertEquals(16, end.getHour());
        assertEquals(0, end.getMinute());
        assertEquals(start.toLocalDate(), end.toLocalDate());

        assertNull(appointmentOptionService.resolveSlotEndTime(null));
        assertNull(appointmentOptionService.resolveSlotEndTime(start.withHour(15)));
    }
}
