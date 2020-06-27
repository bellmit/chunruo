package com.chunruo.cache.portal.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.Product;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.util.StringUtil;

/**
 * 秒杀场次商品列表
 * @author chunruo
 *
 */
@Service("productListBySeckillIdCacheManager")
public class ProductListBySeckillIdCacheManager {
	@Autowired
	private ProductManager productManager;
	
	public Map<String, Product> getSession(Long seckillId){
		Map<String, Product> productMap = new HashMap<String, Product> ();
		try{
			List<Product> list = this.productManager.getProductListBySeckillId(seckillId);
			if(list != null && list.size() > 0){
				for(Product product : list){
					productMap.put(StringUtil.null2Str(product.getProductId()), product);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return productMap;
	}
	
	public void removeSession(Long seckillId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
