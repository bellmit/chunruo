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
import com.chunruo.core.model.ProductSeckillNotice;
import com.chunruo.core.service.ProductSeckillNoticeManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("productSeckillNoticeListByUserIdCacheManager")
public class ProductSeckillNoticeListByUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private ProductSeckillNoticeManager productSeckillNoticeManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productSeckillNoticeList'+#userId")
	public List<ProductSeckillNotice> getSession(Long userId){
		return this.productSeckillNoticeManager.getProductSeckillNoticeListByUserId(userId);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productSeckillNoticeList'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject ();
		ProductSeckillNoticeListByUserIdCacheManager productSeckillNoticeListByUserIdCacheManager = Constants.ctx.getBean(ProductSeckillNoticeListByUserIdCacheManager.class);
		List<ProductSeckillNotice> productSeckillNoticeList = this.productSeckillNoticeManager.getProductSeckillNoticeListByUpdateTime(new Date(nextLastTime));
		if(productSeckillNoticeList != null && productSeckillNoticeList.size() > 0){
			cacheObject.setSize(productSeckillNoticeList.size());
			Date lastUpdateTime = null;
			List<Long> userIdList = new ArrayList<Long> ();
			for(final ProductSeckillNotice productSeckillNotice : productSeckillNoticeList){
				if(lastUpdateTime == null || lastUpdateTime.before(productSeckillNotice.getUpdateTime())){
					lastUpdateTime = productSeckillNotice.getUpdateTime();
				}
				if(!userIdList.contains(productSeckillNotice.getUserId())){
					userIdList.add(productSeckillNotice.getUserId());
				}
			}
			
			if(userIdList != null && userIdList.size() > 0){
				for(final Long userId : userIdList){
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新缓存
								productSeckillNoticeListByUserIdCacheManager.removeSession(userId);
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
