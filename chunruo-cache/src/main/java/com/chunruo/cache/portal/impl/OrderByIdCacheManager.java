package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.model.Order;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("orderByIdCacheManager")
public class OrderByIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private OrderManager orderManager;
	@Autowired
	private OrderItemsListByOrderIdCacheManager orderItemsListByOrderIdCacheManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'order_'+#orderId")
	public Order getSession(Long orderId){
		Order order = this.orderManager.getOrderByOrderId(orderId);
		if(order != null 
				&& order.getOrderId() != null 
				&& order.getOrderItemsList() != null
				&& order.getOrderItemsList().size() > 0) {
			try {
				this.orderItemsListByOrderIdCacheManager.addSession(orderId, order.getOrderItemsList());
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return order;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'order_'+#orderId")
	public void removeSession(Long orderId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}

	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		final OrderByIdCacheManager orderByIdCacheManager = Constants.ctx.getBean(OrderByIdCacheManager.class);
		final OrderListByUserIdCacheManager orderListByUserIdCacheManager = Constants.ctx.getBean(OrderListByUserIdCacheManager.class);
		final OrderListByStoreIdCacheManager orderListByStoreIdCacheManager = Constants.ctx.getBean(OrderListByStoreIdCacheManager.class);
		final OrderWaitEvaluateListByStoreIdCacheManager orderWaitEvaluateListByStoreIdCacheManager = Constants.ctx.getBean(OrderWaitEvaluateListByStoreIdCacheManager.class);
		List<Order> orderList = this.orderManager.getOrderListByUpdateTime(new Date(nextLastTime));
		if(orderList != null && orderList.size() > 0){
			cacheObject.setSize(orderList.size());
			
			Date lastUpdateTime = null;
			final Set<Long> waitEvaluateStoreIdList = new HashSet<Long> ();
			final Set<Long> orderIdList = new HashSet<Long> ();
			final Map<Long, List<Order>> userIdOrderListMap = new HashMap<Long, List<Order>> ();
			final Map<Long, List<Order>> storeIdOrderListMap = new HashMap<Long, List<Order>> ();
			final Map<String, List<Order>> identityNoOrderListMap = new HashMap<String, List<Order>> ();
			for(final Order order : orderList){
				if(lastUpdateTime == null || lastUpdateTime.before(order.getUpdateTime())){
					lastUpdateTime = order.getUpdateTime();
				}
				
				// 已发货
				orderIdList.add(order.getOrderId());
				if(StringUtil.compareObject(OrderStatus.OVER_ORDER_STATUS, StringUtil.nullToInteger(order.getStatus()))){
					waitEvaluateStoreIdList.add(order.getStoreId());
				}
				
				// 用户订单更新记录
				if(userIdOrderListMap.containsKey(order.getUserId())) {
					userIdOrderListMap.get(order.getUserId()).add(order);
				}else {
					List<Order> list = new ArrayList<Order> ();
					list.add(order);
					userIdOrderListMap.put(order.getUserId(), list);
				}
				
				// 店铺订单更新记录
				if(storeIdOrderListMap.containsKey(order.getStoreId())) {
					storeIdOrderListMap.get(order.getStoreId()).add(order);
				}else {
					List<Order> list = new ArrayList<Order> ();
					list.add(order);
					storeIdOrderListMap.put(order.getStoreId(), list);
				}
				
				// 跨境订单身份证限购更新记录
				if(StringUtil.nullToBoolean(order.getIsPaymentSucc()) 
						&& !StringUtil.nullToBoolean(order.getIsSplitSingle())
						&& Constants.PRODUCT_TYPE_CROSS_LIST.contains(order.getProductType())
						&& !StringUtil.isNull(order.getIdentityNo())) {
					if(identityNoOrderListMap.containsKey(order.getIdentityNo())) {
						identityNoOrderListMap.get(order.getIdentityNo()).add(order);
					}else {
						List<Order> list = new ArrayList<Order> ();
						list.add(order);
						identityNoOrderListMap.put(order.getIdentityNo(), list);
					}
				}
			}
			
			BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
				@Override
				public void run() {
					try{
						// 更新订单缓存
						if(orderIdList != null && orderIdList.size() > 0){
							for(Long orderId : orderIdList){
								orderByIdCacheManager.removeSession(orderId);
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					
					try{
						// 用户订单更新记录
						if(userIdOrderListMap != null && userIdOrderListMap.size() > 0){
							for(Entry<Long, List<Order>> entry : userIdOrderListMap.entrySet()){
								orderListByUserIdCacheManager.addSession(entry.getKey(), entry.getValue());
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					
					try{
						// 店铺订单更新记录
						if(storeIdOrderListMap != null && storeIdOrderListMap.size() > 0){
							for(Entry<Long, List<Order>> entry : storeIdOrderListMap.entrySet()){
								orderListByStoreIdCacheManager.addSession(entry.getKey(), entry.getValue());
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					
					try{
						// 更新用户评论缓存
						if(waitEvaluateStoreIdList != null && waitEvaluateStoreIdList.size() > 0){
							for(Long storeId : waitEvaluateStoreIdList){
								orderWaitEvaluateListByStoreIdCacheManager.removeSession(storeId);
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
