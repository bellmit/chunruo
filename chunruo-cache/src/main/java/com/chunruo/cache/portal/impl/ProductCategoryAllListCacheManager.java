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
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.service.ProductCategoryManager;

@Service("productCategoryAllListCacheManager")
public class ProductCategoryAllListCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private ProductCategoryManager productCategoryManager;
	
	public List<ProductCategory> getSession(){
		return this.productCategoryManager.getProductCategoryByStatus(1);
	}
	
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		ProductCategoryAllListCacheManager productCategoryListCacheManager = Constants.ctx.getBean(ProductCategoryAllListCacheManager.class);
		List<ProductCategory> productCategoryList = this.productCategoryManager.getProductCategoryListByUpdateTime(new Date(nextLastTime));
		if(productCategoryList != null && productCategoryList.size() > 0){
			cacheObject.setSize(productCategoryList.size());
			Date lastUpdateTime = null;
			for(final ProductCategory category : productCategoryList){
				if(lastUpdateTime == null || lastUpdateTime.before(category.getUpdateTime())){
					lastUpdateTime = category.getUpdateTime();
				}
			}
			
			try{
				if(lastUpdateTime != null) {
					// 更新渠道缓存列表
					productCategoryListCacheManager.removeSession();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
