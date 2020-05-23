package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.UserCoupon;
import com.chunruo.core.service.UserCouponManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("userCouponListByUserIdCacheManager")
public class UserCouponListByUserIdCacheManager extends BaseCacheManagerImpl {
	private static Log log = LogFactory.getLog(UserCouponListByUserIdCacheManager.class);
	@Autowired
	private UserCouponManager userCouponManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userCouponList_'+#userId")
	public List<UserCoupon> getSession(Long userId){
		List<UserCoupon> userCouponresult = new ArrayList<> ();
		List<UserCoupon> userCouponList = this.userCouponManager.getUserCouponListByUserId(userId);
		if(userCouponList != null && userCouponList.size() > 0){
			for(UserCoupon userCoupon : userCouponList){
				if(!userCouponresult.contains(userCoupon)){
					userCouponresult.add(userCoupon);
				}
			}
		}
		return userCouponresult;
	}
	
	@CachePut(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userCouponList_'+#userId")
	public List<UserCoupon> updateSession(Long userId, UserCoupon userCoupon) {
		List<UserCoupon> userCouponList = this.getSession(userId);
		if(userCouponList == null || userCouponList.size() <= 0){
			userCouponList = new ArrayList<> ();
		}
		if(!userCouponList.contains(userCoupon)){
			userCouponList.add(userCoupon);
		}
		
		return userCouponList;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userCouponList_'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		final UserCouponListByUserIdCacheManager userCouponListByUserIdCacheManager = Constants.ctx.getBean(UserCouponListByUserIdCacheManager.class);
		CacheObject cacheObject = new CacheObject();
		List<UserCoupon> userCouponList = this.userCouponManager.getUserCouponListByUpdateTime(new Date(nextLastTime));
		if(userCouponList != null && userCouponList.size() > 0){
			cacheObject.setSize(userCouponList.size());
			final Set<Long> userCouponUserIdList = new HashSet<Long> ();
			Date lastUpdateTime = null;
			for(UserCoupon record : userCouponList){
				userCouponUserIdList.add(record.getUserId());    
				Date updateTime = record.getUpdateTime();
				if(lastUpdateTime == null || lastUpdateTime.before(updateTime)){
					lastUpdateTime = updateTime;
				}
			}
			
			if(userCouponUserIdList != null && userCouponUserIdList.size() > 0){
				for(final Long userId : userCouponUserIdList){
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新用户优惠券缓存
								userCouponListByUserIdCacheManager.removeSession(userId);
								log.info("userCouponListByUserIdCacheManager===removeSeesion:"+userId);
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
