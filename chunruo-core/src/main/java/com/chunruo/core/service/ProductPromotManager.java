package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductPromot;

public interface ProductPromotManager extends GenericManager<ProductPromot, Long>{
	
	public List<ProductPromot> getProductPromotListByStatus(Boolean status);

	public List<ProductPromot> getProductPromotByUpdateTime(Date updateTime);
}
