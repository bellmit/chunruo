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
import com.chunruo.core.model.ProductCollectionProfit;
import com.chunruo.core.service.ProductCollectionProfitManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("collectionProfitListByUserIdCacheManager")
public class CollectionProfitListByUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private ProductCollectionProfitManager productCollectionProfitManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'collectionProfitListByUserId_'+#userId")
	public Map<String, List<ProductCollectionProfit>> getSession(Long userId){
		Map<String, List<ProductCollectionProfit>> productCollectionProfitIdMap = new HashMap<String, List<ProductCollectionProfit>> ();
		List<ProductCollectionProfit> productCollectionProfitList = this.productCollectionProfitManager.getProductCollectionProfitListByUserId(userId);
		if(productCollectionProfitList != null && productCollectionProfitList.size() > 0){
			for(ProductCollectionProfit productCollectionProfit : productCollectionProfitList){
				String productId = StringUtil.null2Str(productCollectionProfit.getProductId());
				if (productCollectionProfitIdMap.containsKey(productId)) {
					productCollectionProfitIdMap.get(productId).add(productCollectionProfit);
				}else {
					List<ProductCollectionProfit> collectionProfitList = new ArrayList<ProductCollectionProfit>();
					collectionProfitList.add(productCollectionProfit);
					productCollectionProfitIdMap.put(productId, collectionProfitList);
				}
			}
		}
		return productCollectionProfitIdMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'collectionProfitListByUserId_'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		final CollectionProfitListByUserIdCacheManager collectionProfitListByUserIdCacheManager = Constants.ctx.getBean(CollectionProfitListByUserIdCacheManager.class);
		List<ProductCollectionProfit> productCollectionProfitList = productCollectionProfitManager.getCollectionProfitListByUpdateTime(new Date(nextLastTime));
		if(productCollectionProfitList != null && productCollectionProfitList.size() > 0){
			cacheObject.setSize(productCollectionProfitList.size());
			Date lastUpdateTime = null;
			List<Long> userIdList = new ArrayList<Long> ();
			for(ProductCollectionProfit collectionProfit : productCollectionProfitList){
				if(lastUpdateTime == null || lastUpdateTime.before(collectionProfit.getUpdateTime())){
					lastUpdateTime = collectionProfit.getUpdateTime();
				}
				
				// 检查店铺商品收藏是否更新
				if(!userIdList.contains(collectionProfit.getUserId())){
					userIdList.add(collectionProfit.getUserId());
				}
			}
			
			// 店铺商品收藏清除缓存信息
			if(userIdList != null && userIdList.size() > 0){
				for(final Long userId : userIdList){
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新缓存
								collectionProfitListByUserIdCacheManager.removeSession(userId);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					});
				}
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
