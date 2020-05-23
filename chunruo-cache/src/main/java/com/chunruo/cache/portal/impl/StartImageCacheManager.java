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
import com.chunruo.core.model.StartImage;
import com.chunruo.core.service.StartImageManager;

@Service("startImageCacheManager")
public class StartImageCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private StartImageManager startImageManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'startImageList'")
	public List<StartImage> getSession(){
		return this.startImageManager.getStartImageList(1);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'startImageList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		final StartImageCacheManager startImageCacheManager = Constants.ctx.getBean(StartImageCacheManager.class);
		List<StartImage> list = startImageManager.getBeanListByUpdateTime(new Date(nextLastTime));
		if(list != null && list.size() > 0){
			cacheObject.setSize(list.size());
			Date lastUpdateTime = null;
			for(StartImage startImage : list){
				if(lastUpdateTime == null || lastUpdateTime.before(startImage.getUpdateTime())){
					lastUpdateTime = startImage.getUpdateTime();
				}
			}
			startImageCacheManager.removeSession();
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}

}
