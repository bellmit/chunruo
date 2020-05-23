package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductSpecModel;
import com.chunruo.core.repository.ProductSpecModelRepository;
import com.chunruo.core.service.ProductSpecModelManager;

@Transactional
@Component("productSpecModelManager")
public class ProductSpecModelManagerImpl extends GenericManagerImpl<ProductSpecModel, Long> implements ProductSpecModelManager{
	private ProductSpecModelRepository productSpecModelManager;

	@Autowired
	public ProductSpecModelManagerImpl(ProductSpecModelRepository productSpecModelManager) {
		super(productSpecModelManager);
		this.productSpecModelManager = productSpecModelManager;
	}

	@Override
	public boolean isExistName(String name) {
		List<ProductSpecModel> list = this.getProductSpecModelListByName(name);
		if(list != null && list.size() > 0){
			return true;
		}
		return false;
	}

	@Override
	public List<ProductSpecModel> getProductSpecModelListBySort() {
		return this.productSpecModelManager.getProductSpecModelListBySort();
	}

	@Override
	public List<ProductSpecModel> getProductSpecModelListByName(String name) {
		return this.productSpecModelManager.getProductSpecModelListByName(name);
	}
}
