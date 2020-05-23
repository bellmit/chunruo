package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductPromot;
import com.chunruo.core.repository.ProductPromotRepository;
import com.chunruo.core.service.ProductPromotManager;

@Transactional
@Component("productPromotManager")
public class ProductPromotManagerImpl extends GenericManagerImpl<ProductPromot, Long> implements ProductPromotManager{
	private ProductPromotRepository productPromotRepository;
	
	@Autowired
	public ProductPromotManagerImpl(ProductPromotRepository productPromotRepository) {
		super(productPromotRepository);
		this.productPromotRepository = productPromotRepository;
	}

	@Override
	public List<ProductPromot> getProductPromotListByStatus(Boolean status) {
		return this.productPromotRepository.getProductPromotListByStatus(status);
	}
	
	@Override
	public List<ProductPromot> getProductPromotByUpdateTime(Date updateTime) {
		return this.productPromotRepository.getProductPromotByUpdateTime(updateTime);
	}
}
