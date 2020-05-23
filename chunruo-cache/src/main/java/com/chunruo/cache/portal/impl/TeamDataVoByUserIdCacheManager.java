package com.chunruo.cache.portal.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.vo.TeamDataVo;

@Service("teamDataVoByUserIdCacheManager")
public class TeamDataVoByUserIdCacheManager {
	@Autowired
	private UserInfoManager userInfoManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'teammDataVo'+#userId")
	public TeamDataVo getSession(Long userId){
		return this.userInfoManager.getTeamDataInfo(userId);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'teammDataVo'+#userId")
	public void removeSession(Long userId){
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
