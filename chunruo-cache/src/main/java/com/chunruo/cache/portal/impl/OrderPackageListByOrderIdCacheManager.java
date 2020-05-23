package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.OrderPackage;
import com.chunruo.core.service.OrderPackageManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("orderPackageListByOrderIdCacheManager")
public class OrderPackageListByOrderIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private OrderPackageManager orderPackageManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'orderPackageListByOrderId_'+#orderId")
	public List<OrderPackage> getSession(Long orderId){
		return this.orderPackageManager.getOrderPackageListByOrderId(orderId);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'orderPackageListByOrderId_'+#orderId")
	public void removeSession(Long orderId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}

	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		List<OrderPackage> orderPackageList = this.orderPackageManager.getOrderPackageListByUpdateTime(new Date(nextLastTime));
		if(orderPackageList != null && orderPackageList.size() > 0){
			cacheObject.setSize(orderPackageList.size());
			Date lastUpdateTime = null;
			for(final OrderPackage orderPackage : orderPackageList){
				if(lastUpdateTime == null || lastUpdateTime.before(orderPackage.getUpdateTime())){
					lastUpdateTime = orderPackage.getUpdateTime();
				}
				
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
					@Override
					public void run() {
						try{
							// 更新渠道列表页面数据
							final OrderPackageListByOrderIdCacheManager orderPackageListByOrderIdCacheManager = Constants.ctx.getBean(OrderPackageListByOrderIdCacheManager.class);
							orderPackageListByOrderIdCacheManager.removeSession(orderPackage.getOrderId());
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
