package com.sqx.app.admin.recycle.appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.recycle.appointment.domain.bo.AdminAppointmentSlotPageBo;
import com.sqx.app.admin.recycle.appointment.domain.bo.AdminAppointmentSlotSaveBo;
import com.sqx.app.admin.recycle.appointment.domain.bo.AdminAppointmentSlotUpdateBo;
import com.sqx.app.admin.recycle.appointment.service.AdminAppointmentConfigService;
import com.sqx.app.admin.recycle.domain.bo.AdminRecycleIdBo;
import com.sqx.app.order.db.service.IRecycleAppointmentSlotService;
import com.sqx.app.order.domain.entity.RecycleAppointmentSlot;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class AdminAppointmentConfigServiceImpl implements AdminAppointmentConfigService {

    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");

    @Resource
    private IRecycleAppointmentSlotService iRecycleAppointmentSlotService;

    @Override
    public void saveSlot(AdminAppointmentSlotSaveBo saveBo) {
        RecycleAppointmentSlot entity = new RecycleAppointmentSlot();
        BeanUtils.copyProperties(saveBo, entity);
        normalizeAndValidate(entity);
        fillSlotDefaults(entity);
        iRecycleAppointmentSlotService.save(entity);
    }

    @Override
    public IPage<RecycleAppointmentSlot> pageSlot(AdminAppointmentSlotPageBo pageBo) {
        Page<RecycleAppointmentSlot> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<RecycleAppointmentSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(pageBo.getState()), RecycleAppointmentSlot::getState, pageBo.getState())
                .orderByAsc(RecycleAppointmentSlot::getSort)
                .orderByAsc(RecycleAppointmentSlot::getStartTime)
                .orderByDesc(RecycleAppointmentSlot::getCreateTime);
        return iRecycleAppointmentSlotService.page(initPage, wrapper);
    }

    @Override
    public RecycleAppointmentSlot infoSlot(AdminRecycleIdBo infoBo) {
        RecycleAppointmentSlot entity = iRecycleAppointmentSlotService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("上门时间段不存在");
        }
        return entity;
    }

    @Override
    public void updateSlot(AdminAppointmentSlotUpdateBo updateBo) {
        infoSlot(newId(updateBo.getId()));
        RecycleAppointmentSlot entity = new RecycleAppointmentSlot();
        BeanUtils.copyProperties(updateBo, entity);
        normalizeAndValidate(entity);
        fillSlotDefaults(entity);
        iRecycleAppointmentSlotService.updateById(entity);
    }

    @Override
    public void deleteSlot(AdminRecycleIdBo infoBo) {
        iRecycleAppointmentSlotService.removeById(infoBo.getId());
    }

    private void normalizeAndValidate(RecycleAppointmentSlot entity) {
        entity.setStartTime(normalizeTime(entity.getStartTime(), "开始时间"));
        entity.setEndTime(normalizeTime(entity.getEndTime(), "结束时间"));
        if (entity.getStartTime().compareTo(entity.getEndTime()) >= 0) {
            throw new BizException("结束时间必须晚于开始时间");
        }
    }

    private String normalizeTime(String raw, String label) {
        if (StringUtils.isBlank(raw)) {
            throw new BizException(label + "不能为空");
        }
        String value = raw.trim().replace('：', ':');
        if (!TIME_PATTERN.matcher(value).matches()) {
            throw new BizException(label + "格式不正确，请使用如 10:00");
        }
        return value;
    }

    private void fillSlotDefaults(RecycleAppointmentSlot entity) {
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        if (entity.getState() == null) {
            entity.setState(1);
        }
    }

    private AdminRecycleIdBo newId(Long id) {
        AdminRecycleIdBo bo = new AdminRecycleIdBo();
        bo.setId(id);
        return bo;
    }
}
