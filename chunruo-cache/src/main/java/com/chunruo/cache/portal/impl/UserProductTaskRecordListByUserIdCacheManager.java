package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import com.chunruo.core.model.UserProductTaskRecord;
import com.chunruo.core.service.UserProductTaskRecordManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("userProductTaskRecordListByUserIdCacheManager")
public class UserProductTaskRecordListByUserIdCacheManager extends BaseCacheManagerImpl {
	private static Log log = LogFactory.getLog(UserProductTaskRecordListByUserIdCacheManager.class);
	@Autowired
	private UserProductTaskRecordManager userProductTaskRecordManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userProductTaskList_'+#userId")
	public Map<String,UserProductTaskRecord> getSession(Long userId){
		Map<String,UserProductTaskRecord> map = new HashMap<String,UserProductTaskRecord>();
		List<UserProductTaskRecord> userProductTaskRecordList = this.userProductTaskRecordManager.getUserProductTaskRecordListByUserId(userId);
		if(userProductTaskRecordList != null && userProductTaskRecordList.size() > 0){
			for(UserProductTaskRecord record : userProductTaskRecordList){
				map.put(StringUtil.null2Str(record.getTaskId()), record);
			}
		}
		return map;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userProductTaskList_'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		final UserProductTaskRecordListByUserIdCacheManager userProductTaskRecordListByUserIdCacheManager = Constants.ctx.getBean(UserProductTaskRecordListByUserIdCacheManager.class);
		CacheObject cacheObject = new CacheObject();
		List<UserProductTaskRecord> userProductTaskRecordList = this.userProductTaskRecordManager.getUserProductTaskRecordListByUpdateTime(new Date(nextLastTime));
		if(userProductTaskRecordList != null && userProductTaskRecordList.size() > 0){
			cacheObject.setSize(userProductTaskRecordList.size());
			final Set<Long> userIdList = new HashSet<Long> ();
			Date lastUpdateTime = null;
			for(UserProductTaskRecord record : userProductTaskRecordList){
				userIdList.add(record.getUserId());    
				Date updateTime = record.getUpdateTime();
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
								userProductTaskRecordListByUserIdCacheManager.removeSession(userId);
								log.info("userProductTaskRecordListByUserIdCacheManager===removeSeesion:"+userId);
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
