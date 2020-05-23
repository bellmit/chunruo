package com.chunruo.cache;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;

/**
 * redis缓存
 * @author chunruo
 *
 */
public class EhRedisCache implements Cache {
	private Logger log = LoggerFactory.getLogger(EhRedisCache.class);
	/**
	 * redis缓存
	 */
	private RedisCache redisCache;
	/**
	 * 此二级缓存名称
	 */
	private String name;

	public RedisCache getRedisCache() {
		return redisCache;
	}

	/**
	 * set the redisCache
	 * @param redisCache
	 */
	public void setRedisCache(RedisCache redisCache) {
		this.redisCache = redisCache;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Object getNativeCache() {
		return this;
	}

	/**
	 * 从redis中取
	 */
	public ValueWrapper get(Object key) {
		ValueWrapper valueWrapper = redisCache.get(key);
		if (valueWrapper != null) {
			log.debug("Cache L2 (redis) :{}={}", key, valueWrapper.get());
		}
		return valueWrapper;
	}

	public <T> T get(Object key, Class<T> type) {
		return (T)redisCache.get(key, type);
	}

	public <T> T get(Object key, Callable<T> valueLoader) {
		return redisCache.get(key, valueLoader);
	}

	public void put(Object key, Object value) {
		redisCache.put(key, value);
	}

	public ValueWrapper putIfAbsent(Object key, Object value) {
		return redisCache.putIfAbsent(key, value);
	}

	public void evict(Object key) {
		redisCache.evict(key);
	}

	public void clear() {
		redisCache.clear();
	}

}
