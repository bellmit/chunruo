package com.chunruo.cache.portal.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.UserInfoManager;

@Service("userInfoListByTopUserIdCacheManager")
public class UserInfoListByTopUserIdCacheManager {
	@Autowired
	private UserInfoManager userInfoManager;
	
	public List<UserInfo> getSession(Long topUserId){
		List<UserInfo> userInfoList = this.userInfoManager.getUserInfoListByTopUserId(topUserId);
		if(CollectionUtils.isEmpty(userInfoList)){
			return null;
		}
		return userInfoList;
	}
	
	public void removeSession(Long topUserId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
