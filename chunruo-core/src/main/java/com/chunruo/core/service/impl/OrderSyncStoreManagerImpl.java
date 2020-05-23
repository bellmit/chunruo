package com.chunruo.core.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.OrderSyncStore;
import com.chunruo.core.repository.OrderSyncStoreRepository;
import com.chunruo.core.service.OrderSyncStoreManager;
import com.chunruo.core.util.DateUtil;

@Transactional
@Component("orderSyncStoreManager")
public class OrderSyncStoreManagerImpl extends GenericManagerImpl<OrderSyncStore, Long> implements OrderSyncStoreManager {
	private OrderSyncStoreRepository orderSyncStoreRepository;

	@Autowired
	public OrderSyncStoreManagerImpl(OrderSyncStoreRepository orderSyncStoreRepository) {
		super(orderSyncStoreRepository);
		this.orderSyncStoreRepository = orderSyncStoreRepository;
	}

	@Override
	public void updateOrderSyncStoreLastSyncTime(Long appStoreId, Date lastSyncTime) {
		this.orderSyncStoreRepository.updateOrderSyncStoreLastSyncTime(appStoreId, lastSyncTime, DateUtil.getCurrentDate());
	}

}
