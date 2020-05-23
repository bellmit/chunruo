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
import com.chunruo.core.model.UserFriend;
import com.chunruo.core.service.UserFriendManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("userFriendByFriendUserIdCacheManager")
public class UserFriendByFriendUserIdCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private UserFriendManager userFriendManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userFriendByFriendUserId_'+#userId")
	public UserFriend getSession(Long userId){
		return this.userFriendManager.getUserFriendByFriendUserId(userId);
	}
	

	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userFriendByFriendUserId_'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		List<UserFriend> userFriendList = this.userFriendManager.getUserFriendListByUpdateTime(new Date(nextLastTime));
		if(userFriendList != null && userFriendList.size() > 0){
			cacheObject.setSize(userFriendList.size());
			Date lastUpdateTime = null;
			for(final UserFriend userFriend : userFriendList){
				if(lastUpdateTime == null || lastUpdateTime.before(userFriend.getUpdateTime())){
					lastUpdateTime = userFriend.getUpdateTime();
				}
				
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
					@Override
					public void run() {
						try{
							// 更新店铺缓存
							UserFriendByFriendUserIdCacheManager userFriendByFriendUserIdCacheManager = Constants.ctx.getBean(UserFriendByFriendUserIdCacheManager.class);
							userFriendByFriendUserIdCacheManager.removeSession(userFriend.getFriendUserId());
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
