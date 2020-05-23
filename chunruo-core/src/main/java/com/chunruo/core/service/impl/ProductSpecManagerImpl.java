package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.repository.ProductSpecRepository;
import com.chunruo.core.service.ProductSpecManager;

@Transactional
@Component("productSpecManager")
public class ProductSpecManagerImpl extends GenericManagerImpl<ProductSpec, Long> implements ProductSpecManager{
	private ProductSpecRepository productSpecRepository;
	
	@Autowired
	public ProductSpecManagerImpl(ProductSpecRepository productSpecRepository) {
		super(productSpecRepository);
		this.productSpecRepository = productSpecRepository;
	}

	@Override
	public List<ProductSpec> getProductSpecListByProductId(Long productId) {
		return this.productSpecRepository.getProductSpecListByProductId(productId);
	}

	@Override
	public List<ProductSpec> getProductSpecByUpdateTime(Date updateTime) {
		return this.productSpecRepository.getProductSpecByUpdateTime(updateTime);
	}

	@Override
	public List<ProductSpec> getProductSpecListByProductIdList(List<Long> productIdList) {
		if(productIdList != null && productIdList.size() > 0){
			return this.productSpecRepository.getProductSpecListByProductIdList(productIdList);
		}
		return null;
	}

	@Override
	public void updateProductSeckillTotalNumber(Long productSpecId, Integer seckillTotalStock) {
        this.productSpecRepository.updateProductSeckillTotalNumber(productSpecId,seckillTotalStock);		
	}
}