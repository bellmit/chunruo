package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.OrderSync;
import com.chunruo.core.model.OrderSyncList;

public interface OrderSyncListManager extends GenericManager<OrderSyncList, Long> {

	public Date getOrderSyncListMaxEndTime();
	
	public OrderSyncList saveOrderSyncList(OrderSyncList orderSyncList, List<OrderSync> orderSyncRecordList);
}
