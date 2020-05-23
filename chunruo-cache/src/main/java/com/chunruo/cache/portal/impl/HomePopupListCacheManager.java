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
import com.chunruo.core.model.HomePopup;
import com.chunruo.core.service.HomePopupManager;

@Service("homePopupListCacheManager")
public class HomePopupListCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private HomePopupManager homePopupManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'homePopupList'")
	public List<HomePopup> getSession(){
		return this.homePopupManager.getHomePopupListByIsEnable(true);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'homePopupList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		HomePopupListCacheManager homePopupListCacheManager = Constants.ctx.getBean(HomePopupListCacheManager.class);
		List<HomePopup> homePopupList = this.homePopupManager.getHomePopupListByUpdateTime(new Date(nextLastTime));
		if(homePopupList != null && homePopupList.size() > 0){
			cacheObject.setSize(homePopupList.size());
			Date lastUpdateTime = null;
			for(final HomePopup homePopup : homePopupList){
				if(lastUpdateTime == null || lastUpdateTime.before(homePopup.getUpdateTime())){
					lastUpdateTime = homePopup.getUpdateTime();
				}
			}
			
			try{
				// 更新渠道缓存列表
				homePopupListCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
