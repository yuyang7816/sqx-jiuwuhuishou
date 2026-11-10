package com.sqx.app.review.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.app.review.db.service.IOrderReviewService;
import com.sqx.app.review.domain.bo.ReviewInfoBo;
import com.sqx.app.review.domain.bo.ReviewSaveBo;
import com.sqx.app.review.domain.entity.OrderReview;
import com.sqx.app.review.domain.vo.ReviewInfoVo;
import com.sqx.common.core.exception.BizException;
import com.sqx.testsupport.MybatisPlusLambdaInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private IOrderReviewService iOrderReviewService;
    @Mock
    private IRecycleOrderService iRecycleOrderService;
    @Mock
    private IRecycleOrderLogService iRecycleOrderLogService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @BeforeAll
    static void initLambda() {
        MybatisPlusLambdaInit.init(OrderReview.class, RecycleOrder.class, RecycleOrderLog.class);
    }

    @Test
    void infoShouldReportReviewedFlag() {
        when(iRecycleOrderService.getById(1L)).thenReturn(ownedOrder(1L, 10L, RecycleOrderStatus.COMPLETED.getCode()));
        when(iOrderReviewService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        ReviewInfoBo bo = new ReviewInfoBo();
        bo.setOrderId(1L);
        assertFalse(reviewService.info(10L, bo).getReviewed());

        OrderReview review = new OrderReview();
        review.setOrderId(1L);
        review.setScore(5);
        review.setContent("好");
        when(iOrderReviewService.getOne(any(LambdaQueryWrapper.class))).thenReturn(review);
        ReviewInfoVo vo = reviewService.info(10L, bo);
        assertTrue(vo.getReviewed());
        assertEquals(5, vo.getScore());
    }

    @Test
    void infoShouldRejectForeignOrder() {
        when(iRecycleOrderService.getById(1L)).thenReturn(null);
        ReviewInfoBo bo = new ReviewInfoBo();
        bo.setOrderId(1L);
        assertThrows(BizException.class, () -> reviewService.info(10L, bo));

        when(iRecycleOrderService.getById(1L)).thenReturn(ownedOrder(1L, 99L, RecycleOrderStatus.COMPLETED.getCode()));
        assertThrows(BizException.class, () -> reviewService.info(10L, bo));
    }

    @Test
    void saveShouldValidateAndPersistWithComplaint() {
        RecycleOrder order = ownedOrder(1L, 10L, RecycleOrderStatus.WAIT_PAYOUT.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        ReviewSaveBo bo = new ReviewSaveBo();
        bo.setOrderId(1L);
        bo.setScore(4);
        bo.setContent("一般");
        assertThrows(BizException.class, () -> reviewService.save(10L, bo));

        order.setStatus(RecycleOrderStatus.COMPLETED.getCode());
        OrderReview existing = new OrderReview();
        existing.setOrderId(1L);
        when(iOrderReviewService.getOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
        assertThrows(BizException.class, () -> reviewService.save(10L, bo));

        when(iOrderReviewService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        bo.setComplaintType("迟到");
        ReviewInfoVo vo = reviewService.save(10L, bo);
        assertTrue(vo.getReviewed());
        verify(iOrderReviewService).save(any(OrderReview.class));

        ArgumentCaptor<RecycleOrderLog> logCaptor = ArgumentCaptor.forClass(RecycleOrderLog.class);
        verify(iRecycleOrderLogService).save(logCaptor.capture());
        assertTrue(logCaptor.getValue().getContent().contains("迟到"));
    }

    @Test
    void saveShouldAppendPlainContentWhenNoComplaint() {
        RecycleOrder order = ownedOrder(1L, 10L, RecycleOrderStatus.COMPLETED.getCode());
        when(iRecycleOrderService.getById(1L)).thenReturn(order);
        when(iOrderReviewService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        ReviewSaveBo bo = new ReviewSaveBo();
        bo.setOrderId(1L);
        bo.setScore(5);
        bo.setContent("很好");
        reviewService.save(10L, bo);

        ArgumentCaptor<RecycleOrderLog> logCaptor = ArgumentCaptor.forClass(RecycleOrderLog.class);
        verify(iRecycleOrderLogService).save(logCaptor.capture());
        assertEquals("用户提交服务评价", logCaptor.getValue().getContent());
    }

    private RecycleOrder ownedOrder(Long id, Long userId, String status) {
        RecycleOrder order = new RecycleOrder();
        order.setId(id);
        order.setUserId(userId);
        order.setStatus(status);
        return order;
    }
}
