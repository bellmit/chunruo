package com.chunruo.cache.portal.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.FxChildren;
import com.chunruo.core.service.FxChildrenManager;

@Service("fxChildrenListByPageIdCacheManager")
public class FxChildrenListByPageIdCacheManager {
	@Autowired
	private FxChildrenManager fxChildrenManager;
	
	public List<FxChildren> getSession(Long pageId){
		return this.fxChildrenManager.getFxChildrenListByPageId(pageId);
	}
	
	public void removeSession(Long pageId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
