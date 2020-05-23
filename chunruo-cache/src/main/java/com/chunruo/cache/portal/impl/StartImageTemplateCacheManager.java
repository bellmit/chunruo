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
import com.chunruo.core.model.StartImageTemplate;
import com.chunruo.core.service.StartImageManager;
import com.chunruo.core.service.StartImageTemplateManager;
import com.chunruo.core.util.StringUtil;

@Service("startImageTemplateCacheManager")
public class StartImageTemplateCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private StartImageManager startImageManager;
	@Autowired
	private StartImageTemplateManager startImageTemplateManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'startImageTemplateList'")
	public List<StartImageTemplate> getSession(){
		List<StartImageTemplate> templateList = this.startImageTemplateManager.getStartImageTemplateListByStatus(true);
	    if(templateList != null && templateList.size() > 0) {
	    	for(StartImageTemplate template : templateList) {
	    		List<StartImage> startImageList = this.startImageManager.getStartImageListByTemplateId(StringUtil.nullToLong(template.getTemplateId()));
	    		template.setStartImageList(startImageList);
	    	}
	    }
		return templateList;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'startImageTemplateList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		final StartImageTemplateCacheManager startImageTemplateCacheManager = Constants.ctx.getBean(StartImageTemplateCacheManager.class);
		List<StartImageTemplate> list = startImageTemplateManager.getStartImageTemplateListByUpdateTime(new Date(nextLastTime));
		if(list != null && list.size() > 0){
			cacheObject.setSize(list.size());
			Date lastUpdateTime = null;
			for(StartImageTemplate startImageTemplate : list){
				if(lastUpdateTime == null || lastUpdateTime.before(startImageTemplate.getUpdateTime())){
					lastUpdateTime = startImageTemplate.getUpdateTime();
				}
			}
			startImageTemplateCacheManager.removeSession();
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
