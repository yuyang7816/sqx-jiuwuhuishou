package com.sqx.app.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.order.db.service.IRecycleAppointmentSlotService;
import com.sqx.app.order.domain.entity.RecycleAppointmentSlot;
import com.sqx.app.order.domain.vo.AppointmentDateOptionVo;
import com.sqx.app.order.domain.vo.AppointmentSlotOptionVo;
import com.sqx.app.order.service.AppointmentOptionService;
import com.sqx.common.redis.utils.ConfigUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 预约时段服务实现（天数读配置，时段读表 recycle_appointment_slot）
 */
@Service
public class AppointmentOptionServiceImpl implements AppointmentOptionService {

    private static final String CONFIG_KEY = "recycle_appointment_slots";
    private static final int DEFAULT_DAYS = 5;
    private static final Integer STATE_ENABLED = 1;
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("M月d日");

    @Resource
    private IRecycleAppointmentSlotService iRecycleAppointmentSlotService;

    @Override
    public List<AppointmentDateOptionVo> listOptions() {
        int days = resolveDays();
        List<RecycleAppointmentSlot> slotTemplates = listEnabledSlots();

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        List<AppointmentDateOptionVo> result = new ArrayList<>();

        for (int offset = 0; offset < days; offset++) {
            LocalDate date = today.plusDays(offset);
            AppointmentDateOptionVo dateOption = new AppointmentDateOptionVo();
            dateOption.setLabel(resolveDateLabel(offset, date));
            dateOption.setDay(date.format(DAY_FORMATTER));
            dateOption.setDateKey(date.toString());
            dateOption.setSlots(buildSlotsForDate(date, now, slotTemplates));
            result.add(dateOption);
        }
        return result;
    }

    @Override
    public LocalDateTime resolveSlotEndTime(LocalDateTime appointmentStart) {
        if (Objects.isNull(appointmentStart)) {
            return null;
        }
        String startHm = formatHm(appointmentStart.toLocalTime());
        for (RecycleAppointmentSlot slot : listEnabledSlots()) {
            if (slot == null || StringUtils.isAnyBlank(slot.getStartTime(), slot.getEndTime())) {
                continue;
            }
            if (!normalizeHm(slot.getStartTime()).equals(startHm)) {
                continue;
            }
            LocalTime endTime = parseHm(slot.getEndTime());
            if (endTime == null) {
                return null;
            }
            return LocalDateTime.of(appointmentStart.toLocalDate(), endTime);
        }
        return null;
    }

    private static String formatHm(LocalTime time) {
        return String.format("%02d:%02d", time.getHour(), time.getMinute());
    }

    private static String normalizeHm(String value) {
        LocalTime time = parseHm(value);
        return time == null ? StringUtils.trimToEmpty(value) : formatHm(time);
    }

    private static LocalTime parseHm(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String[] parts = value.trim().split(":");
        if (parts.length < 2) {
            return null;
        }
        try {
            return LocalTime.of(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private List<RecycleAppointmentSlot> listEnabledSlots() {
        List<RecycleAppointmentSlot> slots = iRecycleAppointmentSlotService.list(new LambdaQueryWrapper<RecycleAppointmentSlot>()
                .eq(RecycleAppointmentSlot::getState, STATE_ENABLED)
                .orderByAsc(RecycleAppointmentSlot::getSort)
                .orderByAsc(RecycleAppointmentSlot::getStartTime)
                .orderByAsc(RecycleAppointmentSlot::getId));
        if (CollectionUtils.isNotEmpty(slots)) {
            return slots;
        }
        return defaultSlots();
    }

    private List<RecycleAppointmentSlot> defaultSlots() {
        List<RecycleAppointmentSlot> slots = new ArrayList<>();
        slots.add(buildDefaultSlot("10:00", "12:00", 1));
        slots.add(buildDefaultSlot("14:00", "16:00", 2));
        slots.add(buildDefaultSlot("18:00", "20:00", 3));
        return slots;
    }

    private RecycleAppointmentSlot buildDefaultSlot(String start, String end, int sort) {
        RecycleAppointmentSlot slot = new RecycleAppointmentSlot();
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setSort(sort);
        slot.setState(STATE_ENABLED);
        return slot;
    }

    private int resolveDays() {
        try {
            String configValue = ConfigUtil.getConfigValueOrDefault(CONFIG_KEY);
            if (StringUtils.isBlank(configValue)) {
                return DEFAULT_DAYS;
            }
            String value = configValue.trim();
            if (NumberUtils.isCreatable(value)) {
                return Math.max(1, NumberUtils.toInt(value, DEFAULT_DAYS));
            }
            if (value.startsWith("{")) {
                JSONObject json = JSON.parseObject(value);
                int days = json.getIntValue("days");
                return days > 0 ? days : DEFAULT_DAYS;
            }
        } catch (Exception ignored) {
            // 使用默认配置
        }
        return DEFAULT_DAYS;
    }

    private List<AppointmentSlotOptionVo> buildSlotsForDate(LocalDate date, LocalDateTime now,
                                                            List<RecycleAppointmentSlot> slotTemplates) {
        List<AppointmentSlotOptionVo> slots = new ArrayList<>();
        for (RecycleAppointmentSlot template : slotTemplates) {
            if (template == null || StringUtils.isAnyBlank(template.getStartTime(), template.getEndTime())) {
                continue;
            }
            String time = template.getStartTime().trim() + " - " + template.getEndTime().trim();
            LocalDateTime slotStart = parseSlotStart(date, template.getStartTime().trim());
            boolean expired = slotStart != null && !slotStart.isAfter(now);
            boolean full = false;
            boolean disabled = expired || full;

            AppointmentSlotOptionVo slotVo = new AppointmentSlotOptionVo();
            slotVo.setTime(time);
            slotVo.setFull(full);
            slotVo.setDisabled(disabled);
            if (expired) {
                slotVo.setDisabledReason("expired");
                slotVo.setTip("该时段已过");
            } else {
                slotVo.setTip("履约侧已预留运力");
            }
            slots.add(slotVo);
        }
        return slots;
    }

    private LocalDateTime parseSlotStart(LocalDate date, String startTime) {
        String[] parts = startTime.split(":");
        if (parts.length < 2) {
            return null;
        }
        try {
            return date.atTime(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), 0);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static final String[] WEEKDAY_LABELS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    private String resolveDateLabel(int offset, LocalDate date) {
        if (offset == 0) {
            return "今日";
        }
        if (offset == 1) {
            return "明日";
        }
        if (offset == 2) {
            return "后日";
        }
        return WEEKDAY_LABELS[date.getDayOfWeek().getValue() - 1];
    }
}
