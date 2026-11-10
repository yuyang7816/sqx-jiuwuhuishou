package com.sqx.app.admin.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.order.domain.bo.AdminOrderExceptionHandleBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderExceptionInfoBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderExceptionPageBo;
import com.sqx.app.admin.order.domain.bo.AdminOrderHandleBo;
import com.sqx.app.admin.order.domain.vo.AdminOrderExceptionVo;
import com.sqx.app.admin.order.service.AdminOrderManageService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.order.db.service.IOrderExceptionService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.OrderException;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.enums.RecycleOrderStatus;
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

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminOrderExceptionManageServiceImplTest {

    @Mock
    private IOrderExceptionService iOrderExceptionService;
    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private AdminLookupSupport adminLookupSupport;
    @Mock
    private AdminOrderManageService adminOrderManageService;

    @InjectMocks
    private AdminOrderExceptionManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(OrderException.class, RecycleOrder.class);
    }

    @BeforeEach
    void setUp() {
        doAnswer(inv -> {
            AdminOrderExceptionVo vo = inv.getArgument(1);
            vo.setUserName("tester");
            vo.setPhone("13800000000");
            return null;
        }).when(adminLookupSupport).fillUserFields(anyLong(), any(AdminOrderExceptionVo.class));
    }

    @Test
    void pageInfoShouldReturnEmptyWhenFilteredOrderIdsEmpty() {
        when(adminLookupSupport.resolveOrderIds("x")).thenReturn(Collections.emptyList());
        AdminOrderExceptionPageBo pageBo = new AdminOrderExceptionPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setOrderNo("x");

        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void pageInfoShouldQueryWithOrderIdFilter() {
        AdminOrderExceptionPageBo pageBo = new AdminOrderExceptionPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setOrderId(99L);

        OrderException record = new OrderException();
        record.setId(1L);
        record.setOrderId(99L);
        Page<OrderException> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(record));
        when(iOrderExceptionService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        RecycleOrder order = new RecycleOrder();
        order.setId(99L);
        order.setOrderNo("RO99");
        order.setUserId(10L);
        when(iRecycleOrderService.listByIds(anyList())).thenReturn(Collections.singletonList(order));

        assertEquals(1, service.pageInfo(pageBo).getRecords().size());
    }

    @Test
    void pageInfoShouldIntersectFiltersAndHandleMissingOrder() {
        when(adminLookupSupport.resolveOrderIds("RO")).thenReturn(Arrays.asList(1L, 2L));
        when(adminLookupSupport.resolveUserIds("u", null)).thenReturn(Collections.singletonList(10L));

        RecycleOrder order = new RecycleOrder();
        order.setId(1L);
        when(iRecycleOrderService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(order));

        OrderException record = new OrderException();
        record.setId(1L);
        record.setOrderId(1L);
        Page<OrderException> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(record));
        when(iOrderExceptionService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        when(iRecycleOrderService.listByIds(anyList())).thenReturn(Collections.emptyList());

        AdminOrderExceptionPageBo pageBo = new AdminOrderExceptionPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setOrderNo("RO");
        pageBo.setUserName("u");

        AdminOrderExceptionVo vo = service.pageInfo(pageBo).getRecords().get(0);
        assertNull(vo.getOrderNo());
    }

    @Test
    void pageInfoShouldUseUserOnlyAndOrderNoOnlyFilters() {
        when(adminLookupSupport.resolveOrderIds("RO")).thenReturn(Collections.singletonList(1L));
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        Page<OrderException> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iOrderExceptionService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        AdminOrderExceptionPageBo byOrderNo = new AdminOrderExceptionPageBo();
        byOrderNo.setPage(1L);
        byOrderNo.setLimit(10L);
        byOrderNo.setOrderNo("RO");
        assertTrue(service.pageInfo(byOrderNo).getRecords().isEmpty());

        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);
        when(adminLookupSupport.resolveUserIds("u", null)).thenReturn(Collections.singletonList(10L));
        RecycleOrder order = new RecycleOrder();
        order.setId(5L);
        when(iRecycleOrderService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(order));

        AdminOrderExceptionPageBo byUser = new AdminOrderExceptionPageBo();
        byUser.setPage(1L);
        byUser.setLimit(10L);
        byUser.setUserName("u");
        assertTrue(service.pageInfo(byUser).getRecords().isEmpty());
    }

    @Test
    void infoShouldThrowWhenMissing() {
        AdminOrderExceptionInfoBo infoBo = new AdminOrderExceptionInfoBo();
        infoBo.setId(1L);
        when(iOrderExceptionService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));
    }

    @Test
    void handleShouldValidateOrderAndDelegate() {
        AdminOrderExceptionHandleBo handleBo = new AdminOrderExceptionHandleBo();
        handleBo.setId(1L);
        handleBo.setHandleType("RESOLVED");
        handleBo.setRemark("已联系用户");

        OrderException record = new OrderException();
        record.setId(1L);
        record.setOrderId(10L);
        record.setExceptionType("地址错误");
        when(iOrderExceptionService.getById(1L)).thenReturn(record);
        when(iRecycleOrderService.getById(10L)).thenReturn(null);

        assertThrows(BizException.class, () -> service.handle(handleBo));

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus(RecycleOrderStatus.IN_PROGRESS.getCode());
        when(iRecycleOrderService.getById(10L)).thenReturn(order);
        assertThrows(BizException.class, () -> service.handle(handleBo));

        order.setStatus(RecycleOrderStatus.EXCEPTION_PENDING.getCode());
        service.handle(handleBo);

        ArgumentCaptor<AdminOrderHandleBo> captor = ArgumentCaptor.forClass(AdminOrderHandleBo.class);
        verify(adminOrderManageService).handle(captor.capture());
        assertEquals(10L, captor.getValue().getId());
        assertEquals("RESOLVED", captor.getValue().getHandleType());
        assertTrue(captor.getValue().getRemark().contains("地址错误"));
        assertTrue(captor.getValue().getRemark().contains("已联系用户"));
    }

    @Test
    void handleShouldNormalizeHandleTypesAndRejectInvalid() {
        OrderException record = new OrderException();
        record.setId(1L);
        record.setOrderId(10L);
        record.setExceptionType("延迟");
        when(iOrderExceptionService.getById(1L)).thenReturn(record);

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus(RecycleOrderStatus.EXCEPTION_PENDING.getCode());
        when(iRecycleOrderService.getById(10L)).thenReturn(order);

        AdminOrderExceptionHandleBo resolvedCn = new AdminOrderExceptionHandleBo();
        resolvedCn.setId(1L);
        resolvedCn.setHandleType("已处理");
        service.handle(resolvedCn);

        AdminOrderExceptionHandleBo unresolved = new AdminOrderExceptionHandleBo();
        unresolved.setId(1L);
        unresolved.setHandleType("UNRESOLVED");
        service.handle(unresolved);

        AdminOrderExceptionHandleBo unresolvedCn = new AdminOrderExceptionHandleBo();
        unresolvedCn.setId(1L);
        unresolvedCn.setHandleType("未处理");
        service.handle(unresolvedCn);

        AdminOrderExceptionHandleBo blank = new AdminOrderExceptionHandleBo();
        blank.setId(1L);
        blank.setHandleType("");
        assertThrows(BizException.class, () -> service.handle(blank));

        AdminOrderExceptionHandleBo invalid = new AdminOrderExceptionHandleBo();
        invalid.setId(1L);
        invalid.setHandleType("UNKNOWN");
        assertThrows(BizException.class, () -> service.handle(invalid));
    }

    @Test
    void handleShouldBuildRemarkWithoutExtraTextWhenBlank() {
        OrderException record = new OrderException();
        record.setId(1L);
        record.setOrderId(10L);
        record.setExceptionType("延迟");
        when(iOrderExceptionService.getById(1L)).thenReturn(record);

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus(RecycleOrderStatus.EXCEPTION_PENDING.getCode());
        when(iRecycleOrderService.getById(10L)).thenReturn(order);

        AdminOrderExceptionHandleBo handleBo = new AdminOrderExceptionHandleBo();
        handleBo.setId(1L);
        handleBo.setHandleType("RESOLVED");
        service.handle(handleBo);

        ArgumentCaptor<AdminOrderHandleBo> captor = ArgumentCaptor.forClass(AdminOrderHandleBo.class);
        verify(adminOrderManageService).handle(captor.capture());
        assertEquals("异常反馈[延迟]", captor.getValue().getRemark());
    }

    @Test
    void listByOrderIdShouldHandleNullEmptyAndRecords() {
        assertTrue(service.listByOrderId(null).isEmpty());

        when(iOrderExceptionService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertTrue(service.listByOrderId(1L).isEmpty());

        OrderException record = new OrderException();
        record.setId(1L);
        record.setOrderId(10L);
        when(iOrderExceptionService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(record));
        when(iRecycleOrderService.listByIds(anyList())).thenReturn(Collections.emptyList());

        assertEquals(1, service.listByOrderId(10L).size());
    }

    @Test
    void infoShouldReturnVo() {
        AdminOrderExceptionInfoBo infoBo = new AdminOrderExceptionInfoBo();
        infoBo.setId(1L);

        OrderException record = new OrderException();
        record.setId(1L);
        record.setOrderId(10L);
        record.setExceptionType("延迟");
        when(iOrderExceptionService.getById(1L)).thenReturn(record);

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setOrderNo("RO10");
        order.setUserId(100L);
        when(iRecycleOrderService.listByIds(anyList())).thenReturn(Collections.singletonList(order));

        assertEquals("RO10", service.info(infoBo).getOrderNo());
    }

    @Test
    void pageInfoShouldReturnNullFilterWhenNoCriteria() {
        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);

        Page<OrderException> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        when(iOrderExceptionService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        AdminOrderExceptionPageBo pageBo = new AdminOrderExceptionPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void pageInfoShouldReturnEmptyWhenUserHasNoOrdersInFilter() {
        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);
        when(adminLookupSupport.resolveUserIds("u", null)).thenReturn(Collections.singletonList(10L));
        when(iRecycleOrderService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        AdminOrderExceptionPageBo pageBo = new AdminOrderExceptionPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserName("u");
        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void listByOrderIdShouldHandleRecordsWithoutOrderIds() {
        OrderException record = new OrderException();
        record.setId(1L);
        record.setOrderId(null);
        when(iOrderExceptionService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(record));

        assertEquals(1, service.listByOrderId(10L).size());
    }
}
