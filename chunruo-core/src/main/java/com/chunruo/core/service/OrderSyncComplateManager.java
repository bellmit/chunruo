package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.OrderSyncComplate;

public interface OrderSyncComplateManager extends GenericManager<OrderSyncComplate, Long> {
	
	public void updatOrderSyncComplateTask();
	
	public List<OrderSyncComplate> loadOrderSyncComplateTask();
}
