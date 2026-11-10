package com.sqx.app.admin.orderflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.app.admin.orderflow.domain.bo.AdminReviewInfoBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminReviewPageBo;
import com.sqx.app.admin.orderflow.domain.bo.AdminReviewReplyBo;
import com.sqx.app.admin.orderflow.domain.vo.AdminReviewVo;
import com.sqx.app.admin.orderflow.service.AdminReviewManageService;
import com.sqx.app.admin.support.AdminLookupSupport;
import com.sqx.app.order.db.service.IRecycleOrderLogService;
import com.sqx.app.order.db.service.IRecycleOrderService;
import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.order.domain.entity.RecycleOrderLog;
import com.sqx.app.review.db.service.IOrderReviewService;
import com.sqx.app.review.domain.entity.OrderReview;
import com.sqx.common.core.exception.BizException;
import com.sqx.common.mybatis.utils.PageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class AdminReviewManageServiceImpl implements AdminReviewManageService {

    private static final String ACTION_REVIEW_REPLY = "REVIEW_REPLY";
    private static final String OPERATOR_TYPE_ADMIN = "ADMIN";

    @Resource
    private IOrderReviewService iOrderReviewService;
    @Resource
    private AdminLookupSupport adminLookupSupport;
    @Resource
    private IRecycleOrderService iRecycleOrderService;
    @Resource
    private IRecycleOrderLogService iRecycleOrderLogService;

    @Override
    public IPage<AdminReviewVo> pageInfo(AdminReviewPageBo pageBo) {
        Page<OrderReview> initPage = PageUtil.initPage(pageBo.getPage(), pageBo.getLimit());

        List<Long> userIds = adminLookupSupport.resolveUserIds(pageBo.getUserName(), pageBo.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        List<Long> orderIds = adminLookupSupport.resolveOrderIds(pageBo.getOrderNo());
        if (orderIds != null && orderIds.isEmpty()) {
            return PageUtil.copyPage(initPage, Collections.emptyList());
        }

        LambdaQueryWrapper<OrderReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(userIds != null, OrderReview::getUserId, userIds)
                .in(orderIds != null, OrderReview::getOrderId, orderIds)
                .orderByDesc(OrderReview::getCreateTime);
        if ("pending".equalsIgnoreCase(pageBo.getReplyStatus())) {
            wrapper.and(w -> w.isNull(OrderReview::getAdminReply).or().eq(OrderReview::getAdminReply, ""));
        } else if ("replied".equalsIgnoreCase(pageBo.getReplyStatus())) {
            wrapper.isNotNull(OrderReview::getAdminReply).ne(OrderReview::getAdminReply, "");
        }

        IPage<OrderReview> iPage = iOrderReviewService.page(initPage, wrapper);
        List<AdminReviewVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            for (OrderReview record : iPage.getRecords()) {
                voList.add(toVo(record));
            }
        }
        return PageUtil.copyPage(iPage, voList);
    }

    @Override
    public AdminReviewVo info(AdminReviewInfoBo infoBo) {
        OrderReview entity = iOrderReviewService.getById(infoBo.getId());
        if (Objects.isNull(entity)) {
            throw new BizException("评价记录不存在");
        }
        AdminReviewVo vo = toVo(entity);
        RecycleOrder order = iRecycleOrderService.getById(entity.getOrderId());
        if (Objects.nonNull(order)) {
            vo.setOrderStatus(order.getStatus());
            vo.setOrderStatusDesc(order.getStatusDesc());
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reply(AdminReviewReplyBo replyBo) {
        OrderReview review = iOrderReviewService.getById(replyBo.getId());
        if (Objects.isNull(review)) {
            throw new BizException("评价记录不存在");
        }
        review.setAdminReply(replyBo.getAdminReply());
        review.setUpdateTime(LocalDateTime.now());
        iOrderReviewService.updateById(review);

        RecycleOrder order = iRecycleOrderService.getById(review.getOrderId());
        if (Objects.nonNull(order)) {
            RecycleOrderLog log = new RecycleOrderLog();
            log.setOrderId(order.getId());
            log.setActionType(ACTION_REVIEW_REPLY);
            log.setFromStatus(order.getStatus());
            log.setToStatus(order.getStatus());
            log.setContent("管理员回复评价：" + replyBo.getAdminReply());
            log.setOperatorType(OPERATOR_TYPE_ADMIN);
            iRecycleOrderLogService.save(log);
        }
    }

    private AdminReviewVo toVo(OrderReview entity) {
        AdminReviewVo vo = new AdminReviewVo();
        BeanUtils.copyProperties(entity, vo);
        vo.setOrderNo(adminLookupSupport.resolveOrderNo(entity.getOrderId()));
        adminLookupSupport.fillUserFields(entity.getUserId(), vo);
        return vo;
    }
}
