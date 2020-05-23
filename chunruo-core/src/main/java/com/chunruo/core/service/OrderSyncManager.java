package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.OrderPackage;
import com.chunruo.core.model.OrderSync;

public interface OrderSyncManager extends GenericManager<OrderSync, Long> {

	public void updateOrderSyncStatus(OrderSync orderSync);
	
	public List<OrderSync> updateOrderSyncStatusByLoadFunction();
	
	public List<OrderSync> getOrderSyncListByOrderNumberList(List<String> orderNumberList);
	
	public void saveOrUpdateOrderSyncStatus(OrderSync orderSync);
	
	public List<OrderSync> getOrderSyncListByOrderNumber(String orderNumber);
	
	public List<OrderSync> getOrderSyncListByOrderNumber(String orderNumber, Boolean isHandler);
	
	public List<OrderSync> saveOrderSyncOutLibrary(List<OrderSync> orderSyncList, Long userId);
	
	public void updateOrderSyncExpress(OrderSync orderSync,OrderPackage orderPackage);
}
