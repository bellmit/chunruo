package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.repository.ProductCategoryRepository;
import com.chunruo.core.service.ProductCategoryManager;

@Transactional
@Component("productCategoryManager")
public class ProductCategoryManagerImpl extends GenericManagerImpl<ProductCategory, Long> implements ProductCategoryManager {
	private ProductCategoryRepository productCategoryRepository;
	
	@Autowired
	public ProductCategoryManagerImpl(ProductCategoryRepository productCategoryRepository) {
		super(productCategoryRepository);
		this.productCategoryRepository = productCategoryRepository;
	}
	
	@Override
	public List<ProductCategory> getProductCategoryByLevel(int level, int status){
		return this.productCategoryRepository.getProductCategoryByLevel(level, status);
	}

	@Override
	public List<ProductCategory> getProductCategory(Long parentId, Integer status) {
		return productCategoryRepository.getProductCategory(parentId, status);
	}
	
	@Override
	public int updateProductCategoryStatus(List<Long> categoryIdList, int status) {
		if(categoryIdList == null || categoryIdList.size() <= 0){
			return 0;
		}
		return this.productCategoryRepository.updateProductCategoryStatus(categoryIdList, status);
	}

	@Override
	public List<ProductCategory> getProductCategoryByStatus(int status) {
		return productCategoryRepository.getProductCategoryByStatus(status);
	}

	@Override
	public List<ProductCategory> getProductCategoryByParentId(Long parentId, int status) {
		return productCategoryRepository.getProductCategory(parentId, status);
	}

	@Override
	public List<ProductCategory> getCategoryByLevel(int level) {
		return productCategoryRepository.getCategoryByLevel(level);
	}

	@Override
	public int updateCategoryByParentId(Long parentId,int status) {
		return productCategoryRepository.updateCategoryByParentId(parentId,status);
	}

	@Override
	public List<ProductCategory> getProductCategoryListByUpdateTime(Date updateTime) {
		return this.productCategoryRepository.getProductCategoryListByUpdateTime(updateTime);
	}
}