package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Country;

public interface CountryManager extends GenericManager<Country, Long>{

	List<Country> getCountryListByStatus(Boolean status);
	
	List<Country> getCountryListByIsProductShow(Boolean isProductShow);
}
