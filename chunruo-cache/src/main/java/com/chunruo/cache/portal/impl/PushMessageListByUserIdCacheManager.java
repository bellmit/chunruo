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
import com.chunruo.core.model.PushMessage;
import com.chunruo.core.service.PushMessageManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("pushMessageListByUserIdCacheManager")
public class PushMessageListByUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private PushMessageManager pushMessageManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'pushMessageList_'+#userId")
	public Map<String,PushMessage> getSession(Long userId){
		Map<String,PushMessage> messageMap = new HashMap<String, PushMessage>();
		List<PushMessage> messageList = this.pushMessageManager.getPushMessageListByUserId(userId);
		if(messageList != null && messageList.size() > 0){
			for(PushMessage message : messageList){
				messageMap.put(StringUtil.null2Str(message.getMsgId()), message);
			}
		}
		return messageMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'pushMessageList_'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject ();
		PushMessageListByUserIdCacheManager pushMessageListByUserIdCacheManager = Constants.ctx.getBean(PushMessageListByUserIdCacheManager.class);
		List<PushMessage> pushMessageList = this.pushMessageManager.getPushMessageListByUpdateTime(new Date(nextLastTime));
		if(pushMessageList != null && pushMessageList.size() > 0){
			cacheObject.setSize(pushMessageList.size());
			Date lastUpdateTime = null;
			List<Long> userIdList = new ArrayList<Long> ();
			for(final PushMessage pushMessage : pushMessageList){
				if(lastUpdateTime == null || lastUpdateTime.before(pushMessage.getUpdateTime())){
					lastUpdateTime = pushMessage.getUpdateTime();
				}
				if(!userIdList.contains(pushMessage.getUserId())){
					userIdList.add(pushMessage.getUserId());
				}
			}
			
			if(userIdList != null && userIdList.size() > 0){
				for(final Long userId : userIdList){
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新缓存
								pushMessageListByUserIdCacheManager.removeSession(userId);
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
