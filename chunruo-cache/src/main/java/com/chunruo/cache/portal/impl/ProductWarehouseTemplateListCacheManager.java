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
import com.chunruo.core.model.ProductWarehouseTemplate;
import com.chunruo.core.service.ProductWarehouseTemplateManager;
import com.chunruo.core.util.StringUtil;

@Service("productWarehouseTemplateListCacheManager")
public class ProductWarehouseTemplateListCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private ProductWarehouseTemplateManager productWarehouseTemplateManager;
	
	public Map<String,ProductWarehouseTemplate> getSession(){
		Map<String,ProductWarehouseTemplate> productWarehouseTemplateMap = new HashMap<String,ProductWarehouseTemplate>();
		List<ProductWarehouseTemplate> productWarehouseTemplateList = this.productWarehouseTemplateManager.getProductWarehouseTemplateListByStatus(true);
		if(productWarehouseTemplateList != null && productWarehouseTemplateList.size() > 0) {
			for(ProductWarehouseTemplate productWarehouseTemplate :productWarehouseTemplateList) {
				productWarehouseTemplateMap.put(StringUtil.nullToString(productWarehouseTemplate.getTemplateId()), productWarehouseTemplate);
			}
		}
		return productWarehouseTemplateMap;
	}
	
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		ProductWarehouseTemplateListCacheManager productWarehouseTemplateListCacheManager = Constants.ctx.getBean(ProductWarehouseTemplateListCacheManager.class);
		List<ProductWarehouseTemplate> productWarehouseTemplateList = this.productWarehouseTemplateManager.getProductWarehouseTemplateListByUpdateTime(new Date(nextLastTime));
		if(productWarehouseTemplateList != null && productWarehouseTemplateList.size() > 0){
			cacheObject.setSize(productWarehouseTemplateList.size());
			Date lastUpdateTime = null;
			for(final ProductWarehouseTemplate productWarehouseTemplate : productWarehouseTemplateList){
				if(lastUpdateTime == null || lastUpdateTime.before(productWarehouseTemplate.getUpdateTime())){
					lastUpdateTime = productWarehouseTemplate.getUpdateTime();
				}
			}
			
			try{
				// 更新渠道缓存列表
				productWarehouseTemplateListCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
