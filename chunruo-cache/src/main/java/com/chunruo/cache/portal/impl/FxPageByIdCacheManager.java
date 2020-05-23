package com.chunruo.cache.portal.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.FxPage;
import com.chunruo.core.service.FxPageManager;

@Service("fxPageByIdCacheManager")
public class FxPageByIdCacheManager {
	@Autowired
	private FxPageManager fxPageManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'fxPage_'+#pageId")
	public FxPage getSession(Long pageId){
		return this.fxPageManager.get(pageId);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'fxPage_'+#pageId")
	public void removeSession(Long pageId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
