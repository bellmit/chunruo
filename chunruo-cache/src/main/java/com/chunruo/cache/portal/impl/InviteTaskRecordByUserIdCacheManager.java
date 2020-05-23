package com.chunruo.cache.portal.impl;
//package com.chunruo.cache.portal.impl;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//import com.chunruo.cache.portal.BaseCacheManagerImpl;
//import com.chunruo.cache.portal.CacheObject;
//import com.chunruo.core.Constants;
//import com.chunruo.core.model.InviteTaskRecord;
//import com.chunruo.core.model.UserTeam;
//import com.chunruo.core.service.InviteTaskRecordManager;
//import com.chunruo.core.service.UserTeamManager;
//import com.chunruo.core.util.BaseThreadPool;
//
///**
// * 用户邀请经销商任务记录
// * @author chunruo
// *
// */
//@Service("inviteTaskRecordByUserIdCacheManager")
//public class InviteTaskRecordByUserIdCacheManager extends BaseCacheManagerImpl{
//	@Autowired
//	private InviteTaskRecordManager inviteTaskRecordManager;
//	
//	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'inviteTaskRecord_'+#userId")
//	public InviteTaskRecord getSession(Long userId,String monthDate){
//	   return this.inviteTaskRecordManager.getInviteTaskRecordByUserIdAndMonthDate(userId,monthDate);
//	}
//	
//	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'inviteTaskRecord_'+#userId")
//	public void removeSession(Long userId) {
//		//如果过期后要做特殊处理，可在此实现
//		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
//	}
//	
//	@Override
//	public CacheObject run(Long nextLastTime) {
//		CacheObject cacheObject = new CacheObject();
//		
//		InviteTaskRecordByUserIdCacheManager userTeamByUserIdCacheManager = Constants.ctx.getBean(InviteTaskRecordByUserIdCacheManager.class);
//		List<UserTeam> userTeamList = this.userTeamManager.getUserTeamListByUpdateTime(new Date(nextLastTime));
//		if(userTeamList != null && userTeamList.size() > 0){
//			cacheObject.setSize(userTeamList.size());
//			Date lastUpdateTime = null;
//			List<Long> userIdList = new ArrayList<Long> ();
//			for(final UserTeam userTeam : userTeamList){
//				if(lastUpdateTime == null || lastUpdateTime.before(userTeam.getUpdateTime())){
//					lastUpdateTime = userTeam.getUpdateTime();
//				}
//				if(!userIdList.contains(userTeam.getUserId())){
//					userIdList.add(userTeam.getUserId());
//				}
//			}
//			
//			if(userIdList != null && userIdList.size() > 0){
//				for(final Long userId : userIdList){
//					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
//						@Override
//						public void run() {
//							try{
//								// 更新缓存
//								userTeamByUserIdCacheManager.removeSession(userId);
//							}catch(Exception e){
//								e.printStackTrace();
//							}
//						}
//					});
//				}
//			}
//			cacheObject.setLastUpdateTime(lastUpdateTime);
//		}
//		return cacheObject;
//	}
//}
