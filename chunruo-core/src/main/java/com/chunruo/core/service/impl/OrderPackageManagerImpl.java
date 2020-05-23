package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.OrderPackage;
import com.chunruo.core.repository.OrderPackageRepository;
import com.chunruo.core.service.OrderPackageManager;

@Transactional
@Component("orderPackageManager")
public class OrderPackageManagerImpl extends GenericManagerImpl<OrderPackage, Long> implements OrderPackageManager {
	private OrderPackageRepository orderPackageRepository;

	@Autowired
	public OrderPackageManagerImpl(OrderPackageRepository orderPackageRepository) {
		super(orderPackageRepository);
		this.orderPackageRepository = orderPackageRepository;
	}

	@Override
	public List<OrderPackage> getOrderPackageListByOrderId(Long orderId) {
		return this.orderPackageRepository.getOrderPackageListByOrderId(orderId);
	}

	@Override
	public List<OrderPackage> getOrderPackageListByUpdateTime(Date updateTime){
		return this.orderPackageRepository.getOrderPackageListByUpdateTime(updateTime);
	}
	
	@Override
	public List<OrderPackage> getOrderPackageListByOrderId(Long orderId, Boolean isHandler) {
		return this.orderPackageRepository.getOrderPackageListByOrderId(orderId, isHandler);
	}

	@Override
	public List<OrderPackage> getOrderPackageListByOrderIdList(List<Long> orderIdList) {
		if(orderIdList != null && orderIdList.size() > 0){
			return this.orderPackageRepository.getOrderPackageListByOrderIdList(orderIdList);
		}
		return null;
	}

}
