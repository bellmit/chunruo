package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductSeckillNotice;
import com.chunruo.core.repository.ProductSeckillNoticeRepository;
import com.chunruo.core.service.ProductSeckillNoticeManager;
import com.chunruo.core.util.DateUtil;

@Transactional
@Component("productSeckillNoticeManager")
public class ProductSeckillNoticeManagerImpl extends GenericManagerImpl<ProductSeckillNotice, Long> implements ProductSeckillNoticeManager{
	private ProductSeckillNoticeRepository productSeckillNoticeRepository;

	@Autowired
	public ProductSeckillNoticeManagerImpl(ProductSeckillNoticeRepository productSeckillNoticeRepository) {
		super(productSeckillNoticeRepository);
		this.productSeckillNoticeRepository = productSeckillNoticeRepository;
	}

	@Override
	public ProductSeckillNotice getProductSeckillNoticeByUnique(Long seckillId, Long userId,Long productId) {
		List<ProductSeckillNotice> list = this.productSeckillNoticeRepository.getProductSeckillNoticeByUnique(seckillId, userId,productId);
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public void submitProductSeckillNotice(ProductSeckillNotice seckillNotice) {
		ProductSeckillNotice dbNotice = this.getProductSeckillNoticeByUnique(seckillNotice.getSeckillId(), seckillNotice.getUserId(),seckillNotice.getProductId());
		if(dbNotice != null && dbNotice.getNoticeId() != null){
			dbNotice.setProductId(seckillNotice.getProductId());
			dbNotice.setMobile(seckillNotice.getMobile());
			dbNotice.setNoticeTime(seckillNotice.getNoticeTime());
			dbNotice.setUpdateTime(DateUtil.getCurrentDate());
			this.update(dbNotice);
		}else {
			seckillNotice.setCreateTime(DateUtil.getCurrentDate());
			seckillNotice.setUpdateTime(seckillNotice.getCreateTime());
			this.save(seckillNotice);
		}
	}

	@Override
	public List<ProductSeckillNotice> getProductSeckillNoticeListByNoticeTime(Date noticeTime) {
		List<ProductSeckillNotice> noticeList = this.productSeckillNoticeRepository.getProductSeckillNoticeListByNoticeTime(noticeTime);
		if(noticeList != null && noticeList.size() > 0){
			List<Long> noticeIdList = new ArrayList<Long> ();
			for(ProductSeckillNotice notice : noticeList){
				noticeIdList.add(notice.getNoticeId());
			}
			this.deleteByIdList(noticeIdList);
		}
		return noticeList;
	}

	@Override
	public List<ProductSeckillNotice> getProductSeckillNoticeListBySeckillId(Long seckillId) {
		return this.productSeckillNoticeRepository.getProductSeckillNoticeListBySeckillId(seckillId);
	}

	@Override
	public List<ProductSeckillNotice> getProductSeckillNoticeListByUserId(Long userId) {
		return this.productSeckillNoticeRepository.getProductSeckillNoticeListByUserId(userId);
	}

	@Override
	public List<ProductSeckillNotice> getProductSeckillNoticeListByUpdateTime(Date updateTime) {
		return this.productSeckillNoticeRepository.getProductSeckillNoticeListByUpdateTime(updateTime);
	}
}
