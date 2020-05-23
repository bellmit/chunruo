package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductCollectionProfit;
import com.chunruo.core.repository.ProductCollectionProfitRepository;
import com.chunruo.core.service.ProductCollectionProfitManager;

@Transactional
@Component("productCollectionProfitManager")
public class ProductCollectionProfitManagerImpl extends GenericManagerImpl<ProductCollectionProfit, Long> implements ProductCollectionProfitManager{
	private ProductCollectionProfitRepository collectionProfitRepository;
	
	@Autowired
	public ProductCollectionProfitManagerImpl(ProductCollectionProfitRepository collectionProfitRepository) {
		super(collectionProfitRepository);
		this.collectionProfitRepository = collectionProfitRepository;
	}

	

	@Override
	public List<ProductCollectionProfit> getCollectionProfitListByUserIdAndProductId(Long userId, Long productId) {
		return this.collectionProfitRepository.getCollectionProfitListByUserIdAndProductId(userId, productId);
	}

	@Override
	public List<ProductCollectionProfit> getCollectionProfitListByUpdateTime(Date updateTime) {
		return this.collectionProfitRepository.getCollectionProfitListByUpdateTime(updateTime);
	}



	@Override
	public void saveProductCollectionProfit(List<ProductCollectionProfit> collectionProfitList) {
		if(collectionProfitList != null && collectionProfitList.size() > 0) {
			this.batchInsert(collectionProfitList, collectionProfitList.size());
		}
	}



	@Override
	public List<ProductCollectionProfit> getProductCollectionProfitListByUserId(Long userId) {
		return this.collectionProfitRepository.getProductCollectionProfitListByUserId(userId);
	}
}
