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
import com.chunruo.core.model.NoviceGuide;
import com.chunruo.core.service.NoviceGuideManager;

@Service("noviceGuideListCacheManager")
public class NoviceGuideListCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private NoviceGuideManager noviceGuideManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'noviceGuideList'")
	public List<NoviceGuide> getSession(){
		return this.noviceGuideManager.getNoviceGuideListByStatus(true);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'noviceGuideList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		final NoviceGuideListCacheManager noviceGuideListCacheManager = Constants.ctx.getBean(NoviceGuideListCacheManager.class);
		List<NoviceGuide> list = this.noviceGuideManager.getNoviceGuideListByUpdateTime(new Date(nextLastTime));
		if(list != null && list.size() > 0){
			cacheObject.setSize(list.size());
			Date lastUpdateTime = null;
			for(NoviceGuide noviceGuide : list){
				if(lastUpdateTime == null || lastUpdateTime.before(noviceGuide.getUpdateTime())){
					lastUpdateTime = noviceGuide.getUpdateTime();
				}
			}
			noviceGuideListCacheManager.removeSession();
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
