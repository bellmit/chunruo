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
import com.chunruo.core.model.UserRecharge;
import com.chunruo.core.service.UserRechargeManager;
import com.chunruo.core.util.BaseThreadPool;

/**
 * 团队记录
 * @author chunruo
 *
 */
@Service("userRechargeByUserIdCacheManager")
public class UserRechargeByUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private UserRechargeManager userRechargeManager;
	
	public List<UserRecharge> getSession(Long userId){
	   return this.userRechargeManager.getUserRechargeListByUserIdAndStatus(userId,UserRecharge.USER_RECHARGE_SUCC);
	}
	
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		UserRechargeByUserIdCacheManager userRechargeByUserIdCacheManager = Constants.ctx.getBean(UserRechargeByUserIdCacheManager.class);
		List<UserRecharge> userRechargeList = this.userRechargeManager.getUserRechargeListByUpdateTime(new Date(nextLastTime));
		if(userRechargeList != null && userRechargeList.size() > 0){
			cacheObject.setSize(userRechargeList.size());
			Date lastUpdateTime = null;
			List<Long> userIdList = new ArrayList<Long> ();
			for(final UserRecharge userRecharge : userRechargeList){
				if(lastUpdateTime == null || lastUpdateTime.before(userRecharge.getUpdateTime())){
					lastUpdateTime = userRecharge.getUpdateTime();
				}
				if(!userIdList.contains(userRecharge.getUserId())){
					userIdList.add(userRecharge.getUserId());
				}
			}
			
			if(userIdList != null && userIdList.size() > 0){
				for(final Long userId : userIdList){
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新缓存
								userRechargeByUserIdCacheManager.removeSession(userId);
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
