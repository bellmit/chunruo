package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.Order;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.util.StringUtil;

@Service("orderListByUserIdCacheManager")
public class OrderListByUserIdCacheManager {
	private Lock lock = new ReentrantLock();
	@Autowired
	private OrderManager orderManager;
	
	public List<Order> getSession(Long userId){
		List<Order> resultList = new ArrayList<Order> ();
		List<Order> orderList = this.orderManager.getOrderListByUserId(userId, 5000);
		if(orderList != null && orderList.size() > 0){
			for(Order order : orderList){
				if(StringUtil.nullToBoolean(order.getIsDelete())){
					continue;
				}
				if(StringUtil.nullToBoolean(order.getIsInvitationAgent())) {
					continue;
				}
				resultList.add(order);
			}
			
			// 按创建时间排序
			Collections.sort(resultList, new Comparator<Order>() {
				public int compare(Order obj1, Order obj2) {
					Long orderId1 = StringUtil.nullToLong(obj1.getOrderId());
					Long orderId2 = StringUtil.nullToLong(obj2.getOrderId());
					return (orderId1.longValue() < orderId2.longValue()) ? 1 : -1;
				}
			});
		}
		return resultList;
	}
	
	public List<Order> addSession(Long userId, List<Order> modfiyOrderList){
		// 加锁
		lock.lock();
		List<Order> list = null;
		try {
			list = this.getSession(userId);
			if(modfiyOrderList != null && modfiyOrderList.size() > 0) {
				// 已缓存的订单对象转换Map
				Map<Long, Order> orderMap = new HashMap<Long, Order> ();
				if(list != null && list.size() > 0) {
					for(Order order : list) {
						orderMap.put(order.getOrderId(), order);
					}
				}
				
				// 使用已修改的订单替换缓存信息
				for(Order order : modfiyOrderList) {
					orderMap.put(order.getOrderId(), order);
				}
				
				// 重新组合List对象集合
				List<Order> orderList = new ArrayList<Order> ();
				for(Order order : orderMap.values()) {
					if(StringUtil.nullToBoolean(order.getIsDelete())){
						continue;
					}
					
					orderList.add(order);
				}
				
				// 重新按创建时间排序
				Collections.sort(orderList, new Comparator<Order>() {
					public int compare(Order obj1, Order obj2) {
						Long orderId1 = StringUtil.nullToLong(obj1.getOrderId());
						Long orderId2 = StringUtil.nullToLong(obj2.getOrderId());
						return (orderId1.longValue() < orderId2.longValue()) ? 1 : -1;
					}
				});
				return orderList;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			// 释放锁
			lock.unlock(); 
		}
		return list;
	}
	
	public void removeSession( Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
