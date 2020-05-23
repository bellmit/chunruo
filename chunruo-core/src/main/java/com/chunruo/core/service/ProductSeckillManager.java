package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductSeckill;
import com.chunruo.core.model.ProductSpec;

public interface ProductSeckillManager extends GenericManager<ProductSeckill, Long>{

	public List<ProductSeckill> getProductSeckillListByStatus(boolean status);
	
	public List<ProductSeckill> getProductSeckillByUpdateTime(Date updateTime);
	
	public void saveSeckillProduct(Product product, ProductSeckill seckill, List<ProductSpec> productSpecList);
	
	public void deleteSeckillProduct(List<Product> productList, List<Long> productIdList, List<Long> seckillIdList);
}
