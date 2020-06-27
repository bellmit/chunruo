package com.chunruo.cache.portal.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderEvaluate;
import com.chunruo.core.service.OrderEvaluateManager;
import com.chunruo.core.util.StringUtil;

@Service("orderEvaluateListByUserIdCacheManager")
public class OrderEvaluateListByUserIdCacheManager{
	@Autowired
	private OrderEvaluateManager orderEvaluateManager;
	@Autowired
	private OrderWaitEvaluateListByStoreIdCacheManager orderWaitEvaluateListByStoreIdCacheManager;

	public Map<String, OrderEvaluate> getSession(Long userId) {
		 Map<String, OrderEvaluate> map = new HashMap<String, OrderEvaluate>();
		 List<OrderEvaluate> recordList = this.orderEvaluateManager.getOrderEvaluateListByUserId(userId, 50);
		 if(recordList != null && recordList.size() > 0) {
			 for(OrderEvaluate evaluate : recordList) {
				 map.put(StringUtil.null2Str(evaluate.getItemId()), evaluate);
			 }
		 }
		 return map;
	}

	public void removeSession(Long userId, Order order) {
		// 如果过期后要做特殊处理，可在此实现
		try {
			// 待评论数据更新
			orderWaitEvaluateListByStoreIdCacheManager.removeSession(order.getStoreId());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
