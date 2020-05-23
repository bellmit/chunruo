package com.chunruo.cache.portal.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.UserAddress;
import com.chunruo.core.service.UserAddressManager;
import com.chunruo.core.util.StringUtil;

@Service("userAddressListByUserIdCacheManager")
public class UserAddressListByUserIdCacheManager {
	@Autowired
	private UserAddressManager userAddressManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userAddressList_'+#userId")
	public Map<String, UserAddress> getSession(Long userId){
		Map<String, UserAddress> userAddressMap = new HashMap<String, UserAddress> ();
		List<UserAddress> userAddressList = this.userAddressManager.getUserAddressListByUserId(userId);
		if(userAddressList != null && userAddressList.size() > 0){
			for(UserAddress userAddress : userAddressList){
				userAddressMap.put(StringUtil.null2Str(userAddress.getAddressId()), userAddress);
			}
		}
		return userAddressMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userAddressList_'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
