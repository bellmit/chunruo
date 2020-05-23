package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.RechargeTemplate;
import com.chunruo.core.service.RechargeTemplateManager;
import com.chunruo.core.util.StringUtil;

@Service("rechargeTemplateListCacheManager")
public class RechargeTemplateListCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private RechargeTemplateManager rechargeTemplateManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'rechargeTemplateList'")
	public Map<String,RechargeTemplate> getSession(){
		 Map<String,RechargeTemplate> templateMap = new HashMap<String,RechargeTemplate>();
		 List<RechargeTemplate> rechargeTemplateList = this.rechargeTemplateManager.getRechargeTemplateListByIsEnable(true);
		 if(rechargeTemplateList != null && !rechargeTemplateList.isEmpty()) {
			 for(RechargeTemplate rechargeTemplate : rechargeTemplateList) {
				 templateMap.put(StringUtil.null2Str(rechargeTemplate.getTemplateId()), rechargeTemplate);
			 }
		 }
		 return templateMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'rechargeTemplateList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject ();
		List<RechargeTemplate> rechargeTemplateList = this.rechargeTemplateManager.getRechargeTemplateListByUpdateTime(new Date(nextLastTime));
		if(rechargeTemplateList != null && rechargeTemplateList.size() > 0){
			cacheObject.setSize(rechargeTemplateList.size());
			Date lastUpdateTime = null;
			for(final RechargeTemplate rechargeTemplate : rechargeTemplateList){
				if(lastUpdateTime == null || lastUpdateTime.before(rechargeTemplate.getUpdateTime())){
					lastUpdateTime = rechargeTemplate.getUpdateTime();
				}
			}
			
			try{
				// 更新缓存信息
				RechargeTemplateListCacheManager rechargeTemplateListCacheManager = Constants.ctx.getBean(RechargeTemplateListCacheManager.class);
				rechargeTemplateListCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
