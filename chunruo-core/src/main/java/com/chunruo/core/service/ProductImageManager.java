package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductImage;

public interface ProductImageManager extends GenericManager<ProductImage, Long>{
	
	public List<ProductImage> getProductImageListByProductId(Long productId, Integer imageType);
	
	public List<ProductImage> saveAndDelProductImage(Long productId, Integer imageType, List<ProductImage> saveRecordList);

	List<ProductImage> getImageListByUpdateTime(Date updateTime);
}
