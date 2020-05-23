package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductBrand;

public interface ProductBrandManager extends GenericManager<ProductBrand, Long>{

	public List<ProductBrand> getBrandListByIsHot(Boolean isHot);
	
	public List<ProductBrand> getBrandListByUpdateTime(Date updateTime);
}
