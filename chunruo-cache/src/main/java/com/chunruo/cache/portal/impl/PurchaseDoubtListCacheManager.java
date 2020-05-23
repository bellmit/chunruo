package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.PurchaseDoubt;
import com.chunruo.core.service.PurchaseDoubtManager;

@Service("purchaseDoubtListCacheManager")
public class PurchaseDoubtListCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private PurchaseDoubtManager purchaseDoubtManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'purchaseDoubtList'")
	public List<PurchaseDoubt> getSession(){
		return this.purchaseDoubtManager.getAll();
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'purchaseDoubtList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject ();
		List<PurchaseDoubt> purchaseDoubtList = this.purchaseDoubtManager.getPurchaseDoubtListByUpdateTime(new Date(nextLastTime));
		if(purchaseDoubtList != null && purchaseDoubtList.size() > 0){
			cacheObject.setSize(purchaseDoubtList.size());
			Date lastUpdateTime = null;
			for(final PurchaseDoubt purchaseDoubt : purchaseDoubtList){
				if(lastUpdateTime == null || lastUpdateTime.before(purchaseDoubt.getUpdateTime())){
					lastUpdateTime = purchaseDoubt.getUpdateTime();
				}
			}
			
			try{
				// 更新缓存信息
				PurchaseDoubtListCacheManager purchaseDoubtListCacheManager = Constants.ctx.getBean(PurchaseDoubtListCacheManager.class);
				purchaseDoubtListCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
