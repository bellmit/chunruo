package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.OrderSync;
import com.chunruo.core.model.OrderSyncList;
import com.chunruo.core.repository.OrderSyncListRepository;
import com.chunruo.core.repository.OrderSyncRepository;
import com.chunruo.core.service.OrderSyncListManager;
import com.chunruo.core.util.DateUtil;

@Transactional
@Component("orderSyncListManager")
public class OrderSyncListManagerImpl extends GenericManagerImpl<OrderSyncList, Long> implements OrderSyncListManager {
	private OrderSyncListRepository orderSyncListRepository;
	@Autowired
	private OrderSyncRepository orderSyncRepository;
	
	@Autowired
	public OrderSyncListManagerImpl(OrderSyncListRepository orderSyncListRepository) {
		super(orderSyncListRepository);
		this.orderSyncListRepository = orderSyncListRepository;
	}

	@Override
	public Date getOrderSyncListMaxEndTime() {
		return this.orderSyncListRepository.getOrderSyncListMaxEndTime();
	}

	@Override
	public OrderSyncList saveOrderSyncList(OrderSyncList orderSyncList, List<OrderSync> orderSyncRecordList) {
		orderSyncList.setUpdateTime(DateUtil.getCurrentDate());
		orderSyncList = this.save(orderSyncList);
		
		if(orderSyncRecordList != null && orderSyncRecordList.size() > 0){
			this.orderSyncRepository.batchInsert(orderSyncRecordList, orderSyncRecordList.size());
		}
		return orderSyncList;
	}

}
