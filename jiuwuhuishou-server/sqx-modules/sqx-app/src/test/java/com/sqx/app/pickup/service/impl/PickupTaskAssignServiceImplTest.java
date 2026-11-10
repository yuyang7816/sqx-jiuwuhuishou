package com.sqx.app.pickup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.pickup.db.service.IPickupTaskLogService;
import com.sqx.app.pickup.db.service.IPickupTaskService;
import com.sqx.app.pickup.domain.entity.PickupTask;
import com.sqx.app.pickup.domain.entity.PickupTaskLog;
import com.sqx.common.redis.utils.ConfigUtil;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PickupTaskAssignServiceImplTest {

    @Mock
    private IPickupTaskService iPickupTaskService;
    @Mock
    private IPickupTaskLogService iPickupTaskLogService;
    @Mock
    private IUserService iUserService;
    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private IRecycleOrderLogService iRecycleOrderLogService;

    @InjectMocks
    private PickupTaskAssignServiceImpl pickupTaskAssignService;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(PickupTask.class, UserEntity.class, RecycleOrder.class, RecycleOrderLog.class, PickupTaskLog.class);
    }

    @Test
    void assignForOrderShouldSkipWhenTaskAlreadyExists() {
        RecycleOrder order = buildOrder();
        when(iPickupTaskService.count(any(LambdaQueryWrapper.class))).thenReturn(1L);

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("order_courier_pool")).thenReturn("");
            pickupTaskAssignService.assignForOrder(order, "约18kg");
        }

        verify(iUserService, never()).list(any(LambdaQueryWrapper.class));
        verify(iPickupTaskService, never()).save(any(PickupTask.class));
    }

    @Test
    void assignForOrderShouldSkipWhenNoStaffAvailable() {
        RecycleOrder order = buildOrder();
        when(iPickupTaskService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(iUserService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("order_courier_pool")).thenReturn("");
            pickupTaskAssignService.assignForOrder(order, "约18kg");
        }

        verify(iPickupTaskService, never()).save(any(PickupTask.class));
        verify(iRecycleOrderService, never()).updateById(any(RecycleOrder.class));
    }

    @Test
    void assignForOrderShouldCreateTaskAndAdvanceOrderStatus() {
        RecycleOrder order = buildOrder();
        UserEntity staffA = buildStaff(201L, "王师傅", "13800138001");
        UserEntity staffB = buildStaff(202L, "李师傅", "13800138002");

        when(iPickupTaskService.count(any(LambdaQueryWrapper.class))).thenReturn(0L, 1L, 0L);
        when(iUserService.list(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(staffA, staffB));
        doAnswer(invocation -> {
            PickupTask task = invocation.getArgument(0);
            task.setId(900L);
            return true;
        }).when(iPickupTaskService).save(any(PickupTask.class));

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("order_courier_pool")).thenReturn("");
            pickupTaskAssignService.assignForOrder(order, "约18kg");
        }

        ArgumentCaptor<PickupTask> taskCaptor = ArgumentCaptor.forClass(PickupTask.class);
        verify(iPickupTaskService).save(taskCaptor.capture());
        assertEquals(202L, taskCaptor.getValue().getStaffId());
        assertEquals("约18kg", taskCaptor.getValue().getItemSummary());

        ArgumentCaptor<RecycleOrder> orderCaptor = ArgumentCaptor.forClass(RecycleOrder.class);
        verify(iRecycleOrderService).updateById(orderCaptor.capture());
        assertEquals(RecycleOrderStatus.IN_PROGRESS.getCode(), orderCaptor.getValue().getStatus());

        verify(iRecycleOrderLogService).save(any(RecycleOrderLog.class));
        verify(iPickupTaskLogService).save(any(PickupTaskLog.class));
    }

    @Test
    void assignForOrderShouldDefaultItemSummaryAndSkipAdvanceWhenNotWaitAssign() {
        RecycleOrder order = buildOrder();
        order.setStatus(RecycleOrderStatus.IN_PROGRESS.getCode());
        UserEntity staff = buildStaff(201L, "王师傅", "13800138001");

        when(iPickupTaskService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(iUserService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(staff));
        doAnswer(invocation -> {
            PickupTask task = invocation.getArgument(0);
            task.setId(901L);
            return true;
        }).when(iPickupTaskService).save(any(PickupTask.class));

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("order_courier_pool")).thenReturn(null);
            pickupTaskAssignService.assignForOrder(order, "  ");
        }

        ArgumentCaptor<PickupTask> taskCaptor = ArgumentCaptor.forClass(PickupTask.class);
        verify(iPickupTaskService).save(taskCaptor.capture());
        assertEquals("待确认", taskCaptor.getValue().getItemSummary());
        verify(iRecycleOrderService, never()).updateById(any(RecycleOrder.class));
    }

    @Test
    void assignForOrderShouldPreferCourierPoolByUserId() {
        RecycleOrder order = buildOrder();
        UserEntity staff = buildStaff(301L, "池内员工", "13900000001");
        when(iPickupTaskService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(iUserService.getById(301L)).thenReturn(staff);
        doAnswer(invocation -> {
            PickupTask task = invocation.getArgument(0);
            task.setId(902L);
            return true;
        }).when(iPickupTaskService).save(any(PickupTask.class));

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("order_courier_pool"))
                    .thenReturn("[{\"userId\":301}]");
            pickupTaskAssignService.assignForOrder(order, "家电");
        }

        ArgumentCaptor<PickupTask> taskCaptor = ArgumentCaptor.forClass(PickupTask.class);
        verify(iPickupTaskService).save(taskCaptor.capture());
        assertEquals(301L, taskCaptor.getValue().getStaffId());
        verify(iUserService, never()).list(any(LambdaQueryWrapper.class));
    }

    @Test
    void assignForOrderShouldFallbackToMobileWhenPoolUserInvalid() {
        RecycleOrder order = buildOrder();
        UserEntity disabled = buildStaff(301L, "禁用", "13900000001");
        disabled.setStatus(0);
        UserEntity byMobile = buildStaff(302L, "手机匹配", "13900000002");

        when(iPickupTaskService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(iUserService.getById(301L)).thenReturn(disabled);
        when(iUserService.getOne(any(LambdaQueryWrapper.class))).thenReturn(byMobile);
        doAnswer(invocation -> {
            PickupTask task = invocation.getArgument(0);
            task.setId(903L);
            return true;
        }).when(iPickupTaskService).save(any(PickupTask.class));

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("order_courier_pool"))
                    .thenReturn("[{\"userId\":301,\"mobile\":\"13900000002\"}]");
            pickupTaskAssignService.assignForOrder(order, "书籍");
        }

        ArgumentCaptor<PickupTask> taskCaptor = ArgumentCaptor.forClass(PickupTask.class);
        verify(iPickupTaskService).save(taskCaptor.capture());
        assertEquals(302L, taskCaptor.getValue().getStaffId());
    }

    @Test
    void assignForOrderShouldIgnoreEmptyOrBadCourierPool() {
        RecycleOrder order = buildOrder();
        UserEntity staff = buildStaff(201L, "王师傅", "13800138001");
        when(iPickupTaskService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(iUserService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(staff));
        doAnswer(invocation -> {
            PickupTask task = invocation.getArgument(0);
            task.setId(904L);
            return true;
        }).when(iPickupTaskService).save(any(PickupTask.class));

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("order_courier_pool")).thenReturn("[]");
            pickupTaskAssignService.assignForOrder(order, "A");
        }
        verify(iPickupTaskService).save(any(PickupTask.class));

        reset(iPickupTaskService, iUserService, iPickupTaskLogService, iRecycleOrderService, iRecycleOrderLogService);
        when(iPickupTaskService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(iUserService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(staff));
        doAnswer(invocation -> {
            PickupTask task = invocation.getArgument(0);
            task.setId(905L);
            return true;
        }).when(iPickupTaskService).save(any(PickupTask.class));

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("order_courier_pool")).thenReturn("not-json");
            pickupTaskAssignService.assignForOrder(order, "B");
        }
        verify(iPickupTaskService).save(any(PickupTask.class));
    }

    @Test
    void assignForOrderShouldSkipWhenCourierPoolItemIsNull() {
        RecycleOrder order = buildOrder();
        when(iPickupTaskService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(iUserService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("order_courier_pool")).thenReturn("[null]");
            pickupTaskAssignService.assignForOrder(order, "Z");
        }

        verify(iPickupTaskService, never()).save(any(PickupTask.class));
    }

    @Test
    void assignForOrderShouldSkipWhenPoolItemHasNoUsableStaff() {
        RecycleOrder order = buildOrder();
        when(iPickupTaskService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(iUserService.getById(301L)).thenReturn(null);
        when(iUserService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("order_courier_pool"))
                    .thenReturn("[{\"userId\":301,\"mobile\":\"\"}]");
            pickupTaskAssignService.assignForOrder(order, "X");
        }

        verify(iPickupTaskService, never()).save(any(PickupTask.class));
    }

    @Test
    void assignForOrderShouldRejectNonStaffFromFindEnabled() {
        RecycleOrder order = buildOrder();
        UserEntity notStaff = buildStaff(301L, "用户", "13900000001");
        notStaff.setUserType(1);
        when(iPickupTaskService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(iUserService.getById(301L)).thenReturn(notStaff);
        when(iUserService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        try (MockedStatic<ConfigUtil> config = mockStatic(ConfigUtil.class)) {
            config.when(() -> ConfigUtil.getConfigValueOrDefault("order_courier_pool"))
                    .thenReturn("[{\"userId\":301}]");
            pickupTaskAssignService.assignForOrder(order, "Y");
        }

        verify(iPickupTaskService, never()).save(any(PickupTask.class));
    }

    private RecycleOrder buildOrder() {
        RecycleOrder order = new RecycleOrder();
        order.setId(100L);
        order.setOrderNo("RE202607130001");
        order.setStatus(RecycleOrderStatus.WAIT_ASSIGN.getCode());
        order.setStatusDesc(RecycleOrderStatus.WAIT_ASSIGN.getLabel());
        order.setAppointmentTime(LocalDateTime.now().plusDays(1));
        order.setContactName("张伟");
        order.setContactMobile("13812348888");
        order.setAddressText("上海市浦东新区测试路1号");
        order.setCategoryName("旧衣回收");
        return order;
    }

    private UserEntity buildStaff(Long userId, String name, String phone) {
        UserEntity staff = new UserEntity();
        staff.setUserId(userId);
        staff.setUserName(name);
        staff.setPhone(phone);
        staff.setUserType(3);
        staff.setStatus(1);
        return staff;
    }
}
