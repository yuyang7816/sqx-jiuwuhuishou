package com.sqx.app.admin.pickup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.order.domain.bo.AdminOrderAssignPickupBo;
import com.sqx.app.admin.order.service.AdminOrderManageService;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskAssignBo;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskFinishBo;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskInfoBo;
import com.sqx.app.admin.pickup.domain.bo.AdminPickupTaskPageBo;
import com.sqx.app.admin.pickup.domain.vo.AdminPickupTaskDetailVo;
import com.sqx.app.admin.pickup.domain.vo.AdminPickupTaskVo;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.base.user.db.service.IUserService;
import com.sqx.app.base.user.domain.entity.UserEntity;
import com.sqx.app.pickup.db.service.IPickupTaskLogService;
import com.sqx.app.pickup.db.service.IPickupTaskService;
import com.sqx.app.pickup.domain.bo.PickupTaskFinishBo;
import com.sqx.app.pickup.domain.entity.PickupTask;
import com.sqx.app.pickup.domain.entity.PickupTaskLog;
import com.sqx.app.pickup.enums.PickupTaskAction;
import com.sqx.app.pickup.enums.PickupTaskStatus;
import com.sqx.app.pickup.service.PickupTaskService;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminPickupManageServiceImplTest {

    @Mock
    private IPickupTaskService iPickupTaskService;
    @Mock
    private IPickupTaskLogService iPickupTaskLogService;
    @Mock
    private IUserService iUserService;
    @Mock
    private AdminLookupSupport adminLookupSupport;
    @Mock
    private AdminOrderManageService adminOrderManageService;
    @Mock
    private PickupTaskService pickupTaskService;

    @InjectMocks
    private AdminPickupManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(PickupTask.class, PickupTaskLog.class, UserEntity.class);
    }

    @BeforeEach
    void setUp() {
        when(adminLookupSupport.resolveOrderNo(anyLong())).thenReturn("RO001");
    }

    @Test
    void assignShouldDelegateToReassign() {
        PickupTask task = activeTask(1L, 10L);
        when(iPickupTaskService.getById(1L)).thenReturn(task);
        stubStaff(100L);

        AdminPickupTaskAssignBo assignBo = new AdminPickupTaskAssignBo();
        assignBo.setId(1L);
        assignBo.setStaffId(100L);
        service.assign(assignBo);

        ArgumentCaptor<AdminOrderAssignPickupBo> captor = ArgumentCaptor.forClass(AdminOrderAssignPickupBo.class);
        verify(adminOrderManageService).assignPickup(captor.capture());
        assertEquals(10L, captor.getValue().getOrderId());
        assertEquals(100L, captor.getValue().getStaffId());
    }

    @Test
    void reassignShouldRejectMissingTaskInvalidStatusOrStaff() {
        AdminPickupTaskAssignBo assignBo = new AdminPickupTaskAssignBo();
        assignBo.setId(1L);
        assignBo.setStaffId(100L);

        when(iPickupTaskService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.reassign(assignBo));

        PickupTask doneTask = activeTask(1L, 10L);
        doneTask.setStatus(PickupTaskStatus.DONE.name());
        when(iPickupTaskService.getById(1L)).thenReturn(doneTask);
        assertThrows(BizException.class, () -> service.reassign(assignBo));

        when(iPickupTaskService.getById(1L)).thenReturn(activeTask(1L, 10L));
        when(iUserService.getById(100L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.reassign(assignBo));

        UserEntity wrongType = new UserEntity();
        wrongType.setUserId(100L);
        wrongType.setUserType(1);
        when(iUserService.getById(100L)).thenReturn(wrongType);
        assertThrows(BizException.class, () -> service.reassign(assignBo));
    }

    @Test
    void finishShouldRequireStaffAndDelegate() {
        AdminPickupTaskFinishBo finishBo = new AdminPickupTaskFinishBo();
        finishBo.setId(1L);

        when(iPickupTaskService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.finish(finishBo));

        PickupTask task = activeTask(1L, 10L);
        task.setStaffId(null);
        when(iPickupTaskService.getById(1L)).thenReturn(task);
        assertThrows(BizException.class, () -> service.finish(finishBo));

        task.setStaffId(100L);
        finishBo.setRemark(null);
        service.finish(finishBo);

        ArgumentCaptor<PickupTaskFinishBo> captor = ArgumentCaptor.forClass(PickupTaskFinishBo.class);
        verify(pickupTaskService).finish(eq(100L), captor.capture());
        assertEquals(1L, captor.getValue().getId());
        assertEquals("管理员确认完成取件", captor.getValue().getRemark());

        finishBo.setRemark("已完成交接");
        service.finish(finishBo);
        verify(pickupTaskService, times(2)).finish(eq(100L), any(PickupTaskFinishBo.class));
    }

    @Test
    void pageInfoShouldShortCircuitAndQueryWithFilters() {
        when(adminLookupSupport.resolveOrderIds("x")).thenReturn(Collections.emptyList());
        AdminPickupTaskPageBo pageBo = new AdminPickupTaskPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setOrderNo("x");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);
        when(iUserService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        pageBo.setOrderNo(null);
        pageBo.setStaffName("张三");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());

        UserEntity staff = new UserEntity();
        staff.setUserId(100L);
        staff.setUserName("张三");
        staff.setUserType(3);
        when(iUserService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(staff));

        PickupTask task = activeTask(1L, 10L);
        task.setStaffId(100L);
        task.setStatus(PickupTaskStatus.PENDING.name());
        Page<PickupTask> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(task));
        when(iPickupTaskService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        when(iUserService.getById(100L)).thenReturn(staff);

        pageBo.setStaffName(null);
        pageBo.setStaffId(100L);
        pageBo.setStatusKey("pending");
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
        assertEquals("张三", service.pageInfo(pageBo).getRecords().get(0).getStaffName());

        Page<PickupTask> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(Collections.emptyList());
        when(iPickupTaskService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyPage);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void pageInfoShouldHandleTaskWithoutStaffName() {
        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);

        PickupTask task = activeTask(1L, 10L);
        task.setStaffId(null);
        task.setStatus("UNKNOWN");
        Page<PickupTask> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(task));
        when(iPickupTaskService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        AdminPickupTaskPageBo pageBo = new AdminPickupTaskPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);

        AdminPickupTaskVo vo = service.pageInfo(pageBo).getRecords().get(0);
        assertNull(vo.getStaffName());
        assertNull(vo.getStatusKey());
    }

    @Test
    void infoShouldThrowWhenMissing() {
        AdminPickupTaskInfoBo infoBo = new AdminPickupTaskInfoBo();
        infoBo.setId(1L);
        when(iPickupTaskService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));
    }

    @Test
    void infoShouldBuildLogsForPendingRunningAndDone() {
        AdminPickupTaskInfoBo infoBo = new AdminPickupTaskInfoBo();
        infoBo.setId(1L);

        PickupTask task = activeTask(1L, 10L);
        task.setStaffId(100L);
        task.setStatus(PickupTaskStatus.PENDING.name());
        when(iPickupTaskService.getById(1L)).thenReturn(task);

        PickupTaskLog assignLog = new PickupTaskLog();
        assignLog.setActionType(PickupTaskAction.ASSIGN.name());
        assignLog.setContent("分配");
        assignLog.setCreateTime(LocalDateTime.now());

        PickupTaskLog unknownLog = new PickupTaskLog();
        unknownLog.setActionType("CUSTOM");
        unknownLog.setContent("自定义");
        unknownLog.setCreateTime(LocalDateTime.now());

        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(java.util.Arrays.asList(assignLog, unknownLog));
        when(iUserService.getById(100L)).thenReturn(staff(100L, "李四"));

        AdminPickupTaskDetailVo pendingDetail = service.info(infoBo);
        assertEquals(2, pendingDetail.getLogs().size());
        assertTrue(pendingDetail.getLogs().get(1).getActive());

        task.setStatus(PickupTaskStatus.DONE.name());
        PickupTaskLog finishLog = new PickupTaskLog();
        finishLog.setActionType(PickupTaskAction.FINISH.name());
        finishLog.setContent("完成");
        finishLog.setCreateTime(LocalDateTime.now());
        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(java.util.Arrays.asList(assignLog, finishLog));

        AdminPickupTaskDetailVo doneDetail = service.info(infoBo);
        assertTrue(doneDetail.getLogs().get(1).getActive());

        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(service.info(infoBo).getLogs().isEmpty());
    }

    @Test
    void pageInfoShouldResolveStaffIdsByName() {
        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);

        UserEntity staff = new UserEntity();
        staff.setUserId(100L);
        staff.setUserName("张三");
        staff.setUserType(3);
        when(iUserService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(staff));

        PickupTask task = activeTask(1L, 10L);
        task.setStaffId(100L);
        Page<PickupTask> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(task));
        when(iPickupTaskService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        when(iUserService.getById(100L)).thenReturn(staff);

        AdminPickupTaskPageBo pageBo = new AdminPickupTaskPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setStaffName("张三");
        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
    }

    @Test
    void infoShouldHandleBlankActionTypeInLogs() {
        AdminPickupTaskInfoBo infoBo = new AdminPickupTaskInfoBo();
        infoBo.setId(1L);

        PickupTask task = activeTask(1L, 10L);
        task.setStatus(PickupTaskStatus.PENDING.name());
        when(iPickupTaskService.getById(1L)).thenReturn(task);

        PickupTaskLog blankAction = new PickupTaskLog();
        blankAction.setActionType(" ");
        blankAction.setContent("空白动作");
        blankAction.setCreateTime(LocalDateTime.now());
        when(iPickupTaskLogService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(blankAction));

        assertEquals(" ", service.info(infoBo).getLogs().get(0).getActionType());
    }

    private PickupTask activeTask(Long id, Long orderId) {
        PickupTask task = new PickupTask();
        task.setId(id);
        task.setOrderId(orderId);
        task.setStatus(PickupTaskStatus.PENDING.name());
        return task;
    }

    private void stubStaff(Long staffId) {
        when(iUserService.getById(staffId)).thenReturn(staff(staffId, "员工A"));
    }

    private UserEntity staff(Long staffId, String name) {
        UserEntity staff = new UserEntity();
        staff.setUserId(staffId);
        staff.setUserName(name);
        staff.setUserType(3);
        return staff;
    }
}
