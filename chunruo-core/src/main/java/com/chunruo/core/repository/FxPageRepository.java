package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.FxPage;

@Repository("fxPageRepository")
public interface FxPageRepository extends GenericRepository<FxPage, Long> {
	
	@Query("from FxPage where channelId=:channelId and isDelete=false Order by categoryType asc")
	public List<FxPage> getFxPageListByChannelId(@Param("channelId") Long channelId);
	
	@Query("from FxPage where channelId=:channelId and categoryType=:category and isDelete=false")
	public List<FxPage> getFxPageListByChannelId(@Param("channelId") Long channelId, @Param("category") Integer category);

	@Query("from FxPage where channelId in(:channelIdList) and categoryType=:category and isDelete=false")
	public List<FxPage> getFxPageListByChannelIdList(@Param("channelIdList")List<Long> channelIdList, @Param("category") Integer category);

	@Query("from FxPage where categoryType in(1,2) and isDelete=false Order by categoryType asc")
	public List<FxPage> getInnerFxPageList();
}
