package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.Constants;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.Product;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.util.StringUtil;

/**
 * 分类-分销市场商品缓存
 * @author chunruo
 *
 */
@Service("productListByCatIdCacheManager")
public class ProductListByCatIdCacheManager {
	@Autowired
	private ProductManager productManager;

	public List<String> getSession(Long categoryId){
		List<String> productIdList = new ArrayList<String> ();
		try{
			List<Product> productList = null;
			if(StringUtil.compareObject(categoryId, 0)){
				// 全部批发商品
				productList = this.productManager.getProductList(true);
			}else{
				// 按商品分类
				ProductCategory productCategory = Constants.PRODUCT_CATEGORY_MAP.get(categoryId);
				if(productCategory != null && productCategory.getCategoryId() != null){
					if(StringUtil.compareObject(productCategory.getParentId(), 0)){
						productList = this.productManager.getProductListByCategoryFid(categoryId, true);
					}else{
						productList = this.productManager.getProductListByCategoryId(categoryId, true);
					}
				}
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

	public void removeSession(Long categoryId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
