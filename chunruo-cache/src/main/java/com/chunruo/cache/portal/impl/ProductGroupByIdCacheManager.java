package com.chunruo.cache.portal.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.ProductGroup;
import com.chunruo.core.service.ProductGroupManager;

@Service("productGroupByIdCacheManager")
public class ProductGroupByIdCacheManager {
	@Autowired
	private ProductGroupManager productGroupManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productGroup_'+#productGroupId")
	public ProductGroup getSession(Long productGroupId){
		return this.productGroupManager.get(productGroupId);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productGroup_'+#productGroupId")
	public void removeSession(Long productGroupId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
