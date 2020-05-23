package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductSpecType;

public interface ProductSpecTypeManager extends GenericManager<ProductSpecType, Long>{

	public List<ProductSpecType> getProductSpecTypeListByProductId(Long productId);
}
