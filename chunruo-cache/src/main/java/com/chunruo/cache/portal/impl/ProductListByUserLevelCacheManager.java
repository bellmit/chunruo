package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.model.Product;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

/**
 * 分类-分销市场商品缓存
 * @author chunruo
 *
 */
@Service("productListByUserLevelCacheManager")
public class ProductListByUserLevelCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private ProductManager productManager;

	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productListByUserLevel_'+#level")
	public List<String> getSession(Integer level){
		List<String> productIdList = new ArrayList<String> ();
		try{
			
			List<Product> productList = null;
			if(StringUtil.compareObject(level, UserLevel.USER_LEVEL_BUYERS)) {
				productList = this.productManager.getProductListByTagIdInField(1L);
			}else {
				productList = this.productManager.getProductListByIsOpenVPrice();
			}
			// 商品信息转换
			if(productList != null && productList.size() > 0){
				for(Product product : productList){
					productIdList.add(StringUtil.null2Str(product.getProductId()));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return productIdList;
	}

	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productListByUserLevel_'+#level")
	public void removeSession(Integer level) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		ProductListByUserLevelCacheManager productListByUserLevelCacheManager = Constants.ctx.getBean(ProductListByUserLevelCacheManager.class);
		List<Product> productList = this.productManager.getProductByUpdateTime(new Date(nextLastTime));
		if(productList != null && productList.size() > 0){
			cacheObject.setSize(productList.size());
			Date lastUpdateTime = null;
			for(final Product product : productList){
				if(lastUpdateTime == null || lastUpdateTime.before(product.getUpdateTime())){
					lastUpdateTime = product.getUpdateTime();
				}
			}
			
			try{
				// 更新缓存列表
				List<Integer> levelList = new ArrayList<Integer>();
				levelList.add(UserLevel.USER_LEVEL_BUYERS);
				levelList.add(UserLevel.USER_LEVEL_AGENT);
				levelList.add(UserLevel.USER_LEVEL_DEALER);
				levelList.add(UserLevel.USER_LEVEL_V2);
				levelList.add(UserLevel.USER_LEVEL_V3);
				for(Integer level : levelList) {
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable() {
						@Override
						public void run() {
							productListByUserLevelCacheManager.removeSession(level);
						}
					});
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
