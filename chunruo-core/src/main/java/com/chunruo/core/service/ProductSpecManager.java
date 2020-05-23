package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductSpec;

public interface ProductSpecManager extends GenericManager<ProductSpec, Long>{

	public List<ProductSpec> getProductSpecListByProductId(Long productId);
	
	public List<ProductSpec> getProductSpecListByProductIdList(List<Long> productIdList);

	public List<ProductSpec> getProductSpecByUpdateTime(Date updateTime);

	public void updateProductSeckillTotalNumber(Long productSpecId,Integer seckillTotalStock);

}
