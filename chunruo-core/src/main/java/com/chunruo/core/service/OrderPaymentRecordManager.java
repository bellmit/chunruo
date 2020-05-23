package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.OrderPaymentRecord;

public interface OrderPaymentRecordManager extends GenericManager<OrderPaymentRecord, Long>{
	
	public void deleteOtherByOrderId(Long orderId);
	
	public void saveOrderPaymentRecord(OrderPaymentRecord record);
	
	public OrderPaymentRecord getByOrderIdAndPaymentType(Long orderId, String orderNo, Integer paymentType, Long weChatConfigId);
	
	public List<OrderPaymentRecord> updateOrderPaymentRecordByLoadFunction();

}
