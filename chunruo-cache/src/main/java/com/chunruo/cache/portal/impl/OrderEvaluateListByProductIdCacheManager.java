package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.OrderEvaluate;
import com.chunruo.core.service.OrderEvaluateManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("orderEvaluateListByProductIdCacheManager")
public class OrderEvaluateListByProductIdCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private OrderEvaluateManager orderEvaluateManager;

	public Map<String, OrderEvaluate> getSession(Long productId) {
		 Map<String, OrderEvaluate> map = new HashMap<String, OrderEvaluate>();
		 List<OrderEvaluate> recordList = this.orderEvaluateManager.getOrderEvaluateListByProductId(productId);
		 if(recordList != null && recordList.size() > 0) {
			 for(OrderEvaluate evaluate : recordList) {
				 map.put(StringUtil.null2Str(evaluate.getItemId()), evaluate);
			 }
		 }
		 return map;
	}

	public void removeSession(Long productId) {
		// 如果过期后要做特殊处理，可在此实现
		// log.info("removeSession userId:" + userId + ",userToken:" +
		// userToken);
	}

	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		final OrderEvaluateListByProductIdCacheManager orderEvaluateListByProductIdCacheManager = Constants.ctx.getBean(OrderEvaluateListByProductIdCacheManager.class);
		List<OrderEvaluate> evaluateList = this.orderEvaluateManager.getOrderEvaluateListByUpdateTime(new Date(nextLastTime));
		if (evaluateList != null && evaluateList.size() > 0) {
			cacheObject.setSize(evaluateList.size());
			Date lastUpdateTime = null;
			final Set<Long> productIdSet = new HashSet<Long>();
			for (final OrderEvaluate evaluate : evaluateList) {
				productIdSet.add(evaluate.getProductId());
				
				// 同步最后更新时间
				if (lastUpdateTime == null || lastUpdateTime.before(evaluate.getUpdateTime())) {
					lastUpdateTime = evaluate.getUpdateTime();
				}
			}
			
			BaseThreadPool.getThreadPoolExecutor().execute(new Runnable() {
				@Override
				public void run() {
					if(productIdSet != null && productIdSet.size() > 0){
						for (final Long productId : productIdSet) {
							try {
								// 删除缓存，下次重新加载
								orderEvaluateListByProductIdCacheManager.removeSession(productId);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
