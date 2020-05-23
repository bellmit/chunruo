package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductSoldOutNotice;
import com.chunruo.core.repository.ProductSoldOutNoticeRepository;
import com.chunruo.core.service.ProductSoldOutNoticeManager;

@Component("productSoldOutNoticeManager")
public class ProductSoldOutNoticeManagerImpl extends GenericManagerImpl<ProductSoldOutNotice, Long> implements ProductSoldOutNoticeManager{
	private ProductSoldOutNoticeRepository productSoldOutNoticeRepository;

	@Autowired
	public ProductSoldOutNoticeManagerImpl(ProductSoldOutNoticeRepository productSoldOutNoticeRepository) {
		super(productSoldOutNoticeRepository);
		this.productSoldOutNoticeRepository = productSoldOutNoticeRepository;
	}

	@Override
	public ProductSoldOutNotice getProductSoldOutNoticeByProductIdAndUserId(Long productId, Long productSpecId,
			Long userId) {
		return this.productSoldOutNoticeRepository.getProductSoldOutNoticeByProductIdAndUserId(productId,productSpecId,userId);
	}

	@Override
	public List<ProductSoldOutNotice> getProductSoldOutNoticeListByUserId(Long userId) {
		return this.productSoldOutNoticeRepository.getProductSoldOutNoticeListByUserId(userId);
	}

	@Override
	public List<ProductSoldOutNotice> getProductSoldOutNoticeListByUpdateTime(Date updateTime) {
		return this.productSoldOutNoticeRepository.getProductSoldOutNoticeListByUpdateTime(updateTime);
	}

	@Override
	public List<ProductSoldOutNotice> getProductSoldOutNoticeListByProductIdList(List<Long> idList) {
		return this.productSoldOutNoticeRepository.getProductSoldOutNoticeListByProductIdList(idList);
	}
}
