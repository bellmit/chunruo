package com.chunruo.cache.portal.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.Country;
import com.chunruo.core.service.CountryManager;

@Service("countryListCacheManager")
public class CountryListCacheManager {
	@Autowired
	private CountryManager countryManager;
	
	public List<Country> getSession(){
		return this.countryManager.getCountryListByStatus(true);
	}
	
	public Country getSession(Long countryId){
		return this.countryManager.get(countryId);
	}
	
	public void removeSession(Long productId) {
	}
}
