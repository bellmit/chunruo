package com.chunruo.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductGift;
import com.chunruo.core.repository.ProductGiftRepository;
import com.chunruo.core.service.ProductGiftManager;

@Transactional
@Component("productGiftManager")
public class ProductGiftManagerImpl extends GenericManagerImpl<ProductGift, Long>  implements ProductGiftManager {
	private ProductGiftRepository productGiftRepository;
	
	@Autowired
	public ProductGiftManagerImpl(ProductGiftRepository productGiftRepository) {
		super(productGiftRepository);
		this.productGiftRepository = productGiftRepository;
	}
}
