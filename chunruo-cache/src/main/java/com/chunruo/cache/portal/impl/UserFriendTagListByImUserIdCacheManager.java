package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.UserFriendTag;
import com.chunruo.core.service.UserFriendTagManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("userFriendTagListByImUserIdCacheManager")
public class UserFriendTagListByImUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private UserFriendTagManager userFriendTagManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userFriendTagListByImUserId_'+#imUserId")
	public Map<String,UserFriendTag> getSession(Long imUserId){
		Map<String,UserFriendTag> map = new HashMap<String,UserFriendTag>();
	    List<UserFriendTag> userFriendTagList = this.userFriendTagManager.getUserFriendTagListByImUserId(imUserId);
	    if(userFriendTagList != null && !userFriendTagList.isEmpty()) {
	    	for(UserFriendTag userFriendTag : userFriendTagList) {
	    		map.put(StringUtil.null2Str(userFriendTag.getId()), userFriendTag);
	    	}
	    }
	    return map;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userFriendTagListByImUserId_'+#imUserId")
	public void removeSession(Long imUserId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		UserFriendTagListByImUserIdCacheManager userFriendTagListByImUserIdCacheManager = Constants.ctx.getBean(UserFriendTagListByImUserIdCacheManager.class);
		List<UserFriendTag> list = this.userFriendTagManager.getUserFriendTagListByUpdateTime(new Date(nextLastTime));
		if (list != null && list.size() > 0) {
			cacheObject.setSize(list.size());
			Date lastUpdateTime = null;
			Set<Long> userIdSet = new HashSet<Long>();
			for (final UserFriendTag userFriendTag : list) {
				userIdSet.add(userFriendTag.getImUserId());
				if (lastUpdateTime == null || lastUpdateTime.before(userFriendTag.getUpdateTime())) {
					lastUpdateTime = userFriendTag.getUpdateTime();
				}
			}
			
			if (userIdSet != null && userIdSet.size() > 0) {
				for (Long userId : userIdSet) {
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新缓存
								userFriendTagListByImUserIdCacheManager.removeSession(userId);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					});
				}
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
