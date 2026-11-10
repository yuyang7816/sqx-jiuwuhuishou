package com.sqx.app.order.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.order.db.mapper.RecycleAppointmentSlotMapper;
import com.sqx.app.order.db.service.IRecycleAppointmentSlotService;
import com.sqx.app.order.domain.entity.RecycleAppointmentSlot;
import org.springframework.stereotype.Service;

@Service
public class IRecycleAppointmentSlotServiceImpl
        extends ServiceImpl<RecycleAppointmentSlotMapper, RecycleAppointmentSlot>
        implements IRecycleAppointmentSlotService {
}
