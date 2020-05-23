package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductCollection;
import com.chunruo.core.model.ProductCollectionProfit;

public interface ProductCollectionProfitManager extends GenericManager<ProductCollectionProfit, Long>{
	
	public List<ProductCollectionProfit> getCollectionProfitListByUserIdAndProductId(Long userId, Long productId);
	
	public List<ProductCollectionProfit> getCollectionProfitListByUpdateTime(Date updateTime);

	public void saveProductCollectionProfit(List<ProductCollectionProfit> collectionProfitList);

	public List<ProductCollectionProfit> getProductCollectionProfitListByUserId(Long userId);
}
