package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.FxChannel;

@Repository("fxChannelRepository")
public interface FxChannelRepository extends GenericRepository<FxChannel, Long> {

	@Query("from FxChannel where status in (:statusList) order by sort")
	public List<FxChannel> getFxChannelListByStatusList(@Param("statusList")List<Integer> statusList);
	
	@Query("from FxChannel where status =:status order by sort")
	public List<FxChannel> getFxChannelListByStatus(@Param("status")Integer status);
	
	@Modifying
	@Query("update FxChannel set status =:status, updateTime =:modiDate where channelId in (:channelIdList)")
	public void updateFxChannelStatus(@Param("channelIdList") List<Long> channelIdList, @Param("status")Integer status, @Param("modiDate") Date modiDate);
	
	@Query("from FxChannel where updateTime >:updateTime")
	public List<FxChannel> getFxChannelListByUpdateTime(@Param("updateTime")Date updateTime);
	
	@Modifying
	@Query("update FxChannel set updateTime =:modiDate where channelId =:channelId")
	public void updateFxChannelUpdateTimeByChannelId(@Param("channelId") Long channelId, @Param("modiDate") Date modiDate);
}
