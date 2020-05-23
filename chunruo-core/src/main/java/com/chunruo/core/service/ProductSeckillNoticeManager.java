package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductSeckillNotice;

public interface ProductSeckillNoticeManager extends GenericManager<ProductSeckillNotice, Long>{

	public ProductSeckillNotice getProductSeckillNoticeByUnique(Long seckillId, Long userId,Long productId);
	
	public void submitProductSeckillNotice(ProductSeckillNotice seckillNotice);
	
	public List<ProductSeckillNotice> getProductSeckillNoticeListByNoticeTime(Date noticeTime);

	public List<ProductSeckillNotice> getProductSeckillNoticeListBySeckillId(Long seckillId);

	public List<ProductSeckillNotice> getProductSeckillNoticeListByUserId(Long userId);

	public List<ProductSeckillNotice> getProductSeckillNoticeListByUpdateTime(Date updateTime);
}
