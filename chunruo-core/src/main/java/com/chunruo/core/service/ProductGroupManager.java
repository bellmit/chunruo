package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductGroup;

public interface ProductGroupManager extends GenericManager<ProductGroup, Long>{
	
	public List<ProductGroup> getProductGroupListByProductGroupId(Long productGroupId);
	
	public List<ProductGroup> getProductGroupListByProductGroupId(Long productGroupId, Long productId);
}
