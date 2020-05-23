package com.chunruo.cache.portal.impl;

import java.util.Calendar;
import java.util.Date;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("dateCacheManager")
public class DateCacheManager {

	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'dateCacheManager_'+#cacheName")
	public Long getSession(String cacheName){
		// 取当前时间减一天
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -1);
		return c.getTimeInMillis();
	}
	
	@CachePut(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'dateCacheManager_'+#cacheName")
	public Long updateSession(String cacheName, Long currentTimeMillis){
		return currentTimeMillis;
	}
}
