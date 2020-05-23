package com.chunruo.core.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.Refund;
import com.chunruo.core.vo.ChilderOrderVo;

public interface OrderManager extends GenericManager<Order, Long> {

	public List<Order> getOrderListByUserId(Long userId, int limit);
	
	public List<Order> getOrderListByShareUserId(Long shareUserId);
	
	public List<Order> getOrderListByStatus(Integer status, Boolean isSubOrder);

	public List<Order> getOrderSubListByParentOrderId(Long parentOrderId);

	public Order getOrderByOrderId(Long orderId);

	public Order saveOrder(Order order);

	public Order saveOrder(Order order, List<ChilderOrderVo> childerOrderList, List<Long> userCartIdList);

	public Order getOrderByOrderNo(String orderNo);

	public List<OrderItems> getOrderItemsListByOrderIdList(List<Long> orderIdList);
	
	public List<Order> getOrderListByOrderNoList(List<String> orderNoList);

	public void deleteOrder(Long orderId);
	
	public List<Order> updateOrderPushErpRecordByLoadFunction();
	
	public void updateOrderPushErpStatus(Long orderId, boolean isSuccess, String errorMsg);

	public List<Order> getOrderListByUpdateTime(Date updateTime);
	
	public Map<String, List<Long>> updateOrderPaymentSuccStatus(Long orderId, String transactionId, Integer paymentType, Long weChatConfigId, String responseData) throws Exception ;
	
	public boolean userAccountPaymentErrorClose(Long orderId, String transactionId, Integer paymentType, Long weChatConfigId, String responseData);
	
	public int updateOrderUnPaymentStatusClose();
	
	public void updateOrderPaymentTradeNo(Long orderId, String tradeNo, Integer status, Integer paymentType, Long weChatConfigId);

	public List<Order> getOrderListAfterCreateTime(Integer status, boolean isSubOrder, Date createTime, Date endDate);
	
	public void updateOrderCloseStatus(List<Long> orderIdList, String message, Long userId);
	
	public void updateSubOrderCloseStatus(List<Long> subOrderIdList, String message, Long userId);
	
	public void updateOrderReduction(List<Long> orderIdList, String message, Long userId);
	
	public List<Long> updateOrderCloseStatus(Long orderId, String message, Long userId, Refund refund, Long reasondId)  throws Exception;
	
	public List<Order> getOrderStatusListBeforeSyncTime(Integer status, Date beforeDate);
	
	public void updateOrderCompleteStatus(Integer status, Long orderId);
	
	public void updateOrderCompleteStatus(Integer status, List<Long> orderIdList);
	
	public void updateOrderOthrerCompleteStatus(Integer status, List<Long> orderIdList, Long parentOrderId);
	
	public void updateDirectMailWaitLibraryStatus(List<Long> orderIdList, Long userId);
	
	/**
	 * 修改结算状态
	 * @param isCheck
	 * @param orderId
	 */
	public void updateOrderCheckById(boolean isCheck, Long orderId);
	
	/**
	 * 获取待结算的订单
	 * @param status
	 * @param complateTime 
	 * @param isSubOrder
	 * @param isCheck
	 * @param interuptedStatus
	 * @return
	 */
	List<Order> getWaitCheckOrders(Integer status, Date complateTime, boolean isSubOrder, boolean isCheck);

	List<Order> getOrderListByUserIdList(List<Long> userIdList);
	
	public List<Order> getOrderListByTime(Date beginDate, Date endDate, List<Long> userIdList);
	
	//得到异常订单
	public List<Order> getAbmormalOrderList();
	
	public List<Order> getOrderListByCreateTime(Integer status, Date beginDate, Date endDate, Long userId);
	
	public List<Order> getCouponOrderListByUserCouponId(Long couponId);
	
	public List<Order> getBecameVipOrderList(Long userId, Date createTime);
	
	public List<Order> getOrderListByLikeParentOrderNo(String orderNo);
	
	public void updateOrderRestore(List<Long> orderIdList, String message, Long userId);
	
	public void updateOrderRestore(Long orderId, String message, Long userId);

	public void updateUnDeliverOrderStatus(Order order, String message);

	public List<Order> getOrderListByStoreId(Long storeId, int limit);

	public int countByIdentityNo(String identityNo, List<Integer> productTypeList);

	public List<Object[]> getOrderDetailByUserId(Long userId);
}
