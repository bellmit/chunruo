package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductSoldOutNotice;

public interface ProductSoldOutNoticeManager extends GenericManager<ProductSoldOutNotice, Long>{

	public ProductSoldOutNotice getProductSoldOutNoticeByProductIdAndUserId(Long productId, Long productSpecId, Long userId);

	public List<ProductSoldOutNotice> getProductSoldOutNoticeListByUserId(Long userId);

	public List<ProductSoldOutNotice> getProductSoldOutNoticeListByUpdateTime(Date updateTime);

	public List<ProductSoldOutNotice> getProductSoldOutNoticeListByProductIdList(List<Long> idList);

}
