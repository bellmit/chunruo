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
import com.chunruo.core.model.PostageTemplate;
import com.chunruo.core.service.PostageTemplateManager;

@Service("postageTemplateCacheManager")
public class PostageTemplateCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private PostageTemplateManager postageTemplateManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'postageTemplate'")
	public List<PostageTemplate> getSession(){
		return this.postageTemplateManager.getAll();
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'postageTemplate'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}

	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject ();
		List<PostageTemplate> postageTemplateList = this.postageTemplateManager.getPostageTemplateListByUpdateTime(new Date(nextLastTime));
		if(postageTemplateList != null && postageTemplateList.size() > 0){
			cacheObject.setSize(postageTemplateList.size());
			Date lastUpdateTime = null;
			for(final PostageTemplate postageTemplate : postageTemplateList){
				if(lastUpdateTime == null || lastUpdateTime.before(postageTemplate.getUpdateTime())){
					lastUpdateTime = postageTemplate.getUpdateTime();
				}
			}
			
			try{
				// 更新缓存信息
				PostageTemplateCacheManager postageTemplateCacheManager = Constants.ctx.getBean(PostageTemplateCacheManager.class);
				postageTemplateCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
