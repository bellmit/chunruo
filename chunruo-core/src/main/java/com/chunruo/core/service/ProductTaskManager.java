package com.chunruo.core.service;


import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductTask;

public interface ProductTaskManager extends GenericManager<ProductTask, Long> {

	List<ProductTask> getProductTaskListByIsEnable(boolean isEnable);

	List<ProductTask> getProductTaskListByUpdateTime(Date updateTime);

	List<ProductTask> getProductTaskListByProductId(Long productId);
}
