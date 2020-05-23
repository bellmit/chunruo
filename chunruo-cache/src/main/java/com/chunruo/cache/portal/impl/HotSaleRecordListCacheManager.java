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
import com.chunruo.core.model.HotSaleRecord;
import com.chunruo.core.repository.HotSaleRecordRepository;
import com.chunruo.core.util.StringUtil;

/**
 * 热卖商品记录
 * @author chunruo
 *
 */
@Service("hotSaleRecordListCacheManager")
public class HotSaleRecordListCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private HotSaleRecordRepository hotSaleRecordRepository;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'hotSaleRecordList'")
	public Map<String, HotSaleRecord> getSession(){
		Map<String, HotSaleRecord> hotSaleRecordMap = new HashMap<String, HotSaleRecord> ();
		try{
			List<HotSaleRecord> list = this.hotSaleRecordRepository.findAll();
			if(list != null && list.size() > 0){
				for(HotSaleRecord hotSaleRecord : list){
					hotSaleRecord.setCategoryIdList(StringUtil.stringToLongArray(hotSaleRecord.getCategoryIds()));
					hotSaleRecord.setCategoryFidList(StringUtil.stringToLongArray(hotSaleRecord.getCategoryFids()));
					hotSaleRecordMap.put(StringUtil.null2Str(hotSaleRecord.getProductId()), hotSaleRecord);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return hotSaleRecordMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'hotSaleRecordList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		HotSaleRecordListCacheManager hotSaleRecordListCacheManager = Constants.ctx.getBean(HotSaleRecordListCacheManager.class);
		List<HotSaleRecord> hotSaleRecordList = hotSaleRecordRepository.getHotSaleRecordListByUpdateTime(new Date(nextLastTime));
		if(hotSaleRecordList != null && hotSaleRecordList.size() > 0){
			cacheObject.setSize(hotSaleRecordList.size());
			Date lastUpdateTime = null;
			for(final HotSaleRecord hotSaleRecord : hotSaleRecordList){
				if(lastUpdateTime == null || lastUpdateTime.before(hotSaleRecord.getUpdateTime())){
					lastUpdateTime = hotSaleRecord.getUpdateTime();
				}
			}
			
			try{
				// 更新渠道缓存列表
				hotSaleRecordListCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
