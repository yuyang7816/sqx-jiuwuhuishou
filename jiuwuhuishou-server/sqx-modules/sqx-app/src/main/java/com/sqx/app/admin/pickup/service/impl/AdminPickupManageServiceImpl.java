package com.sqx.app.admin.pickup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.order.domain.bo.AdminOrderAssignPickupBo;
import com.sqx.app.admin.order.service.AdminOrderManageService;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskAssignBo;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskFinishBo;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskInfoBo;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskPageBo;
import com.sqx.app.admin.pickup.domain.vo.AdminPickupTaskDetailVo;
import com.sqx.app.admin.pickup.domain.vo.AdminPickupTaskVo;
import com.sqx.app.admin.pickup.service.AdminPickupManageService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.pickup.db.service.IPickupTaskLogService;
import com.sqx.app.pickup.db.service.IPickupTaskService;
import com.sqx.app.pickup.domain.entity.PickupTask;
import com.sqx.app.pickup.domain.entity.PickupTaskLog;
import com.sqx.app.pickup.domain.vo.PickupTaskLogVo;
import com.sqx.app.pickup.domain.bo.PickupTaskFinishBo;
import com.sqx.app.pickup.enums.PickupTaskAction;
import com.sqx.app.pickup.enums.PickupTaskStatus;
import com.sqx.app.pickup.service.PickupTaskService;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdminPickupManageServiceImpl implements AdminPickupManageService {

    private static final Integer STAFF_USER_TYPE = 3;

    @Resource
    private IPickupTaskService iPickupTaskService;
    @Resource
    private IPickupTaskLogService iPickupTaskLogService;
    @Resource
    private IUserService iUserService;
    @Resource
    private AdminLookupSupport adminLookupSupport;
    @Resource
    private AdminOrderManageService adminOrderManageService;
    @Resource
    private PickupTaskService pickupTaskService;

    @Override
    public void assign(AdminPickupTaskAssignBo assignBo) {
        reassign(assignBo);
    }

    @Override
    public void reassign(AdminPickupTaskAssignBo assignBo) {
        PickupTask task = requireActiveTask(assignBo.getId());
        requireStaff(assignBo.getStaffId());
        AdminOrderAssignPickupBo orderAssignBo = new AdminOrderAssignPickupBo();
        orderAssignBo.setOrderId(task.getOrderId());
        orderAssignBo.setStaffId(assignBo.getStaffId());
        adminOrderManageService.assignPickup(orderAssignBo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finish(AdminPickupTaskFinishBo finishBo) {
        PickupTask task = requireActiveTask(finishBo.getId());
        if (Objects.isNull(task.getStaffId())) {
            throw new BizException("请先分配取件员");
        }
        PickupTaskFinishBo pickupFinishBo = new PickupTaskFinishBo();
        pickupFinishBo.setId(finishBo.getId());
        pickupFinishBo.setRemark(StringUtils.defaultIfBlank(finishBo.getRemark(), "管理员确认完成取件"));
        pickupTaskService.finish(task.getStaffId(), pickupFinishBo);
    }

    @Override
    public IPage<AdminPickupTaskVo> pageInfo(AdminPickupTaskPageBo pageBo) {
        Page<PickupTask> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());

        List<Long> orderIds = adminLookupSupport.resolveOrderIds(pageBo.getOrderNo());
        if (orderIds != null && orderIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        List<Long> staffIds = resolveStaffIds(pageBo.getStaffName());
        if (staffIds != null && staffIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        LambdaQueryWrapper<PickupTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(orderIds != null, PickupTask::getOrderId, orderIds)
                .eq(Objects.nonNull(pageBo.getStaffId()), PickupTask::getStaffId, pageBo.getStaffId())
                .in(staffIds != null, PickupTask::getStaffId, staffIds);

        PickupTaskStatus status = PickupTaskStatus.fromKey(pageBo.getStatusKey());
        if (Objects.nonNull(status)) {
            queryWrapper.eq(PickupTask::getStatus, status.name());
        }

        queryWrapper.orderByDesc(PickupTask::getAppointmentTime)
                .orderByDesc(PickupTask::getCreateTime);

        IPage<PickupTask> iPage = iPickupTaskService.page(initPage, queryWrapper);
        List<AdminPickupTaskVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (PickupTask record : iPage.getRecords()) {
                voList.add(toVo(record));
            }
        }
        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public AdminPickupTaskDetailVo info(AdminPickupTaskInfoBo infoBo) {
        PickupTask task = iPickupTaskService.getById(infoBo.getId());
        if (Objects.isNull(task)) {
            throw new BizException("取件任务不存在");
        }

        AdminPickupTaskDetailVo detailVo = new AdminPickupTaskDetailVo();
        BeanUtils.copyProperties(toVo(task), detailVo);

        PickupTaskStatus status = PickupTaskStatus.fromName(task.getStatus());
        List<PickupTaskLog> logs = iPickupTaskLogService.list(new LambdaQueryWrapper<PickupTaskLog>()
                .eq(PickupTaskLog::getTaskId, task.getId())
                .orderByAsc(PickupTaskLog::getCreateTime));
        detailVo.setLogs(buildLogVos(logs, status));
        return detailVo;
    }

    private List<Long> resolveStaffIds(String staffName) {
        if (StringUtils.isBlank(staffName)) {
            return null;
        }
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(UserEntity::getUserName, staffName)
                .eq(UserEntity::getUserType, STAFF_USER_TYPE);
        List<UserEntity> users = iUserService.list(wrapper);
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream().map(UserEntity::getUserId).collect(Collectors.toList());
    }

    private AdminPickupTaskVo toVo(PickupTask record) {
        AdminPickupTaskVo vo = new AdminPickupTaskVo();
        BeanUtils.copyProperties(record, vo);

        PickupTaskStatus status = PickupTaskStatus.fromName(record.getStatus());
        if (Objects.nonNull(status)) {
            vo.setStatusKey(status.getKey());
            vo.setStatusText(status.getLabel());
        }

        vo.setOrderNo(adminLookupSupport.resolveOrderNo(record.getOrderId()));
        fillStaffName(record.getStaffId(), vo);
        return vo;
    }

    private void fillStaffName(Long staffId, AdminPickupTaskVo vo) {
        if (staffId == null) {
            return;
        }
        UserEntity staff = iUserService.getById(staffId);
        if (staff != null) {
            vo.setStaffName(staff.getUserName());
        }
    }

    private List<PickupTaskLogVo> buildLogVos(List<PickupTaskLog> logs, PickupTaskStatus status) {
        List<PickupTaskLogVo> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(logs)) {
            return voList;
        }
        for (int i = 0; i < logs.size(); i++) {
            PickupTaskLog log = logs.get(i);
            PickupTaskAction action = resolveAction(log.getActionType());
            PickupTaskLogVo vo = new PickupTaskLogVo();
            vo.setActionType(log.getActionType());
            vo.setTitle(Objects.nonNull(action) ? action.getTitle() : log.getActionType());
            vo.setContent(log.getContent());
            vo.setCreateTime(log.getCreateTime());
            boolean isLast = i == logs.size() - 1;
            vo.setActive(isLast && !PickupTaskStatus.DONE.equals(status));
            if (PickupTaskStatus.DONE.equals(status) && PickupTaskAction.FINISH.name().equals(log.getActionType())) {
                vo.setActive(true);
            }
            voList.add(vo);
        }
        return voList;
    }

    private PickupTaskAction resolveAction(String actionType) {
        if (StringUtils.isBlank(actionType)) {
            return null;
        }
        try {
            return PickupTaskAction.valueOf(actionType);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private PickupTask requireActiveTask(Long taskId) {
        PickupTask task = iPickupTaskService.getById(taskId);
        if (Objects.isNull(task)) {
            throw new BizException("取件任务不存在");
        }
        PickupTaskStatus status = PickupTaskStatus.fromName(task.getStatus());
        if (!PickupTaskStatus.PENDING.equals(status) && !PickupTaskStatus.RUNNING.equals(status)) {
            throw new BizException("当前任务状态不允许操作");
        }
        return task;
    }

    private void requireStaff(Long staffId) {
        UserEntity staff = iUserService.getById(staffId);
        if (Objects.isNull(staff) || !Objects.equals(staff.getUserType(), STAFF_USER_TYPE)) {
            throw new BizException("取件员工不存在或类型不正确");
        }
    }
}
