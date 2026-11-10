package com.sqx.app.order.service;

import com.sqx.app.order.domain.vo.AppointmentDateOptionVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约时段服务
 */
public interface AppointmentOptionService {

    List<AppointmentDateOptionVo> listOptions();

    /**
     * 根据预约开始时间匹配配置时段，解析出对应时段结束时间。
     * 用于订单展示「HH:mm - HH:mm」时间段。
     */
    LocalDateTime resolveSlotEndTime(LocalDateTime appointmentStart);
}
