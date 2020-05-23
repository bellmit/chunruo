package com.chunruo.core.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.WebUrlConfig;

@Repository("webUrlConfigRepository")
public interface WebUrlConfigRepository extends GenericRepository<WebUrlConfig, Long> {

	@Query("from WebUrlConfig where url=:url")
	public WebUrlConfig getWebUrlConfigByUrl(@Param("url")String url);

}
