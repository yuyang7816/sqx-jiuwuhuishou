package com.sqx.app.pickup.service;

import com.sqx.app.order.domain.entity.RecycleOrder;

public interface PickupTaskAssignService {

    void assignForOrder(RecycleOrder order, String itemSummary);
}
