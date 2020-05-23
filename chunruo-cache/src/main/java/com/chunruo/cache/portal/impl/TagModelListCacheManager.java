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
import com.chunruo.core.model.TagModel;
import com.chunruo.core.service.TagModelManager;

@Service("tagModelListCacheManager")
public class TagModelListCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private TagModelManager tagModelManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'tagModelList'")
	public List<TagModel> getSession(){
		return this.tagModelManager.getAll();
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'tagModelList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		TagModelListCacheManager tagModelListCacheManager = Constants.ctx.getBean(TagModelListCacheManager.class);
		List<TagModel> tagModelList = tagModelManager.getTagModelListByUpdateTime(new Date(nextLastTime));
		if(tagModelList != null && tagModelList.size() > 0){
			cacheObject.setSize(tagModelList.size());
			Date lastUpdateTime = null;
			for(final TagModel tagModel : tagModelList){
				if(lastUpdateTime == null || lastUpdateTime.before(tagModel.getUpdateTime())){
					lastUpdateTime = tagModel.getUpdateTime();
				}
			}
			
			try {
				tagModelListCacheManager.removeSession();
			}catch(Exception e) {
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}

