package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.OrderHistory;

public interface OrderHistoryManager extends GenericManager<OrderHistory, Long>{

	public List<OrderHistory> getOrderHistoryListByOrderId(Long orderId);
	
	/**
	 * 拼装日志BEAN
	 * @param orderId
	 * @param name
	 * @param message
	 * @return
	 */
	OrderHistory createOrderHistoryBean(Long orderId, String name, String message);
}
