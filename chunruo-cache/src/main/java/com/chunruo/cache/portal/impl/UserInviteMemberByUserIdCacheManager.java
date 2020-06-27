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
import com.chunruo.core.model.UserInviteMember;
import com.chunruo.core.service.UserInviteMemberManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("userInviteMemberByUserIdCacheManager")
public class UserInviteMemberByUserIdCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private UserInviteMemberManager userInviteMemberManager;
	
	public UserInviteMember getSession(Long userId){
		return this.userInviteMemberManager.getUserInviteMemberByUserId(userId);
	}
	
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		List<UserInviteMember> userInviteMemberList = this.userInviteMemberManager.getUserInviteMemberListByUpdateTime(new Date(nextLastTime));
		if(userInviteMemberList != null && userInviteMemberList.size() > 0){
			cacheObject.setSize(userInviteMemberList.size());
			Date lastUpdateTime = null;
			for(final UserInviteMember record : userInviteMemberList){
				if(lastUpdateTime == null || lastUpdateTime.before(record.getUpdateTime())){
					lastUpdateTime = record.getUpdateTime();
				}
				
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
					@Override
					public void run() {
						try{
							// 更新店铺缓存
							UserInviteMemberByUserIdCacheManager userInviteMemberByUserIdCacheManager = Constants.ctx.getBean(UserInviteMemberByUserIdCacheManager.class);
							userInviteMemberByUserIdCacheManager.removeSession(record.getUserId());
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
