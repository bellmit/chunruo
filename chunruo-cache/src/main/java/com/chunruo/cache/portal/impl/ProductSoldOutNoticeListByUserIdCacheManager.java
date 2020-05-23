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
import com.chunruo.core.model.ProductSoldOutNotice;
import com.chunruo.core.service.ProductSoldOutNoticeManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("productSoldOutNoticeListByUserIdCacheManager")
public class ProductSoldOutNoticeListByUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private ProductSoldOutNoticeManager productSoldOutNoticeManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productSoldOutNoticeList_'+#userId")
	public Map<String,List<ProductSoldOutNotice>> getSession(Long userId){
		Map<String,List<ProductSoldOutNotice>> noticeMap = new HashMap<String,List<ProductSoldOutNotice>>();
		List<ProductSoldOutNotice> productSoldOutNoticeList = this.productSoldOutNoticeManager.getProductSoldOutNoticeListByUserId(userId);
	    if(productSoldOutNoticeList != null && !productSoldOutNoticeList.isEmpty()) {
	    	for(ProductSoldOutNotice notice : productSoldOutNoticeList) {
	    		String productId = StringUtil.null2Str(notice.getProductId());
	    		if(noticeMap.containsKey(productId)) {
	    			noticeMap.get(productId).add(notice);
	    		}else {
	    			List<ProductSoldOutNotice> list = new ArrayList<ProductSoldOutNotice>();
	    			list.add(notice);
	    			noticeMap.put(productId, list);
	    		}
	    	}
	    }
	    return noticeMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productSoldOutNoticeList_'+#userId")
	public void removeSession(Long userId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject ();
		ProductSoldOutNoticeListByUserIdCacheManager productSoldOutNoticeListByUserIdCacheManager = Constants.ctx.getBean(ProductSoldOutNoticeListByUserIdCacheManager.class);
		List<ProductSoldOutNotice> productSoldOutNoticeList = this.productSoldOutNoticeManager.getProductSoldOutNoticeListByUpdateTime(new Date(nextLastTime));
		if(productSoldOutNoticeList != null && productSoldOutNoticeList.size() > 0){
			cacheObject.setSize(productSoldOutNoticeList.size());
			Date lastUpdateTime = null;
			List<Long> userIdList = new ArrayList<Long> ();
			for(final ProductSoldOutNotice productSoldOutNotice : productSoldOutNoticeList){
				if(lastUpdateTime == null || lastUpdateTime.before(productSoldOutNotice.getUpdateTime())){
					lastUpdateTime = productSoldOutNotice.getUpdateTime();
				}
				if(!userIdList.contains(productSoldOutNotice.getUserId())){
					userIdList.add(productSoldOutNotice.getUserId());
				}
			}
			
			if(userIdList != null && userIdList.size() > 0){
				for(final Long userId : userIdList){
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新缓存
								productSoldOutNoticeListByUserIdCacheManager.removeSession(userId);
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
