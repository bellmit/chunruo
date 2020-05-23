package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.OrderCustomsRecord;

public interface OrderCustomsRecordManager extends GenericManager<OrderCustomsRecord, Long> {
	
	public OrderCustomsRecord getOrderCustomsRecordByOrderNo(String orderNo);
	
	public void updateOrderCustomsStatusSucc(Long recordId, Boolean isPushCustomSucc, String errorMsg);
	
	public List<OrderCustomsRecord> updateLoadPushPayCustomsQueryFunction();
	
	public OrderCustomsRecord saveOrderCustomsRecord(OrderCustomsRecord orderCustomsRecord);
	
	public OrderCustomsRecord saveRecord(Long orderId, OrderCustomsRecord orderCustomsRecord);
}
