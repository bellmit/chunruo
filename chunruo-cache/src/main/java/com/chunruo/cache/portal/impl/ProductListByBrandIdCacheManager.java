package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.Constants;
import com.chunruo.core.model.ProductBrand;
import com.chunruo.core.model.Product;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.util.StringUtil;

/**
 * 品类-分销市场商品缓存
 * @author chunruo
 *
 */
@Service("productListByBrandIdCacheManager")
public class ProductListByBrandIdCacheManager {
	@Autowired
	private ProductManager productManager;

	public List<String> getSession(Long brandId){
		List<String> productIdList = new ArrayList<String> ();
		try{
			List<Product> productList = null;
			
			// 按商品分类
			ProductBrand productBrand = Constants.PRODUCT_BRAND_MAP.get(brandId);
			if(productBrand != null && productBrand.getBrandId() != null){
				productList = this.productManager.getProductListByBrandId(productBrand.getBrandId(), true);
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

	public void removeSession(Long brandId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
