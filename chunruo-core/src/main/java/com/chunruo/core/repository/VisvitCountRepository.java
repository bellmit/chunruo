package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.VisvitCount;

@Repository("visvitCountRepository")
public interface VisvitCountRepository extends GenericRepository<VisvitCount, Long> {

	@Query("from VisvitCount where updateTime >:updateTime")
	public List<VisvitCount> getVisvitCountListByUpdateTime(@Param("updateTime")Date updateTime);


}
