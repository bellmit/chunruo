package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
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
import com.chunruo.core.model.PurchaseLimit;
import com.chunruo.core.service.PurchaseLimitManager;
import com.chunruo.core.util.StringUtil;

/**
 * 限购信息
 * @author chunruo
 */
@Service("purchaseLimitListCacheManager")
public class PurchaseLimitListCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private PurchaseLimitManager purchaseLimitManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'purchaseLimitList'")
	public Map<String, List<PurchaseLimit>> getSession(){
		Map<String, List<PurchaseLimit>> purchaseLimitMap = new HashMap<String, List<PurchaseLimit>> ();
		try{
			List<PurchaseLimit> purchaseLimitList = this.purchaseLimitManager.getPurchaseLimitListByIsEnable(true);
			if(purchaseLimitList != null && purchaseLimitList.size() > 0){
				for(PurchaseLimit purchaseLimit : purchaseLimitList){
					String type = StringUtil.null2Str(purchaseLimit.getType());
					if(purchaseLimitMap.containsKey(type)) {
						purchaseLimitMap.get(type).add(purchaseLimit);
					}else {
						List<PurchaseLimit> limitList = new ArrayList<PurchaseLimit>();
						limitList.add(purchaseLimit);
						purchaseLimitMap.put(type, limitList);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return purchaseLimitMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'purchaseLimitList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		PurchaseLimitListCacheManager purchaseLimitListCacheManager = Constants.ctx.getBean(PurchaseLimitListCacheManager.class);
		List<PurchaseLimit> purchaseLimitList = this.purchaseLimitManager.getPurchaseLimitRecordListByUpdateTime(new Date(nextLastTime));
		if(purchaseLimitList != null && purchaseLimitList.size() > 0){
			cacheObject.setSize(purchaseLimitList.size());
			Date lastUpdateTime = null;
			for(final PurchaseLimit purchaseLimit : purchaseLimitList){
				if(lastUpdateTime == null || lastUpdateTime.before(purchaseLimit.getUpdateTime())){
					lastUpdateTime = purchaseLimit.getUpdateTime();
				}
			}
			
			try{
				// 更新渠道缓存列表
				purchaseLimitListCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
