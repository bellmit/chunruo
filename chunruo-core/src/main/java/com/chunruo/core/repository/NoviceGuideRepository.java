package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.NoviceGuide;

@Repository("noviceGuideRepository")
public interface NoviceGuideRepository extends GenericRepository<NoviceGuide, Long> {

	@Query("from NoviceGuide where phoneType=:phoneType and height=:height and width=:width")
	public NoviceGuide getNoviceGuideByPhoneTypeAndHeightWidth(@Param("phoneType")Integer phoneType, @Param("height")Integer height, @Param("width")Integer width);

	@Query("from NoviceGuide where status=:status")
	public List<NoviceGuide> getNoviceGuideListByStatus(@Param("status")Boolean status);

	@Query("from NoviceGuide where updateTime >:updateTime")
	public List<NoviceGuide> getNoviceGuideListByUpdateTime(@Param("updateTime")Date updateTime);

	
}
