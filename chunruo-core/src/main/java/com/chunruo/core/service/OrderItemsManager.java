package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.OrderItems;

public interface OrderItemsManager extends GenericManager<OrderItems, Long>{

	public List<OrderItems> getOrderItemsByOrderIdList(List<Long> orderIdList);

	public List<OrderItems> getOrderItemsListByOrderId(Long orderId);
	
	public List<OrderItems> getOrderItemsListByQroupUniqueBatch(Long orderId, String groupUniqueBatch);
	
	public List<OrderItems> getOrderSubItemsListByOrderId(Long orderId, Long subOrderId);
	
	public void updateOrderItemsEvaluateStatus(Long itemId, Boolean isEvaluate);

	public List<Object[]> getOrderItemListByIsInvitationAgentOrder(Boolean isInvitationAgent);

	public List<Object[]> getOrderItemsByOrderNoList(List<String> orderNoList);
	
	public List<OrderItems> getOrderItemsBySubOrderIdList(List<Long> subOrderIdList);

	public List<OrderItems> getOrderItemsListByNoEvaluate(Long storeId);
	
	public List<OrderItems> getListByPurchaseLimit(Long userId, String startPayTime, Long productId);
}
