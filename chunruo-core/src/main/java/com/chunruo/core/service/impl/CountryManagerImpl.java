package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Country;
import com.chunruo.core.repository.CountryRepository;
import com.chunruo.core.service.CountryManager;

@Transactional
@Component("countryManager")
public class CountryManagerImpl extends GenericManagerImpl<Country, Long> implements CountryManager{
	private CountryRepository countryRepository;

	@Autowired
	public CountryManagerImpl(CountryRepository countryRepository) {
		super(countryRepository);
		this.countryRepository = countryRepository;
	}

	@Override
	public List<Country> getCountryListByStatus(Boolean status) {
		return this.countryRepository.getCountryListByStatus(status);
	}

	@Override
	public List<Country> getCountryListByIsProductShow(Boolean isProductShow) {
		return this.countryRepository.getCountryListByIsProductShow(isProductShow);
	}

}
