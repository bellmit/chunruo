package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.FxChannel;
import com.chunruo.core.model.FxPage;
import com.chunruo.core.service.FxChannelManager;
import com.chunruo.core.service.FxPageManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("fxChannelListMapByIdCacheManager")
public class FxChannelListMapByIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private FxChannelManager fxChannelManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'fxChannelListMap'")
	public Map<String, FxChannel> getSession(){
		Map<String, FxChannel> fxChannelMap = new HashMap<String, FxChannel> ();
		List<FxChannel> fxChannelList = this.fxChannelManager.getFxChannelListByStatus(FxChannel.FX_CHANNEL_STATUS_ENABLE);
		if(fxChannelList != null && fxChannelList.size() > 0){
			Date lastUpdateTime = null;
			for(FxChannel fxChannel : fxChannelList){
				if(lastUpdateTime == null || lastUpdateTime.before(fxChannel.getUpdateTime())){
					lastUpdateTime = fxChannel.getUpdateTime();
				}
				fxChannelMap.put(StringUtil.null2Str(fxChannel.getChannelId()), fxChannel);
			}
		
			// 更新缓存时间
			this.dateCacheManager.updateSession(this.getCacheName(), lastUpdateTime.getTime());
		}
		return fxChannelMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'fxChannelListMap'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}

	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		final FxPageManager fxPageManager = Constants.ctx.getBean(FxPageManager.class);
		final FxPageByIdCacheManager fxPageByIdCacheManager = Constants.ctx.getBean(FxPageByIdCacheManager.class);
		final FxChannelListMapByIdCacheManager fxChannelListMapByIdCacheManager = Constants.ctx.getBean(FxChannelListMapByIdCacheManager.class);
		final FxPageListByChannelIdCacheManager fxPageListByChannelIdCacheManager = Constants.ctx.getBean(FxPageListByChannelIdCacheManager.class);
		final FxChildrenListByPageIdCacheManager fxChildrenListByPageIdCacheManager = Constants.ctx.getBean(FxChildrenListByPageIdCacheManager.class);
		
		List<FxChannel> fxChannelList = fxChannelManager.getFxChannelListByUpdateTime(new Date(nextLastTime));
		if(fxChannelList != null && fxChannelList.size() > 0){
			cacheObject.setSize(fxChannelList.size());
			Date lastUpdateTime = null;
			for(final FxChannel fxChannel : fxChannelList){
				if(lastUpdateTime == null || lastUpdateTime.before(fxChannel.getUpdateTime())){
					lastUpdateTime = fxChannel.getUpdateTime();
				}
				
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
					@Override
					public void run() {
						try{
							List<FxPage> fxPageList = fxPageManager.getFxPageListByChannelId(fxChannel.getChannelId());
							if(fxPageList != null && fxPageList.size() > 0){
								for(FxPage fxPage : fxPageList){
									try{
										// 更新渠道页面详情页面数据
										fxChildrenListByPageIdCacheManager.removeSession(fxPage.getPageId());
										fxPageByIdCacheManager.removeSession(fxPage.getPageId());
									}catch(Exception e){
										continue;
									}
								}
							}
							
							// 更新渠道列表页面数据
							fxPageListByChannelIdCacheManager.removeSession(fxChannel.getChannelId());
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
			
			try{
				// 更新渠道缓存列表
				fxChannelListMapByIdCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
