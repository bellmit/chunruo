package com.chunruo.cache.portal.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.OrderItems;
import com.chunruo.core.service.OrderItemsManager;

@Service("orderItemsListByOrderIdCacheManager")
public class OrderItemsListByOrderIdCacheManager {
	@Autowired
	private OrderItemsManager orderItemsManager;
	
	public List<OrderItems> getSession(Long orderId){
		return this.orderItemsManager.getOrderItemsListByOrderId(orderId);
	}
	
	public List<OrderItems> addSession(Long orderId, List<OrderItems> orderItemsList){
		return orderItemsList;
	}
	
	public void removeSession(Long orderId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
