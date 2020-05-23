package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.RollingNotice;

public interface RollingNoticeRepository  extends GenericRepository<RollingNotice, Long> {

	@Query("from RollingNotice where isEnabled = 1")
	public List<RollingNotice> getRollingNotice();

	@Query("from RollingNotice where isEnabled = :isEnabled and type =:type")
	public List<RollingNotice> getRollingNoticeListByTypeAndIsEnabled(@Param("type")Integer type, @Param("isEnabled")Integer isEnabled);
}
