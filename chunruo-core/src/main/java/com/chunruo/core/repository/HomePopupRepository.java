package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.HomePopup;

@Repository("homePopupRepository")
public interface HomePopupRepository extends GenericRepository<HomePopup, Long> {
	
	@Query("from HomePopup where isEnable = :isEnable")
	public List<HomePopup> getHomePopupListByIsEnable(@Param("isEnable")Boolean isEnable);

	@Query("from HomePopup where updateTime>:updateTime")
	public List<HomePopup> getHomePopupListByUpdateTime(@Param("updateTime")Date updateTime);
}
