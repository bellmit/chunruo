package com.chunruo.cache.portal.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.Country;
import com.chunruo.core.service.CountryManager;

@Service("countryListCacheManager")
public class CountryListCacheManager {
	@Autowired
	private CountryManager countryManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'countryList'")
	public List<Country> getSession(){
		return this.countryManager.getCountryListByStatus(true);
	}
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'countryList'+#countryId")
	public Country getSession(Long countryId){
		return this.countryManager.get(countryId);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'countryList'")
	public void removeSession(Long productId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
