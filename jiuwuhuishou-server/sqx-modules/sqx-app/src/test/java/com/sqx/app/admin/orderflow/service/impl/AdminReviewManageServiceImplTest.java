package com.sqx.app.admin.orderflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.orderflow.domain.bo.AdminReviewInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminReviewPageBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminReviewReplyBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminReviewVo;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.review.db.service.IOrderReviewService;
import com.sqx.app.review.domain.entity.OrderReview;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminReviewManageServiceImplTest {

    @Mock
    private IOrderReviewService iOrderReviewService;
    @Mock
    private AdminLookupSupport adminLookupSupport;
    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private IRecycleOrderLogService iRecycleOrderLogService;

    @InjectMocks
    private AdminReviewManageServiceImpl service;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(OrderReview.class, RecycleOrder.class, RecycleOrderLog.class);
    }

    @BeforeEach
    void setUp() {
        doAnswer(inv -> {
            AdminReviewVo vo = inv.getArgument(1);
            vo.setUserName("tester");
            vo.setPhone("13800000000");
            return null;
        }).when(adminLookupSupport).fillUserFields(anyLong(), any(AdminReviewVo.class));
        when(adminLookupSupport.resolveOrderNo(anyLong())).thenReturn("RO001");
    }

    @Test
    void pageInfoShouldShortCircuitWhenUserIdsEmpty() {
        when(adminLookupSupport.resolveUserIds("a", "b")).thenReturn(Collections.emptyList());
        AdminReviewPageBo pageBo = new AdminReviewPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setUserName("a");
        pageBo.setPhone("b");

        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
        verify(iOrderReviewService, never()).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void pageInfoShouldShortCircuitWhenOrderIdsEmpty() {
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        when(adminLookupSupport.resolveOrderIds("missing")).thenReturn(Collections.emptyList());
        AdminReviewPageBo pageBo = new AdminReviewPageBo();
        pageBo.setPage(1L);
        pageBo.setLimit(10L);
        pageBo.setOrderNo("missing");

        assertTrue(service.pageInfo(pageBo).getRecords().isEmpty());
    }

    @Test
    void pageInfoShouldFilterPendingAndRepliedAndReturnRecords() {
        when(adminLookupSupport.resolveUserIds(null, null)).thenReturn(null);
        when(adminLookupSupport.resolveOrderIds(null)).thenReturn(null);

        OrderReview review = new OrderReview();
        review.setId(1L);
        review.setOrderId(10L);
        review.setUserId(100L);
        Page<OrderReview> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(review));
        when(iOrderReviewService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        AdminReviewPageBo pendingBo = new AdminReviewPageBo();
        pendingBo.setPage(1L);
        pendingBo.setLimit(10L);
        pendingBo.setReplyStatus("pending");
        assertEquals(1, service.pageInfo(pendingBo).getRecords().size());

        AdminReviewPageBo repliedBo = new AdminReviewPageBo();
        repliedBo.setPage(1L);
        repliedBo.setLimit(10L);
        repliedBo.setReplyStatus("replied");
        assertEquals(1, service.pageInfo(repliedBo).getRecords().size());

        Page<OrderReview> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(Collections.emptyList());
        when(iOrderReviewService.page(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyPage);
        AdminReviewPageBo allBo = new AdminReviewPageBo();
        allBo.setPage(1L);
        allBo.setLimit(10L);
        assertTrue(service.pageInfo(allBo).getRecords().isEmpty());
    }

    @Test
    void infoShouldThrowWhenMissing() {
        AdminReviewInfoBo infoBo = new AdminReviewInfoBo();
        infoBo.setId(1L);
        when(iOrderReviewService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.info(infoBo));
    }

    @Test
    void infoShouldReturnVoWithAndWithoutOrder() {
        AdminReviewInfoBo infoBo = new AdminReviewInfoBo();
        infoBo.setId(1L);
        OrderReview review = new OrderReview();
        review.setId(1L);
        review.setOrderId(10L);
        review.setUserId(100L);
        when(iOrderReviewService.getById(1L)).thenReturn(review);

        when(iRecycleOrderService.getById(10L)).thenReturn(null);
        AdminReviewVo noOrder = service.info(infoBo);
        assertEquals("RO001", noOrder.getOrderNo());
        assertNull(noOrder.getOrderStatus());

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus("COMPLETED");
        order.setStatusDesc("已完成");
        when(iRecycleOrderService.getById(10L)).thenReturn(order);
        AdminReviewVo withOrder = service.info(infoBo);
        assertEquals("COMPLETED", withOrder.getOrderStatus());
        assertEquals("已完成", withOrder.getOrderStatusDesc());
    }

    @Test
    void replyShouldThrowWhenMissing() {
        AdminReviewReplyBo replyBo = new AdminReviewReplyBo();
        replyBo.setId(1L);
        replyBo.setAdminReply("感谢反馈");
        when(iOrderReviewService.getById(1L)).thenReturn(null);
        assertThrows(BizException.class, () -> service.reply(replyBo));
    }

    @Test
    void replyShouldUpdateReviewAndAppendLogWhenOrderExists() {
        AdminReviewReplyBo replyBo = new AdminReviewReplyBo();
        replyBo.setId(1L);
        replyBo.setAdminReply("感谢反馈");

        OrderReview review = new OrderReview();
        review.setId(1L);
        review.setOrderId(10L);
        when(iOrderReviewService.getById(1L)).thenReturn(review);

        RecycleOrder order = new RecycleOrder();
        order.setId(10L);
        order.setStatus("COMPLETED");
        when(iRecycleOrderService.getById(10L)).thenReturn(order);

        service.reply(replyBo);

        verify(iOrderReviewService).updateById(any(OrderReview.class));
        ArgumentCaptor<RecycleOrderLog> captor = ArgumentCaptor.forClass(RecycleOrderLog.class);
        verify(iRecycleOrderLogService).save(captor.capture());
        assertEquals("REVIEW_REPLY", captor.getValue().getActionType());
        assertTrue(captor.getValue().getContent().contains("感谢反馈"));
    }

    @Test
    void replyShouldSkipLogWhenOrderMissing() {
        AdminReviewReplyBo replyBo = new AdminReviewReplyBo();
        replyBo.setId(1L);
        replyBo.setAdminReply("感谢反馈");

        OrderReview review = new OrderReview();
        review.setId(1L);
        review.setOrderId(10L);
        when(iOrderReviewService.getById(1L)).thenReturn(review);
        when(iRecycleOrderService.getById(10L)).thenReturn(null);

        service.reply(replyBo);

        verify(iOrderReviewService).updateById(any(OrderReview.class));
        verify(iRecycleOrderLogService, never()).save(any());
    }
}
