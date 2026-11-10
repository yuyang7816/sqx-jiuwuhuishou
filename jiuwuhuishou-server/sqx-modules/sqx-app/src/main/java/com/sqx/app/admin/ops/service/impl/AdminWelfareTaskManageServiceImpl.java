package com.sqx.app.admin.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.ops.domain.bo.*;
import com.sqx.app.admin.ops.service.AdminWelfareTaskManageService;
import com.sqx.app.welfare.db.service.IWelfareTaskService;
import com.sqx.app.welfare.domain.entity.WelfareTask;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class AdminWelfareTaskManageServiceImpl implements AdminWelfareTaskManageService {

    @Resource
    private IWelfareTaskService iWelfareTaskService;

    @Override
    public void saveInfo(AdminWelfareTaskSaveBo saveBo) {
        WelfareTask entity = new WelfareTask();
        BeanUtils.copyProperties(saveBo, entity);
        iWelfareTaskService.save(entity);
    }

    @Override
    public IPage<WelfareTask> pageInfo(AdminWelfareTaskPageBo pageBo) {
        Page<WelfareTask> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());
        LambdaQueryWrapper<WelfareTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(pageBo.getTitle()), WelfareTask::getTitle, pageBo.getTitle())
                .eq(Objects.nonNull(pageBo.getState()), WelfareTask::getState, pageBo.getState())
                .orderByAsc(WelfareTask::getSort)
                .orderByDesc(WelfareTask::getCreateTime);
        return iWelfareTaskService.page(initPage, wrapper);
    }

    @Override
    public WelfareTask info(AdminWelfareTaskInfoBo infoBo) {
        WelfareTask entity = iWelfareTaskService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("福利任务不存在");
        }
        return entity;
    }

    @Override
    public void updateInfo(AdminWelfareTaskUpdateBo updateBo) {
        WelfareTask entity = new WelfareTask();
        BeanUtils.copyProperties(updateBo, entity);
        iWelfareTaskService.updateById(entity);
    }

    @Override
    public void delete(AdminWelfareTaskInfoBo infoBo) {
        iWelfareTaskService.removeById(infoBo.getId());
    }
}
