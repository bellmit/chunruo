package com.chunruo.cache.portal.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.Order;
import com.chunruo.core.service.OrderManager;

@Service("orderSubListByOrderIdCacheManager")
public class OrderSubListByOrderIdCacheManager {
	@Autowired
	private OrderManager orderManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'orderSubList_'+#parentOrderId")
	public List<Order> getSession(Long parentOrderId){
		return this.orderManager.getOrderSubListByParentOrderId(parentOrderId);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'orderSubList_'+#parentOrderId")
	public void removeSession(Long parentOrderId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
