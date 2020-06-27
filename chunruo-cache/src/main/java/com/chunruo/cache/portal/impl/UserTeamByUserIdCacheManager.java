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
import com.chunruo.core.model.UserTeam;
import com.chunruo.core.service.UserTeamManager;
import com.chunruo.core.util.BaseThreadPool;

/**
 * 团队记录
 * @author chunruo
 *
 */
@Service("userTeamByUserIdCacheManager")
public class UserTeamByUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private UserTeamManager userTeamManager;
	
	public UserTeam getSession(Long userId){
	   return this.userTeamManager.getUserTeamByUserId(userId);
	}
	
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		UserTeamByUserIdCacheManager userTeamByUserIdCacheManager = Constants.ctx.getBean(UserTeamByUserIdCacheManager.class);
		List<UserTeam> userTeamList = this.userTeamManager.getUserTeamListByUpdateTime(new Date(nextLastTime));
		if(userTeamList != null && userTeamList.size() > 0){
			cacheObject.setSize(userTeamList.size());
			Date lastUpdateTime = null;
			List<Long> userIdList = new ArrayList<Long> ();
			for(final UserTeam userTeam : userTeamList){
				if(lastUpdateTime == null || lastUpdateTime.before(userTeam.getUpdateTime())){
					lastUpdateTime = userTeam.getUpdateTime();
				}
				if(!userIdList.contains(userTeam.getUserId())){
					userIdList.add(userTeam.getUserId());
				}
			}
			
			if(userIdList != null && userIdList.size() > 0){
				for(final Long userId : userIdList){
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新缓存
								userTeamByUserIdCacheManager.removeSession(userId);
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
