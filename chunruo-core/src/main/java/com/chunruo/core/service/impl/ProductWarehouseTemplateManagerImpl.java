package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductWarehouseTemplate;
import com.chunruo.core.repository.ProductWarehouseTemplateRepository;
import com.chunruo.core.service.ProductWarehouseTemplateManager;

@Component("productWarehouseTemplateManager")
public class ProductWarehouseTemplateManagerImpl extends GenericManagerImpl<ProductWarehouseTemplate, Long> implements ProductWarehouseTemplateManager{
	private ProductWarehouseTemplateRepository productWarehouseTemplateRepository;

	@Autowired
	public ProductWarehouseTemplateManagerImpl(ProductWarehouseTemplateRepository productWarehouseTemplateRepository) {
		super(productWarehouseTemplateRepository);
		this.productWarehouseTemplateRepository = productWarehouseTemplateRepository;
	}

	@Override
	public List<ProductWarehouseTemplate> getProductWarehouseTemplateListByStatus(boolean status) {
		return this.productWarehouseTemplateRepository.getProductWarehouseTemplateListByStatus(status);
	}

	@Override
	public List<ProductWarehouseTemplate> getProductWarehouseTemplateListByUpdateTime(Date updateTime) {
		return this.productWarehouseTemplateRepository.getProductWarehouseTemplateListByUpdateTime(updateTime);
	}

	@Override
	public ProductWarehouseTemplate getMemberYearsTemplateByName(String name) {
		return this.productWarehouseTemplateRepository.getMemberYearsTemplateByName(name);
	}
}
