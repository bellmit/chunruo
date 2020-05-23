package com.chunruo.cache.portal.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.vo.WeixinAuthLoginVo;

/**
 * pc微信授权登录
 * @author chunruo
 *
 */
@Service("weixinAuthLoginCacheManager")
public class WeixinAuthLoginCacheManager{

	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'weixinAuthLogin_'+#sessionId")
	public WeixinAuthLoginVo getSession(String sessionId){
		return null;
	}
	
	@CachePut(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'weixinAuthLogin_'+#sessionId")
	public WeixinAuthLoginVo updateSession(String sessionId, WeixinAuthLoginVo weixinAuthLogin) {
		return weixinAuthLogin;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'weixinAuthLogin_'+#sessionId")
	public void removeSession(String sessionId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}

}
