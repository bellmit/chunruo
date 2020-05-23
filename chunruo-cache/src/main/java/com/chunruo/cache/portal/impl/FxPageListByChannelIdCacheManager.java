package com.chunruo.cache.portal.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.FxPage;
import com.chunruo.core.service.FxPageManager;
import com.chunruo.core.util.StringUtil;

@Service("fxPageListByChannelIdCacheManager")
public class FxPageListByChannelIdCacheManager {
	@Autowired
	private FxPageManager fxPageManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'fxPageList_'+#channelId")
	public Map<String, FxPage> getSession(Long channelId){
		Map<String, FxPage> fxPageMap = new HashMap<String, FxPage> ();
		List<FxPage> fxPageList = this.fxPageManager.getFxPageListByChannelId(channelId);
		if(fxPageList != null && fxPageList.size() > 0){
			for(FxPage fxPage : fxPageList){
				fxPageMap.put(StringUtil.null2Str(fxPage.getPageId()), fxPage);
			}
		}
		return fxPageMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'fxPageList_'+#channelId")
	public void removeSession(Long channelId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
