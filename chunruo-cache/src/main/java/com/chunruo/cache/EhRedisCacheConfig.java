package com.chunruo.cache;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableCaching
public class EhRedisCacheConfig {
	Logger log = LoggerFactory.getLogger(EhRedisCacheConfig.class);

	@Bean(name = "sessionEhRedisCache")
	public EhRedisCache ehRedisCache(
			@Qualifier("redisTemplate")RedisTemplate redisTemplate, 
			@Value(value = "${session.ehcache.name}")String ehcacheName,
			@Value(value = "${session.ehRedisCache.name}")String name,
			@Value(value = "${session.redis.expiration}")Long expiration
			) {
		//redis
		RedisCache redisCache = new RedisCache(ehcacheName, null, redisTemplate, expiration);
		
		EhRedisCache sessionEhRedisCache = new EhRedisCache();
		sessionEhRedisCache.setRedisCache(redisCache);
		sessionEhRedisCache.setName(name);
		return sessionEhRedisCache;
	}
	
	@Bean(name = "sessionEhRedisCacheManager")
	public SimpleCacheManager simpleCacheManager(EhRedisCache ehRedisCache) {
		SimpleCacheManager sessionCacheManager = new SimpleCacheManager();
		Collection caches = new ArrayList();
		caches.add(ehRedisCache);
		sessionCacheManager.setCaches(caches);
		return sessionCacheManager;
	}
}
