package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.SignRecord;
import com.chunruo.core.service.SignRecordManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("signRecordListByUserIdCacheManager")
public class SignRecordListByUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private SignRecordManager signRecordManager;

	@Cacheable(value = "sessionEhRedisCache", cacheManager = "sessionEhRedisCacheManager", key = "'signRecordListByUserId_'+#userId")
	public List<SignRecord> getSession(Long userId) {
		return this.signRecordManager.getSignRecordListByUserId(userId);
	}

	@CacheEvict(value = "sessionEhRedisCache", cacheManager = "sessionEhRedisCacheManager", key = "'signRecordListByUserId_'+#userId")
	public void removeSession(Long userId) {
		// 如果过期后要做特殊处理，可在此实现
		// log.info("removeSession userId:" + userId + ",userToken:" +
		// userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		final SignRecordListByUserIdCacheManager signRecordListByUserIdCacheManager = Constants.ctx.getBean(SignRecordListByUserIdCacheManager.class);
		List<SignRecord> signRecordList = signRecordManager.getSignRecordListByUpdateTime(new Date(nextLastTime));
		if(signRecordList != null && signRecordList.size() > 0){
			cacheObject.setSize(signRecordList.size());
			Date lastUpdateTime = null;
			List<Long> userIdList = new ArrayList<Long> ();
			for(final SignRecord signRecord : signRecordList){
				if(lastUpdateTime == null || lastUpdateTime.before(signRecord.getUpdateTime())){
					lastUpdateTime = signRecord.getUpdateTime();
				}
				
				if(!userIdList.contains(signRecord.getUserId())){
					userIdList.add(signRecord.getUserId());
				}
			}
			
			if(userIdList != null && userIdList.size() > 0){
				for(final Long userId : userIdList){
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新缓存
								signRecordListByUserIdCacheManager.removeSession(userId);
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
