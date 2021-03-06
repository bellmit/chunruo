package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Refund;
import com.chunruo.core.service.RefundManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("refundByOrderItemIdCacheManager")
public class RefundByOrderItemIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private RefundManager refundManager;

	public Refund getSession(Long orderItemId) {
		List<Refund> refundList = this.refundManager.getRefundListByOrderItemId(orderItemId, true);
		if(refundList != null && refundList.size() > 0){
			return refundList.get(0);
		}
		return null;
	}

	public void removeSession(Long orderItemId) {
		// 如果过期后要做特殊处理，可在此实现
		// log.info("removeSession userId:" + userId + ",userToken:" +
		// userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		final RefundByOrderItemIdCacheManager refundByOrderItemIdCacheManager = Constants.ctx.getBean(RefundByOrderItemIdCacheManager.class);
		final RefundListByUserIdCacheManager refundListByUserIdCacheManager = Constants.ctx.getBean(RefundListByUserIdCacheManager.class);
		final RefundListByStoreIdCacheManager refundListByStoreIdCacheManager = Constants.ctx.getBean(RefundListByStoreIdCacheManager.class);

		
		List<Refund> refundList = this.refundManager.getRefundListByUpdateTime(new Date(nextLastTime));
		if (refundList != null && refundList.size() > 0) {
			cacheObject.setSize(refundList.size());
			
			Date lastUpdateTime = null;
			Set<Long> userIdSet = new HashSet<Long> ();
			Set<Long> storeIdSet = new HashSet<Long>();
			Set<Long> orderItemIdSet = new HashSet<Long>();
			for (final Refund refund : refundList) {
				userIdSet.add(refund.getUserId());
				storeIdSet.add(refund.getStoreId());
				orderItemIdSet.add(refund.getOrderItemId());
				// 同步最后更新时间
				if (lastUpdateTime == null || lastUpdateTime.before(refund.getUpdateTime())) {
					lastUpdateTime = refund.getUpdateTime();
				}
			}
			
			for (final Long userId : userIdSet) {
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable() {
					@Override
					public void run() {
						try {
							// 删除缓存，下次重新加载
							refundListByUserIdCacheManager.removeSession(userId);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			
			for (final Long storeId : storeIdSet) {
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable() {
					@Override
					public void run() {
						try {
							// 删除缓存，下次重新加载
							refundListByStoreIdCacheManager.removeSession(storeId);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			
			for (final Long itemId : orderItemIdSet) {
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable() {
					@Override
					public void run() {
						try {
							// 删除缓存，下次重新加载
							refundByOrderItemIdCacheManager.removeSession(itemId);
						} catch (Exception e) {
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
