package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductCategory;

public interface ProductCategoryManager extends GenericManager<ProductCategory, Long>{
	
	public List<ProductCategory> getProductCategoryByLevel(int level, int status);
	
	public List<ProductCategory> getProductCategoryByStatus(int status);

	int updateProductCategoryStatus(List<Long> categoryIdList, int status);
	
	public List<ProductCategory> getProductCategoryByParentId(Long parentId, int status);

	List<ProductCategory> getProductCategory(Long parentId, Integer status);
	
	public List<ProductCategory> getCategoryByLevel(int level);
	
	public int updateCategoryByParentId(Long parentId,int status);

	public List<ProductCategory> getProductCategoryListByUpdateTime(Date updateTime);
}
