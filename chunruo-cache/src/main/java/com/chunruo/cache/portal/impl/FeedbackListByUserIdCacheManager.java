package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Feedback;
import com.chunruo.core.service.FeedbackManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("feedbackListByUserIdCacheManager")
public class FeedbackListByUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private FeedbackManager feedbackManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'feedbackList_'+#userId")
	public List<Feedback> getSession(Long userId){
		return this.feedbackManager.getFeedbackListByUserId(userId);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'feedbackList_'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		FeedbackListByUserIdCacheManager feedbackListByUserIdCacheManager = (FeedbackListByUserIdCacheManager) Constants.ctx
				.getBean("feedbackListByUserIdCacheManager");
		List<Feedback> list = feedbackManager.getFeedbackListByUpdateTime(new Date(nextLastTime));
		if (list != null && list.size() > 0) {
			cacheObject.setSize(list.size());
			Date lastUpdateTime = null;
			Set<Long> userIdSet = new HashSet<Long>();
			for (final Feedback feedback : list) {
				userIdSet.add(feedback.getUserId());
				if (lastUpdateTime == null || lastUpdateTime.before(feedback.getUpdateTime())) {
					lastUpdateTime = feedback.getUpdateTime();
				}
			}
			
			if (userIdSet != null && userIdSet.size() > 0) {
				for (Long userId : userIdSet) {
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新缓存
								feedbackListByUserIdCacheManager.removeSession(userId);
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
