package com.sqx.app.payout.service;

import com.sqx.app.order.domain.entity.RecycleOrder;
import com.sqx.app.payout.domain.bo.PayoutInfoBo;
import com.sqx.app.payout.domain.vo.PayoutInfoVo;

import java.math.BigDecimal;

public interface PayoutService {

    PayoutInfoVo info(Long userId, PayoutInfoBo infoBo);

    void createAndComplete(RecycleOrder order, BigDecimal amount, Long operatorId);

    void startPayout(RecycleOrder order, BigDecimal amount, Long operatorId);

    void finishPayout(RecycleOrder order, Long operatorId);
}
