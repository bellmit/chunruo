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
import com.chunruo.core.model.InviteTask;
import com.chunruo.core.service.InviteTaskManager;

@Service("inviteTaskListCacheManager")
public class InviteTaskListCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private InviteTaskManager inviteTaskManager;

	@Cacheable(value = "sessionEhRedisCache", cacheManager = "sessionEhRedisCacheManager", key = "'inviteTaskList'")
	public List<InviteTask> getSession() {
		return this.inviteTaskManager.getAll();
	}

	@CacheEvict(value = "sessionEhRedisCache", cacheManager = "sessionEhRedisCacheManager", key = "'inviteTaskList'")
	public void removeSession() {
		// 如果过期后要做特殊处理，可在此实现
		// log.info("removeSession userId:" + userId + ",userToken:" +
		// userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		final InviteTaskListCacheManager inviteTaskListCacheManager = Constants.ctx.getBean(InviteTaskListCacheManager.class);
		List<InviteTask> inviteTaskList = inviteTaskManager.getInviteTaskListByUpdateTime(new Date(nextLastTime));
		if(inviteTaskList != null && inviteTaskList.size() > 0){
			cacheObject.setSize(inviteTaskList.size());
			Date lastUpdateTime = null;
			for(final InviteTask inviteTask : inviteTaskList){
				if(lastUpdateTime == null || lastUpdateTime.before(inviteTask.getUpdateTime())){
					lastUpdateTime = inviteTask.getUpdateTime();
				}
				
			}
			
			if(lastUpdateTime != null) {
				// 更新缓存列表
				inviteTaskListCacheManager.removeSession();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
	
	
}
