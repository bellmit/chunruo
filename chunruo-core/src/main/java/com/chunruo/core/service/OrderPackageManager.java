package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.OrderPackage;

public interface OrderPackageManager extends GenericManager<OrderPackage, Long>{

	public List<OrderPackage> getOrderPackageListByOrderId(Long orderId);
	
	public List<OrderPackage> getOrderPackageListByUpdateTime(Date updateTime);
	
	public List<OrderPackage> getOrderPackageListByOrderIdList( List<Long> orderIdList);
	
	public List<OrderPackage> getOrderPackageListByOrderId(Long orderId, Boolean isHandler);
}
