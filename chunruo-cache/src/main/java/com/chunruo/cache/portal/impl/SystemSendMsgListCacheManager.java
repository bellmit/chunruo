package com.chunruo.cache.portal.impl;

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
import com.chunruo.core.model.SystemSendMsg;
import com.chunruo.core.service.SystemSendMsgManager;
import com.chunruo.core.util.StringUtil;

/**
 * 系统消息
 * @author chunruo
 *
 */
@Service("systemSendMsgListCacheManager")
public class SystemSendMsgListCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private SystemSendMsgManager systemSendMsgManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'systemSendMsgList'")
	public Map<String, SystemSendMsg> getSession(){
		Map<String, SystemSendMsg> msgMap = new HashMap<String, SystemSendMsg> ();
		try{
			List<SystemSendMsg> list = this.systemSendMsgManager.getAll();
			if(list != null && list.size() > 0){
				for(SystemSendMsg msg : list){
					msgMap.put(StringUtil.null2Str(msg.getId()), msg);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return msgMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'systemSendMsgList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		SystemSendMsgListCacheManager systemSendMsgListCacheManager = Constants.ctx.getBean(SystemSendMsgListCacheManager.class);
		List<SystemSendMsg> msgList = this.systemSendMsgManager.getMsgListByUpdateTime(new Date(nextLastTime));
		if(msgList != null && msgList.size() > 0){
			cacheObject.setSize(msgList.size());
			Date lastUpdateTime = null;
			for(final SystemSendMsg msg : msgList){
				if(lastUpdateTime == null || lastUpdateTime.before(msg.getUpdateTime())){
					lastUpdateTime = msg.getUpdateTime();
				}
			}
			try{
				// 更新渠道缓存列表
				systemSendMsgListCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
