package com.chunruo.cache.portal.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.UserCart;
import com.chunruo.core.service.UserCartManager;
import com.chunruo.core.util.StringUtil;

@Service("userCartListByUserIdCacheManager")
public class UserCartListByUserIdCacheManager{
	@Autowired
	private UserCartManager userCartManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userCartList_'+#userId")
	public Map<String, UserCart> getSession(Long userId){
		Map<String, UserCart> userCartIdMap = new HashMap<String, UserCart> ();
		List<UserCart> userCartList = this.userCartManager.getUserCartListByUserId(userId);
		if(userCartList != null && userCartList.size() > 0){
			for(UserCart userCart : userCartList){
				userCartIdMap.put(StringUtil.null2Str(userCart.getCartId()), userCart);
			}
		}
		return userCartIdMap;
	}
	
	@CachePut(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userCartList_'+#userId")
	public Map<String, UserCart> updateSession(Long userId, UserCart userCart) {
		Map<String, UserCart> userCartIdMap = this.getSession(userId);
		if(userCartIdMap == null || userCartIdMap.size() <= 0){
			userCartIdMap = new HashMap<String, UserCart> ();
		}
		
		userCartIdMap.put(StringUtil.null2Str(userCart.getCartId()), userCart);
		return userCartIdMap;
	}
	
	@CachePut(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userCartList_'+#userId")
	public Map<String, UserCart> deleteSession(Long userId, List<Long> userCartIdList) {
		Map<String, UserCart> userCartIdMap = this.getSession(userId);
		if(userCartIdList != null 
				&& userCartIdList.size() > 0
				&& userCartIdMap != null
				&& userCartIdMap.size() > 0){
			for(Long userCartId : userCartIdList){
				if(userCartIdMap.containsKey(StringUtil.null2Str(userCartId))){
					userCartIdMap.remove(StringUtil.null2Str(userCartId));
				}
			}
		}
		return userCartIdMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userCartList_'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ", class:" + this.getClass().toString());
	}
}
