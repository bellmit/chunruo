package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.service.ProductCategoryManager;
import com.chunruo.core.util.StringUtil;

@Service("productCategoryListCacheManager")
public class ProductCategoryListCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private ProductCategoryManager productCategoryManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productCategoryList'")
	public List<ProductCategory> getSession(){
		List<ProductCategory> realProductCategoryList = new LinkedList<ProductCategory>();
		List<ProductCategory> productCategoryList = this.productCategoryManager.getProductCategoryByStatus(1);
		if(productCategoryList != null && productCategoryList.size() > 0) {
			List<ProductCategory> firstCategoryList = new ArrayList<>(); //一级分类
			Map<Long,List<ProductCategory>> productCategoryMap = new HashMap<Long,List<ProductCategory>>();
			for(ProductCategory productCategory : productCategoryList) {
				if(StringUtil.nullToBoolean(productCategory.getStatus())) {
					if(StringUtil.compareObject(productCategory.getLevel(), ProductCategory.PRODUCT_CATEGORY_LEVEL_FIRST)) {
						firstCategoryList.add(productCategory);
					}else if(StringUtil.compareObject(productCategory.getLevel(), ProductCategory.PRODUCT_CATEGORY_LEVEL_SECOND)){
						if(productCategoryMap.containsKey(productCategory.getParentId())) {
							productCategoryMap.get(productCategory.getParentId()).add(productCategory);
						}else {
							List<ProductCategory> secondCategoryList = new ArrayList<ProductCategory>();
							secondCategoryList.add(productCategory);
							productCategoryMap.put(StringUtil.nullToLong(productCategory.getParentId()), secondCategoryList);
						}
					}
				}
			}
			
			//一级分类先排序
			if(firstCategoryList != null && firstCategoryList.size() > 0) {
				Collections.sort(firstCategoryList,new Comparator<ProductCategory>() {
					@Override
					public int compare(ProductCategory o1, ProductCategory o2) {
						try {
							Integer sort1 = StringUtil.nullToInteger(o1.getSort());
							Integer sort2 = StringUtil.nullToInteger(o2.getSort());
							return sort1.compareTo(sort2);
						}catch(Exception e) {
							e.printStackTrace();
						}
						return 0;
					}
				});
				
				//在按照二级分类排序
				for(ProductCategory productCategory :  firstCategoryList) {
					List<ProductCategory> list = productCategoryMap.get(StringUtil.nullToLong(productCategory.getCategoryId()));
				    if(list != null && list.size() > 0) {
				    	Collections.sort(list,new Comparator<ProductCategory>() {
							@Override
							public int compare(ProductCategory o1, ProductCategory o2) {
								try {
									Integer sort1 = StringUtil.nullToInteger(o1.getSort());
									Integer sort2 = StringUtil.nullToInteger(o2.getSort());
									return sort1.compareTo(sort2);
								}catch(Exception e) {
									e.printStackTrace();
								}
								return 0;
							}
						});
					    realProductCategoryList.addAll(list);
				    }
				}
			}
			
		}
	    return realProductCategoryList;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productCategoryList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		ProductCategoryListCacheManager productCategoryListCacheManager = Constants.ctx.getBean(ProductCategoryListCacheManager.class);
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
