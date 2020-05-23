package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.service.OrderItemsManager;
import com.chunruo.core.util.StringUtil;

@Service("orderWaitEvaluateListByStoreIdCacheManager")
public class OrderWaitEvaluateListByStoreIdCacheManager {
	@Autowired
	private OrderItemsManager orderItemsManager;
	@Autowired
	private OrderByIdCacheManager orderByIdCacheManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'orderWaitEvaluateList_'+#storeId")
	public Map<String, List<OrderItems>> getSession(Long storeId){
		Map<String,List<OrderItems>> map = new HashMap<String,List<OrderItems>>();
		List<OrderItems> orderItemsList = this.orderItemsManager.getOrderItemsListByNoEvaluate(storeId);
		if(orderItemsList != null && !orderItemsList.isEmpty()) {
			for(OrderItems orderItems : orderItemsList) {
				String orderId = StringUtil.null2Str(orderItems.getOrderId());
				Order order = this.orderByIdCacheManager.getSession(StringUtil.nullToLong(orderId));
				if(order == null 
						|| order.getOrderId() == null
						|| StringUtil.nullToBoolean(order.getIsDelete())
						|| StringUtil.nullToBoolean(order.getIsInvitationAgent())) {
					continue;
				}
				
				orderItems.setIsMyselfStore(StringUtil.nullToBoolean(order.getIsMyselfStore()));
				if(map.containsKey(orderId)) {
					map.get(orderId).add(orderItems);
				}else {
					List<OrderItems> list = new ArrayList<OrderItems>();
					list.add(orderItems);
					map.put(orderId, list);
				}
			}
		}
		return map;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'orderWaitEvaluateList_'+#storeId")
	public void removeSession(Long storeId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
