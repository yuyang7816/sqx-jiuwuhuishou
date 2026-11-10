package com.sqx.app.review.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqx.app.review.db.mapper.OrderReviewMapper;
import com.sqx.app.review.db.service.IOrderReviewService;
import com.sqx.app.review.domain.entity.OrderReview;
import org.springframework.stereotype.Service;

@Service
public class IOrderReviewServiceImpl extends ServiceImpl<OrderReviewMapper, OrderReview> implements IOrderReviewService {
}
