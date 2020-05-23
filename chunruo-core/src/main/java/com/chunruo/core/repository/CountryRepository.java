package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.Country;

@Repository("countryRepository")
public interface CountryRepository extends GenericRepository<Country, Long> {
	
	@Query("from Country where status =:status order by telCode")
	public List<Country> getCountryListByStatus(@Param("status")Boolean status);
	
	@Query("from Country where isProductShow =:isProductShow order by telCode")
	public List<Country> getCountryListByIsProductShow(@Param("isProductShow")Boolean isProductShow);
}
