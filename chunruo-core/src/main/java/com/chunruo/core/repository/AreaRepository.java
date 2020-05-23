package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.Area;

@Repository("areaRepository")
public interface AreaRepository extends GenericRepository<Area, Long> {

	@Query("from Area where areaName like %:areaName% order by areaId")
	public List<Area> getAreaListByAreaName(@Param("areaName") String areaName);
	
	@Query("from Area a where a.areaId in (:areaIdList) order by a.level")
	public List<Area> getAreaListByAreaIdList(@Param("areaIdList") List<Long> areaIdList);

	@Query("from Area where parentId=:parentId")
	public List<Area> getAreaListByParentId(@Param("parentId") Long parentId);

	@Query("from Area where isDisUse=:isDisUse")
	public List<Area> getAreaListByIsDisUse(@Param("isDisUse")Boolean isDisUse);
}
