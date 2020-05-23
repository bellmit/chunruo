package com.chunruo.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.repository.ProductWarehouseRepository;
import com.chunruo.core.service.ProductWarehouseManager;

@Transactional
@Component("productWarehouseManager")
public class ProductWarehouseManagerImpl extends GenericManagerImpl<ProductWarehouse, Long> implements ProductWarehouseManager{
	private ProductWarehouseRepository productWarehouseRepository;

	@Autowired
	public ProductWarehouseManagerImpl(ProductWarehouseRepository productWarehouseRepository) {
		super(productWarehouseRepository);
		this.productWarehouseRepository = productWarehouseRepository;
	}

}