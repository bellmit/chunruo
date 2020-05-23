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
import com.chunruo.core.model.ProductIntro;
import com.chunruo.core.service.ProductIntroManager;

@Service("productIntroListCacheManager")
public class ProductIntroListCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private ProductIntroManager productIntroManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productIntroList'")
	public List<ProductIntro> getSession(){
		return this.productIntroManager.getAll();
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productIntroList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject ();
		List<ProductIntro> productIntroList = this.productIntroManager.getProductIntroListByUpdateTime(new Date(nextLastTime));
		if(productIntroList != null && productIntroList.size() > 0){
			cacheObject.setSize(productIntroList.size());
			Date lastUpdateTime = null;
			for(final ProductIntro productIntro : productIntroList){
				if(lastUpdateTime == null || lastUpdateTime.before(productIntro.getUpdateTime())){
					lastUpdateTime = productIntro.getUpdateTime();
				}
			}
			
			try{
				// 更新缓存信息
				ProductIntroListCacheManager productIntroListCacheManager = Constants.ctx.getBean(ProductIntroListCacheManager.class);
				productIntroListCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
