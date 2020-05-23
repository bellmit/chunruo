package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.PurchaseDoubt;
import com.chunruo.core.repository.PurchaseDoubtRepository;
import com.chunruo.core.service.PurchaseDoubtManager;

@Transactional
@Component("purchaseDoubtManager")
public class PurchaseDoubtManagerImpl extends GenericManagerImpl<PurchaseDoubt, Long> implements PurchaseDoubtManager{
    private PurchaseDoubtRepository purchaseDoubtRepository;
	
	public PurchaseDoubtManagerImpl(PurchaseDoubtRepository purchaseDoubtRepository) {
		super(purchaseDoubtRepository);
		this.purchaseDoubtRepository=purchaseDoubtRepository;
	}

	@Override
	public List<PurchaseDoubt> getPurchaseDoubtListByUpdateTime(Date updateTime) {
		return this.purchaseDoubtRepository.getPurchaseDoubtListByUpdateTime(updateTime);
	}

}
