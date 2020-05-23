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
import com.chunruo.core.model.UserAdviserTag;
import com.chunruo.core.service.UserAdviserTagManager;
import com.chunruo.core.util.StringUtil;

@Service("userAdviserTagListCacheManager")
public class UserAdviserTagListCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private UserAdviserTagManager userAdviserTagManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userAdviserTagList'")
	public Map<String,UserAdviserTag> getSession(){
		Map<String,UserAdviserTag> map = new HashMap<String,UserAdviserTag>();
		List<UserAdviserTag> userAdviserTagList = this.userAdviserTagManager.getUserAdviserTagListByIsEnable(true);
		if(userAdviserTagList != null && !userAdviserTagList.isEmpty()) {
			for(UserAdviserTag userAdviserTag : userAdviserTagList) {
				map.put(StringUtil.null2Str(userAdviserTag.getTagId()), userAdviserTag);
			}
		}
		return map;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userAdviserTagList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		UserAdviserTagListCacheManager userAdviserTagListCacheManager = Constants.ctx.getBean(UserAdviserTagListCacheManager.class);
		List<UserAdviserTag> list = this.userAdviserTagManager.getUserAdviserTagListByUpdateTime(new Date(nextLastTime));
		if (list != null && list.size() > 0) {
			cacheObject.setSize(list.size());
			Date lastUpdateTime = null;
			for (final UserAdviserTag userAdviserTag : list) {
				if (lastUpdateTime == null || lastUpdateTime.before(userAdviserTag.getUpdateTime())) {
					lastUpdateTime = userAdviserTag.getUpdateTime();
				}
			}
			
			try{
				// 更新缓存
				userAdviserTagListCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
