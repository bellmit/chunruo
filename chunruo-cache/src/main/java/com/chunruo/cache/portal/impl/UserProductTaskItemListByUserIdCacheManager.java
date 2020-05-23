package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.UserProductTaskItem;
import com.chunruo.core.service.UserProductTaskItemManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("userProductTaskItemListByUserIdCacheManager")
public class UserProductTaskItemListByUserIdCacheManager extends BaseCacheManagerImpl {
	private static Log log = LogFactory.getLog(UserProductTaskItemListByUserIdCacheManager.class);
	@Autowired
	private UserProductTaskItemManager userProductTaskItemManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userProductTaskItemList_'+#userId")
	public List<UserProductTaskItem> getSession(Long userId){
		return this.userProductTaskItemManager.getUserProductTaskItemListByUserId(userId);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userProductTaskItemList_'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		final UserProductTaskItemListByUserIdCacheManager userProductTaskItemListByUserIdCacheManager = Constants.ctx.getBean(UserProductTaskItemListByUserIdCacheManager.class);
		CacheObject cacheObject = new CacheObject();
		List<UserProductTaskItem> userProductTaskItemList = this.userProductTaskItemManager.getUserProductTaskItemListByUpdateTime(new Date(nextLastTime));
		if(userProductTaskItemList != null && userProductTaskItemList.size() > 0){
			cacheObject.setSize(userProductTaskItemList.size());
			final Set<Long> userIdList = new HashSet<Long> ();
			Date lastUpdateTime = null;
			for(UserProductTaskItem item : userProductTaskItemList){
				userIdList.add(item.getUserId());    
				Date updateTime = item.getUpdateTime();
				if(lastUpdateTime == null || lastUpdateTime.before(updateTime)){
					lastUpdateTime = updateTime;
				}
			}
			
			if(userIdList != null && userIdList.size() > 0){
				for(final Long userId : userIdList){
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新缓存
								userProductTaskItemListByUserIdCacheManager.removeSession(userId);
								log.info("userProductTaskItemListByUserIdCacheManager===removeSeesion:"+userId);
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
