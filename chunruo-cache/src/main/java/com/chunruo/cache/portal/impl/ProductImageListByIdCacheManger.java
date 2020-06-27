package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.ProductImage;
import com.chunruo.core.service.ProductImageManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("productImageListByIdCacheManger")
public class ProductImageListByIdCacheManger extends BaseCacheManagerImpl {
	@Autowired
	private ProductImageManager productImageManager;
	
	public List<ProductImage> getSession(Long productId, Integer imageType){
		return this.productImageManager.getProductImageListByProductId(productId, imageType);
	}
	
	public void removeSession(Long productId, Integer imageType) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}

	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject ();
		final ProductImageListByIdCacheManger productImageListByIdCacheManger = Constants.ctx.getBean(ProductImageListByIdCacheManger.class);
		List<ProductImage> imageList = this.productImageManager.getImageListByUpdateTime(new Date(nextLastTime));
		if(imageList != null && imageList.size() > 0){
			cacheObject.setSize(imageList.size());
			Date lastUpdateTime = null;
			final Map<Long, List<Integer>> productIdListMap = new HashMap<Long, List<Integer>> ();
			for(final ProductImage image : imageList){
				if(lastUpdateTime == null || lastUpdateTime.before(image.getUpdateTime())){
					lastUpdateTime = image.getUpdateTime();
				}
				
				// 更新商品图片
				if(productIdListMap.containsKey(image.getProductId())){
					List<Integer> list = productIdListMap.get(image.getProductId());
					if(list == null || !list.contains(image.getImageType())){
						productIdListMap.get(image.getProductId()).add(image.getImageType());
					}
				}else{
					List<Integer> list = new ArrayList<Integer> ();
					list.add(image.getImageType());
					productIdListMap.put(image.getProductId(), list);
				}
			}
			
			// 更新缓存
			if(productIdListMap != null && productIdListMap.size() > 0){
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
					@Override
					public void run() {
						for(Entry<Long, List<Integer>> entry : productIdListMap.entrySet()){
							Long productId = entry.getKey();
							List<Integer> imageTypeList = entry.getValue();
							if(imageTypeList != null && imageTypeList.size() > 0){
								for(Integer imageType : imageTypeList){
									try{
										// 更新图片缓存信息
										productImageListByIdCacheManger.removeSession(productId, imageType);
									}catch(Exception e){
										e.printStackTrace();
									}
								}
							}
						}
					}
				});
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
