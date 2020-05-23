package com.chunruo.cache.portal.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.vo.ImageVo;

@Service("imageRateCacheManager")
public class ImageRateCacheManager {
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'imageRate'+#filePath")
	public ImageVo getSession(String filePath){
		return null;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'imageRate'+#filePath")
	public void removeSession(String filePath){
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@CachePut(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'imageRate'+#filePath")
	public ImageVo updateSession(String filePath, ImageVo imageVo){
		return imageVo;
	}
}
