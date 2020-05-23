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
import com.chunruo.core.model.ProductCollection;
import com.chunruo.core.model.ProductCollectionProfit;
import com.chunruo.core.service.ProductCollectionManager;
import com.chunruo.core.service.ProductCollectionProfitManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("collectionListByUserIdCacheManager")
public class CollectionListByUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private ProductCollectionManager productCollectionManager;
	@Autowired
	private ProductCollectionProfitManager productCollectionProfitManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'collectionListByUserId_'+#userId")
	public Map<String, ProductCollection> getSession(Long userId){
		Map<String, ProductCollection> productCollectionIdMap = new HashMap<String, ProductCollection> ();
		List<ProductCollection> productCollectionList = this.productCollectionManager.getProductCollectionListByUserId(userId, true);
		if(productCollectionList != null && productCollectionList.size() > 0){
			for(ProductCollection productCollection : productCollectionList){
				Long productId = StringUtil.nullToLong(productCollection.getProductId());
				if (productId != null) {
					List<ProductCollectionProfit> collectionProfitList = this.productCollectionProfitManager.getCollectionProfitListByUserIdAndProductId(userId, productId);
					productCollection.setCollectionProfitList(collectionProfitList);
					productCollectionIdMap.put(StringUtil.null2Str(productCollection.getProductId()), productCollection);
				}
			}
		}
		return productCollectionIdMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'collectionListByUserId_'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		final CollectionListByUserIdCacheManager collectionListByUserIdCacheManager = Constants.ctx.getBean(CollectionListByUserIdCacheManager.class);
		List<ProductCollection> productCollectionList = productCollectionManager.getProductCollectionListByUpdateTime(new Date(nextLastTime));
		if(productCollectionList != null && productCollectionList.size() > 0){
			cacheObject.setSize(productCollectionList.size());
			Date lastUpdateTime = null;
			List<Long> userIdList = new ArrayList<Long> ();
			for(ProductCollection collection : productCollectionList){
				if(lastUpdateTime == null || lastUpdateTime.before(collection.getUpdateTime())){
					lastUpdateTime = collection.getUpdateTime();
				}
				
				// 检查店铺商品收藏是否更新
				if(!userIdList.contains(collection.getUserId())){
					userIdList.add(collection.getUserId());
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
								collectionListByUserIdCacheManager.removeSession(userId);
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
