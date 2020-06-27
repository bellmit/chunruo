package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("userInfoByIdCacheManager")
public class UserInfoByIdCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private UserInfoManager userInfoManager;
	
	public UserInfo getSession(Long userId){
		return this.userInfoManager.get(userId);
	}
	
	public UserInfo updateSession(Long userId, UserInfo userInfo) {
		return userInfo;
	}
	
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		List<UserInfo> userInfoList = userInfoManager.getUserInfoListByUpdateTime(new Date(nextLastTime));
		if(userInfoList != null && userInfoList.size() > 0){
			cacheObject.setSize(userInfoList.size());
			Date lastUpdateTime = null;
			for(final UserInfo userInfo : userInfoList){
				if(lastUpdateTime == null || lastUpdateTime.before(userInfo.getUpdateTime())){
					lastUpdateTime = userInfo.getUpdateTime();
				}
				
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
					@Override
					public void run() {
						try{
							// 更新店铺缓存
							UserInfoByIdCacheManager userInfoByIdCacheManager = Constants.ctx.getBean(UserInfoByIdCacheManager.class);
							userInfoByIdCacheManager.removeSession(userInfo.getUserId());
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
