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
import com.chunruo.core.model.PostageConfig;
import com.chunruo.core.service.PostageConfigManager;

@Service("postageConfigListCacheManager")
public class PostageConfigListCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private PostageConfigManager postageConfigManager;
	
	public List<PostageConfig> getSession(){
		return this.postageConfigManager.getAll();
	}
	
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}

	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject ();
		List<PostageConfig> postageConfigList = this.postageConfigManager.getPostageConfigListByUpdateTime(new Date(nextLastTime));
		if(postageConfigList != null && postageConfigList.size() > 0){
			cacheObject.setSize(postageConfigList.size());
			Date lastUpdateTime = null;
			for(final PostageConfig postageConfig : postageConfigList){
				if(lastUpdateTime == null || lastUpdateTime.before(postageConfig.getUpdateTime())){
					lastUpdateTime = postageConfig.getUpdateTime();
				}
			}
			
			try{
				PostageConfigListCacheManager postageConfigListCacheManager = Constants.ctx.getBean(PostageConfigListCacheManager.class);
				postageConfigListCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
