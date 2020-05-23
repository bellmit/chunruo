package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductSpecType;
import com.chunruo.core.repository.ProductSpecTypeRepository;
import com.chunruo.core.service.ProductSpecTypeManager;

@Transactional
@Component("productSpecTypeManager")
public class ProductSpecTypeManagerImpl extends GenericManagerImpl<ProductSpecType, Long> implements ProductSpecTypeManager{
	private ProductSpecTypeRepository productSpecTypeRepository;

	@Autowired
	public ProductSpecTypeManagerImpl(ProductSpecTypeRepository productSpecTypeRepository) {
		super(productSpecTypeRepository);
		this.productSpecTypeRepository = productSpecTypeRepository;
	}

	@Override
	public List<ProductSpecType> getProductSpecTypeListByProductId(Long productId) {
		return this.productSpecTypeRepository.getProductSpecTypeListByProductId(productId);
	}
}
