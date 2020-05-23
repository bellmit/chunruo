package com.chunruo.core.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.OrderSyncComplate;
import com.chunruo.core.repository.OrderSyncComplateRepository;
import com.chunruo.core.service.OrderSyncComplateManager;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("orderSyncComplateManager")
public class OrderSyncComplateManagerImpl extends GenericManagerImpl<OrderSyncComplate, Long> implements OrderSyncComplateManager{
	private OrderSyncComplateRepository orderSyncComplateRepository;
	
	public OrderSyncComplateManagerImpl(OrderSyncComplateRepository orderSyncComplateRepository) {
		super(orderSyncComplateRepository);
		this.orderSyncComplateRepository = orderSyncComplateRepository;
	}
	
	@Override
	public void updatOrderSyncComplateTask() {
		this.orderSyncComplateRepository.executeSqlFunction("{?=call updatOrderSyncComplateTask_Fnc()}");
	}
	
	@Override
	public List<OrderSyncComplate> loadOrderSyncComplateTask() {
		String uniqueString = StringUtil.null2Str(UUID.randomUUID().toString());
		String batchNumber = this.orderSyncComplateRepository.executeSqlFunction("{?=call loadOrderSyncComplateTask_Fnc(?)}", new Object[]{uniqueString});
		log.debug("loadOrderSyncComplateTask_Fnc=======>>> " + StringUtil.null2Str(uniqueString));
		if(StringUtil.compareObject(uniqueString, batchNumber)){
			return this.orderSyncComplateRepository.getOrderSyncComplateListByBatchNumber(batchNumber);
		}
		return null;
	}
	
}
