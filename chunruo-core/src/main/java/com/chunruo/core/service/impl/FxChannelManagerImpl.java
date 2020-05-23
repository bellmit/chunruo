package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.FxChannel;
import com.chunruo.core.model.ProductSeckill;
import com.chunruo.core.repository.FxChannelRepository;
import com.chunruo.core.service.FxChannelManager;
import com.chunruo.core.service.ProductSeckillManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("fxChannelManager")
public class FxChannelManagerImpl extends GenericManagerImpl<FxChannel, Long> implements FxChannelManager {
	private FxChannelRepository fxChannelRepository;
	@Autowired
	private ProductSeckillManager productSeckillManager;
	
	@Autowired
	public FxChannelManagerImpl(FxChannelRepository fxChannelRepository) {
		super(fxChannelRepository);
		this.fxChannelRepository = fxChannelRepository;
	}

	@Override
	public List<FxChannel> getFxChannelListByStatusList(List<Integer> statusList) {
		if(statusList != null && statusList.size() > 0){
			return this.fxChannelRepository.getFxChannelListByStatusList(statusList);
		}
		return null;
	}
	
	@Override
	public List<FxChannel> getFxChannelListByStatus(Integer status){
		return this.fxChannelRepository.getFxChannelListByStatus(status);
	}

	@Override
	public void updateFxChannelStatus(List<Long> channelIdList, Integer status) {
		this.fxChannelRepository.updateFxChannelStatus(channelIdList, status, DateUtil.getCurrentDate());
	}

	@Override
	public List<FxChannel> getFxChannelListByUpdateTime(Date updateTime) {
		return this.fxChannelRepository.getFxChannelListByUpdateTime(updateTime);
	}

	@Override
	public void updateFxChannelUpdateTimeByChannelId(Long channelId) {
		this.fxChannelRepository.updateFxChannelUpdateTimeByChannelId(channelId, DateUtil.getCurrentDate());
	}

	@Override
	public boolean offSeckillFxChannelUpdateTimeByChannelId(Long channelId) {
		FxChannel fxChannel = this.fxChannelRepository.getOne(channelId);
		if(fxChannel != null 
				&& fxChannel.getChannelId() != null
				&& StringUtil.compareObject(fxChannel.getStatus(), FxChannel.FX_CHANNEL_STATUS_ENABLE)){
			// 默认下架秒杀场次频道
			boolean isOffSeckillFxChannel = true;
			List<ProductSeckill> list = this.productSeckillManager.getProductSeckillListByStatus(true);
			if(list != null && list.size() > 0){
				// 检查当前秒杀有效
				Long currentTimeMillis = System.currentTimeMillis();
				for(ProductSeckill productSeckill : list){
					// 秒杀场次信息
					if(productSeckill != null 
							&& productSeckill.getSeckillId() != null
							&& DateUtil.isEffectiveTime(DateUtil.DATE_HOUR, productSeckill.getStartTime())
							&& DateUtil.isEffectiveTime(DateUtil.DATE_HOUR, productSeckill.getEndTime())){
						Date seckillStartDate = DateUtil.parseDate(DateUtil.DATE_FORMAT_HOUR, productSeckill.getStartTime());
						Date seckillEndDate = DateUtil.parseDate(DateUtil.DATE_FORMAT_HOUR, productSeckill.getEndTime());
						if(seckillStartDate != null && seckillEndDate != null){
							Long seckillEndTime = seckillEndDate.getTime();
							if(currentTimeMillis <= seckillEndTime){
								// 秒杀场次不能下架
								isOffSeckillFxChannel = false;
							}
						}
					}
				}
			}
			
			// 检查是否秒杀场次下架
			if(isOffSeckillFxChannel){
				// 频道下架
				fxChannel.setStatus(FxChannel.FX_CHANNEL_STATUS_STOP);
				fxChannel.setUpdateTime(DateUtil.getCurrentDate());
				this.fxChannelRepository.save(fxChannel);
				return true;
			}
		}
		return false;
	}	
}
