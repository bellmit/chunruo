package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.PurchaseLimit;
import com.chunruo.core.repository.PurchaseLimitRepository;
import com.chunruo.core.service.PurchaseLimitManager;

@Component("purchaseLimitManager")
public class PurchaseLimitManagerImpl extends GenericManagerImpl<PurchaseLimit, Long> implements PurchaseLimitManager{
	private PurchaseLimitRepository purchaseLimitRepository;

	@Autowired
	public PurchaseLimitManagerImpl(PurchaseLimitRepository purchaseLimitRepository) {
		super(purchaseLimitRepository);
		this.purchaseLimitRepository = purchaseLimitRepository;
	}

	@Override
	public List<PurchaseLimit> getPurchaseLimitListByIsEnable(boolean isEnable) {
		return this.purchaseLimitRepository.getPurchaseLimitListByIsEnable(isEnable);
	}

	@Override
	public List<PurchaseLimit> getPurchaseLimitRecordListByUpdateTime(Date updateTime) {
		return this.purchaseLimitRepository.getPurchaseLimitRecordListByUpdateTime(updateTime);
	}

	@Override
	public List<PurchaseLimit> getPurchaseLimitListByTypeAndIsEnable(Integer type, boolean isEnable) {
		return this.purchaseLimitRepository.getPurchaseLimitListByTypeAndIsEnable(type,isEnable);
	}

	@Override
	public List<PurchaseLimit> getPurchaseLimitListProductId(Long productId) {
		return this.purchaseLimitRepository.getPurchaseLimitListProductId(productId);
	}

	
}
