package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.OrderLockStock;

public interface OrderLockStockManager extends GenericManager<OrderLockStock, Long> {
	
	public List<OrderLockStock> getOrderLockStockListByProductId(Long productId);
	
	public List<OrderLockStock> getOrderLockStockListByProductIdList(List<Long> productIdList, Boolean status);
	
	public List<OrderLockStock> getOrderLockStockListByProductId(Long productId, Boolean status);
	
	public List<OrderLockStock> getOrderLockStockListByOrderId(Long orderId);
	
	public void deleteOrderLockStockByOrderId(Long orderId);
	
	public List<OrderLockStock> getOrderLockStockListByUpdateTime(Date updateTime);
	
	public List<Long> closeOrderLockStockListByOrderId(Long orderId);

	public List<OrderLockStock> getOrderLockStockListByCreateTime(Boolean status, Date createTime);
	
	public List<OrderLockStock> getOrderLockStockListBySeckillId(Long seckillId, Long userId);
}
