package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductTask;
import com.chunruo.core.repository.ProductTaskRepository;
import com.chunruo.core.service.ProductTaskManager;

@Component("productTaskManager")
public class ProductTaskManagerImpl extends GenericManagerImpl<ProductTask, Long> implements ProductTaskManager{
	private ProductTaskRepository productTaskRepository;

	@Autowired
	public ProductTaskManagerImpl(ProductTaskRepository productTaskRepository) {
		super(productTaskRepository);
		this.productTaskRepository = productTaskRepository;
	}

	@Override
	public List<ProductTask> getProductTaskListByIsEnable(boolean isEnable) {
		return this.productTaskRepository.getProductTaskListByIsEnable(isEnable);
	}

	@Override
	public List<ProductTask> getProductTaskListByUpdateTime(Date updateTime) {
		return this.productTaskRepository.getProductTaskListByUpdateTime(updateTime);
	}

	@Override
	public List<ProductTask> getProductTaskListByProductId(Long productId) {
		return this.productTaskRepository.getProductTaskListByProductId(productId);
	}

}
