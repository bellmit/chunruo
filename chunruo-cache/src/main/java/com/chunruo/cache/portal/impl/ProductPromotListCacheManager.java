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
import com.chunruo.core.model.ProductPromot;
import com.chunruo.core.service.ProductPromotManager;

@Service("productPromotListCacheManager")
public class ProductPromotListCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private ProductPromotManager productPromotManager;

	@Cacheable(value = "sessionEhRedisCache", cacheManager = "sessionEhRedisCacheManager", key = "'productPromotList'")
	public List<ProductPromot> getSession() {
		return this.productPromotManager.getProductPromotListByStatus(true);
	}

	@CacheEvict(value = "sessionEhRedisCache", cacheManager = "sessionEhRedisCacheManager", key = "'productPromotList'")
	public void removeSession() {
		// 如果过期后要做特殊处理，可在此实现
		// log.info("removeSession userId:" + userId + ",userToken:" +
		// userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		ProductPromotListCacheManager productPromotListCacheManager = Constants.ctx.getBean(ProductPromotListCacheManager.class);
		List<ProductPromot> productPromotList = this.productPromotManager.getProductPromotByUpdateTime(new Date(nextLastTime));
		if (productPromotList != null && productPromotList.size() > 0) {
			cacheObject.setSize(productPromotList.size());
		
			Date lastUpdateTime = null;
			for (final ProductPromot productPromot : productPromotList) {
				// 同步最后更新时间
				if (lastUpdateTime == null || lastUpdateTime.before(productPromot.getUpdateTime())) {
					lastUpdateTime = productPromot.getUpdateTime();
				}
			}
			
			try {
				// 删除缓存，下次重新加载
				productPromotListCacheManager.removeSession();
			} catch (Exception e) {
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
