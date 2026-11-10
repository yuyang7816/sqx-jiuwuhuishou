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
import com.sqx.app.review.service.ReviewService;
import com.sqx.common.core.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final String ACTION_REVIEW = "REVIEW";
    private static final String OPERATOR_TYPE_USER = "USER";

    @Resource
    private IOrderReviewService iOrderReviewService;
    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private IRecycleOrderLogService iRecycleOrderLogService;

    @Override
    public ReviewInfoVo info(Long userId, ReviewInfoBo infoBo) {
        RecycleOrder order = getOwnedOrder(userId, infoBo.getOrderId());
        OrderReview review = getReview(order.getId());
        ReviewInfoVo vo = new ReviewInfoVo();
        vo.setOrderId(order.getId());
        if (Objects.nonNull(review)) {
            BeanUtils.copyProperties(review, vo);
            vo.setReviewed(true);
        } else {
            vo.setReviewed(false);
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewInfoVo save(Long userId, ReviewSaveBo saveBo) {
        RecycleOrder order = getOwnedOrder(userId, saveBo.getOrderId());
        if (!RecycleOrderStatus.COMPLETED.getCode().equals(order.getStatus())) {
            throw new BizException("订单未完成，暂不可评价");
        }

        OrderReview existing = getReview(order.getId());
        if (Objects.nonNull(existing)) {
            throw new BizException("订单已评价，请勿重复提交");
        }

        OrderReview review = new OrderReview();
        review.setOrderId(order.getId());
        review.setUserId(userId);
        review.setScore(saveBo.getScore());
        review.setComplaintType(saveBo.getComplaintType());
        review.setContent(saveBo.getContent());
        iOrderReviewService.save(review);

        String content = "用户提交服务评价";
        if (StringUtils.isNotBlank(saveBo.getComplaintType())) {
            content = content + "：" + saveBo.getComplaintType();
        }
        appendOrderLog(order.getId(), ACTION_REVIEW, order.getStatus(), order.getStatus(), content, userId);

        ReviewInfoVo vo = new ReviewInfoVo();
        BeanUtils.copyProperties(review, vo);
        vo.setReviewed(true);
        return vo;
    }

    private RecycleOrder getOwnedOrder(Long userId, Long orderId) {
        RecycleOrder order = iRecycleOrderService.getById(orderId);
        if (Objects.isNull(order) || !Objects.equals(order.getUserId(), userId)) {
            throw new BizException("订单不存在");
        }
        return order;
    }

    private OrderReview getReview(Long orderId) {
        return iOrderReviewService.getOne(new LambdaQueryWrapper<OrderReview>()
                .eq(OrderReview::getOrderId, orderId)
                .last("LIMIT 1"));
    }

    private void appendOrderLog(Long orderId, String actionType, String fromStatus, String toStatus,
                                 String content, Long operatorId) {
        RecycleOrderLog log = new RecycleOrderLog();
        log.setOrderId(orderId);
        log.setActionType(actionType);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setContent(content);
        log.setOperatorId(operatorId);
        log.setOperatorType(OPERATOR_TYPE_USER);
        iRecycleOrderLogService.save(log);
    }
}
