package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.OrderLockStock;
import com.chunruo.core.repository.OrderLockStockRepository;
import com.chunruo.core.service.OrderLockStockManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("orderLockStockManager")
public class OrderLockStockManagerImpl extends GenericManagerImpl<OrderLockStock, Long> implements OrderLockStockManager {
	private OrderLockStockRepository orderLockStockRepository;
	
	@Autowired
	public OrderLockStockManagerImpl(OrderLockStockRepository orderLockStockRepository) {
		super(orderLockStockRepository);
		this.orderLockStockRepository = orderLockStockRepository;
	}

	@Override
	public List<OrderLockStock> getOrderLockStockListByOrderId(Long orderId) {
		return this.orderLockStockRepository.getOrderLockStockListByOrderId(orderId);
	}
	
	@Override
	public void deleteOrderLockStockByOrderId(Long orderId) {
		this.orderLockStockRepository.deleteOrderLockStockByOrderId(orderId);
	}

	@Override
	public List<OrderLockStock> getOrderLockStockListByProductId(Long productId) {
		return this.orderLockStockRepository.getOrderLockStockListByProductId(productId);
	}

	@Override
	public List<OrderLockStock> getOrderLockStockListByUpdateTime(Date updateTime) {
		return this.orderLockStockRepository.getOrderLockStockListByUpdateTime(updateTime);
	}

	@Override
	public List<OrderLockStock> getOrderLockStockListByProductId(Long productId, Boolean status) {
		return this.orderLockStockRepository.getOrderLockStockListByProductId(productId, status);
	}
	
	@Override
	public List<Long> closeOrderLockStockListByOrderId(Long orderId) {
		List<Long> lockStockProductIdList = new ArrayList<Long> ();
		List<OrderLockStock> lockStockList = this.getOrderLockStockListByOrderId(orderId);
		if(lockStockList != null && lockStockList.size() > 0){
			Date currentDate = DateUtil.getCurrentDate();
			for(OrderLockStock orderLockStock : lockStockList){
				orderLockStock.setStatus(true);
				orderLockStock.setUpdateTime(currentDate);
				
				if(!lockStockProductIdList.contains(orderLockStock.getProductId())){
					lockStockProductIdList.add(orderLockStock.getProductId());
				}
			}
			// 更新秒杀锁定库存
			this.batchInsert(lockStockList, lockStockList.size());
		}
		return lockStockProductIdList;
	}

	@Override
	public List<OrderLockStock> getOrderLockStockListByCreateTime(Boolean status, Date createTime) {
		return this.orderLockStockRepository.getOrderLockStockListByCreateTime(status, createTime);
	}

	@Override
	public List<OrderLockStock> getOrderLockStockListByProductIdList(List<Long> productIdList, Boolean status) {
		if(productIdList != null && productIdList.size() > 0){
			return this.orderLockStockRepository.getOrderLockStockListByProductIdList(productIdList, status);
		}
		return null;
	}

	@Override
	public List<OrderLockStock> getOrderLockStockListBySeckillId(Long seckillId, Long userId) {
		List<OrderLockStock> resultList = new ArrayList<OrderLockStock> ();
		String sql = "select los.*, o.status as order_status from jkd_order_lock_stock los, jkd_order o where los.order_id = o.order_id and los.type = 1 and los.seckill_id =? and o.user_id =?";
		List<Map<String, Object>> list = orderLockStockRepository.querySqlMap(sql, Arrays.asList(seckillId, userId).toArray());
		if(list != null && list.size() > 0) {
			for(Map<String, Object> map : list) {
				OrderLockStock orderLockStock = new OrderLockStock();
				orderLockStock.setLockStockId(StringUtil.nullToLong(map.get("lock_stock_id")));
				orderLockStock.setOrderId(StringUtil.nullToLong(map.get("order_id")));
				orderLockStock.setItemId(StringUtil.nullToLong(map.get("item_id")));
				orderLockStock.setProductId(StringUtil.nullToLong(map.get("product_id")));
				orderLockStock.setProductSpecId(StringUtil.nullToLong(map.get("product_spec_id")));
				orderLockStock.setSeckillId(StringUtil.nullToLong(map.get("seckill_id")));
				orderLockStock.setIsSpceProduct(StringUtil.nullToBoolean(map.get("is_spce_product")));
				orderLockStock.setQuantity(StringUtil.nullToInteger(map.get("quantity")));
				orderLockStock.setStatus(StringUtil.nullToBoolean(map.get("status")));
				orderLockStock.setType(StringUtil.nullToInteger(map.get("type")));
				orderLockStock.setOrderStatus(StringUtil.nullToInteger(map.get("order_status")));
				resultList.add(orderLockStock);
			}
		}
		return resultList; 
	}
}
