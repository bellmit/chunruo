package com.chunruo.cache.portal.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.chunruo.core.model.ProductShareRecord;
import com.chunruo.core.service.ProductShareRecordManager;

@Service("productShareRecordByTokenCacheManager")
public class ProductShareRecordByTokenCacheManager {
	@Autowired
	private ProductShareRecordManager productShareRecordManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productShareRecordByToken_'+#token")
	public ProductShareRecord getSession(String token){
		return this.productShareRecordManager.getProductShareRecordByToken(token);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productShareRecordByToken_'+#token")
	public void removeSession(String token) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
