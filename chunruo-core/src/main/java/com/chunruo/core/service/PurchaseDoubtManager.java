package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.PurchaseDoubt;

public interface PurchaseDoubtManager extends GenericManager<PurchaseDoubt, Long>{

	List<PurchaseDoubt> getPurchaseDoubtListByUpdateTime(Date updateTime);

	
	
}
