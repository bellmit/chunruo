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
import com.chunruo.core.model.UserFriend;
import com.chunruo.core.service.UserFriendManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("userFriendListByUserIdCacheManager")
public class UserFriendListByUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private UserFriendManager userFriendManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userFriendList_'+#userId")
	public Map<String,UserFriend> getSession(Long userId){
		Map<String,UserFriend> map = new HashMap<String,UserFriend>();
	    List<UserFriend> userFriendList = this.userFriendManager.getUserFriendListByUserId(userId);
	    if(userFriendList != null && !userFriendList.isEmpty()) {
	    	for(UserFriend userFriend : userFriendList) {
	    		map.put(StringUtil.null2Str(userFriend.getFriendUserId()), userFriend);
	    	}
	    }
	    return map;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userFriendList_'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		UserFriendListByUserIdCacheManager userFriendListByUserIdCacheManager = Constants.ctx.getBean(UserFriendListByUserIdCacheManager.class);
		List<UserFriend> list = this.userFriendManager.getUserFriendListByUpdateTime(new Date(nextLastTime));
		if (list != null && list.size() > 0) {
			cacheObject.setSize(list.size());
			Date lastUpdateTime = null;
			Set<Long> userIdSet = new HashSet<Long>();
			for (final UserFriend userFriend : list) {
				userIdSet.add(userFriend.getUserId());
				if (lastUpdateTime == null || lastUpdateTime.before(userFriend.getUpdateTime())) {
					lastUpdateTime = userFriend.getUpdateTime();
				}
			}
			
			if (userIdSet != null && userIdSet.size() > 0) {
				for (Long userId : userIdSet) {
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新缓存
								userFriendListByUserIdCacheManager.removeSession(userId);
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
