package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.FxChannel;

public interface FxChannelManager extends GenericManager<FxChannel, Long>{
	
	public List<FxChannel> getFxChannelListByStatus(Integer status);

	public List<FxChannel> getFxChannelListByStatusList(List<Integer> statusList);

	public void updateFxChannelStatus(List<Long> channelIdList, Integer status);
	
	public List<FxChannel> getFxChannelListByUpdateTime(Date updateTime);
	
	public void updateFxChannelUpdateTimeByChannelId(Long channelId);
	
	public boolean offSeckillFxChannelUpdateTimeByChannelId(Long channelId);
}
