package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductCollection;
import com.chunruo.core.vo.MsgModel;

public interface ProductCollectionManager extends GenericManager<ProductCollection, Long>{
	
	public List<ProductCollection> getProductCollectionListByUserId(Long userId, boolean status);
	
	public List<ProductCollection> getProductCollectionListByUpdateTime(Date updateTime);
	
	public ProductCollection getProductCollectionByProductId(Long productId, Long userId);
	
	public void saveProductCollection(Product product,Long userId,Boolean status);
}
