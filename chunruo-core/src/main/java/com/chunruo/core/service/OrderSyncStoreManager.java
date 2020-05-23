package com.chunruo.core.service;

import java.util.Date;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.OrderSyncStore;

public interface OrderSyncStoreManager extends GenericManager< OrderSyncStore, Long> {

	public void updateOrderSyncStoreLastSyncTime(Long appStoreId, Date lastSyncTime);
}
