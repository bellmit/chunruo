package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductSpecModel;

public interface ProductSpecModelManager extends GenericManager<ProductSpecModel, Long>{
	
	public boolean isExistName(String name);

	public List<ProductSpecModel> getProductSpecModelListBySort();
	
	public List<ProductSpecModel> getProductSpecModelListByName(String name);
}
