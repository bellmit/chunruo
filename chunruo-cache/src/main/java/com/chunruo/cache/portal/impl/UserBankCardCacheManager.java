package com.chunruo.cache.portal.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.chunruo.core.model.UserBankCard;

@Service("userBankCardCacheManager")
public class UserBankCardCacheManager{
	public static Log log = LogFactory.getLog(UserBankCardCacheManager.class);

	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userBankCard_'+#outTradeNo")
	public UserBankCard getSession(String outTradeNo){
		return null;
	}
	
	@CachePut(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userBankCard_'+#outTradeNo")
	public UserBankCard updateSession(String outTradeNo, UserBankCard userBankCard) {
		return userBankCard;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'userBankCard_'+#outTradeNo")
	public void removeSession(String outTradeNo) {
		//如果过期后要做特殊处理，可在此实现
		log.info("removeSession outTradeNo:" + outTradeNo + ", class:" + this.getClass().toString());
	}

}
