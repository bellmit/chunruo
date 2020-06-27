package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.UserSaleRecord;
import com.chunruo.core.service.UserSaleRecordManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("userSaleRecordListByUserIdCacheManager")
public class UserSaleRecordListByUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private UserSaleRecordManager userSaleRecordManager;
	
	public List<UserSaleRecord> getSession(Long userId){
		List<UserSaleRecord> userSaleRecordList = this.userSaleRecordManager.getUserSaleRecordListByUserId(userId);
		if(CollectionUtils.isEmpty(userSaleRecordList)){
			return null;
		}
		return userSaleRecordList;
	}
	
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		UserSaleRecordListByUserIdCacheManager userSaleRecordListByUserIdCacheManager = Constants.ctx.getBean(UserSaleRecordListByUserIdCacheManager.class);
		List<UserSaleRecord> userSaleRecordList = this.userSaleRecordManager.getUserSaleRecordListByUpdateTime(new Date(nextLastTime));
		if(userSaleRecordList != null && userSaleRecordList.size() > 0){
			cacheObject.setSize(userSaleRecordList.size());
			Date lastUpdateTime = null;
			List<Long> userIdList = new ArrayList<Long> ();
			for(final UserSaleRecord userSaleRecord : userSaleRecordList){
				if(lastUpdateTime == null || lastUpdateTime.before(userSaleRecord.getUpdateTime())){
					lastUpdateTime = userSaleRecord.getUpdateTime();
				}
				if(!userIdList.contains(userSaleRecord.getUserId())){
					userIdList.add(userSaleRecord.getUserId());
				}
			}
			
			if(userIdList != null && userIdList.size() > 0){
				for(final Long userId : userIdList){
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新缓存
								userSaleRecordListByUserIdCacheManager.removeSession(userId);
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
