package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.OrderLockStock;
import com.chunruo.core.service.OrderLockStockManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("orderLockStockByProductIdCacheManager")
public class OrderLockStockByProductIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private OrderLockStockManager orderLockStockManager;
	
	public List<OrderLockStock> getSession(Long productId){
		return this.orderLockStockManager.getOrderLockStockListByProductId(productId, false);
	}
	
	public void removeSession(Long productId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}

	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		List<OrderLockStock> orderLockStockList = this.orderLockStockManager.getOrderLockStockListByUpdateTime(new Date(nextLastTime));
		if(orderLockStockList != null && orderLockStockList.size() > 0){
			cacheObject.setSize(orderLockStockList.size());
			Date lastUpdateTime = null;
			final List<Long> productIdList = new ArrayList<Long> ();
			for(final OrderLockStock orderLockStock : orderLockStockList){
				if(lastUpdateTime == null || lastUpdateTime.before(orderLockStock.getUpdateTime())){
					lastUpdateTime = orderLockStock.getUpdateTime();
				}
				
				if(!productIdList.contains(orderLockStock.getProductId())){
					productIdList.add(orderLockStock.getProductId());
				}
			}
			
			// 更新缓存
			if(productIdList != null && productIdList.size() > 0){
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
					@Override
					public void run() {
						try{
							// 更新渠道列表页面数据
							OrderLockStockByProductIdCacheManager orderLockStockByProductIdCacheManager = Constants.ctx.getBean(OrderLockStockByProductIdCacheManager.class);
							for(Long productId : productIdList){
								try{
									orderLockStockByProductIdCacheManager.removeSession(productId);
								}catch(Exception e){
									continue;
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}