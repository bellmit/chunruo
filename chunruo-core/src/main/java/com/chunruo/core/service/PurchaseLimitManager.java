package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.PurchaseLimit;

public interface PurchaseLimitManager extends GenericManager<PurchaseLimit, Long> {

	public List<PurchaseLimit> getPurchaseLimitListByIsEnable(boolean isEnable);

	public List<PurchaseLimit> getPurchaseLimitRecordListByUpdateTime(Date updateTime);

	public List<PurchaseLimit> getPurchaseLimitListByTypeAndIsEnable(Integer type, boolean isEnable);

	public List<PurchaseLimit> getPurchaseLimitListProductId(Long productId);

}
