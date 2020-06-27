package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.UserWithdrawal;
import com.chunruo.core.service.UserWithdrawalManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("userWithdrawalListByUserIdCacheManager")
public class UserWithdrawalListByUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private UserWithdrawalManager userWithdrawalManager;
	
	public Map<String, UserWithdrawal> getSession(Long userId){
		Map<String, UserWithdrawal> userWithdrawalMap = new HashMap<String, UserWithdrawal> ();
		List<UserWithdrawal> storeWithdrawalList = this.userWithdrawalManager.getUserWithdrawalListByUserId(userId);
		if(storeWithdrawalList != null && storeWithdrawalList.size() > 0){
			for(UserWithdrawal userWithdrawal : storeWithdrawalList){
				userWithdrawalMap.put(StringUtil.null2Str(userWithdrawal.getRecordId()), userWithdrawal);
			}
		}
		return userWithdrawalMap;
	}
	
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}

	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject ();
		final UserWithdrawalListByUserIdCacheManager userWithdrawalListByUserIdCacheManager = Constants.ctx.getBean(UserWithdrawalListByUserIdCacheManager.class);
		List<UserWithdrawal> userWithdrawalList = this.userWithdrawalManager.getUserWithdrawalListByUpdateTime(new Date(nextLastTime));
		if(userWithdrawalList != null && userWithdrawalList.size() > 0){
			cacheObject.setSize(userWithdrawalList.size());
			Date lastUpdateTime = null;
			List<Long> userIdList = new ArrayList<Long> ();
			for(final UserWithdrawal userWithdrawal : userWithdrawalList){
				if(lastUpdateTime == null || lastUpdateTime.before(userWithdrawal.getUpdateTime())){
					lastUpdateTime = userWithdrawal.getUpdateTime();
				}
				
				// group by storeId
				if(!userIdList.contains(userWithdrawal.getUserId())){
					userIdList.add(userWithdrawal.getUserId());
				}
			}
			
			if(userIdList != null && userIdList.size() > 0){
				for(final Long userId : userIdList){
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新订单缓存
								userWithdrawalListByUserIdCacheManager.removeSession(userId);
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
