package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductGroup;
import com.chunruo.core.repository.ProductGroupRepository;
import com.chunruo.core.service.ProductGroupManager;

@Transactional
@Component("productGroupManager")
public class ProductGroupManagerImpl extends GenericManagerImpl<ProductGroup, Long> implements ProductGroupManager{
	private ProductGroupRepository productGroupRepository;
	
	@Autowired
	public ProductGroupManagerImpl(ProductGroupRepository productGroupRepository) {
		super(productGroupRepository);
		this.productGroupRepository = productGroupRepository;
	}

	@Override
	public List<ProductGroup> getProductGroupListByProductGroupId(Long productGroupId) {
		return this.productGroupRepository.getProductGroupListByProductGroupId(productGroupId);
	}

	@Override
	public List<ProductGroup> getProductGroupListByProductGroupId(Long productGroupId, Long productId) {
		return this.productGroupRepository.getProductGroupListByProductGroupId(productGroupId, productId);
	}
}