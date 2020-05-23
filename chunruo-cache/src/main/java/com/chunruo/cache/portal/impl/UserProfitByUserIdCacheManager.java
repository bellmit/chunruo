package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.UserProfitRecord;
import com.chunruo.core.service.UserProfitRecordManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("userProfitByUserIdCacheManager")
public class UserProfitByUserIdCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private UserProfitRecordManager userProfitRecordManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userProfitByUserId_'+#userId")
	public Map<String, UserProfitRecord> getSession(Long userId){
		Map<String, UserProfitRecord> map = new HashMap<String, UserProfitRecord>();
		List<UserProfitRecord> userProfitList = this.userProfitRecordManager.getUserProfitRecordList(userId);
		if(CollectionUtils.isEmpty(userProfitList)){
			return map;
		}
		
		for(UserProfitRecord record : userProfitList){
			Long recordId = record.getRecordId();
			map.put(StringUtil.null2Str(recordId), record);
		}
		return map;
	}
	
	@CachePut(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userProfitByUserId_'+#userId")
	public Map<String, UserProfitRecord> updateSession(Long userId, UserProfitRecord storeProfitRecord) {
		Map<String, UserProfitRecord> map = this.getSession(userId);
		if(map == null || map.size() <= 0){
			map = new HashMap<String, UserProfitRecord> ();
		}
		
		if (storeProfitRecord != null 
				&& storeProfitRecord.getRecordId() != null 
				&& storeProfitRecord.getCreateTime() != null){
			map.put(StringUtil.null2Str(storeProfitRecord.getRecordId()), storeProfitRecord);
		}
		return map;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userProfitByUserId_'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		final UserProfitByUserIdCacheManager userProfitByUserIdCacheManager = Constants.ctx.getBean(UserProfitByUserIdCacheManager.class);
		List<UserProfitRecord> storeProfitList = this.userProfitRecordManager.getUserProfitRecordListByUpdateTime(new Date(nextLastTime));
		if(storeProfitList != null && storeProfitList.size() > 0){
			cacheObject.setSize(storeProfitList.size());
			Date lastUpdateTime = null;
			final Set<Long> userIdSet = new HashSet<Long> ();
			for(UserProfitRecord record : storeProfitList){
				if(lastUpdateTime == null || lastUpdateTime.before(record.getUpdateTime())){
					lastUpdateTime = record.getUpdateTime();
				}
				userIdSet.add(record.getUserId());
			}
			
			// 更新当前店铺利润记录
			if(userIdSet != null && userIdSet.size() > 0){
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
					@Override
					public void run() {
						for(Long userId : userIdSet){
							try{
								// 更新当前店铺利润记录
								userProfitByUserIdCacheManager.removeSession(userId);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
				});
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
