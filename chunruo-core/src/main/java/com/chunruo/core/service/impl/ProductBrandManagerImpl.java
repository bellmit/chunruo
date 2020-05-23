package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductBrand;
import com.chunruo.core.repository.ProductBrandRepository;
import com.chunruo.core.service.ProductBrandManager;

@Transactional
@Component("productBrandManager")
public class ProductBrandManagerImpl extends GenericManagerImpl<ProductBrand, Long>  implements ProductBrandManager{
	private ProductBrandRepository productBrandRepository;
	
	@Autowired
	public ProductBrandManagerImpl(ProductBrandRepository productBrandRepository) {
		super(productBrandRepository);
		this.productBrandRepository = productBrandRepository;
	}

	@Override
	public List<ProductBrand> getBrandListByIsHot(Boolean isHot) {
		return this.productBrandRepository.getBrandListByIsHot(isHot);
	}

	@Override
	public List<ProductBrand> getBrandListByUpdateTime(Date updateTime) {
		return this.productBrandRepository.getBrandListByUpdateTime(updateTime);
	}
}
