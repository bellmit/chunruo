package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.OrderHistory;
import com.chunruo.core.repository.OrderHistoryRepository;
import com.chunruo.core.service.OrderHistoryManager;
import com.chunruo.core.util.DateUtil;

@Transactional
@Component("orderHistoryManager")
public class OrderHistoryManagerImpl extends GenericManagerImpl<OrderHistory, Long> implements OrderHistoryManager{
	private OrderHistoryRepository orderHistoryRepository;

	@Autowired
	public OrderHistoryManagerImpl(OrderHistoryRepository orderHistoryRepository) {
		super(orderHistoryRepository);
		this.orderHistoryRepository = orderHistoryRepository;
	}

	@Override
	public List<OrderHistory> getOrderHistoryListByOrderId(Long orderId) {
		return this.orderHistoryRepository.getOrderHistoryListByOrderId(orderId);
	}
	
	@Override
	public OrderHistory createOrderHistoryBean(Long orderId, String name, String message){
		OrderHistory orderHistory = new OrderHistory ();
		orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
		orderHistory.setOrderId(orderId);
		orderHistory.setCreateTime(DateUtil.getCurrentDate());
		orderHistory.setName(name);
		orderHistory.setMessage(message);
		return orderHistory;
	}
}
