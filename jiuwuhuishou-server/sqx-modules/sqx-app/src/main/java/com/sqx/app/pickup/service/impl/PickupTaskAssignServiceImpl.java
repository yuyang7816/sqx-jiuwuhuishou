package com.sqx.app.pickup.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.order.support.RecycleOrderStateMachine;
import com.sqx.app.pickup.db.service.IPickupTaskLogService;
import com.sqx.app.pickup.db.service.IPickupTaskService;
import com.sqx.app.pickup.domain.entity.PickupTask;
import com.sqx.app.pickup.domain.entity.PickupTaskLog;
import com.sqx.app.pickup.enums.PickupTaskAction;
import com.sqx.app.pickup.enums.PickupTaskStatus;
import com.sqx.app.pickup.service.PickupTaskAssignService;
import com.sqx.common.core.utils.GenUtil;
import com.sqx.common.redis.utils.ConfigUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
public class PickupTaskAssignServiceImpl implements PickupTaskAssignService {

    private static final Integer STAFF_USER_TYPE = 3;
    private static final Integer USER_STATUS_NORMAL = 1;
    private static final String CONFIG_COURIER_POOL = "order_courier_pool";
    private static final String ACTION_ASSIGN_PICKUP = "ASSIGN_PICKUP";
    private static final String OPERATOR_TYPE_SYSTEM = "SYSTEM";

    @Resource
    private IPickupTaskService iPickupTaskService;
    @Resource
    private IPickupTaskLogService iPickupTaskLogService;
    @Resource
    private IUserService iUserService;
    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private IRecycleOrderLogService iRecycleOrderLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignForOrder(RecycleOrder order, String itemSummary) {
        long exists = iPickupTaskService.count(new LambdaQueryWrapper<PickupTask>()
                .eq(PickupTask::getOrderId, order.getId()));
        if (exists > 0) {
            return;
        }

        UserEntity staff = resolveStaff(order);
        if (Objects.isNull(staff)) {
            return;
        }

        PickupTask task = new PickupTask();
        task.setTaskNo("PK" + GenUtil.genOrderNo());
        task.setOrderId(order.getId());
        task.setStaffId(staff.getUserId());
        task.setStatus(PickupTaskStatus.PENDING.name());
        task.setAppointmentTime(order.getAppointmentTime());
        task.setContactName(order.getContactName());
        task.setContactMobile(order.getContactMobile());
        task.setAddressText(order.getAddressText());
        task.setCategoryName(order.getCategoryName());
        task.setItemSummary(StringUtils.defaultIfBlank(itemSummary, "待确认"));
        iPickupTaskService.save(task);

        PickupTaskLog pickupLog = new PickupTaskLog();
        pickupLog.setTaskId(task.getId());
        pickupLog.setActionType(PickupTaskAction.ASSIGN.name());
        pickupLog.setContent("系统自动分配取件员：" + staff.getUserName());
        pickupLog.setOperatorId(staff.getUserId());
        iPickupTaskLogService.save(pickupLog);

        advanceOrderAfterAssign(order, staff);
    }

    private void advanceOrderAfterAssign(RecycleOrder order, UserEntity staff) {
        if (!RecycleOrderStatus.WAIT_ASSIGN.getCode().equals(order.getStatus())) {
            return;
        }
        String fromStatus = order.getStatus();
        RecycleOrderStateMachine.assertCanTransition(fromStatus, RecycleOrderStatus.IN_PROGRESS);
        order.setStatus(RecycleOrderStatus.IN_PROGRESS.getCode());
        order.setStatusDesc("员工正在上门取件");
        iRecycleOrderService.updateById(order);

        RecycleOrderLog orderLog = new RecycleOrderLog();
        orderLog.setOrderId(order.getId());
        orderLog.setActionType(ACTION_ASSIGN_PICKUP);
        orderLog.setFromStatus(fromStatus);
        orderLog.setToStatus(order.getStatus());
        orderLog.setContent("系统自动分配取件员：" + staff.getUserName());
        orderLog.setOperatorId(staff.getUserId());
        orderLog.setOperatorType(OPERATOR_TYPE_SYSTEM);
        iRecycleOrderLogService.save(orderLog);
    }

    private UserEntity resolveStaff(RecycleOrder order) {
        UserEntity fromPool = resolveStaffFromCourierPool(order.getId());
        if (Objects.nonNull(fromPool)) {
            return fromPool;
        }
        return resolveStaffByLoadBalance();
    }

    private UserEntity resolveStaffFromCourierPool(Long orderId) {
        String configValue = ConfigUtil.getConfigValueOrDefault(CONFIG_COURIER_POOL);
        if (StringUtils.isBlank(configValue)) {
            return null;
        }
        try {
            JSONArray pool = JSON.parseArray(configValue);
            if (CollectionUtils.isEmpty(pool)) {
                return null;
            }
            JSONObject item = pool.getJSONObject((int) (orderId % pool.size()));
            if (Objects.isNull(item)) {
                return null;
            }
            Long userId = item.getLong("userId");
            if (Objects.nonNull(userId)) {
                UserEntity staff = findEnabledStaff(userId);
                if (Objects.nonNull(staff)) {
                    return staff;
                }
            }
            String mobile = item.getString("mobile");
            if (StringUtils.isBlank(mobile)) {
                return null;
            }
            return iUserService.getOne(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getPhone, mobile)
                    .eq(UserEntity::getUserType, STAFF_USER_TYPE)
                    .eq(UserEntity::getStatus, USER_STATUS_NORMAL)
                    .orderByAsc(UserEntity::getUserId)
                    .last("LIMIT 1"));
        } catch (Exception ignored) {
            return null;
        }
    }

    private UserEntity resolveStaffByLoadBalance() {
        List<UserEntity> staffList = iUserService.list(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUserType, STAFF_USER_TYPE)
                .eq(UserEntity::getStatus, USER_STATUS_NORMAL)
                .orderByAsc(UserEntity::getUserId));
        if (CollectionUtils.isEmpty(staffList)) {
            return null;
        }

        UserEntity selected = staffList.get(0);
        long minTaskCount = Long.MAX_VALUE;
        for (UserEntity staff : staffList) {
            long activeTaskCount = iPickupTaskService.count(new LambdaQueryWrapper<PickupTask>()
                    .eq(PickupTask::getStaffId, staff.getUserId())
                    .in(PickupTask::getStatus, PickupTaskStatus.PENDING.name(), PickupTaskStatus.RUNNING.name()));
            if (activeTaskCount < minTaskCount) {
                minTaskCount = activeTaskCount;
                selected = staff;
            }
        }
        return selected;
    }

    private UserEntity findEnabledStaff(Long userId) {
        UserEntity staff = iUserService.getById(userId);
        if (Objects.isNull(staff)
                || !Objects.equals(staff.getUserType(), STAFF_USER_TYPE)
                || !Objects.equals(staff.getStatus(), USER_STATUS_NORMAL)) {
            return null;
        }
        return staff;
    }
}
